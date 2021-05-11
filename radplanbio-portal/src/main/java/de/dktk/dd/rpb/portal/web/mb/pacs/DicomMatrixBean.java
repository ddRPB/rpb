/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.portal.web.mb.pacs;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.RtTreatmentCase;
import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;

import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel bean for study centric patient DICOM matrix view for monitoring of DICOM data collection progress
 *
 * @author tomas@skripcak.net
 * @since 22 September 2015
 */
@Named("mbDicomMatrix")
@Scope("view")
public class DicomMatrixBean extends CrudEntityViewModel<StudySubject, Integer> {

    //region Injects

    //region Repository - Dummy

    @SuppressWarnings("unused")
    private IStudySubjectRepository repository;

    /**
     * Get StudyRepository
     * @return StudyRepository
     */
    @Override
    public IStudySubjectRepository getRepository() {
        return this.repository;
    }

    //endregion

    //region Main bean

    @Inject
    private MainBean mainBean;

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //endregion

    //region Members

    private String viewType;
    private Study rpbStudy;

    private EventDefinition selectedEventDef;
    private ItemDefinition selectedItemDef;

    private List<Integer> eventDataRepeatKeys;
    private Integer selectedEventDataRepeatKey;

    //endregion

    //region Properties

    public String getViewType() {
        return this.viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public Study getRpbStudy() {
        return this.rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    public EventDefinition getSelectedEventDef() {
        return this.selectedEventDef;
    }

    public void setSelectedEventDef(EventDefinition eventDef) {
        this.selectedEventDef = eventDef;
    }

    public ItemDefinition getSelectedItemDef() {
        return this.selectedItemDef;
    }

    public void setSelectedItemDef(ItemDefinition itemDef) {
        this.selectedItemDef = itemDef;
    }

    public List<Integer> getEventDataRepeatKeys() {
        return this.eventDataRepeatKeys;
    }

    public void setEventDataRepeatKeys(List<Integer> list) {
        this.eventDataRepeatKeys = list;
    }

    public Integer getSelectedEventDataRepeatKey() {
        return this.selectedEventDataRepeatKey;
    }

    public void setSelectedEventDataRepeatKey(Integer repeatKey) {
        this.selectedEventDataRepeatKey = repeatKey;
    }

    public boolean canShowRtDataTable() {
        return (this.selectedEventDef != null && this.selectedItemDef != null && this.selectedEventDataRepeatKey != null);
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.viewType = "DICOM";

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();
    }

    //endregion

    //region Commands

    public void changeView() {
        if (this.viewType != null) {
            if (this.viewType.equals("DICOM")) {
                this.setPreSortOrder(
                    this.buildSortOrder()
                );
            }
            else if (this.viewType.equals("DICOM-RT")) {
                this.setPreSortOrder(
                    this.buildRtSortOrder()
                );
            }
        }
    }

    public void reloadOccurrences() {
        this.eventDataRepeatKeys = new ArrayList<>();

        int max = -1;
        if (this.entityList != null && this.selectedEventDef != null) {
            for (StudySubject ss : this.entityList) {
                int temp = ss.getEventOccurrencesCountForEventDef(this.selectedEventDef);
                if (temp > max) {
                    max = temp;
                }
            }
        }

        for (int i = 0;i < max; i++) {
            this.eventDataRepeatKeys.add(i + 1);
        }
    }

    //region DICOM

    public String loadDicomStudyType(StudySubject ss, EventData eventData, ItemDefinition dicomItemDef) {
        String result = "-";

        // TODO: all of this should go out of viewModel, ideally to facade where we update our domain entity (StudySubject)
        try {
            // Get value of Item (StudyInstanceUID)
            ItemData itemData = ss.getItemDataForItemDef(
                    eventData,
                    dicomItemDef
            );

            // When the DICOM study was not loaded yet, load it from PACS
            if (itemData != null && itemData.hasValue() && !ss.hasDicomStudyWithUid(itemData.getValue())) {
                if  (this.mainBean.getPacsService() != null) {
                    DicomStudy dicomStudy = this.mainBean.getPacsService()
                            .loadPatientStudy(
                                    ss.getPid(),
                                    itemData.getValue()
                            );

                    // Should be just one DICOM study
                    if (dicomStudy != null) {
                        // Store relation to crf Item definition
                        dicomStudy.setCrfItemDefinition(dicomItemDef);

                        result = dicomStudy.getStudyType() + " [" + dicomStudy.getStudyDate() + "]";

                        // We place this study to entity graph so it does not have to be always queried from PACS
                        ss.addDicomStudyForSubject(dicomStudy);
                    }
                }
            }
            // Otherwise use preloaded
            else if (itemData != null && itemData.hasValue()) {
                DicomStudy dicomStudy = ss.getDicomStudyWithUid(itemData.getValue());
                result = dicomStudy.getStudyType() + " [" + dicomStudy.getStudyDate() + "]";
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        return result;
    }

    public String loadDicomStudyLegend(StudySubject ss, EventData eventData, ItemDefinition dicomItemDef) {
        String result = "DICOM study does not exists in research PACS";

        // Get item data for item definition
        ItemData itemData = ss.getItemDataForItemDef(
                eventData,
                dicomItemDef
        );

        if (itemData != null && itemData.hasValue()) {
            for (DicomStudy ds : ss.getPerson().getDicomStudies()) {
                if (ds.getCrfItemDefinition().equals(dicomItemDef)) {
                    result = ds.getStudyDescription();
                    break;
                }
            }
        }
        else {
            // isRequired does not exist... have to be loaded from itemRef...
            //if (dicomItemDef.getIsRequired()) {
                result = "No reference to DICOM study in EDC";
            //}
            //else {
            //    result = "No reference to optional DICOM study in EDC";
            //}
        }

        return result;
    }

    public String loadDicomStudyStatus(StudySubject ss, EventData eventData, ItemDefinition dicomItemDef) {
        String result = "FAILURE";

        // Get value of Item
        ItemData itemData = ss.getItemDataForItemDef(
                eventData,
                dicomItemDef
        );

        if (itemData != null && itemData.hasValue()) {
            for (DicomStudy ds : ss.getPerson().getDicomStudies()) {
                if (ds.getCrfItemDefinition().equals(dicomItemDef)) {
                    result = "SUCCESS";
                    break;
                }
            }
        }
//        else {
//            if (!dicomItemDef.getIsRequired()) {
//                result = "PARTIAL_SUCCESS";
//            }
//        }

        return result;
    }

    public String loadDicomStudyUid(StudySubject ss, EventData eventData, ItemDefinition dicomItemDef) {
        String result = "";

        try {
            // Get value of Item
            ItemData itemData = ss.getItemDataForItemDef(
                    eventData,
                    dicomItemDef
            );

            if (itemData != null) {
                result = itemData.getValue();
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        return result;
    }

    //endregion

    //region DICOM-RT

    public RtTreatmentCase loadTreatmentCase(StudySubject ss) {
        RtTreatmentCase result = null;

        if (ss != null && this.selectedEventDef != null) {
            try {
                EventData selectedEventData = ss.getEventOccurrenceForEventDef(
                        this.selectedEventDef,
                        this.selectedEventDataRepeatKey
                );

                if (selectedEventData != null) {
                    // Get value of Item (StudyInstanceUID)
                    ItemData itemData = ss.getItemDataForItemDef(
                            selectedEventData,
                            this.selectedItemDef
                    );

                    if (itemData != null) {
                        DicomStudy dicomStudy = ss.getDicomStudyWithUid(itemData.getValue());
                        if (dicomStudy != null && dicomStudy.getRtTreatmentCase() == null) {
                            result = this.mainBean.getPacsService().loadRtTreatmentCase(
                                    ss.getPid(),
                                    dicomStudy
                            );

                            dicomStudy.setRtTreatmentCase(result);
                        }
                        else if (dicomStudy != null) {
                            result = dicomStudy.getRtTreatmentCase();
                        }
                    }
                }
            }
            catch (Exception err) {
                this.messageUtil.error(err);
            }
        }

        return result;
    }

    //endregion

    //endregion

    //region Overrides

    @Override
    public void load() {
        try {
            // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
            this.studyIntegrationFacade.init(this.mainBean);
            this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

            this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();
            this.entityList = this.studyIntegrationFacade.loadOdmStudySubjects();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    protected List<SortMeta> buildRtSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtRtEntities:colRtStudySubjectId", "colRtStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // StudySubjectID
        result.add(Boolean.FALSE); // PID
        result.add(Boolean.FALSE); // SecondaryID
        result.add(Boolean.FALSE); // Gender
        result.add(Boolean.TRUE); // RTPLAN
        result.add(Boolean.TRUE); // RTSTRUCT
        result.add(Boolean.TRUE); // RTDOSE

        return result;
    }

    //endregion

}
