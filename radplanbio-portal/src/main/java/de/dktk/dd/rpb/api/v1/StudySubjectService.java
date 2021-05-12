/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.*;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Service handling StudySubjects participating in provided study (studyIdentifier)
 */
@Component
@Path("/v1/studies/{studyIdentifier}/studysubjects")
public class StudySubjectService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(StudySubjectService.class);

    //endregion

    //region Injects

    @Inject
    private CtpService svcCtp;

    //endregion

    //region Resource Methods

    //region GET

    @GET
    @Path("/{studySubjectIdentifier}/")
    @Produces({"application/vnd.studysubject.v1+json"})
    public Response readStudySubject(@Context HttpHeaders headers,
                                     @PathParam("studyIdentifier") String studyIdentifier,
                                     @PathParam("studySubjectIdentifier") String studySubjectIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
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
        IOpenClinicaService svcEdc = createEdcConnection(userAccount);

        // Which study do we work with base on metadata query
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);
        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        Study study = null;
        if (odm != null && odm.getStudies() != null) {
            if (odm.getStudies().size() > 0) {
                study = new Study();
                study.setStudyOID(odm.getStudies().get(0).getOid());
                study.setStudyIdentifier(odm.getStudies().get(0).getGlobalVariables().getProtocolName());
            }
        }

        if (study == null) {
            log.info("Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        // StudySubject enrolled in study
        StudySubjectWithEventsType existingStudySubject = null;
        try {

            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    studySubjectIdentifier
            );

            // First check if the subject does exists
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {

                // Only then try to find it as full entity
                for (StudySubjectWithEventsType ss : svcEdc.listAllStudySubjectsByStudy(study)) {
                    if (ss.getLabel().equals(studySubjectIdentifier)) {
                        existingStudySubject = ss;
                        break;
                    }
                }
            } else {
                // Not Found
                log.info("Study Subject " + studySubjectIdentifier + " does not exist in study " + studyIdentifier);
                return javax.ws.rs.core.Response.status(404).build();
            }
        } catch (Exception err) {
            return javax.ws.rs.core.Response.status(400).build();
        }

        if (existingStudySubject != null) {
            // TODO: marshal to JSON and return but return ODM Subject Data
        }

        // OK
        return javax.ws.rs.core.Response.status(200).build();
    }

    @GET
    @Produces({"application/vnd.studysubject.v1+json"})
    public Response readStudySubjects(@Context HttpHeaders headers,
                                      @PathParam("studyIdentifier") String studyIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
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
        IOpenClinicaService svcEdc = createEdcConnection(userAccount);

        // Which study do we work with base on metadata query
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);
        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        Study study = null;
        if (odm != null && odm.getStudies() != null) {
            if (odm.getStudies().size() > 0) {
                study = new Study();
                study.setStudyOID(odm.getStudies().get(0).getOid());
                study.setStudyIdentifier(odm.getStudies().get(0).getGlobalVariables().getProtocolName());
            }
        }

        if (study == null) {
            log.info("Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        // StudySubject list enrolled in study
        List<StudySubjectWithEventsType> existingStudySubjectList;
        try {
            existingStudySubjectList = svcEdc.listAllStudySubjectsByStudy(study);
        } catch (Exception err) {
            return javax.ws.rs.core.Response.status(400).build();
        }

        if (existingStudySubjectList != null) {
            // TODO: marshal to JSON and return but return ODM Subject Data list
        }

        // OK
        return javax.ws.rs.core.Response.status(200).build();
    }

    //endregion

    //region POST

    /**
     * Create a subject pseudonym and enroll him into specified study
     *
     * @param headers         HTTP headers with X-Api-Key
     * @param body            JSON body containing details about a new study subject
     * @param studyIdentifier EDC study identifier where a new subject will be enrolled
     * @return Response HTTP response with code
     */
    @POST
    @Consumes({"application/vnd.studysubject.v1+json"})
    public Response createStudySubject(@Context HttpHeaders headers, String body,
                                       @PathParam("studyIdentifier") String studyIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
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

        // StudySubject
        StudySubject newStudySubject = new StudySubject();
        try {
            JSONObject newJsonPerson = new JSONObject(body);
            Person newPerson = this.unmarshallPerson(newJsonPerson);
            newStudySubject = this.prepareStudySubject(newPerson, newJsonPerson, studyIdentifier);
        } catch (Exception err) {
            log.error(err);
            this.emailService.sendMailToAdmin(userAccount.getEmail(), "POST: JSON parsing failed", err.toString());
        }

        // Create pseudonym according to person identity
        IMainzellisteService svcPid = createPidConnection(userAccount);

        // StudySubject enrollment
        IOpenClinicaService svcEdc = createEdcConnection(userAccount);

        // Which study do we work with base on metadata query
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);
        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        Study study = null;
        if (odm != null && odm.getStudies() != null) {
            if (odm.getStudies().size() > 0) {
                study = new Study();
                study.setStudyOID(odm.getStudies().get(0).getOid());
                study.setStudyIdentifier(odm.getStudies().get(0).getGlobalVariables().getProtocolName());
            }
        }

        if (study == null) {
            log.info("POST: Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        try {
            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    newStudySubject.getStudySubjectId()
            );

            // First check if the subject does already exists within a study
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {

                // Log this event
                log.info("POST: StudySubject with specified SSID already exists within a study: " + newStudySubject.getStudySubjectId());
                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        "StudySubject already exists within a study",
                        "StudySubject with specified SSID already exists within a study: " + newStudySubject.getStudySubjectId()
                );

                // Conflict
                return javax.ws.rs.core.Response.status(409).build();
            }

            // Create a new subject (patient) in identity database
            JSONObject finalResult = svcPid.createPatient(newStudySubject.getPerson());

            String newId;
            if (finalResult != null) {
                newId = finalResult.optString("newId");
                if (!newId.isEmpty()) {
                    this.auditLogService.event(AuditEvent.PIDCreation, newId);
                }

                newStudySubject.setPid(newId);
            }
        } catch (Exception err) {
            // Generation failed or unsure patient continue with enrollment (without PID)
            log.info(err);

            String message = "PID generation failed";
            String details = "Failed to create pseudonym for patient with HIS ID: " + newStudySubject.getStudySubjectId() + ". Enrollment will continue without pseudonym.";

            // Unsure patient -> Notify about possible match with already registered patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.auditLogService.event(AuditEvent.PIDUnsure, newStudySubject.getPerson().toString());

                details += "Unsure patient identity";

                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        message,
                        details
                );
            }
            // Otherwise notify that the enrollment will continue without pseudonym
            else {
                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        message,
                        details
                );
            }
        }

        // Construct StudySubject for SOAP web service
        de.dktk.dd.rpb.core.ocsoap.types.StudySubject subjectToEnroll = this.prepareSoapStudySubject(newStudySubject, study, userAccount.getPartnerSite());
        String ssid = null;
        try {
            ssid = svcEdc.createNewStudySubject(subjectToEnroll);
        } catch (OCConnectorException e) {
            String errorMessage = "There is a problem creating a new StudySubject.";
            this.log.error(errorMessage, e);
        }

        if (ssid != null) {

            // Update CTP patient lookup tables
            if (this.svcCtp != null) {
                //TODO: read study EDC code from the DB
                if (studyIdentifier.equals(Constants.study0Identifier)) {
                    this.svcCtp.updateStudySubjectPseudonym(
                            "S0",
                            ssid.replace(Constants.HISprefix, ""),
                            subjectToEnroll.getPersonID()
                    );
                }
            }

            this.auditLogService.event(
                    AuditEvent.EDCStudySubjectEnrollment,
                    subjectToEnroll.getPersonID(),
                    study.getStudyIdentifier(),
                    ssid
            );
            log.info("StudySubject have been successfully enrolled into a specified study: " + ssid + " " + studyIdentifier);
            return javax.ws.rs.core.Response.status(201).build();
        } else {
            log.error("Study subject enrollment failed.");
            this.emailService.sendMailToAdmin(
                    userAccount.getEmail(),
                    "[POST]: StudySubject enrollment failed",
                    "Failed to enroll: " + newStudySubject.getStudySubjectId() + " into study: " + studyIdentifier
            );
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //region PUT

    /**
     * Update a existing study subject IDAT
     *
     * @param headers                HTTP headers with X-Api-Key
     * @param body                   JSON body containing details about a modified study subject
     * @param studyIdentifier        EDC study identifier where a new subject will be enrolled
     * @param studySubjectIdentifier Existing study subject identifier that should be updated
     * @return Response HTTP response with code
     */
    @PUT
    @Path("/{studySubjectIdentifier}/")
    @Consumes({"application/vnd.studysubject.v1+json"})
    public Response updateStudySubject(@Context HttpHeaders headers, String body,
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

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // StudySubject
        StudySubject modifiedStudySubject = new StudySubject();
        try {
            JSONObject modifiedJsonPerson = new JSONObject(body);
            Person modifiedPerson = this.unmarshallPerson(modifiedJsonPerson);
            modifiedStudySubject = this.prepareStudySubject(modifiedPerson, modifiedJsonPerson, studyIdentifier);
        } catch (Exception err) {
            log.error(err);
            this.emailService.sendMailToAdmin(userAccount.getEmail(), "Update Study Subject: JSON parsing failed", err.toString());
        }

        // Create pseudonym according to person identity
        IMainzellisteService svcPid = createPidConnection(userAccount);

        // StudySubject enrollment
        IOpenClinicaService svcEdc;
        if (password == null || password.isEmpty()) {
            svcEdc = createEdcConnection(userAccount);
        } else {
            svcEdc = createEdcConnection(userAccount, password);
        }

        // Which study do we work with base on metadata query
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);
        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        Study study = null;
        if (odm != null && odm.getStudies() != null) {
            if (odm.getStudies().size() > 0) {
                study = new Study();
                study.setStudyOID(odm.getStudies().get(0).getOid());
                study.setStudyIdentifier(odm.getStudies().get(0).getGlobalVariables().getProtocolName());
            }
        }

        if (study == null) {
            log.info("Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        // StudySubject enrolled in study
        StudySubject existingStudySubjectDb = null;

        try {
            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    modifiedStudySubject.getStudySubjectId()
            );

            // First check if the subject does exists
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {
                existingStudySubjectDb = this.openClinicaDataRepository.getStudySubjectByStudySubjectId(studyIdentifier, studySubjectIdentifier);
            }

            // StudySubject exists in RPB I am going to check whether IDAT was changed
            if (existingStudySubjectDb != null) {

                // Take patient pseudonym and load current IDAT
                JSONObject finalResult = svcPid.newSession();

                String sessionId = "";
                if (finalResult != null) {
                    sessionId = finalResult.getString("sessionId");
                }

                // Remove RPB partner site identifier from pseudonym
                String pid = existingStudySubjectDb.getPid().replace(userAccount.getPartnerSite().getIdentifier() + Constants.RPB_IDENTIFIERSEP, "");

                finalResult = svcPid.readPatientToken(sessionId, pid);

                String tokenId = "";
                if (finalResult != null) {
                    tokenId = finalResult.getString("id");
                }

                Person existingIdat = svcPid.getPatient(tokenId);

                // Store PID for existing and modified person (PID is the same as it is the same person)
                existingIdat.setPid(pid);
                modifiedStudySubject.getPerson().setPid(pid);

                // Check whether there was a change in patient IDAT data
                if (!existingIdat.patientIdatEquals(modifiedStudySubject.getPerson())) {

                    // Edit the IDAT according to the modifiedStudySubject data
                    boolean patientEditResult = svcPid.editPatient(modifiedStudySubject.getPerson());
                    if (patientEditResult) {
                        this.auditLogService.event(AuditEvent.PIDModification, modifiedStudySubject.getPerson().getPid());
                        log.info("IDAT was successfully updated for PID: " + modifiedStudySubject.getPerson().getPid());
                    }
                    // Automatic update failed
                    else {
                        log.info("PID patient IDAT need to be updated manually");
                        this.emailService.sendMailToAdmin(
                                userAccount.getEmail(),
                                "PID patient IDAT need to be updated manually",
                                "You need to update IDAT for patient with HIS ID: " + modifiedStudySubject.getStudySubjectId()
                        );
                    }

                    // OK
                    return javax.ws.rs.core.Response.status(200).build();
                }
            }
            // StudySubject does not exists in study, get/create a pseudonym
            else {
                JSONObject finalResult = svcPid.createPatient(modifiedStudySubject.getPerson());

                String newId;
                if (finalResult != null) {
                    newId = finalResult.optString("newId");
                    if (!newId.isEmpty()) {
                        this.auditLogService.event(AuditEvent.PIDCreation, newId);
                    }

                    modifiedStudySubject.setPid(newId);
                }
            }
        } catch (Exception err) {
            String message = "PID patient IDAT update or creation failed";
            String details = "Failed to update or create IDAT for patient with HIS ID: " + modifiedStudySubject.getStudySubjectId() + ".";

            log.info(message);

            // Unsure patient -> notify about possible match with already registered patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.auditLogService.event(AuditEvent.PIDUnsure, modifiedStudySubject.getPerson().toString());

                details += " PID was not generated because the pseudonymisation subsystem cannot decide whether there is a patient match in a patient identity database.";
                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        message,
                        details
                );
            }
            // Otherwise unknown error
            else {
                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        message,
                        details
                );
            }

            return javax.ws.rs.core.Response.status(400).build();
        }

        // StudySubject does not exists in RPB I am going to enroll a new one
        if (existingStudySubjectDb == null) {

            // Construct StudySubject for SOAP web service
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject subjectToEnroll = this.prepareSoapStudySubject(modifiedStudySubject, study, userAccount.getPartnerSite());
            String ssid = null;
            try {
                ssid = svcEdc.createNewStudySubject(subjectToEnroll);
            } catch (OCConnectorException e) {
                String errorMessage = "There is a problem creating a new StudySubject.";
                this.log.error(errorMessage, e);
            }

            if (ssid != null) {

                // Update CTP patient lookup tables
                if (this.svcCtp != null) {
                    //TODO: read study EDC code from the DB
                    if (studyIdentifier.equals(Constants.study0Identifier)) {
                        this.svcCtp.updateStudySubjectPseudonym(
                                "S0",
                                ssid.replace(Constants.HISprefix, ""),
                                subjectToEnroll.getPersonID()
                        );
                    }
                }

                this.auditLogService.event(
                        AuditEvent.EDCStudySubjectEnrollment,
                        subjectToEnroll.getPersonID(),
                        study.getStudyIdentifier(),
                        ssid
                );
                log.info("StudySubject have been successfully enrolled into a specified study: " + ssid + " " + studyIdentifier);

                // OK
                return javax.ws.rs.core.Response.status(200).build();
            } else {
                log.error("Study subject enrollment failed.");
                this.emailService.sendMailToAdmin(
                        userAccount.getEmail(),
                        "[PUT]: StudySubject enrollment failed",
                        "Failed to enroll: " + modifiedStudySubject.getStudySubjectId() + " into study: " + studyIdentifier
                );
                return javax.ws.rs.core.Response.status(400).build();
            }
        } else {
            // OK
            return javax.ws.rs.core.Response.status(200).build();
        }
    }

    /**
     * Notify about necessity to merge patients
     *
     * @param headers                     HTTP headers with X-Api-Key
     * @param body                        JSON body containing details about a modified study subject
     * @param studyIdentifier             EDC study identifier where a new subject will be enrolled
     * @param priorStudySubjectIdentifier Existing study subject identifier that should be updated
     * @return Response HTTP response with code
     */
    @PUT
    @Path("/{studySubjectIdentifier}/merge/")
    @Consumes({"application/vnd.studysubject.v1+json"})
    public Response mergeStudySubject(@Context HttpHeaders headers, String body,
                                      @PathParam("studyIdentifier") String studyIdentifier,
                                      @PathParam("studySubjectIdentifier") String priorStudySubjectIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Clear OC password is necessary for authentication with RESTfull URLs
        String password = headers.getRequestHeader("X-Password").get(0);

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        JSONObject inputJson;
        StudySubject modifiedStudySubject = new StudySubject();
        String modifiedStudySubjectId = "";
        try {
            inputJson = new JSONObject(body);

            // Person
            Person modifiedPerson = new Person();
            modifiedPerson.setFirstname(inputJson.getString("firstName"));
            modifiedPerson.setSurname(inputJson.getString("lastName"));
            modifiedPerson.setBirthname(inputJson.optString("birthName"));
            modifiedPerson.setCity(inputJson.optString("city"));
            modifiedPerson.setZipcode(inputJson.optString("zip"));


            // Birth data can be missing
            if (!inputJson.getString("dateOfBirth").isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date birthDate = format.parse(inputJson.getString("dateOfBirth"));
                modifiedPerson.setBirthdate(birthDate);
            } else {
                modifiedPerson.setBirthdate(null);
            }

            // StudySubject
            modifiedStudySubject.setPerson(modifiedPerson);

            // Use HIS- in front of HIS ID in order to prevent EDC to consider provided integer as highest ID
            if (studyIdentifier.equals(Constants.study0Identifier)) {
                if (inputJson.getString("mergeInfo").startsWith(Constants.HISprefix)) {
                    modifiedStudySubject.setStudySubjectId(
                            inputJson.getString("mergeInfo")
                    );
                } else {
                    modifiedStudySubject.setStudySubjectId(
                            Constants.HISprefix + inputJson.getString("mergeInfo")
                    );
                }
            } else {
                modifiedStudySubject.setStudySubjectId(
                        inputJson.getString("mergeInfo")
                );
            }

            if (!modifiedStudySubject.getStudySubjectId().equals(priorStudySubjectIdentifier)) {
                throw new Exception(
                        "Study subject identifier " + priorStudySubjectIdentifier +
                                "does not match mergeInfo " + modifiedStudySubject.getStudySubjectId() +
                                "in the request body."
                );
            }

            modifiedStudySubject.setSex(inputJson.getString("sex").toLowerCase());
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat ocFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ocDate = ocFormat.format(sourceFormat.parse(inputJson.getString("dateOfBirth")));
            modifiedStudySubject.setDateOfBirth(ocDate);

            // New modified StudySubjectId
            if (inputJson.getString("patientId").startsWith(Constants.HISprefix)) {
                modifiedStudySubjectId = inputJson.getString("patientId");
            } else {
                modifiedStudySubjectId = Constants.HISprefix + inputJson.getString("patientId");
            }

        } catch (Exception err) {
            log.error(err);
            this.emailService.sendMailToAdmin(
                    userAccount.getEmail(),
                    "JSON parsing failed",
                    err.toString()
            );
        }

        // StudySubject enrollment
        IOpenClinicaService svcEdc;
        if (password == null || password.isEmpty()) {
            svcEdc = createEdcConnection(userAccount);
        } else {
            svcEdc = createEdcConnection(userAccount, password);
        }

        // Which study do we work with base on metadata query
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);
        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        Study study = null;
        if (odm != null && odm.getStudies() != null) {
            if (odm.getStudies().size() > 0) {
                study = new Study();
                study.setStudyOID(odm.getStudies().get(0).getOid());
                study.setStudyIdentifier(odm.getStudies().get(0).getGlobalVariables().getProtocolName());
            }
        }

        if (study == null) {
            log.info("Study " + studyIdentifier + " does not exist");
            return javax.ws.rs.core.Response.status(404).build();
        }

        try {
            // StudySubject enrolled in study
            StudySubject existingStudySubjectDb = null;

            // Create subject for SOAP query
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject querySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject(
                    modifiedStudySubject.getStudySubjectId()
            );

            // First check if the subject does already exists within a study
            if (svcEdc.studySubjectExistsInStudy(querySubject, study)) {
                existingStudySubjectDb = this.openClinicaDataRepository.getStudySubjectByStudySubjectId(studyIdentifier, priorStudySubjectIdentifier);
            }

            // StudySubject does not exists within a study, nothing to merge: No Content
            if (existingStudySubjectDb == null) {
                log.info("StudySubject " + modifiedStudySubject.getStudySubjectId() + " does not exist in study " + studyIdentifier);
                return javax.ws.rs.core.Response.status(204).build();
            }

            // StudySubject need to be merged manually
            log.info("StudySubject ID need to be manually changed");
            this.emailService.sendMailToAdmin(
                    userAccount.getEmail(),
                    "StudySubject ID need to be manually changed",
                    "Need to manually update SSID for existing patient with HIS ID: " + modifiedStudySubject.getStudySubjectId() + " to " + modifiedStudySubjectId
            );

            // OK
            return javax.ws.rs.core.Response.status(200).build();
        } catch (Exception err) {
            log.info("PID merge failed: " + err.getMessage());
            this.emailService.sendMailToAdmin(
                    userAccount.getEmail(),
                    "PID merge failed",
                    "Failed to merge PID: " + modifiedStudySubject.getStudySubjectId() + " to " + modifiedStudySubjectId
            );
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //endregion

    //region Private Methods

    private IMainzellisteService createPidConnection(DefaultAccount account) {
        IMainzellisteService svcPid = null;

        if (account != null &&
                account.getPartnerSite().getPid() != null &&
                account.getPartnerSite().getPortal() != null) {

            svcPid = new MainzellisteService();

            String apiKey = account.getPartnerSite().getPid().getApiKey();
            String generatorBaseUrl = account.getPartnerSite().getPid().getGeneratorBaseUrl();
            String apiVersion = account.getPartnerSite().getPid().getApiVersion();
            
            // No callback necessary for direct portal to PID communication
            String callback = "";

            svcPid.setupConnectionInfo(
                    generatorBaseUrl,
                    apiKey,
                    apiVersion,
                    callback
            );
        }

        return svcPid;
    }

    private Person unmarshallPerson(JSONObject jsonPerson) throws JSONException, ParseException {
        Person person = new Person();

        // IDAT
        person.setSurname(jsonPerson.getString("lastName"));
        person.setFirstname(jsonPerson.getString("firstName"));
        person.setBirthname(jsonPerson.optString("birthName"));

        // Empty string if city of residence and zip code are empty
        person.setCity(jsonPerson.optString("city", ""));
        person.setZipcode(jsonPerson.optString("zip", ""));

        // Date of birth can be missing
        if (!jsonPerson.getString("dateOfBirth").isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date birthDate = format.parse(jsonPerson.getString("dateOfBirth"));
            person.setBirthdate(birthDate);
        } else {
            person.setBirthdate(null);
        }

        return person;
    }

    private StudySubject prepareStudySubject(Person person, JSONObject jsonPerson, String studyIdentifier) throws JSONException, ParseException {
        StudySubject studySubject = new StudySubject();
        studySubject.setPerson(person);

        // Use HIS- in front of HIS ID in order to prevent EDC to consider provided integer as highest ID
        if (studyIdentifier.equals(Constants.study0Identifier)) {
            if (jsonPerson.getString("patientId").startsWith(Constants.HISprefix)) {
                studySubject.setStudySubjectId(
                        jsonPerson.getString("patientId")
                );
            } else {
                studySubject.setStudySubjectId(
                        Constants.HISprefix + jsonPerson.getString("patientId")
                );
            }
        } else {
            studySubject.setStudySubjectId(jsonPerson.getString("patientId"));
        }

        // Gender
        studySubject.setSex(jsonPerson.getString("sex").toLowerCase());

        // Date of birth can be missing
        if (!jsonPerson.getString("dateOfBirth").isEmpty()) {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat ocFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ocDate = ocFormat.format(sourceFormat.parse(jsonPerson.getString("dateOfBirth")));
            studySubject.setDateOfBirth(ocDate);
        } else {
            studySubject.setDateOfBirth(null);
        }

        return studySubject;
    }

    private de.dktk.dd.rpb.core.ocsoap.types.StudySubject prepareSoapStudySubject(StudySubject studySubject, Study study, PartnerSite site) {
        de.dktk.dd.rpb.core.ocsoap.types.StudySubject subjectToEnroll = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject();

        subjectToEnroll.setStudy(study);
        subjectToEnroll.setStudySubjectLabel(studySubject.getStudySubjectId());

        // Only allow m and f values
        subjectToEnroll.setSex(studySubject.getSex());

        // Calendar
        try {
            GregorianCalendar c = new GregorianCalendar();
            if (studySubject.getPerson().getBirthdate() != null) {
                c.setTime(studySubject.getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                subjectToEnroll.setDateOfBirth(birthDateXML);
            }

            c = new GregorianCalendar();
            c.setTime(c.getTime());
            XMLGregorianCalendar enrollmentDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            subjectToEnroll.setDateOfRegistration(enrollmentDateXML);
        } catch (Exception err) {
            log.error("Cannot get current date for study subject enrollment");
        }

        if (studySubject.getPid() != null && !studySubject.getPid().isEmpty()) {
            subjectToEnroll.setPersonID(
                    site.getIdentifier() + Constants.RPB_IDENTIFIERSEP + studySubject.getPid()
            );
        }

        return subjectToEnroll;
    }

    //endregion

}
