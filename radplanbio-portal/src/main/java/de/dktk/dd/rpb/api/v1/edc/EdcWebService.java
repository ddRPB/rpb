/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.api.v1.edc;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.builder.edc.OdmBuilder;
import de.dktk.dd.rpb.core.builder.edc.OdmBuilderDirector;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.pacs.DicomUploadSlot;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/v1/edc")
public class EdcWebService extends BaseService {
    // region Finals

    private static final Logger log = LoggerFactory.getLogger(EdcWebService.class);

    // endregion

    /**
     * Writes or updates the related item in the EDC system that will refer to the DICOM Study,
     * based on the UID.
     *
     * @param headers HttpHeaders header of the request
     * @param slot    DicomUploadSlot detail information for the EDC
     * @return Response
     */
    @POST
    @Path("/linkdicomstudy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkDicomStudy(@Context HttpHeaders headers,
                                   DicomUploadSlot slot) {

        List<String> errorMessagesList = new ArrayList<>();
        List<String> traceMessagesList = new ArrayList<>();
        Study rpbStudy; // RPB study

        // region user authentication account settings

        DefaultAccount userAccount = getUserAccountFromRequestHeader(headers, errorMessagesList, traceMessagesList);
        this.verifyUserAccount(userAccount, errorMessagesList, traceMessagesList);

        if (errorMessagesList.size() > 0) {
            JSONObject jsonObj = this.getJsonObject(errorMessagesList, traceMessagesList);
            return javax.ws.rs.core.Response.status(401).entity(jsonObj.toString()).build();
        }

        // endregion

        // region prepare - setup components

        this.auditLogService.setUsername(userAccount.getUsername());
        rpbStudy = this.loadRpbStudy(slot, errorMessagesList, traceMessagesList);
        this.createEdcConnections(userAccount, errorMessagesList, traceMessagesList);

        if (errorMessagesList.size() > 0) {
            JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);
            return javax.ws.rs.core.Response.status(500).entity(jsonObj.toString()).build();
        }

        // endregion

        // region load existing data from EDC

        Odm selectedStudySubjectOdm = getOdmSubjectFromEdc(slot, errorMessagesList, traceMessagesList);

