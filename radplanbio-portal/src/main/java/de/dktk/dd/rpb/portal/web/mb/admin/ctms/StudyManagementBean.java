/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.admin.ctms;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.facade.CtpPipelineLookupTableUpdaterFacade;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for EDC study management.
 *
 * @author tomas@skripcak.net
 * @since 13 Sep 2013
 */
@Named("mbStudyManagement")
@Scope("view")
public class StudyManagementBean extends CrudEntityViewModel<Study, Integer> {

    //region Injects

    //region Main bean

    private MainBean mainBean;

    //endregion

    //region Repository

    private IStudyRepository repository;

    /**
     * Get StudyRepository
     * @return repository
     */
    public IStudyRepository getRepository() {
        return this.repository;
    }

    //endregion

    private AuditLogService auditLogService;

    private CtpPipelineLookupTableUpdaterFacade ctpPipelineLookupTableUpdaterFacade;

    //endregion

    //region Constructors

    @Inject
    public StudyManagementBean(
            IStudyRepository repository,
            MainBean mainBean,
            AuditLogService auditLogService,
            CtpPipelineLookupTableUpdaterFacade ctpPipelineLookupTableUpdaterFacade) {
        this.repository = repository;
        this.mainBean = mainBean;
        this.auditLogService = auditLogService;
        this.ctpPipelineLookupTableUpdaterFacade = ctpPipelineLookupTableUpdaterFacade;
    }

    //endregion

    //region Members

    private List<StudyType> studyTypeList; // EDC studies
    private EventDefinition selectedEventDefinition;
    private FormDefinition selectedFormDefinition;
    private ItemGroupDefinition selectedItemGroupDefinition;
    private ItemDefinition selectedItemDefinition;

    private Odm odm;

    //endregion

    //region Properties

    //region StudyTypeList

    /**
     * Get Study Type List
     *
     * @return List - Study Type List
     */
    public List<StudyType> getStudyTypeList() {
        return this.studyTypeList;
    }

    //endregion

    //region SelectedEventDefinition

    public EventDefinition getSelectedEventDefinition() {
        return this.selectedEventDefinition;
    }

    public void setSelectedEventDefinition(EventDefinition value) {
        this.selectedEventDefinition = value;
    }

    //endregion

    //region SelectedFormDefinition

    public FormDefinition getSelectedFormDefinition() {
        return this.selectedFormDefinition;
    }

    public void setSelectedFormDefinition(FormDefinition value) {
        this.selectedFormDefinition = value;
    }

    //endregion

    //region SelectedItemGroupDefinition

    public ItemGroupDefinition getSelectedItemGroupDefinition() {
        return this.selectedItemGroupDefinition;
    }

    public void setSelectedItemGroupDefinition(ItemGroupDefinition selectedItemGroupDefinition) {
        this.selectedItemGroupDefinition = selectedItemGroupDefinition;
    }

    //endregion

    //region SelectedItemDefinition

    public ItemDefinition getSelectedItemDefinition() {
        return this.selectedItemDefinition;
    }

    public void setSelectedItemDefinition(ItemDefinition selectedItemDefinition) {
        this.selectedItemDefinition = selectedItemDefinition;
    }

    //endregion

    //region Odm

    public Odm getOdm() {
        return this.odm;
    }

    public void setOdm(Odm odm) {
        this.odm = odm;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.reload();
    }

    //endregion

    //region Commands

    //region Study

