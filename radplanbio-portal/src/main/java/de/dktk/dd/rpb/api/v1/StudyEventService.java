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

package de.dktk.dd.rpb.api.v1;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.AnnotationType;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.repository.admin.AnnotationTypeRepository;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.DataTransformationService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openclinica.ws.beans.StudyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling StudyEvents of specified StudySubject (studySubjectIdentifier) participating in provided study (studyIdentifier)
 */
@Component
@Path("/v1/studies/{studyIdentifier}/studysubjects/{studySubjectIdentifier}/studyevents")
public class StudyEventService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(StudyEventService.class);

    //endregion

    //region Members

    private IStudyRepository studyRepository;
    private AnnotationTypeRepository annotationTypeRepository;
    private DataTransformationService dataTransformationService;

    //endregion

    //region Constructors

    @Inject
    public StudyEventService(IStudyRepository studyRepository, AnnotationTypeRepository annotationTypeRepository, DataTransformationService dataTransformationService) {

        this.studyRepository = studyRepository;
        this.annotationTypeRepository = annotationTypeRepository;
        this.dataTransformationService = dataTransformationService;
    }

    //endregion

    //region Resource Methods

    //region POST

    /**
     * Schedule specified study event for specified subject and import data based on RPB annotations
     *
     * @param headers HTTP headers with X-Api-Key
     * @param body JSON body containing data details
     * @param studyIdentifier EDC study identifier (in which study)
     * @param studySubjectIdentifier Subject identifier (for which subject)
     * @return Response HTTP response with code
     */
    @POST
    @Consumes({"application/vnd.studyevent.v1+json"})
    public Response createStudyEvent(@Context HttpHeaders headers, String body,
                                     @PathParam("studyIdentifier") String studyIdentifier,
                                     @PathParam("studySubjectIdentifier") String studySubjectIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Clear OC password is necessary for authentication with RESTfull URLs
        String password = headers.getRequestHeader("X-Password").get(0);
        if (password == null || password.isEmpty()) {
            log.info("Missing X-Password, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // StudySubject enrollment
        IOpenClinicaService svcEdc = createEdcConnection(userAccount, password);

        // Which study do we work with
        Study study = null;
        for (StudyType ocStudy : svcEdc.listAllStudies()) {
            if (ocStudy.getIdentifier().equals(studyIdentifier)) {
                study = new Study(ocStudy);
                break;
            }
        }

        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;
        if (study == null) {
            log.info("POST: Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }
        else {
            rpbStudy = this.studyRepository.getByOcStudyIdentifier(study.getStudyIdentifier());
        }

        try {
            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    studySubjectIdentifier
            );

            // First check if the subject does already exists within a study
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {

                // Load full ODM resource for study subject from EDC
                String queryOdmXmlPath = study.getStudyOID() + "/" + studySubjectIdentifier + "/*/*";
                Odm subjectOdm = svcEdc.getStudyCasebookOdm(
                        OpenClinicaService.CasebookFormat.XML,
                        OpenClinicaService.CasebookMethod.VIEW,
                        queryOdmXmlPath
                );

                // What StudyEvent are we processing
                JSONObject newJsonBody = new JSONObject(body);
                JSONObject jsonStudyEventData = newJsonBody.getJSONObject("StudyEventData");
                String studyEventIdentifier = jsonStudyEventData.getString("StudyEventOID");

                String studyEventStartDate = jsonStudyEventData.optString("StartDate", "");

                String studyEventStartDateTime = "";
                if (!studyEventStartDate.isEmpty()) {
                    studyEventStartDateTime = studyEventStartDate;
                }

                String studyEventStartTime = jsonStudyEventData.optString("StartTime", "");
                // OC format for event time is more precise
                if (!studyEventStartTime.isEmpty()) {
                    studyEventStartDateTime += " " + studyEventStartTime + ":00.0";
                }

                CrfFieldAnnotation eventAnnotationExample = new CrfFieldAnnotation();
                eventAnnotationExample.setEventDefinitionOid(studyEventIdentifier);

                // Annotations specifies items in CRFs that are the content of the event (event equality check)
                List<CrfFieldAnnotation> eventItemsAnnotations = rpbStudy.findAnnotations(
                    this.extractRpbAnnotationNames(jsonStudyEventData),
                    eventAnnotationExample
                );

                // Parse the newJsonBody to get the Event details (for checking is there already an event with same date or date time if time is also provided)
                EventData eventData = this.createEventData(
                        studyEventIdentifier,
                        studyEventStartDateTime,
                        eventItemsAnnotations,
                        jsonStudyEventData
                );

                // If this event data does not exists
                if (subjectOdm.findMatchingEventData(eventData) == null) {

                    // Load event definition from metadata
                    EventDefinition eventDefinition = subjectOdm.findUniqueEventDefinitionOrNone(studyEventIdentifier);

                    // Get next repeat key
                    int newEventRepeatKey = subjectOdm.calculateNextEventDataRepeatKey(eventData);

                    // Schedule new event if it is first occurrence or repeating event
                    if (newEventRepeatKey == 1 || eventDefinition.getIsRepeating()) {

                        eventData.setStudyEventRepeatKey(String.valueOf(newEventRepeatKey));

                        // Schedule
                        ScheduledEvent newEvent = new ScheduledEvent(eventData);
                        newEvent.setStartDate(studyEventStartDate);
                        newEvent.setStartTime(studyEventStartTime);
                        String scheduledRepeatKey = svcEdc.scheduleStudyEvent(querySubject, newEvent);
                        if (scheduledRepeatKey != null && !scheduledRepeatKey.isEmpty()) {
                            this.auditLogService.event(
                                    AuditEvent.EDCStudyEventSchedule,
                                    studyEventIdentifier + "["+ scheduledRepeatKey +"]",
                                    study.getStudyIdentifier(),
                                    studySubjectIdentifier
                            );
                        }

                        // Generate import ODM
                        Odm importEventOdm = this.createEventDataImportOdm(eventData, subjectOdm);

                        // Import
                        String odmString = this.dataTransformationService.transformOdmToString(importEventOdm);
                        svcEdc.importData(odmString);

                        return javax.ws.rs.core.Response.status(201).build();
                    }
                    // Otherwise response Conflict
                    else {
                        log.info("POST: EventData " + studyEventIdentifier + " [" + studyEventStartDateTime + "] cannot be scheduled because the event is defined as non repeating and first occurence already exists.");

                        return javax.ws.rs.core.Response.status(409).build();
                    }
                }
                // Matching event data was found based on provided items that require equality
                else {
                    // Match events but reset the equality check - we want to detect whether there are some item changes
                    eventData = this.createEventDataDiff(eventData, subjectOdm.findMatchingEventData(eventData));

                    // Import data
                    Odm importEventOdm = this.createEventDataImportOdm(eventData, subjectOdm);
                    String odmString = this.dataTransformationService.transformOdmToString(importEventOdm);
                    svcEdc.importData(odmString);

                    log.info("PUT: EventData " + studyEventIdentifier + " [" + studyEventStartDateTime + "] already exists and is overwritten.");

                    // Debug email, to be commented when this functionality works fine
                    this.emailService.sendMailToAdmin(
                            userAccount.getEmail(),
                            "EventData was updated",
                            "EventData " + studyEventIdentifier + " was updated for patient with HIS ID: " + studySubjectIdentifier
                    );

                    return javax.ws.rs.core.Response.status(201).build();
                }
            }
            else {
                log.info("POST: StudySubject " + studySubjectIdentifier + " does not exist");
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        catch (Exception err) {
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //region PUT

    @PUT
    @Consumes({"application/vnd.studyevent.v1+json"})
    public Response updateStudyEvent(@Context HttpHeaders headers, String body,
                                     @PathParam("studyIdentifier") String studyIdentifier,
                                     @PathParam("studySubjectIdentifier") String studySubjectIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Clear OC password is necessary for authentication with REST full URLs
        String password = headers.getRequestHeader("X-Password").get(0);
        if (password == null || password.isEmpty()) {
            log.info("Missing X-Password, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // StudySubject enrollment
        IOpenClinicaService svcEdc = createEdcConnection(userAccount, password);

        // Which study do we work with
        Study study = null;

        this.studyIntegrationFacade.init(svcEdc);
        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy = this.studyIntegrationFacade
                .loadStudyWithMetadataByIdentifier(studyIdentifier, studyIdentifier);

        if (rpbStudy != null && rpbStudy.getEdcStudy() != null) {
            study = new Study();
            study.setStudyOID(rpbStudy.getEdcStudy().getOid());
            study.setStudyIdentifier(rpbStudy.getEdcStudy().getGlobalVariables().getProtocolName());
        }

        if (study == null) {
            log.info("PUT: EventData -  Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        try {
            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    studySubjectIdentifier
            );

            // First check if the subject does already exists within a study
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {

                // Load full ODM resource for study subject from EDC
                String queryOdmXmlPath = study.getStudyOID() + "/" + studySubjectIdentifier + "/*/*";
                Odm subjectOdm = svcEdc.getStudyCasebookOdm(
                        OpenClinicaService.CasebookFormat.XML,
                        OpenClinicaService.CasebookMethod.VIEW,
                        queryOdmXmlPath
                );

                // 15 seconds - hardcoded for now
//                int retryTimeout = 15000;
//                long endTime = System.currentTimeMillis() + retryTimeout;
//                while (subjectOdm == null && System.currentTimeMillis() < endTime) {
//                    subjectOdm = svcEdc.getStudyCasebookOdm(
//                            OpenClinicaService.CasebookFormat.XML,
//                            OpenClinicaService.CasebookMethod.VIEW,
//                            queryOdmXmlPath // Url
//                    );
//                }

                // What StudyEvent are we processing
                JSONObject newJsonBody = new JSONObject(body);
                JSONObject jsonStudyEventData = newJsonBody.getJSONObject("StudyEventData");
                String studyEventIdentifier = jsonStudyEventData.getString("StudyEventOID");

                String studyEventStartDate = jsonStudyEventData.optString("StartDate", "");

                String studyEventStartDateTime = "";
                if (!studyEventStartDate.isEmpty()) {
                    studyEventStartDateTime = studyEventStartDate;
                }

                String studyEventStartTime = jsonStudyEventData.optString("StartTime", "");
                // OC format for event time is more precise
                if (!studyEventStartTime.isEmpty()) {
                    studyEventStartDateTime += " " + studyEventStartTime + ":00.0";
                }

                CrfFieldAnnotation eventAnnotationExample = new CrfFieldAnnotation();
                eventAnnotationExample.setEventDefinitionOid(studyEventIdentifier);

                // Annotations specifies items in CRFs that are the content of the event (event equality check)
                List<CrfFieldAnnotation> eventItemsAnnotations = rpbStudy.findAnnotations(
                        this.extractRpbAnnotationNames(jsonStudyEventData),
                        eventAnnotationExample
                );

                // Parse the newJsonBody to get the Event details (for checking is there already an event with same date or date time if time is also provided)
                EventData eventData = this.createEventData(
                        studyEventIdentifier,
                        studyEventStartDateTime,
                        eventItemsAnnotations,
                        jsonStudyEventData
                );

                // TODO: perform event data matching only if repeat key information is not provided
                // If this event data does not exists
                EventData existingEventData =  subjectOdm.findMatchingEventData(eventData);

                // Event data was not found based on provided items that require equality - try to create
                if (existingEventData == null) {

                    // Load event definition from metadata
                    EventDefinition eventDefinition = subjectOdm.findUniqueEventDefinitionOrNone(studyEventIdentifier);

                    // Get next repeat key
                    int newEventRepeatKey = subjectOdm.calculateNextEventDataRepeatKey(eventData);

                    // Schedule new event if it is first occurrence or repeating event
                    if (newEventRepeatKey == 1 || eventDefinition.getIsRepeating()) {

                        eventData.setStudyEventRepeatKey(String.valueOf(newEventRepeatKey));

                        // Schedule only when it does not exists as unscheduled
                        if (subjectOdm.findEventData(studyEventIdentifier, newEventRepeatKey) == null) {
                            ScheduledEvent newEvent = new ScheduledEvent(eventData);
                            newEvent.setStartDate(studyEventStartDate);
                            newEvent.setStartTime(studyEventStartTime);
                            String scheduledRepeatKey = svcEdc.scheduleStudyEvent(querySubject, newEvent);
                            if (scheduledRepeatKey != null && !scheduledRepeatKey.isEmpty()) {
                                // Audit Log
                                this.auditLogService.event(
                                    AuditEvent.EDCStudyEventSchedule,
                                    studyEventIdentifier + "["+ scheduledRepeatKey +"]",
                                     study.getStudyIdentifier(),
                                     studySubjectIdentifier
                                );
                            }
                        }

                        // Import data
                        Odm importEventOdm = this.createEventDataImportOdm(eventData, subjectOdm);
                        String odmString = this.dataTransformationService.transformOdmToString(importEventOdm);
                        svcEdc.importData(odmString);

                        // Log
                        String message = "EventData " + studyEventIdentifier;
                        message += eventData.getStudyEventRepeatKey() != null ?  " [" + eventData.getStudyEventRepeatKey() + "] " : " ";
                        message += "was created for patient with HIS ID: " + studySubjectIdentifier;
                        log.info("PUT: " + message);

                        // Audit Log
                        this.auditLogService.event(
                            AuditEvent.EDCDataCreation,
                            studyEventIdentifier + "["+ eventData.getStudyEventRepeatKey() +"]",
                            study.getStudyIdentifier(),
                            studySubjectIdentifier
                        );

                        // OK, created
                        return javax.ws.rs.core.Response.status(201).build();
                    }
                    // Otherwise response Conflict
                    else {
                        // Log
                        String message = "EventData " + studyEventIdentifier;
                        message += " cannot be scheduled because the event is defined as non repeating and first occurrence already exists.";
                        log.info("PUT: " + message);

                        // Conflict
                        return javax.ws.rs.core.Response.status(409).build();
                    }
                }
                // Matching event data was found based on provided items that require equality - try to update
                else {
                    // Match events but reset the equality check - we want to detect whether there are some item changes
                    eventData = this.createEventDataDiff(eventData, existingEventData);

                    // Only if eventData diff has some changed items for importing
                    if (eventData.getAllItemData().size() > 0) {
                        
                        // Import data
                        Odm importEventOdm = this.createEventDataImportOdm(eventData, subjectOdm);
                        String odmString = this.dataTransformationService.transformOdmToString(importEventOdm);
                        svcEdc.importData(odmString);

                        // Log
                        String message = "EventData " + studyEventIdentifier;
                        message += eventData.getStudyEventRepeatKey() != null ?  " [" + eventData.getStudyEventRepeatKey() + "] " : " ";
                        message += "was updated for patient with HIS ID: " + studySubjectIdentifier;
                        log.info("PUT: " + message);
                        
                        // Audit Log
                        this.auditLogService.event(
                                AuditEvent.EDCDataModification,
                                studyEventIdentifier + "["+ eventData.getStudyEventRepeatKey() +"]",
                                study.getStudyIdentifier(),
                                studySubjectIdentifier
                        );

                        // OK, updated
                        return javax.ws.rs.core.Response.status(200).build();
                    }
                    // All data matches no changes necessary
                    else {
                        // OK
                        return javax.ws.rs.core.Response.status(200).build();
                    }
                }
            }
            else {
                // Log
                log.info("PUT: EventData - StudySubject " + studySubjectIdentifier + " does not exist");

                // Not found
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        catch (Exception err) {                                              
            // Log
            log.info("EventData - exception");
            log.error(err.getMessage(), err);

            // Error
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //region DELETE

    @DELETE
    @Consumes({"application/vnd.studyevent.v1+json"})
    public Response removeStudyEvent(@Context HttpHeaders headers, String body,
                                     @PathParam("studyIdentifier") String studyIdentifier,
                                     @PathParam("studySubjectIdentifier") String studySubjectIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Clear OC password is necessary for authentication with REST full URLs
        String password = headers.getRequestHeader("X-Password").get(0);
        if (password == null || password.isEmpty()) {
            log.info("Missing X-Password, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // StudySubject enrollment
        IOpenClinicaService svcEdc = createEdcConnection(userAccount, password);

        // Which study do we work with
        Study study = null;
        for (StudyType ocStudy : svcEdc.listAllStudies()) {
            if (ocStudy.getIdentifier().equals(studyIdentifier)) {
                study = new Study(ocStudy);
                break;
            }
        }

        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;
        if (study == null) {
            log.info("DELETE: Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }
        else {
            rpbStudy = this.studyRepository.getByOcStudyIdentifier(study.getStudyIdentifier());
        }

        try {
            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    studySubjectIdentifier
            );

            // First check if the subject does already exists within a study
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {

                //TODO: check if the event exists

                // What StudyEvent are we processing
                JSONObject newJsonBody = new JSONObject(body);
                JSONObject jsonStudyEventData = newJsonBody.getJSONObject("StudyEventData");
                String studyEventIdentifier = jsonStudyEventData.getString("StudyEventOID");

                String studyEventStartDate = jsonStudyEventData.optString("StartDate", "");

                String studyEventStartDateTime = "";
                if (!studyEventStartDate.isEmpty()) {
                    studyEventStartDateTime = studyEventStartDate;
                }

                String studyEventStartTime = jsonStudyEventData.optString("StartTime", "");
                // OC format for event time is more precise
                if (!studyEventStartTime.isEmpty()) {
                    studyEventStartDateTime += " " + studyEventStartTime + ":00.0";
                }

                CrfFieldAnnotation eventExample = new CrfFieldAnnotation();
                eventExample.setEventDefinitionOid(studyEventIdentifier);

                // Annotations specifies items in CRFs that are the content of the event (event equality check)
                List<CrfFieldAnnotation> eventItemsAnnotations = rpbStudy.findAnnotations(
                        this.extractRpbAnnotationNames(jsonStudyEventData),
                        eventExample
                );

                // Parse the newJsonBody to get the Event details (for checking is there already an event with same date or date time if time is also provided)
                EventData eventData = this.createEventData(studyEventIdentifier, studyEventStartDateTime, eventItemsAnnotations, jsonStudyEventData);

                //String odmString = this.dataTransformationService.transformOdmToString(importEventOdm);

                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        "StudyEvent need to be removed manually",
                        studyEventIdentifier + " [" + studyEventStartDate  + "]"
                );

                // Ok
                return javax.ws.rs.core.Response.status(200).build();
            } else {
                log.info("DELETE: StudySubject " + studySubjectIdentifier + " does not exist");
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        catch (Exception err) {
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //endregion

    //region Private Methods
    
    private List<String> extractRpbAnnotationNames(JSONObject jsonBody) throws JSONException {

        List<String> result = new ArrayList<>();

        JSONArray jsonItemDataList = jsonBody.getJSONArray("ItemData");
        for (int i = 0; i < jsonItemDataList.length(); i++) {

            JSONObject jsonItemData = jsonItemDataList.getJSONObject(i);

            AnnotationType annotationType = annotationTypeRepository.getByName(jsonItemData.getString("RPB:AnnotationType"));
            if (annotationType != null) {
                result.add(annotationType.getName());
            }
        }

        return result;
    }

    private EventData createEventData(String studyEventIdentifier, String studyEventStartDateTime,  List<CrfFieldAnnotation> eventCrfAnnotations, JSONObject jsonBody) throws JSONException {

        EventData resultEvent = new EventData(studyEventIdentifier);
        if (!studyEventStartDateTime.isEmpty()) {
            resultEvent.setStartDate(studyEventStartDateTime);
        }

        // Create data structures according to annotations
        for (CrfFieldAnnotation crfFieldAnnotation : eventCrfAnnotations) {

            // Add form to the event if it does not exists
            if (!resultEvent.containsFormData(crfFieldAnnotation.getFormOid())) {

                FormData formData = new FormData(crfFieldAnnotation.getFormOid());
                resultEvent.addFormData(formData);
            }

            //jsonBody.getBoolean("RPB:CheckEqualityDateTime");
            
            // Add item data group to the form if it does not exists
            FormData formData = resultEvent.findFormData(crfFieldAnnotation.getFormOid());
            if (formData != null && !formData.containsItemGroupData(crfFieldAnnotation.getGroupOid())) {

                ItemGroupData itemGroupData = new ItemGroupData(crfFieldAnnotation.getGroupOid());
                formData.addItemGroupData(itemGroupData);
            }

            // Add item data to the group if it does not exists
            ItemGroupData itemGroupData = formData.findItemGroupData(crfFieldAnnotation.getGroupOid());
            if (itemGroupData != null && !itemGroupData.containsItemData(crfFieldAnnotation.getCrfItemOid())) {

                // Get ItemData value
                JSONArray jsonItemDataList = jsonBody.getJSONArray("ItemData");
                for (int i = 0; i < jsonItemDataList.length(); i++) {

                    JSONObject jsonItemData = jsonItemDataList.getJSONObject(i);
                    String annotationName = jsonItemData.getString("RPB:AnnotationType");

                    if (crfFieldAnnotation.getAnnotationType().getName().equals(annotationName)) {

                        // Item data
                        ItemData itemData = new ItemData(crfFieldAnnotation.getCrfItemOid());
                        itemData.setValue(jsonItemData.optString("Value", ""));
                        itemData.setCheckValueEquality(jsonItemData.optBoolean("RPB:CheckEquality", Boolean.FALSE));

                        itemGroupData.addItemData(itemData);

                        break;
                    }
                }
            }
        }

        return resultEvent;
    }

    private EventData createEventDataDiff(EventData newEventData, EventData matchingEventData) {
        newEventData.setStudyEventRepeatKey(matchingEventData.getStudyEventRepeatKey());

        List<ItemData> itemsToExcludeFromImport = new ArrayList<>();

        for (FormData formData : newEventData.getFormDataList()) {
            for (ItemGroupData itemGroupData : formData.getItemGroupDataList()) {
                for (ItemData itemData : itemGroupData.getItemDataList()) {
                    itemData.setCheckValueEquality(Boolean.TRUE);
                    if (itemData.getValue().equals(
                        matchingEventData
                            .findFormData(formData.getFormOid())
                            .findItemGroupData(itemGroupData.getItemGroupOid())
                            .findItemData(itemData.getItemOid())
                            .getValue()
                        )) {
                        itemsToExcludeFromImport.add(itemData);
                    }
                }
            }
        }

        for (FormData formData : newEventData.getFormDataList()) {
            for (ItemGroupData itemGroupData : formData.getItemGroupDataList()) {
                for (ItemData itemToRemove : itemsToExcludeFromImport) {
                    if (itemGroupData.getItemDataList().contains(itemToRemove)) {
                        itemGroupData.removeItemData(itemToRemove);
                    }
                }
            }
        }

        return newEventData;
    }

    private Odm createEventDataImportOdm(EventData eventData, Odm subjectOdm) {
        // Generate import ODM
        Odm importEventOdm = new Odm(subjectOdm);
        importEventOdm.addStudySubject(subjectOdm.findFirstStudySubjectOrNone());
        importEventOdm.findFirstStudySubjectOrNone().initDefaultStudyEventDataList();
        importEventOdm.findFirstStudySubjectOrNone().addStudyEventData(eventData);
        importEventOdm.cleanAttributesUnnecessaryForImport();

        return importEventOdm;
    }

    //endregion

}