        if (errorMessagesList.size() > 0) {
            JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);
            return javax.ws.rs.core.Response.status(500).entity(jsonObj.toString()).build();
        }

        // endregion

        // region get item value from existing ODM
        String itemValue = "";
        EventData odmEvent = extractSlotEventFromOdmIfExists(slot, selectedStudySubjectOdm, errorMessagesList, traceMessagesList);
        itemValue = getItemValueIfExists(slot, odmEvent, errorMessagesList, traceMessagesList);

        if (errorMessagesList.size() > 0) {
            JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);
            return javax.ws.rs.core.Response.status(500).entity(jsonObj.toString()).build();
        }

        // endregion

        // region compare values

        if (!itemValue.isEmpty() && itemValue.equals(slot.getDicomStudyInstanceItemValue())) {
            // item is already assigned
            traceMessagesList.add("Item is already assigned. Nothing to change.");
            JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);

            return javax.ws.rs.core.Response.status(200).entity(jsonObj.toString()).build();
        }

        // endregion

        // region register

        Odm newOdmData = createOdmWithDicomStudyItem(rpbStudy, slot, errorMessagesList, traceMessagesList);

        if (newOdmData == null) {
            JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);
            return javax.ws.rs.core.Response.status(500).entity(jsonObj.toString()).build();
        }

        try {
            this.studyIntegrationFacade.importData(newOdmData, 1);
        } catch (OCConnectorException e) {
            errorMessagesList.add("ODM export to EDC failed: " + e.getMessage());
        }

        // Audit the trigger of download of DICOM study
        Boolean isUpdated = itemValue.isEmpty() ? false : true;
        CrfFieldAnnotation exampleCrfFieldAnnotation = getCrfFieldAnnotationForSlotParameter(slot);
        this.writeAuditLogForAssigningDicomStudy(
                isUpdated,
                itemValue,
                exampleCrfFieldAnnotation,
                newOdmData,
                errorMessagesList,
                traceMessagesList
        );

        JSONObject jsonObj = getJsonObject(errorMessagesList, traceMessagesList);
        return javax.ws.rs.core.Response.status(200).entity(jsonObj.toString()).build();

        // endregion
    }

    private Odm getOdmSubjectFromEdc(DicomUploadSlot slot, List<String> errorMessagesList, List<String> traceMessagesList) {
        Odm selectedStudySubjectOdm = null;

        String queryOdmXmlPath = slot.getStudyOid() + "/" + slot.getSubjectId() + "/*/*";
        selectedStudySubjectOdm = this.engineOpenClinicaService.getStudyCasebookOdm(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        if (selectedStudySubjectOdm != null) {
            selectedStudySubjectOdm.updateHierarchy();
        } else {
            errorMessagesList.add("Subject data not available in EDC. StudyOid: " + slot.getStudyOid() + " Subject: " + slot.getSubjectId());
        }
        return selectedStudySubjectOdm;
    }

    private void verifyUserAccount(DefaultAccount userAccount, List<String> errorMessagesList, List<String> traceMessagesList) {
        if (!userAccount.hasOpenClinicaAccount() ||
                !userAccount.getPartnerSite().getEdc().getIsEnabled()) {
            errorMessagesList.add("This account does not have EDC module activated.");
        } else {
            traceMessagesList.add("User account verified.");
        }

        if (!userAccount.hasRoleName("ROLE_PACS_UPLOAD")) {
            errorMessagesList.add("This account does not have permissions for that task.");
        } else {
            traceMessagesList.add("User has permissions.");
        }
    }

    private void createEdcConnections(DefaultAccount userAccount, List<String> errorMessagesList, List<String> traceMessagesList) {
        this.initializeOpenClinicaService(userAccount);
        if (this.openClinicaService == null) {
            errorMessagesList.add("Setup EDC connection failed.");
        } else {
            traceMessagesList.add("EDC setup successful.");
        }

        this.initEngineEdcConnection();
        if (this.engineOpenClinicaService == null) {
            errorMessagesList.add("Setup EDC for service user failed.");
        } else {
            traceMessagesList.add("EDC service user setup successful.");
        }
    }

    private JSONObject getJsonObject(List<String> errorMessagesList, List<String> traceMessagesList) {
        String errorMessages = String.join("; ", errorMessagesList);
        String traceMessages = String.join("; ", traceMessagesList);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("errors", errorMessages);
            jsonObj.put("messages", traceMessages);
        } catch (JSONException e) {
            log.error("There is a problem during generation of the JSON response.", e);
        } finally {
            return jsonObj;
        }
    }

    private DefaultAccount getUserAccountFromRequestHeader(HttpHeaders headers, List<String> errorMessagesList, List<String> traceMessagesList) {
        // ApiKey for authentication
        String apiKey = null;

        if (headers.getRequestHeader("X-Api-Key").size() > 0) {
            apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        }

        if (apiKey == null || apiKey.equals("")) {
            String message = "Missing X-Api-Key, unauthorised";
            log.info(message);
            errorMessagesList.add(message);
            return null;
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            String message = "No apiKey corresponding user, unauthorised";
            log.info(message);
            errorMessagesList.add(message);
            return null;
        } else {
            traceMessagesList.add("User account setup successful.");
        }
        return userAccount;
    }

    private String getItemValueIfExists(DicomUploadSlot slot, EventData odmEvent, List<String> errorMessagesList, List<String> traceMessagesList) {
        String item = "";
        try {
            List<FormData> formDataList = odmEvent.getFormDataList();
            if (formDataList != null) {
                for (FormData formData : formDataList) {
                    if (formData.getFormOid().equals(slot.getFormOid())) {
                        List<ItemGroupData> itemGroupDataList = formData.getItemGroupDataList();

                        if (itemGroupDataList != null) {
                            for (ItemGroupData itemGroupData : itemGroupDataList) {
                                if (itemGroupData.getItemGroupOid().equals(slot.getItemGroupOid())) {
                                    List<ItemData> itemDataList = itemGroupData.getItemDataList();

                                    if (itemDataList != null) {
                                        for (ItemData itemData : itemDataList) {
                                            if (itemData.getItemOid().equals(slot.getDicomStudyInstanceItemOid())) {
                                                item = itemData.getValue();
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            String message = "There is a problem getting the correct event data from the ODM.";
            errorMessagesList.add(message + ": " + e.getMessage());
        }

        traceMessagesList.add("Existing item value: " + item);
        return item;
    }

    private EventData extractSlotEventFromOdmIfExists(DicomUploadSlot slot, Odm selectedStudySubjectOdm, List<String> errorMessagesList, List<String> traceMessagesList) {
        List<ClinicalData> clinicalDataList = selectedStudySubjectOdm.getClinicalDataList();

        if (clinicalDataList != null) {
            if (clinicalDataList.get(0) != null && clinicalDataList.size() == 1) {
                List<StudySubject> studySubjectList = clinicalDataList.get(0).getStudySubjects();

                if (studySubjectList != null) {
                    if (studySubjectList.get(0) != null && studySubjectList.size() == 1) {
                        List<EventData> eventDataList = studySubjectList.get(0).getStudyEventDataList();

                        if (eventDataList != null) {
                            for (EventData event : eventDataList) {
                                if (event.getStudyEventOid().equals(slot.getStudyEventOid())) {
                                    if (event.getStudyEventRepeatKey().equals(slot.getStudyEventRepeatKey())) {
                                        traceMessagesList.add("Found event.");
                                        return event;
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }
        return null;
    }

    private CrfFieldAnnotation getCrfFieldAnnotationForSlotParameter(DicomUploadSlot slot) {
        CrfFieldAnnotation exampleCrfFieldAnnotation = new CrfFieldAnnotation();
        exampleCrfFieldAnnotation.setEventDefinitionOid(slot.getStudyEventOid());
        exampleCrfFieldAnnotation.setFormOid(slot.getFormOid());
        exampleCrfFieldAnnotation.setGroupOid(slot.getItemGroupOid());
        exampleCrfFieldAnnotation.setCrfItemOid(slot.getDicomPatientIdItemOid());
        return exampleCrfFieldAnnotation;
    }

    private Odm createOdmWithDicomStudyItem(Study rpbStudy, DicomUploadSlot slot, List<String> errorMessagesList, List<String> traceMessagesList) {
        Odm result = null;
        try {
            CrfFieldAnnotation exampleCrfFieldAnnotation = getCrfFieldAnnotationForSlotParameter(slot);
            CrfFieldAnnotation patientIdAnnotations = rpbStudy.findAnnotation("DICOM_PATIENT_ID", exampleCrfFieldAnnotation);
            result = createOdm(patientIdAnnotations, slot);
        } catch (MissingPropertyException e) {
            errorMessagesList.add("ODM creation failed. " + e.getMessage());
        }

        traceMessagesList.add("ODM for update EDC created.");
        return result;
    }

    private Study loadRpbStudy(
            DicomUploadSlot slot,
            List<String> errorMessagesList,
            List<String> traceMessagesList
    ) {
        Study rpbStudy = this.studyIntegrationFacade.loadStudyByIdentifier(slot.getStudyIdentifier());
        if (rpbStudy == null) {
            errorMessagesList.add("RPB Study is null. StudyIdentifier: " + slot.getStudyIdentifier());
        } else {
            traceMessagesList.add("RPB Study loaded.");
        }
        return rpbStudy;
    }

    private Odm createOdm(CrfFieldAnnotation patientIdField, DicomUploadSlot slot) throws MissingPropertyException {
        String dicomStudyInstanceItemOid = slot.getDicomStudyInstanceItemOid();
        String dicomStudyInstanceItemValue = slot.getDicomStudyInstanceItemValue();

        String dicomPatientIdItemOid = patientIdField.getCrfItemOid();
        String dicomPatientIdItemValue = slot.getPatientId();

        String itemGroupOid = slot.getItemGroupOid();

        String formOid = slot.getFormOid();

        String studyEventOid = slot.getStudyEventOid();
        String studyEventRepeatKey = slot.getStudyEventRepeatKey();

        String subjectKey = slot.getSubjectKey();

        String studyOid = slot.getStudyOid();

        OdmBuilderDirector odmBuilderDirector = new OdmBuilderDirector(OdmBuilder.getInstance());
        return odmBuilderDirector.buildUpdateCrfAnnotationOdm(dicomStudyInstanceItemOid,
                dicomStudyInstanceItemValue,
                dicomPatientIdItemOid,
                dicomPatientIdItemValue,
                itemGroupOid,
                formOid,
                studyEventOid,
                studyEventRepeatKey,
                subjectKey,
                studyOid);
    }

    private AuditEvent getEdcAuditEvent(boolean isUpdate) {
        AuditEvent auditEvent = AuditEvent.EDCDataCreation;
        if (isUpdate) {
            auditEvent = AuditEvent.EDCDataModification;
        }
        return auditEvent;
    }

    private void writeAuditLogForAssigningDicomStudy(
            boolean isUpdate,
            String previouslyAssociatedStudyInstanceUid,
            CrfFieldAnnotation patientIdField,
            Odm newOdmData,
            List<String> errorMessagesList,
            List<String> traceMessagesList
    ) {
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
        traceMessagesList.add("Audit log item created.");
    }

    private String getAuditValueTwoAssignDicomStudy(String studyOid, String subjectKey, String studyEventOid, String studyEventRepeatKey, String formOid, String itemGroupOid, String itemOid) {
        return (studyOid + "/" + subjectKey + "/" + studyEventOid + "[" + studyEventRepeatKey + "]" + "/" + formOid + "/" + itemGroupOid + "/" + itemOid).trim();
    }
}