    /**
     * Reload fresh data from EDC
     */
    public void reload() {
        try {
            // Load study entities
            super.load();
            // Reload available EDC studies via web service
            this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Reload study metadata from EDC OpenClinica
     */
    public void loadStudyMetadata() {
        try {
            // Get ODM metadata for user's active study
            MetadataODM metadata = this.mainBean
                    .getOpenClinicaService()
                    .getStudyMetadata(this.selectedEntity.getOcStudyIdentifier());

            // XML to DomainObjects
            this.odm = metadata.unmarshallOdm();
            this.odm.updateHierarchy();
        } catch (Exception err) {
            messageUtil.error(err);
        }
    }

    public void refreshStudyMetadata(Study study) {

        try {
            // Reload study metadata for parent and all children
            if (this.studyTypeList != null) {
                for (StudyType st : this.studyTypeList) {
                    if (st.getIdentifier().equalsIgnoreCase(study.getOcStudyIdentifier())) {

                        this.mainBean
                                .getOpenClinicaService()
                                .refreshStudyMetadataCache(study.getOcStudyIdentifier());

                        if (st.getSites() != null && st.getSites().getSite() != null) {
                            for (SiteType siteType : st.getSites().getSite()) {

                                this.mainBean
                                        .getOpenClinicaService()
                                        .refreshStudyMetadataCache(siteType.getIdentifier());
                            }
                        }

                        break;
                    }
                }
            }

            messageUtil.infoText("Study metadata cache refreshed.");
        } catch (Exception err) {
            messageUtil.error(err);
        }
    }

    //endregion

    //region updateCtp

    /***
     * Updates the subject identifier lookup table on the CTP system. In case of a multi centric study,
     * it uses the partner_site_identifier property to identify the corresponding site related study
     * and the associated StudySubjects.
     * @param study Study
     */

    public void doUpdateSubjectLookupTable(Study study) {
        String edcCode = study.getTagValue("EDC-code");
        String partnerSideIdentifier = this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier");

        if (edcCode == null || edcCode.isEmpty()) {
            messageUtil.warningText("Study has no EDC-code tag.");
            return;
        }

        AuditEvent auditEvent = AuditEvent.CTPLookupUpdate;
        auditLogService.event(auditEvent, edcCode);

        List<Boolean> successList = ctpPipelineLookupTableUpdaterFacade.updateSubjectLookupForStudy(study, partnerSideIdentifier);

        if (successList.size() == 0) {
            messageUtil.warningText("No elements updated of the study " + edcCode);
        } else {
            if (successList.contains(false)) {
                messageUtil.errorText("There was a problem updating the CTP lookup table for the study with edcCode " + edcCode + ".");
            } else {
                messageUtil.infoText("Successful update of " + successList.size() + " CTP lookup table elements");
            }
        }
    }

    //endregion

    //region DataMapping

    public void doAddDataMapping(Mapping mapping) {
        this.selectedEntity.addDataMapping(mapping);
        this.doUpdateEntity();
    }

    public void doRemoveDataMapping(Mapping mapping) {
        this.selectedEntity.removeDataMapping(mapping);
        this.doUpdateEntity();
    }

    //endregion

    //region CrfAnnotation

    /**
     * Create a new eCRF field annotation for a study
     */
    public void doCreateAnnotation(CrfFieldAnnotation annotation, DualListModel<ItemDefinition> itemDefinitions) {
        try {
            // Collect pick list selection
            for (ItemDefinition selectedItem : itemDefinitions.getTarget()) {

                CrfFieldAnnotation newAnnotation = new CrfFieldAnnotation(annotation);
                newAnnotation.setCrfItemOid(selectedItem.getOid());
                this.selectedEntity.addCrfFieldAnnotation(newAnnotation);
            }

            this.doUpdateEntity();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete existing eCRF field annotation
     */
    public void doRemoveAnnotation(CrfFieldAnnotation annotation) {
        try {
            this.selectedEntity.removeCrfFieldAnnotation(annotation);
            this.doUpdateEntity();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudies:colStudyName", "colStudyName", SortOrder.ASCENDING);
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

        result.add(Boolean.FALSE); // EDC Identifier
        result.add(Boolean.TRUE); // Study short label
        result.add(Boolean.FALSE); // Principal site
        result.add(Boolean.FALSE); // Stratify site

        return result;
    }

    //endregion

}