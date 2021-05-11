/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;

import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;

import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;

import org.omnifaces.util.Faces;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

/**
 * ViewModel bean for DICOM patient study subjects
 *
 * @author tomas@skripcak.net
 * @since 07 August 2013
 */
@Named("mbDicomStudies")
@Scope("view")
public class DicomPatientStudyBean extends CrudEntityViewModel<StudySubject, Integer> {

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

    private MainBean mainBean;
    private StudyIntegrationFacade studyIntegrationFacade;
    private IPartnerSiteRepository partnerSiteRepository;

    //endregion

    //region Members

    private Study rpbStudy; // RPB study
    private PartnerSite selectedSubjectSite; // RPB partner site
    private List<EventDefinition> dicomEventDefinitions;

    private EventData selectedDicomEventData;
    private List<DicomStudy> dicomStudyList;

    private List<Boolean> dicomStudyColumnVisibilityList;

    private String newDicomViewType;
    private ItemDefinition newDicomItem;

    //endregion

    //region Constructor

    @Inject
    public DicomPatientStudyBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade, IPartnerSiteRepository partnerSiteRepository) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.partnerSiteRepository = partnerSiteRepository;
    }
    
    //endregion

    //region Properties

    //region RPB Study

    public Study getRpbStudy() {
        return this.rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    //endregion

    //region RPB PartnerSite

    public PartnerSite getSelectedSubjectSite() {
        return this.selectedSubjectSite;
    }

    public void setSelectedSubjectSite(PartnerSite selectedSubjectSite) {
        this.selectedSubjectSite = selectedSubjectSite;
    }

    //endregion

    //region DICOM EventDefinitions

    public List<EventDefinition> getDicomEventDefinitions() {
        return this.dicomEventDefinitions;
    }

    //endregion

    //region DICOM SelectedEventData

    public EventData getSelectedDicomEventData() {
        return selectedDicomEventData;
    }

    public void setSelectedDicomEventData(EventData selectedDicomEventData) {
        this.selectedDicomEventData = selectedDicomEventData;
    }

    //endregion

    //region DICOM StudyList

    public List<DicomStudy> getDicomStudyList() {
        return dicomStudyList;
    }

    public void setDicomStudyList(List<DicomStudy> dicomStudyList) {
        this.dicomStudyList = dicomStudyList;
    }

    //endregion

    //region DICOM StudyColumnVisibilityList

    public List<Boolean> getDicomStudyColumnVisibilityList() {
        return dicomStudyColumnVisibilityList;
    }

    public void setDicomStudyColumnVisibilityList(List<Boolean> dicomStudyColumnVisibilityList) {
        this.dicomStudyColumnVisibilityList = dicomStudyColumnVisibilityList;
    }

    //endregion

    //region DICOM New

    public String getNewDicomViewType() {
        return this.newDicomViewType;
    }

    public void setNewDicomViewType(String newDicomViewType) {
        this.newDicomViewType = newDicomViewType;
    }

    public ItemDefinition getNewDicomItem() {
        return this.newDicomItem;
    }

    public void setNewDicomItem(ItemDefinition newDicomItem) {
        this.newDicomItem = newDicomItem;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        
        this.newDicomViewType = "CLIENT";

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.setDicomStudyColumnVisibilityList(
                this.buildDicomStudiesVisibilityList()
        );

        // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
        this.studyIntegrationFacade.init(this.mainBean);
        this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

        this.load();
    }

    //endregion

    //region Commands

    //region Subjects

    public void resetSubjects() {
        this.entityList = null;
        this.selectedEntity = null;
        this.selectedSubjectSite = null;
        this.selectedDicomEventData = null;
        this.dicomStudyList = null;
    }

    /**
     * Load clinical data as well as metadata details about selected study subject
     */
    public void loadSelectedSubjectDetails() {

        // Clear loaded collections
        this.resetSubjectEvents();
        this.resetSubjectDicomStudies();

        try {
            // Load ODM resource for selected study subject
            String queryOdmXmlPath = this.mainBean.getActiveStudy().getOcoid() + "/" + this.selectedEntity.getStudySubjectId() + "/*/*";
            Odm selectedStudySubjectOdm = this.mainBean.getOpenClinicaService().getStudyCasebookOdm(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
            );

            // Create Defs from Refs
            selectedStudySubjectOdm.updateHierarchy();

            // Load RPB partner site where the subject belong
            this.selectedSubjectSite = this.determineSubjectPartnerSite(selectedStudySubjectOdm);

            // Replace selected SOAP subject with REST subject that has clinical data
            this.selectedEntity = selectedStudySubjectOdm.findUniqueStudySubjectOrNone(
                this.selectedEntity.getStudySubjectId()
            );

            // Add reference to study
            ClinicalData cd = selectedStudySubjectOdm.findUniqueClinicalDataOrNone();
            de.dktk.dd.rpb.core.domain.edc.Study study = selectedStudySubjectOdm.findUniqueStudyOrNone(cd.getStudyOid());
            this.selectedEntity.setStudy(study);

            // Link clinical data with definitions from ODM
            this.selectedEntity.linkOdmDefinitions(selectedStudySubjectOdm);

            this.messageUtil.infoText(
            "Event data for " + this.selectedEntity.getStudySubjectId() +
                Constants.RPB_IDENTIFIERSEP + "[" + this.selectedEntity.getPid() + "]" +
                " loaded."
            );
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Events

    public void resetSubjectEvents() {
        this.selectedDicomEventData = null;
    }

    //endregion

    //region DICOM

    public void resetSubjectDicomStudies() {
        this.dicomStudyList = null;
    }

    /**
     * Reload DICOM studies in selected study/subject/event based on annotated CRF values
     */
    public void loadDicomStudies() {
        this.resetSubjectDicomStudies();

        try {
            if (this.selectedDicomEventData != null) {
                
                // Just annotation concerning selected event
                CrfFieldAnnotation eventExample = new CrfFieldAnnotation();
                eventExample.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());

                // Load CRF items referencing DICOM studies
                List<ItemData> dicomCrfItemData = this.selectedDicomEventData.findAnnotatedItemData(
                    this.rpbStudy.findAnnotations("DICOM_STUDY_INSTANCE_UID", eventExample)
                );

                if (this.mainBean.getPacsService() != null) {

                    // Load DICOM data from PACS
                    this.dicomStudyList = this.mainBean.getPacsService().loadPatientStudies(
                            this.selectedEntity.getPid(),
                            dicomCrfItemData
                    );

                    // Extend DICOM study data about CRF field label
                    for (DicomStudy dcmStudy : this.dicomStudyList) {
                        for (ItemData itemData : dicomCrfItemData) {
                            if (dcmStudy.getStudyInstanceUID().equals(itemData.getValue())) {
                                dcmStudy.setCrfItemDefinition(itemData.getItemDefinition());
                            }
                        }
                    }
                }

                this.messageUtil.infoText(
                    "DICOM data for " + this.selectedEntity.getStudySubjectId() +
                    Constants.RPB_IDENTIFIERSEP + "[" + this.selectedEntity.getPid() + "]" +
                    " loaded."
                );
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public List<ItemDefinition> loadUploadSlots() {

        List<ItemDefinition> results = new ArrayList<>();

        if (rpbStudy != null && this.selectedDicomEventData != null) {

            List<ItemDefinition> dicomItemDefinitions = this.rpbStudy.findAnnotatedItemDefinitionsForEventDef(
                    "DICOM_STUDY_INSTANCE_UID",
                    this.selectedDicomEventData.getEventDefinition()
            );

            if (dicomItemDefinitions != null) {
                for (ItemDefinition dicomItemDefinition : dicomItemDefinitions) {
                    boolean hasValue = false;
                    if (this.dicomStudyList != null) {
                        for (DicomStudy dicomStudy : this.dicomStudyList) {
                            if (dicomStudy.getCrfItemDefinition().getOid().equals(dicomItemDefinition.getOid())) {
                                hasValue = true;
                                break;
                            }
                        }
                    }

                    if (!hasValue) {
                        results.add(dicomItemDefinition);
                    }
                }
            }
        }

         return results;
    }

    //TODO: we will be not supporting web start anymore, only rich client for upload
    public void upload() throws IOException {

        // Core attributes (pseudonym and salt for hashing)
        String queryStrings = "?@PID=" + this.selectedEntity.getPid();

        // Salt is PartnerSite identifier - EDC-code - StudySubjectID
        String salt = this.selectedSubjectSite.getIdentifier() +
                Constants.RPB_IDENTIFIERSEP +
                this.rpbStudy.getTagValue("EDC-code") +
                Constants.RPB_IDENTIFIERSEP +
                this.selectedEntity.getStudySubjectId();
        queryStrings += "&@SALT=" + salt;

        // EDC attributes for import
        queryStrings += "&studyOID=" + this.selectedEntity.getStudy().getOid();
        queryStrings += "&subjectKey=" + this.selectedEntity.getSubjectKey();
        queryStrings += "&studyEventOID=" + this.selectedDicomEventData.getStudyEventOid();
        queryStrings += "&eventRepeatKey=" + this.selectedDicomEventData.getStudyEventRepeatKey();
        queryStrings += "&formOID=" + this.newDicomItem.getItemGroupDefinition().getFormDefinition().getOid();
        queryStrings += "&itemGroupOID=" + this.newDicomItem.getItemGroupDefinition().getOid();
        queryStrings += "&dicomStudyItemOID=" + this.newDicomItem.getOid();

        // Find corresponding PatientID annotation
        CrfFieldAnnotation example = new CrfFieldAnnotation();
        example.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());
        example.setFormOid(this.newDicomItem.getItemGroupDefinition().getFormDefinition().getOid());
        example.setGroupOid(this.newDicomItem.getItemGroupDefinition().getOid());

        // Should be just one
        List<CrfFieldAnnotation> patientIdAnnotations = this.rpbStudy.findAnnotations("DICOM_PATIENT_ID", example);
        if (patientIdAnnotations != null && patientIdAnnotations.size() == 1) {
            queryStrings += "&patientIDItemOID=" + patientIdAnnotations.get(0).getCrfItemOid();
        }

        // If gender is collected
        if (this.selectedEntity.getSex() != null && !this.selectedEntity.getSex().isEmpty()) {
            queryStrings += "&gender=" + this.selectedEntity.getSex();
        }

        // If full dob is collected
        if (this.selectedEntity.getStudy().getMetaDataVersion().getStudyDetails().getStudyParameterConfiguration().getCollectSubjectDob().equals(EnumCollectSubjectDob.YES) &&
            this.selectedEntity.getDateOfBirth() != null && !this.selectedEntity.getDateOfBirth().isEmpty()) {
            queryStrings += "&birthDate=" + this.selectedEntity.getDateOfBirth();
        }
        // Otherwise if year of birth is collected
        else if (this.selectedEntity.getStudy().getMetaDataVersion().getStudyDetails().getStudyParameterConfiguration().getCollectSubjectDob().equals(EnumCollectSubjectDob.YES) &&
                 this.selectedEntity.getYearOfBirth() != null) {
            queryStrings +=  "&birthDate=" + this.selectedEntity.getYearOfBirth().toString();
        }

        // TODO: First I need to create necessary DICOM study annotation types in database
        //studyType=TreatmentPlan
        //CrfFieldAnnotation dicomStudyItemAnnotation = new CrfFieldAnnotation();
        //dicomStudyItemAnnotation.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());
        //dicomStudyItemAnnotation.setFormOid(this.newDicomItem.getItemGroupDefinition().getFormDefinition().getOid());
        //dicomStudyItemAnnotation.setGroupOid(this.newDicomItem.getItemGroupDefinition().getOid());
        //dicomStudyItemAnnotation.setCrfItemOid(this.newDicomItem.getOid());
        //List<AnnotationType> annotationTypes = this.rpbStudy.findAnnotationsTypes(dicomStudyItemAnnotation);
        
        // TODO: depends on partner site the idea was to have one client per partner site to allow customisations
        queryStrings += "&app=" + "CTPClient";

        // Reset for new upload
        this.newDicomItem = null;

        FacesContext.getCurrentInstance().getExternalContext().redirect("/pacs/studyUpload.faces" + queryStrings);
    }

    public void downloadDicomStudy(String dicomPatientId, String dicomStudyUid, Integer verifySeriesCount) {
        try {
            InputStream is = this.mainBean.getSvcWebApi().pacsCreateStudyArchive(
                    dicomPatientId,
                    dicomStudyUid,
                    verifySeriesCount,
                    mainBean.getMyAccount().getApiKey()
            );

            if (is != null) {
                String filename = dicomPatientId + "-" + dicomStudyUid + ".zip";
                Faces.sendFile(is, filename, true);
            } else {
                this.messageUtil.error("Download of DICOM study failed.");
            }

        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void downloadDicomSeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid) {
        try {
            InputStream is = this.mainBean.getSvcWebApi().pacsCreateSeriesArchive(
                    dicomPatientId,
                    dicomStudyUid,
                    dicomSeriesUid,
                    mainBean.getMyAccount().getApiKey()
            );

            if (is != null) {
                String filename = dicomPatientId + "-" + dicomSeriesUid + ".zip";
                Faces.sendFile(is, filename, true);
            } else {
                this.messageUtil.error("Download of DICOM series failed.");
            }

        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Overrides

    @Override
    public void load() {
        try {
            this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();

            // Does this study involved DICOM sample data collection
            if (Boolean.valueOf(this.rpbStudy.getTagValue("DICOM"))) {

                // Look for event that are collecting DICOM data
                String annotationTypeName = "DICOM_STUDY_INSTANCE_UID";
                this.dicomEventDefinitions = this.rpbStudy.findAnnotatedEventDefinitions(annotationTypeName);

                this.resetSubjects();
                this.entityList = this.studyIntegrationFacade.loadStudySubjects();

                this.messageUtil.infoText("Study subjects loaded.");
            }
            else {
                this.messageUtil.infoText("To access RPB DICOM patient feature the active study have to be be tagged with DICOM study tag.");
            }
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
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtDicomSubjects:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
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
        result.add(Boolean.TRUE); // PID
        result.add(Boolean.FALSE); // SecondaryID
        result.add(Boolean.TRUE); // Gender
        result.add(Boolean.TRUE); // Enrollment date

        return result;
    }

    //endregion

    //region Private methods

    private List<Boolean> buildDicomStudiesVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // eCRF Item label
        results.add(Boolean.FALSE); // DICOM Study Description
        results.add(Boolean.TRUE); // Type (modality)
        results.add(Boolean.TRUE); // Study Date
        results.add(Boolean.FALSE); // Study UID

        return results;
    }

    // TODO: this functionality should be moved somewhere else
    private PartnerSite determineSubjectPartnerSite(Odm studySubjectOdm) {

        PartnerSite result;

        // Query by example
        String siteIdentifier;
        PartnerSite example = new PartnerSite();

        // Active study is mono-centre or it is parent study in multi-centre setup
        if (this.mainBean.getActiveStudy().getUniqueIdentifier().equals(this.rpbStudy.getOcStudyIdentifier())) {

            String parentStudyProtocolId = this.rpbStudy.getOcStudyIdentifier();
            ClinicalData cd = studySubjectOdm.findUniqueClinicalDataOrNone();
            de.dktk.dd.rpb.core.domain.edc.Study studySite = studySubjectOdm.findUniqueStudyOrNone(cd.getStudyOid());
            siteIdentifier = studySite.extractPartnerSiteIdentifier(parentStudyProtocolId);
        }
        // Otherwise it is site study in multi-centre setup
        else {
            String siteStudyProtocolId = this.mainBean.getActiveStudy().getUniqueIdentifier();
            int index = siteStudyProtocolId.indexOf(Constants.RPB_IDENTIFIERSEP);
            siteIdentifier = siteStudyProtocolId.substring(0, index);
        }

        // Mono-centre get partner site from RPB study principal site
        if (siteIdentifier.isEmpty()) {
            result = this.rpbStudy.getPartnerSite();
        }
        // Multi-centre
        else {
            example.setIdentifier(siteIdentifier);
            result = this.partnerSiteRepository.findUniqueOrNone(example);
        }

        return result;
    }

    //endregion

}