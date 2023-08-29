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

import de.dktk.dd.rpb.core.builder.edc.OdmBuilder;
import de.dktk.dd.rpb.core.builder.edc.OdmBuilderDirector;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.EnumCollectSubjectDob;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.IUploaderService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.omnifaces.util.Faces;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;
import static javax.faces.context.FacesContext.getCurrentInstance;

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

    private MainBean mainBean;
    private StudyIntegrationFacade studyIntegrationFacade;
    private IPartnerSiteRepository partnerSiteRepository;
    private IUploaderService uploaderService;

    //endregion

    //region Members

    private static final Logger log = LoggerFactory.getLogger(DicomPatientStudyBean.class);
    private final AuditLogService auditLogService;
    private Study rpbStudy; // RPB study
    @SuppressWarnings("unused")
    private IStudySubjectRepository repository;
    private PartnerSite selectedSubjectSite; // RPB partner site
    private List<EventDefinition> dicomEventDefinitions;

    private EventData selectedDicomEventData;
    private List<DicomStudy> dicomStudyList;
    private List<DicomStudy> assignableDicomStudies;

    private List<Boolean> dicomStudyColumnVisibilityList;

    private String newDicomViewType;
    private ItemDefinition uploadSlotItemDefinition;

    private ItemDefinition selectedSlotItemDefinition;
    private DicomStudy associatedDicomStudy;

    private String redirectUrl = "";

    //endregion

    // region URL parameters

    private String pid;
    private String eventId;
    private String eventRepeatKey;

    // endregion

    //region Constructor

    @Inject
    public DicomPatientStudyBean(
            MainBean mainBean,
            StudyIntegrationFacade studyIntegrationFacade,
            IPartnerSiteRepository partnerSiteRepository,
            AuditLogService auditLogService,
            IUploaderService uploaderService
    ) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.partnerSiteRepository = partnerSiteRepository;
        this.auditLogService = auditLogService;
        this.uploaderService = uploaderService;
    }

    //endregion

    // region Getter and Setter Members

    public StudySubject getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(StudySubject selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    /**
     * Get StudyRepository
     *
     * @return StudyRepository
     */
    public IStudySubjectRepository getRepository() {
        return this.repository;
    }

    // endregion

    // region Getter and Setter URL parameters

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventRepeatKey() {
        return eventRepeatKey;
    }

    public void setEventRepeatKey(String eventRepeatKey) {
        this.eventRepeatKey = eventRepeatKey;
    }

    // endregion


    //region Properties

    //region RPB Study

    public Study getRpbStudy() {
        return this.rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    //endregion

    //region FilteredEntities

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

    public List<DicomStudy> getAssignableDicomStudyCandidates() {
        if (this.assignableDicomStudies == null || this.uploadSlotItemDefinition == null) {
            return new ArrayList<>();
        }

        return this.assignableDicomStudies;
    }


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

    public ItemDefinition getUploadSlotItemDefinition() {
        return this.uploadSlotItemDefinition;
    }

    public void setUploadSlotItemDefinition(ItemDefinition uploadSlotItemDefinition) {
        this.uploadSlotItemDefinition = uploadSlotItemDefinition;
    }

    //endregion

    public ItemDefinition getSelectedSlotItemDefinition() {
        return selectedSlotItemDefinition;
    }

    public void setSelectedSlotItemDefinition(ItemDefinition selectedSlotItemDefinition) {
        this.selectedSlotItemDefinition = selectedSlotItemDefinition;
    }

    public DicomStudy getAssociatedDicomStudy() {
        return associatedDicomStudy;
    }

    public void setAssociatedDicomStudy(DicomStudy associatedDicomStudy) {
        this.associatedDicomStudy = associatedDicomStudy;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


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

    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (this.pid != null && this.selectedEntity == null) {
                this.setFirstMatchingStudySubjectAsSelectedEntity();
                this.setFirstMatchingEventOccurrenceAsSelectedDicomEvent();

            }
        }
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

            String studyOid = this.mainBean.getActiveStudy().getOcoid();
            String studySubjectId = this.selectedEntity.getStudySubjectId();
            Odm selectedStudySubjectOdm = getOdmFromEdc(studySubjectId, studyOid);

            // Load RPB partner site where the subject belong
            this.selectedSubjectSite = this.determineSubjectPartnerSite(selectedStudySubjectOdm);
            this.updateSelectedEntityWithEdcData(studySubjectId, studyOid, selectedStudySubjectOdm);

            this.messageUtil.infoText(
                    "Event data for " + studySubjectId +
                            Constants.RPB_IDENTIFIERSEP + "[" + this.selectedEntity.getPid() + "]" +
                            " loaded."
            );
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load ODM for specific study and subject from EDC
     *
     * @param studyOid       String Openclinica OID for study
     * @param studySubjectId String StudySubjectId
     * @return ODM data from OpenClinica
     */
    private Odm getOdmFromEdc(String studySubjectId, String studyOid) {
        String queryOdmXmlPath = studyOid + "/" + studySubjectId + "/*/*";
        Odm selectedStudySubjectOdm = this.mainBean.getEngineOpenClinicaService().getStudyCasebookOdm(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        // Create Defs from Refs
        selectedStudySubjectOdm.updateHierarchy();
        return selectedStudySubjectOdm;
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
        this.selectedSlotItemDefinition = null;
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
        } catch (Exception err) {
            log.error(err.getMessage(), err);
            this.messageUtil.error(err);
        }
    }

    /**
     * Loads DicomStudies with matching StudySubject pid from the PACS system and filters for DicomStudies with EDC-code
     * that matches the selected study. Results will be stored in this.assignableDicomStudies.
     */
    public void loadAssignableDicomStudies() {
        if (this.uploadSlotItemDefinition == null) {
            String errorText = "There is a problem loading assignable Dicom studies.";
            String adviceText = "Please try to reload the page.";
            messageUtil.errorText(errorText + " " + adviceText);
            return;
        }

        if (this.selectedDicomEventData == null) {
            messageUtil.errorText("Please choose an event from the #{msg.studyEvent_plural} tab");
            return;
        }

        if (this.selectedEntity == null || this.selectedEntity.getPid().isEmpty()) {
            messageUtil.errorText("Please choose a #{msg.studySubject_plural}");
            return;
        }

        String studySubjectPid = this.selectedEntity.getPid();
        String edcCodePrefix = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern(
                this.rpbStudy.getTagValue("EDC-code")
        );

        List<DicomStudy> dicomStudies = this.mainBean.getPacsService().loadPatientStudies(studySubjectPid);
        this.assignableDicomStudies = getFilteredDicomStudiesByEdcCode(edcCodePrefix, dicomStudies);
    }

    public URL getRedirectUrlToUploaderForSelectedSlot() throws MalformedURLException, UnsupportedEncodingException {
        if (this.selectedSlotItemDefinition != null) {
            return this.getRedirectUrlToUploader(this.selectedSlotItemDefinition);
        }
        return null;
    }

    /***
     * Creates a URL to the uploader component including the parameters of the upload slot that was selected in UI
     *
     * @param itemDefinition selected ItemDefinition (Upload Slot) to be used to generate the URL parameters
     * @return URL to the RPB Uploader component
     * @throws MalformedURLException
     */
    public URL getRedirectUrlToUploader(ItemDefinition itemDefinition) throws MalformedURLException, UnsupportedEncodingException {

        this.uploadSlotItemDefinition = itemDefinition;

        List<String> parametersArray = new ArrayList<>();

        if (this.rpbStudy != null && itemDefinition != null) {
            if (this.rpbStudy.getEdcStudy() != null) {
                extractUploadSlotParametersFromRpbStudy(itemDefinition, parametersArray);
            }
        }

        if (this.selectedDicomEventData != null) {
            extractUploadSlotParameterFromEvent(parametersArray);
        }

        if (itemDefinition != null) {
            extractUploadSlotParameterFromItemDefinition(itemDefinition, parametersArray);
        }

        if (this.selectedEntity != null) {
            extractUploadSlotParameterFromSelectedEntity(parametersArray);
        }

        return uploaderService.getUploaderUrl(parametersArray);
    }

    private void extractUploadSlotParameterFromSelectedEntity(List<String> parametersArray) {

        if (this.selectedEntity.getStudySubjectId() != null) {
            parametersArray.add(UPLOADER_URL_SUBJECT_IDENTIFIER + "=" + this.selectedEntity.getStudySubjectId());
        }

        if (this.selectedEntity.getPid() != null) {
            parametersArray.add(UPLOADER_URL_PATIENT_IDENTIFIER + "=" + this.selectedEntity.getPid());
        }
        if (this.selectedEntity.getSubjectKey() != null) {
            parametersArray.add(UPLOADER_URL_SUBJECT_KEY + "=" + this.selectedEntity.getSubjectKey());
        }

        if (this.selectedEntity.getDateOfBirth() != null) {
            parametersArray.add(UPLOADER_URL_DATE_OF_BIRTH + "=" + this.selectedEntity.getDateOfBirth());
        }
        if (this.selectedEntity.getYearOfBirth() > 0) {
            parametersArray.add(UPLOADER_URL_YEAR_OF_BIRTH + "=" + String.valueOf(this.selectedEntity.getYearOfBirth()));
        }
        if (this.selectedEntity.getSex() != null) {
            parametersArray.add(UPLOADER_URL_GENDER + "=" + this.selectedEntity.getSex());
        }
    }

    private void extractUploadSlotParameterFromItemDefinition(ItemDefinition itemDefinition, List<String> parametersArray) {
        if (itemDefinition.getFormOids() != null) {
            parametersArray.add(UPLOADER_URL_FORM_IDENTIFIER + "=" + itemDefinition.getFormOids());
        }

        if (itemDefinition.getItemGroupDefinition() != null) {
            if (!itemDefinition.getItemGroupDefinition().getOid().isEmpty()) {
                parametersArray.add(UPLOADER_URL_ITEM_GROUP_IDENTIFIER + "=" + itemDefinition.getItemGroupDefinition().getOid());
            }
            parametersArray.add(UPLOADER_URL_ITEM_GROUP_REPEATKEY + "=" + "1");
        }

        if (itemDefinition.getLabel() != null) {
            parametersArray.add(UPLOADER_URL_ITEM_LABEL + "=" + itemDefinition.getLabel());
        }

        // dicomStudyInstanceItemOid
        if (itemDefinition.getOid() != null) {
            parametersArray.add(UPLOADER_URL_STUDY_INSTANCE_ITEM_OID + "=" + itemDefinition.getOid());
        }

        if (itemDefinition.getDescription() != null) {
            parametersArray.add(UPLOADER_URL_ITEM_DESCRIPTION + "=" + itemDefinition.getDescription());
        }
    }

    private void extractUploadSlotParameterFromEvent(List<String> parametersArray) {
        if (this.selectedDicomEventData.getStudyEventOid() != null) {
            parametersArray.add(UPLOADER_URL_EVENT_IDENTIFIER + "=" + this.selectedDicomEventData.getStudyEventOid());
        }
        if (this.selectedDicomEventData.getStudyEventRepeatKey() != null) {
            parametersArray.add(UPLOADER_URL_EVENT_REPEATKEY + "=" + this.selectedDicomEventData.getStudyEventRepeatKey());
        }
        if (this.selectedDicomEventData.getStartDate() != null) {
            parametersArray.add(UPLOADER_URL_EVENT_START_DATE + "=" + this.selectedDicomEventData.getStartDate());
        }
        if (this.selectedDicomEventData.getEndDate() != null) {
            parametersArray.add(UPLOADER_URL_EVENT_END_DATE + "=" + this.selectedDicomEventData.getEndDate());
        }
        if (this.selectedDicomEventData.getEventDefinition() != null) {
            parametersArray.add(UPLOADER_URL_EVENT_NAME + "=" + this.selectedDicomEventData.getEventDefinition().getName());
            parametersArray.add(UPLOADER_URL_EVENT_DESCRIPTION + "=" + this.selectedDicomEventData.getEventDefinition().getDescription());
        }
    }

    private void extractUploadSlotParametersFromRpbStudy(ItemDefinition itemDefinition, List<String> parametersArray) {
        if (!this.rpbStudy.getEdcStudy().extractStudyIdentifier().isEmpty()) {
            parametersArray.add(UPLOADER_URL_STUDY_IDENTIFIER + "=" + this.rpbStudy.getEdcStudy().extractStudyIdentifier());
        }
        if (!this.rpbStudy.getEdcStudy().extractStudySiteIdentifier().isEmpty()) {
            parametersArray.add(UPLOADER_URL_SITE_IDENTIFIER + "=" + this.rpbStudy.getEdcStudy().extractStudySiteIdentifier());
        }

        if (this.rpbStudy.getTagValue("EDC-code") != null) {
            parametersArray.add(UPLOADER_URL_STUDY_EDC_CODE + "=" + this.rpbStudy.getTagValue("EDC-code"));
        }

        if (this.rpbStudy.getEdcStudy().getOid() != null) {
            parametersArray.add(UPLOADER_URL_STUDY_OID + "=" + this.rpbStudy.getEdcStudy().getOid());
        }

        CrfFieldAnnotation exampleCrfFieldAnnotation = new CrfFieldAnnotation();
        exampleCrfFieldAnnotation.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());
        exampleCrfFieldAnnotation.setFormOid(itemDefinition.getItemGroupDefinition().getFormDefinition().getOid());
        exampleCrfFieldAnnotation.setGroupOid(itemDefinition.getItemGroupDefinition().getOid());

        CrfFieldAnnotation patientIdField = this.rpbStudy.findAnnotation("DICOM_PATIENT_ID", exampleCrfFieldAnnotation);

        String dicomPatientIdItemOid = patientIdField.getCrfItemOid();
        parametersArray.add(UPLOADER_URL_PATIENT_IDENTIFIER_ITEM_OID + "=" + dicomPatientIdItemOid);
    }

    private List<DicomStudy> getFilteredDicomStudiesByEdcCode(String edcCodePrefix, List<DicomStudy> dicomStudies) {
        List<DicomStudy> filteredDicomStudies = new ArrayList<>();

        for (DicomStudy study : dicomStudies) {
            if (study.getStudyDescription().startsWith(edcCodePrefix)) {
                filteredDicomStudies.add(study);
            }
        }
        return filteredDicomStudies;
    }

    /**
     * Reset properties that are set during the dialog dlgAssignDicomStudy
     */
    public void clearDlgAssignDicomStudy() {
        this.uploadSlotItemDefinition = null;
        this.associatedDicomStudy = null;
    }

    /**
     * Searches for CRF annotations of the selected study that can be assigned to DicomStudies in order to create an
     * association between a DicomStudy stored on the PACS system and a CRF field of an event in OpenClinica.
     *
     * @return List<ItemDefinition> List of Itemdefinition that can be used as ItemData in Odm to update the
     * dataset in Openclinica
     */
    public List<ItemDefinition> loadUploadSlots() {
        List<ItemDefinition> results = new ArrayList<>();

        if (rpbStudy != null && this.selectedDicomEventData != null) {
            results = this.rpbStudy.findAnnotatedItemDefinitionsForEventDef(
                    "DICOM_STUDY_INSTANCE_UID",
                    this.selectedDicomEventData.getEventDefinition()
            );
        }
        return results;
    }

    /**
     * Searches within the list of DicomStudies (this.dicomStudyList) for a study that are tagged with the corresponding
     * ItemOid
     *
     * @param oid ItemOid of the CRF Item
     * @return DicomStudy DicomStudy with corresponding ItemOid
     */
    public DicomStudy findAssociatedDicomStudies(String oid) {
        if (oid != null && !oid.isEmpty() && this.dicomStudyList != null) {
            for (DicomStudy study : this.dicomStudyList) {
                if (study.getCrfItemDefinition().getOid().equalsIgnoreCase(oid)) {
                    return study;
                }
            }
        }
        return null;
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
        queryStrings += "&formOID=" + this.uploadSlotItemDefinition.getItemGroupDefinition().getFormDefinition().getOid();
        queryStrings += "&itemGroupOID=" + this.uploadSlotItemDefinition.getItemGroupDefinition().getOid();
        queryStrings += "&dicomStudyItemOID=" + this.uploadSlotItemDefinition.getOid();

        // Find corresponding PatientID annotation
        CrfFieldAnnotation example = new CrfFieldAnnotation();
        example.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());
        example.setFormOid(this.uploadSlotItemDefinition.getItemGroupDefinition().getFormDefinition().getOid());
        example.setGroupOid(this.uploadSlotItemDefinition.getItemGroupDefinition().getOid());

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
            queryStrings += "&birthDate=" + this.selectedEntity.getYearOfBirth().toString();
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
        this.uploadSlotItemDefinition = null;

//        FacesContext.getCurrentInstance().getExternalContext().redirect("/pacs/studyUpload.faces" + queryStrings);
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

    /**
     * Removes any EDC code prefix from the given String
     *
     * @param stringWithEdcCodePrefix String with EDC code prefix
     * @return String without EDC code prefix
     */
    public String removeEdcCodePrefix(String stringWithEdcCodePrefix) {
        return DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(stringWithEdcCodePrefix);
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
            } else {
                this.messageUtil.infoText("To access RPB DICOM patient feature the active study have to be be tagged with DICOM study tag.");
            }
        } catch (Exception err) {
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
     * FlowControl for AssignDicomStudy Wizard UI Element
     *
     * @param event FlowEvent that reflects a state change within the wizard triggered by the UI
     * @return String with the name of the next tab within the wizard
     */
    public String onFlowProcess(FlowEvent event) {
        if (event.getOldStep().equalsIgnoreCase("UploadSlotAssignment")) {
            // do not step further if no study is chosen
            if (this.associatedDicomStudy == null) {
                getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please choose a DicomStudy to be assigned!"));
                return event.getOldStep();
            }
            // assigning the same study is not necessary
            if (this.findAssociatedDicomStudies(this.uploadSlotItemDefinition.getOid()) != null) {
                DicomStudy alreadyAssociatedStudy = this.findAssociatedDicomStudies(this.uploadSlotItemDefinition.getOid());
                if (this.associatedDicomStudy.getStudyInstanceUID().equalsIgnoreCase(alreadyAssociatedStudy.getStudyInstanceUID())) {
                    getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "This DicomStudy data is already assigned to the DICOM item."));
                    return event.getOldStep();
                }
            }
        }
        return event.getNewStep();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     *
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
     *
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

            ClinicalData cd = studySubjectOdm.findUniqueClinicalDataOrNone();
            de.dktk.dd.rpb.core.domain.edc.Study studySite = studySubjectOdm.findUniqueStudyOrNone(cd.getStudyOid());
            siteIdentifier = studySite.extractPartnerSiteIdentifier();
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

    /**
     * Creates an Odm object that updates OpenClinica with CRF annotation that creates an association between
     * the study (this.associatedDicomStudy) and the upload slot item (this.uploadSlotItemDefinition).
     */
    public void assignSeriesToUploadSlot() {

        try {
            boolean isUpdate = false;
            String previouslyAssociatedStudyInstanceUidValueString = "";
            if (this.uploadSlotItemDefinition != null) {
                DicomStudy alreadyAssociatedStudy = this.findAssociatedDicomStudies(this.uploadSlotItemDefinition.getOid());
                if (alreadyAssociatedStudy != null) {
                    isUpdate = true;
                    previouslyAssociatedStudyInstanceUidValueString = " from [" + alreadyAssociatedStudy.getStudyInstanceUID() + "] to ";
                }
            }

            CrfFieldAnnotation patientIdField = getPatientIdFieldCrfFieldAnnotation();
            Odm newOdmData = createOdm(patientIdField);
            this.studyIntegrationFacade.importData(newOdmData, 1);

            this.writeAuditLogForAssigningDicomStudy(isUpdate, previouslyAssociatedStudyInstanceUidValueString, patientIdField, newOdmData);
            messageUtil.infoText(this.associatedDicomStudy.getStudyDescription() + " associated to " + this.uploadSlotItemDefinition.getDescription() + ".");

            this.reloadDataFromEdcSystem();
            this.clearDlgAssignDicomStudy();

        } catch (Exception err) {
            log.error(err.getMessage(), err);
            String studyData = this.associatedDicomStudy.toString();
            String uploadSlot = this.uploadSlotItemDefinition.toString();
            String message = "There was problem associating the study " + studyData + " to " + uploadSlot + ".";
            messageUtil.errorText(message);
            log.debug(message);
        }
    }

    private void writeAuditLogForAssigningDicomStudy(boolean isUpdate, String previouslyAssociatedStudyInstanceUid, CrfFieldAnnotation patientIdField, Odm newOdmData) {
        try {
            AuditEvent auditEvent = getEdcAuditEvent(isUpdate);
            final String auditValueOneAssignDicomStudy = "ItemData";

            ClinicalData clinicalData = newOdmData.getClinicalDataList().get(0);

            String studyOid = clinicalData.getStudyOid();
            String subjectKey = clinicalData.getStudySubjects().get(0).getSubjectKey();
            String studyEventOid = clinicalData.getStudySubjects().get(0).getStudyEventDataList().get(0).getStudyEventOid();
            String studyEventRepeatKey = clinicalData.getStudySubjects().get(0).getStudyEventDataList().get(0).getStudyEventRepeatKey();
            String formOid = clinicalData.getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getFormOid();

            ItemGroupData itemGroupData = clinicalData.getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0);
            String itemGroupOid = itemGroupData.getItemGroupOid();

            for (ItemData itemData : itemGroupData.getItemDataList()) {
                String itemOid = itemData.getItemOid();
                String itemValue = itemData.getValue();

                if (!isUpdate) {
                    String auditValueTwoAssignDicomStudy = getAuditValueTwoAssignDicomStudy(studyOid, subjectKey, studyEventOid, studyEventRepeatKey, formOid, itemGroupOid, itemOid);
                    String auditValueThreeAssignDicomStudy = itemValue;
                    this.auditLogService.event(auditEvent, auditValueOneAssignDicomStudy, auditValueTwoAssignDicomStudy, auditValueThreeAssignDicomStudy);
                } else {
                    if (!(itemOid.equalsIgnoreCase(patientIdField.getCrfItemOid()))) {
                        String auditValueTwoAssignDicomStudy = getAuditValueTwoAssignDicomStudy(studyOid, subjectKey, studyEventOid, studyEventRepeatKey, formOid, itemGroupOid, itemOid);
                        String auditValueThreeAssignDicomStudy = previouslyAssociatedStudyInstanceUid + "[" + itemValue + "]";
                        this.auditLogService.event(auditEvent, auditValueOneAssignDicomStudy, auditValueTwoAssignDicomStudy, auditValueThreeAssignDicomStudy);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Writing audit log entries for assigning Dicom studies to EDC events failed.", e);
        }
    }

    private String getAuditValueTwoAssignDicomStudy(String studyOid, String subjectKey, String studyEventOid, String studyEventRepeatKey, String formOid, String itemGroupOid, String itemOid) {
        return studyOid + "/" + subjectKey + "/" + studyEventOid + "[" + studyEventRepeatKey + "]" + "/" + formOid + "/" + itemGroupOid + "/" + itemOid;
    }

    private AuditEvent getEdcAuditEvent(boolean isUpdate) {
        AuditEvent auditEvent = AuditEvent.EDCDataCreation;
        if (isUpdate) {
            auditEvent = AuditEvent.EDCDataModification;
        }
        return auditEvent;
    }

    private CrfFieldAnnotation getPatientIdFieldCrfFieldAnnotation() {
        // Find corresponding PatientID annotation
        CrfFieldAnnotation exampleCrfFieldAnnotation = new CrfFieldAnnotation();
        exampleCrfFieldAnnotation.setEventDefinitionOid(this.selectedDicomEventData.getStudyEventOid());
        exampleCrfFieldAnnotation.setFormOid(this.uploadSlotItemDefinition.getItemGroupDefinition().getFormDefinition().getOid());
        exampleCrfFieldAnnotation.setGroupOid(this.uploadSlotItemDefinition.getItemGroupDefinition().getOid());

        return this.rpbStudy.findAnnotation("DICOM_PATIENT_ID", exampleCrfFieldAnnotation);
    }

    private Odm createOdm(CrfFieldAnnotation patientIdField) throws MissingPropertyException {
        String dicomStudyInstanceItemOid = uploadSlotItemDefinition.getOid();
        String dicomStudyInstanceItemValue = this.associatedDicomStudy.getStudyInstanceUID();

        String dicomPatienIdItemOid = patientIdField.getCrfItemOid();
        String dicomPatienIdItemValue = this.selectedEntity.getPid();

        String itemGroupOid = uploadSlotItemDefinition.getItemGroupDefinition().getOid();

        String formOid = uploadSlotItemDefinition.getItemGroupDefinition().getFormDefinition().getOid();

        String studyEventOid = this.selectedDicomEventData.getStudyEventOid();
        String studyEventRepeatKey = this.selectedDicomEventData.getStudyEventRepeatKey();

        String subjectKey = this.selectedEntity.getSubjectKey();

        String studyOid = this.selectedEntity.getStudy().getOid();

        OdmBuilderDirector odmBuilderDirector = new OdmBuilderDirector(OdmBuilder.getInstance());
        return odmBuilderDirector.buildUpdateCrfAnnotationOdm(dicomStudyInstanceItemOid,
                dicomStudyInstanceItemValue,
                dicomPatienIdItemOid,
                dicomPatienIdItemValue,
                itemGroupOid,
                formOid,
                studyEventOid,
                studyEventRepeatKey,
                subjectKey,
                studyOid);
    }

    /**
     * reload and update data on all selected objects from EDC (OpenClinica)
     */
    public void reloadDataFromEdcSystem() {
        this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();
//        this.entityList = this.studyIntegrationFacade.loadStudySubjects();

        String studyOid = this.mainBean.getActiveStudy().getOcoid();

        if (this.selectedEntity != null && this.selectedEntity.getStudySubjectId() != null) {
            String studySubjectId = this.selectedEntity.getStudySubjectId();

            Odm selectedStudySubjectOdm = getOdmFromEdc(studySubjectId, studyOid);

            this.updateSelectedEntityWithEdcData(studySubjectId, studyOid, selectedStudySubjectOdm);
            this.updateSelectedDicomEventData();
            this.loadDicomStudies();
        }
    }

    /**
     * Updates selectedDicomEventData (EventData) object with data from selectedEntity (StudySubject)
     */
    private void updateSelectedDicomEventData() {
        if (this.selectedDicomEventData != null && this.selectedDicomEventData.getStudyEventOid() != null) {
            String studyEventOid = this.selectedDicomEventData.getStudyEventOid();
            List<EventData> eventDataList = this.selectedEntity.getEventOccurrencesForEvenDefs(dicomEventDefinitions);

            this.selectedDicomEventData = getFirstMatchingEventDataByStudyEventOid(studyEventOid, eventDataList);
        }
    }

    /**
     * Return the first matching event with the same StudyEventOid
     *
     * @param studyEventOid String identifier for the study event on EDC Odm
     * @param eventDataList List of Event Data
     * @return EventData object
     */
    private EventData getFirstMatchingEventDataByStudyEventOid(String studyEventOid, List<EventData> eventDataList) {
        for (EventData eventData : eventDataList) {
            if (eventData.getStudyEventOid().equalsIgnoreCase(studyEventOid)) {
                return eventData;
            }
        }
        return null;
    }

    /**
     * Update the StudySubject object (selectedEntity) with information from EDC and link ODM information
     *
     * @param studySubjectId          String StudySubjectId to find object in EDC ODM object (selectedStudySubjectOdm)
     * @param studyOid                String OID of the study in OpenClinica to find Study object in EDC ODM object (selectedStudySubjectOdm)
     * @param selectedStudySubjectOdm ODM object that consists of EDC data for the specific subject and study
     */
    private void updateSelectedEntityWithEdcData(String studySubjectId, String studyOid, Odm selectedStudySubjectOdm) {
        // Replace selected SOAP subject with REST subject that has clinical data
        this.selectedEntity = selectedStudySubjectOdm.findUniqueStudySubjectOrNone(studySubjectId);

        // Add reference to study
        this.selectedEntity.setStudy(selectedStudySubjectOdm.findUniqueStudyOrNone(studyOid));

        // Link clinical data with definitions from ODM
        this.selectedEntity.linkOdmDefinitions(selectedStudySubjectOdm);
    }

    /**
     * The URL parameter "pid" allows to pre-select a subject and activate the next tab in the UI.
     */
    private void setFirstMatchingStudySubjectAsSelectedEntity() {

        if (this.pid != null) {
            for (StudySubject subject : this.entityList) {
                if (subject.getPid().equals(this.pid) && !this.pid.isEmpty()) {
                    this.selectedEntity = subject;
                    this.loadSelectedSubjectDetails();
                    this.tab.setActiveIndex(1);
                    return;
                }
            }
        }

    }

    /**
     * The URL parameters "event" and "eventrepeatkey" allow to pre-select a event occurrence and activate the next tab
     * in the UI. Precondition is the pre-selection of the subject via "pid".
     */
    private void setFirstMatchingEventOccurrenceAsSelectedDicomEvent() {

        if (this.eventId != null && this.eventRepeatKey != null && selectedEntity != null) {
            int repeatKey = Integer.parseInt(this.eventRepeatKey);
            this.selectedDicomEventData = this.selectedEntity.getEventOccurrenceForEventDef(eventId, repeatKey);
            this.loadDicomStudies();
            this.tab.setActiveIndex(2);
        }

    }

    public boolean selectedSlotIsAlreadyAssociated() {
        if (this.selectedSlotItemDefinition != null) {
            return this.findAssociatedDicomStudies(this.selectedSlotItemDefinition.getOid()) != null;
        }
        return false;
    }


    /**
     * Redirects to the Uploader page if the selected slot is not assigned to another DICOM study
     */
    public void handleOpenConfirmDialog() {
        if (this.findAssociatedDicomStudies(this.selectedSlotItemDefinition.getOid()) == null) {
            try {
                redirectToUploader();
            } catch (Exception e) {
                String errorMessage = "There is a problem, redirecting to the Uploader";
                this.messageUtil.error(errorMessage, e);
                this.log.error(errorMessage, e);
            }
        }
    }

    private void redirectToUploader() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        URL redirectUrl = this.getRedirectUrlToUploader(this.selectedSlotItemDefinition);
        externalContext.redirect(redirectUrl.toString());
    }

    //endregion

}
