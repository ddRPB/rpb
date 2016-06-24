/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Service handling Studies as aggregate root resources (study centric)
 */
@Component
@Path("/v1/studies")
public class StudyService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(StudyService.class);

    //endregion

    //region Injects

    //region AuditService

    @Inject
    private AuditLogService auditLogService;

    @SuppressWarnings("unused")
    public void setAuditLogService(AuditLogService svc) {
        this.auditLogService = svc;
    }

    @Inject
    private EmailService emailService;

    @SuppressWarnings("unused")
    public void setEmailService(EmailService svc) {
        this.emailService = svc;
    }

    //endregion

    //endregion

    //region StudySubjects

    //region POST

    /**
     * Create a subject pseudonym and enroll him into specified study
     *
     * @param headers HTTP headers with X-Api-Key
     * @param body JSON body containing details about a new study subject
     * @param studyIdentifier EDC study identifier where a new subject will be enrolled
     * @return Response HTTP response with code
     */
    @POST
    @Path("/{param}/studysubjects/")
    @Consumes({"application/vnd.studysubject.v1+json"})
    public Response addStudySubject(@Context HttpHeaders headers, String body,
                                    @PathParam("param") String studyIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        JSONObject inputJson;
        StudySubject newStudySubject = new StudySubject();
        try {
            inputJson = new JSONObject(body);

            // Person
            Person newPerson = new Person();
            newPerson.setFirstname(inputJson.getString("firstName"));
            newPerson.setSurname(inputJson.getString("lastName"));
            newPerson.setBirthname(inputJson.optString("birthName"));
            newPerson.setCity(inputJson.optString("city"));
            newPerson.setZipcode(inputJson.optString("zip"));
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date birthDate = format.parse(inputJson.getString("dateOfBirth"));
            newPerson.setBirthdate(birthDate);

            // StudySubject
            newStudySubject.setPerson(newPerson);
            newStudySubject.setStudySubjectId(inputJson.getString("patientId"));
            newStudySubject.setSex(inputJson.getString("sex").toLowerCase());
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat ocFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ocDate = ocFormat.format(sourceFormat.parse(inputJson.getString("dateOfBirth")));
            newStudySubject.setDateOfBirth(ocDate);
        }
        catch (Exception err) {
            log.error(err);
            this.emailService.sendMailToAdmin("API StudySubjectService", "JSON parsing failed",  err.toString());
        }

        // To load user password hash
        IRadPlanBioWebApiService svcRpb = createRpbWebApiConnection(userAccount);

        // Create pseudonym according to person identity
        IMainzellisteService svcPid = createPidConnection(userAccount);

        // StudySubject enrollment
        IOpenClinicaService svcEdc = createEdcConnection(userAccount, svcRpb);

        // Which study do we work with
        Study study = null;
        for (StudyType ocStudy : svcEdc.listAllStudies()) {
            if (ocStudy.getIdentifier().equals(studyIdentifier)) {
                study = new Study(ocStudy);
                break;
            }
        }

        if (study == null) {
            log.info("Study does not exist");
            return javax.ws.rs.core.Response.status(400).build();
        }

        try {
            // First check if the subject does not exists
            for (StudySubjectWithEventsType ss : svcEdc.listAllStudySubjectsByStudy(study)) {
                if (ss.getLabel().equals(newStudySubject.getStudySubjectId())) {
                    log.info("StudySubject with specified studySubjectId already exists within a study: " + newStudySubject.getStudySubjectId());
                    this.emailService.sendMailToAdmin("API StudySubjectService", "StudySubject already exists within a study", "StudySubject with specified studySubjectId already exists within a study: " + newStudySubject.getStudySubjectId());
                    return javax.ws.rs.core.Response.status(409).build();
                }
            }

            JSONObject finalResult = svcPid.newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = svcPid.newPatientToken(sessionId);

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("tokenId");
            }

            finalResult = svcPid.createPatientJson(
                    tokenId,
                    newStudySubject.getPerson()
            );

            String newId;
            if (finalResult != null) {
                newId = finalResult.optString("newId");
                if (!newId.isEmpty()) {
                    this.auditLogService.event(AuditEvent.PIDCreation, newId);
                }

                newStudySubject.setPid(newId);
            }
        }
        catch (Exception err) {
            // Generation failed or unsure patient continue with enrollment (without PID)
            log.info("PID generation failed: unsure patient identity");
            this.emailService.sendMailToAdmin("API StudySubjectService", "PID generation failed",  "Failed to create pseudonym for patient with HIS ID: " + newStudySubject.getStudySubjectId() + ". Enrollment will continue without pseudonym.");
        }

        // Construct SS for SOAP web service
        de.dktk.dd.rpb.core.ocsoap.types.StudySubject subjectToEnroll = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject();
        subjectToEnroll.setStudy(study);
        subjectToEnroll.setStudySubjectLabel(newStudySubject.getStudySubjectId());

        // Only allow m and f values
        subjectToEnroll.setSex(newStudySubject.getSex());

        // Calendar
        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(newStudySubject.getPerson().getBirthdate());
            XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            subjectToEnroll.setDateOfBirth(birthDateXML);

            c = new GregorianCalendar();
            c.setTime(c.getTime());
            XMLGregorianCalendar enrollmentDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            subjectToEnroll.setDateOfRegistration(enrollmentDateXML);
        }
        catch (Exception err) {
            log.error("Cannot get current date for study subject enrollment");
        }

        if (newStudySubject.getPid() != null && !newStudySubject.getPid().equals("")) {
            subjectToEnroll.setPersonID(
                    userAccount.getPartnerSite().getIdentifier() + "-" + newStudySubject.getPid()
            );
        }

        if (svcEdc.createNewStudySubject(subjectToEnroll)) {
            log.info("StudySubject have been successfully enrolled into a specified study: " + newStudySubject.getStudySubjectId() + " " + studyIdentifier);
            return javax.ws.rs.core.Response.status(201).build();
        }
        else {
            log.error("Study subject enrollment failed.");
            this.emailService.sendMailToAdmin("API StudySubjectService", "StudySubject enrollment failed",  "Failed to enroll: " + newStudySubject.getStudySubjectId() + " into study: " + studyIdentifier );
            return javax.ws.rs.core.Response.status(400).build();
        }
    }

    //endregion

    //endregion

    //region Private

    private IMainzellisteService createPidConnection(DefaultAccount account) {
        IMainzellisteService svcPid = null;

        if (account != null &&
                account.getPartnerSite().getPid() != null &&
                account.getPartnerSite().getPortal() != null) {

            svcPid = new MainzellisteService();

            String apiKey = account.getPartnerSite().getPid().getApiKey();
            String genratorBaseUrl = account.getPartnerSite().getPid().getGeneratorBaseUrl();
            String callback = account.getPartnerSite().getPortal().getPortalBaseUrl();

            //TODO: setup for use with apiVersion parameter 2.1 from DB
            String apiVersion = "2.1";

            svcPid.setupConnectionInfo(
                    genratorBaseUrl,
                    apiKey,
                    apiVersion,
                    callback
            );
        }

        return svcPid;
    }

    private IOpenClinicaService createEdcConnection(DefaultAccount account, IRadPlanBioWebApiService svcRpb) {
        IOpenClinicaService svcEdc = null;

        // OpenClinica user account
        if (account != null &&
                account.hasOpenClinicaAccount() &&
                account.getPartnerSite().getPortal() != null &&
                account.getPartnerSite().getEdc() != null &&
                svcRpb != null) {

            // I need to get OC user hash to be able to use SOAP (the RPB and OC password can be different)
            String ocHash = svcRpb.loadAccountPasswordHash(account);

            // Try to connect to the location of OC SOAP web service instance
            // TODO: REST last argument should be this.myAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            // I use internal URL of portal because JERSEY has troubles with reverse proxy redirection
            svcEdc = new OpenClinicaService();
            svcEdc.connectWithHash(
                    account.getOcUsername(),
                    ocHash,
                    account.getPartnerSite().getEdc().getSoapBaseUrl(),
                    account.getPartnerSite().getPortal().getPortalBaseUrl() + "OpenClinica/"
            );
        }

        return svcEdc;
    }

    private IRadPlanBioWebApiService createRpbWebApiConnection(DefaultAccount account) {
        IRadPlanBioWebApiService svcRpb = null;

        if (account != null &&
                account.getPartnerSite().getServer() != null) {

            svcRpb = new RadPlanBioWebApiService();
            svcRpb.setupConnection(
                    account.getPartnerSite().getServer().getPublicUrl()
            );
        }

        return svcRpb;
    }

    //endregion

}
