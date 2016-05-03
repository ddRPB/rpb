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

package de.dktk.dd.rpb.core.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.Odm;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.openclinica.ws.data.v1.ImportResponse;
import org.openclinica.ws.event.v1.ScheduleResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.net.MalformedURLException;

import de.dktk.dd.rpb.core.ocsoap.connect.ConnectInfo;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.connect.OCWebServices;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.Event;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;

import org.openclinica.ws.beans.StudyType;
import org.openclinica.ws.study.v1.ListAllResponse;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.studysubject.v1.ListAllByStudyResponse;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.studysubject.v1.CreateResponse;

/**
 * OpenClinica service helps to access SOAP and REST based web-service provided by EDC system
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
@Transactional(readOnly = true)
@Named("openClinicaService")
public class OpenClinicaService implements IOpenClinicaService {

    //region Enums

    /**
     * OpenClinica REST full URLs casebook formats
     */
    public enum CasebookFormat {
        JSON("json"), XML("xml"), HTML("html");

        private String value;

        CasebookFormat(String value) {
            this.value = value;
        }
    }

    /**
     * OpenClinica REST full URLs casebook methods
     */
    public enum CasebookMethod {
        VIEW("view"), PRINT("print");

        private String value;

        CasebookMethod(String value) {
            this.value = value;
        }
    }

    /**
     * OpenClinica REST full URLs casebook data type filter
     */
    public enum CasebookDataType {
        CLINICALDATA("clinicaldata");

        private String value;

        CasebookDataType(String value) {
            this.value = value;
        }
    }

    //endregion

    //region Finals

    private static final Logger log = Logger.getLogger(OpenClinicaService.class);

    final static String success = "Success";
    final static String loginMethod = "j_spring_security_check";
    final static String uname = "j_username";
    final static String passwd = "j_password";

    //endregion

    //region Members

    private OCWebServices ocws;
    private String restBaseUrl;

    //endregion

    //region Constructors

    public OpenClinicaService() {
        // NOOP
    }

    //endregion

    //region Properties

    public boolean isConnected() {
        return this.ocws != null;
    }

    //endregion

    //region Methods

    //region Connect

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(String username, String password, String wsBaseUrl, String restBaseUrl) {
        try {
            ConnectInfo ci = new ConnectInfo(wsBaseUrl, username);
            ci.setPassword(password);
            this.ocws = OCWebServices.getInstance(
                    ci,
                    false, // logging
                    true // forceInstantiation, I am forcing because otherwise it is holding the old one
            );
            this.restBaseUrl = restBaseUrl;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectWithHash(String username, String passwordHash, String wsBaseUrl, String restBaseUrl) {
        try {
            ConnectInfo ci = new ConnectInfo(wsBaseUrl, username, passwordHash);
            this.ocws = OCWebServices.getInstance(
                    ci,
                    false, // logging
                    true // forceInstantiation, I am forcing because otherwise it is holding the old one
            );
            this.restBaseUrl = restBaseUrl;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region SOAP

    //region Metadata

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataODM getStudyMetadata(Study study) {
        MetadataODM metadata = null;

        try {
            if (this.isConnected()) {
                metadata = this.ocws.fetchStudyMetadata(study);
            }
        } catch (OCConnectorException e) {
            e.printStackTrace();
        }

        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataODM getStudyMetadata(String identifier) {
        MetadataODM metadata = null;

        try {
            if (this.isConnected()) {
                metadata = this.ocws.fetchMetadataByIdentifier(identifier);
            }
        } catch (OCConnectorException e) {
            e.printStackTrace();
        }

        return metadata;
    }

    //endregion

    //region Study

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudyType> listAllStudies() {

        ListAllResponse allStudies = null;

        try {
            if (this.isConnected()) {
                allStudies = this.ocws.listAllStudies();
            }
        } catch (OCConnectorException e) {
            e.printStackTrace();
        }

        return allStudies != null ? allStudies.getStudies().getStudy() : null;
    }

    //endregion

    //region Study Subject

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createNewStudySubject(StudySubject studySubject) {
        CreateResponse response;
        boolean result = false;

        try {
            if (this.isConnected()) {
                response = this.ocws.createStudySubject(studySubject);
                if (response.getResult().equals(success)) {
                    result = true;
                }
            }
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        } catch (OCConnectorException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudySubjectWithEventsType> listAllStudySubjectsByStudy(Study study)  throws OCConnectorException {

        // List all subject according to study/site
        ListAllByStudyResponse allStudySubjecs = this.ocws.listAllByStudy(study);
        log.info(allStudySubjecs.getResult());

        return allStudySubjecs.getStudySubjects().getStudySubject();
    }

    @Override
    public List<StudySubject> getSubjectsByStudy(de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy)
            throws OCConnectorException {

        List<StudySubject> subjectList = new ArrayList<StudySubject>();

        Study ocStudy = this.convertRpbStudyToOcStudy(rpbStudy);

        for (StudySubjectWithEventsType sswe : this.listAllStudySubjectsByStudy(ocStudy)) {
            StudySubject ss = null;

            try {
                ss = new StudySubject(ocStudy, sswe);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }

            // Assign events
            ArrayList<ScheduledEvent> scheduledEvents = new ArrayList<ScheduledEvent>();
            if (sswe.getEvents() != null) {
                for (EventType eventType : sswe.getEvents().getEvent()) {

                    ScheduledEvent newEvent = null;
                    try {
                        newEvent = new ScheduledEvent(eventType);
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }

                    scheduledEvents.add(newEvent);
                }

                if (ss != null) {
                    ss.setScheduledEvents(scheduledEvents);
                }
            }

            subjectList.add(ss);
        }

        return subjectList;
    }

    //endregion

    //region Study Event

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Event> listAllEventDefinitionsByStudy(Study study) {
        ArrayList<Event> events = null;

        try {
            if (this.isConnected()) {
                events = this.ocws.fetchEventDefinitions(study);
            }
        } catch (OCConnectorException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public void scheduleStudyEvent(StudySubject subject, ScheduledEvent event) throws OCConnectorException {
        ScheduleResponse response = this.ocws.scheduleEvent(subject, event);
        log.info(response.getResult());
    }

    //endregion

    //region Data

    public void importData(String odmXmlData) throws OCConnectorException {
        if (this.isConnected()) {

            // When using OC SOAP ws the xml should start with ODM element as root
            odmXmlData = odmXmlData.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
            odmXmlData = odmXmlData.replace(" xmlns:ns2=\"http://www.openclinica.org/ns/odm_ext_v130/v3.1\" xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"", "");

            // Import
            ImportResponse response = this.ocws.importODM(odmXmlData);
            log.info(response.getResult());
        }
    }

    //endregion

    //endregion

    //region REST

    //region Study Subject

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudySubject> getStudyCasebookSubjects(String studyOid) {
        List<StudySubject> results = new ArrayList<StudySubject>();

        String method = studyOid + "/*/*/*";
        String format = "json";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(format, method);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            try {
                JSONObject obj = new JSONObject(output);

                JSONObject clinicalData = obj.getJSONObject("ClinicalData");

                // Multiple subjects (for one subject it will be json object not an array)
                JSONArray subjectData;
                subjectData = clinicalData.optJSONArray("SubjectData");
                if (subjectData != null) {
                    for (int i = 0; i < subjectData.length(); i++) {
                        StudySubject subject = new StudySubject();
                        subject.setStudySubjectOID(subjectData.getJSONObject(i).getString("@SubjectKey"));
                        subject.setStudySubjectLabel(subjectData.getJSONObject(i).getString("@OpenClinica:StudySubjectID"));
                        subject.setPersonID(subjectData.getJSONObject(i).optString("@OpenClinica:UniqueIdentifier", ""));

                        results.add(subject);
                    }
                }
                // Only one subject
                else {
                    JSONObject s = clinicalData.getJSONObject("SubjectData");

                    StudySubject subject = new StudySubject();
                    subject.setStudySubjectOID(s.getString("@SubjectKey"));
                    subject.setStudySubjectLabel(s.getString("@OpenClinica:StudySubjectID"));
                    subject.setPersonID(s.optString("@OpenClinica:UniqueIdentifier", ""));

                    results.add(subject);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudySubject getStudyCasebookSubject(String studyOid, String studySubjectIdentifier) {
        StudySubject result = null;

        String method = studyOid + "/" + studySubjectIdentifier+ "/*/*";
        String format = "json";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(format, method);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            try {
                JSONObject obj = new JSONObject(output);

                JSONObject clinicalData = obj.getJSONObject("ClinicalData");
                JSONObject s = clinicalData.getJSONObject("SubjectData");

                StudySubject subject = new StudySubject();
                subject.setStudySubjectOID(s.getString("@SubjectKey"));
                subject.setStudySubjectLabel(s.getString("@OpenClinica:StudySubjectID"));
                subject.setPersonID(s.optString("@OpenClinica:UniqueIdentifier", ""));

                result = subject;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudyCasebookSubjects(CasebookFormat format, CasebookMethod method, String queryOdmXmlPath) {

        List<de.dktk.dd.rpb.core.domain.edc.StudySubject> results = new ArrayList<de.dktk.dd.rpb.core.domain.edc.StudySubject>();

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(format.value, queryOdmXmlPath);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            Odm result;
            try {
                // Unmarshall XML
                if (format == CasebookFormat.XML) {
                    JAXBContext context = JAXBContext.newInstance(Odm.class);
                    Unmarshaller un = context.createUnmarshaller();
                    StringReader reader = new StringReader(output);
                    result = (Odm) un.unmarshal(reader);

                    if (result != null) {

                        if (result.getClinicalDataList() != null) {
                            for (ClinicalData cd : result.getClinicalDataList()) {
                                if (cd.getStudySubjects() != null) {
                                    results = cd.getStudySubjects();
                                    break;
                                }
                            }
                        }
                    }
                }
                else if (format == CasebookFormat.JSON) {
                    // TODO: how to fast unmarshall JSON
                }
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }

        return results;
    }

    //endregion

    //region Study Event

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDefinition> getStudyCasebookEvents(String studyOid, String studySubjectIdentifier) {
        List<EventDefinition> results = new ArrayList<EventDefinition>();

        // Depending on OC version studySubjectIdentifier can be StudySubjectID instead of subject OID
        String method = studyOid + "/" + studySubjectIdentifier + "/*/*";
        String format = "json";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(format, method);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            try {
                JSONObject obj = new JSONObject(output);
                JSONObject clinicalData = obj.getJSONObject("ClinicalData");
                JSONObject subjectData = clinicalData.getJSONObject("SubjectData");

                // Multiple events
                JSONArray eventData;
                eventData = subjectData.optJSONArray("StudyEventData");
                if (eventData != null) {
                    for (int i = 0; i < eventData.length(); i++) {
                        EventDefinition event = new EventDefinition();

                        event.setOid(eventData.getJSONObject(i).getString("@StudyEventOID"));
                        event.setStatus(eventData.getJSONObject(i).getString("@OpenClinica:Status"));
                        String date = eventData.getJSONObject(i).getString("@OpenClinica:StartDate");
                        event.setStartDate(date);
                        event.setSubjectAgeAtEvent(eventData.getJSONObject(i).optInt("OpenClinica:SubjectAgeAtEvent", 0));
                        event.setStudyEventRepeatKey(eventData.getJSONObject(i).getInt("@StudyEventRepeatKey"));

                        results.add(event);
                    }
                }
                // Only one event
                else {
                    JSONObject ed = subjectData.getJSONObject("StudyEventData");
                    EventDefinition event = new EventDefinition();

                    event.setOid(ed.getString("@StudyEventOID"));
                    event.setStatus(ed.getString("@OpenClinica:Status"));
                    String date = ed.getString("@OpenClinica:StartDate");
                    event.setStartDate(date);
                    event.setSubjectAgeAtEvent(ed.optInt("OpenClinica:SubjectAgeAtEvent", 0));
                    event.setStudyEventRepeatKey(ed.getInt("@StudyEventRepeatKey"));

                    results.add(event);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    //endregion

    //region ePRO

    public List<EventData> loadParticipateEvents(String studyOid, String studySubjectOid) {
        List<EventData> results = new ArrayList<EventData>();

        String method = "pages/odmk/study/" + studyOid + "/studysubject/" + studySubjectOid + "/events";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.restOcGet(method);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            // Clean away the non standard namespaces (cannot unmarshal these)
            output = output.replace("ns5:", "");

            InputStream xmlStream = new ByteArrayInputStream(output.getBytes());

            Odm odm = null;
            try {
                JAXBContext context = JAXBContext.newInstance(Odm.class);
                Unmarshaller un = context.createUnmarshaller();
                odm = (Odm) un.unmarshal(xmlStream);
            }
            catch (Exception err) {
                err.printStackTrace();
            }

            // Extract results
            if (odm != null && odm.getClinicalDataList() != null && odm.getClinicalDataList().size() == 1) {

                ClinicalData cd = odm.getClinicalDataList().get(0);

                // Exactly one studySubject
                if (cd.getStudySubjects() != null && cd.getStudySubjects().size() == 1) {
                    results.addAll(cd.getStudySubjects().get(0).getStudyEventDataList());
                }
            }
        }

        return results;
    }

    //endregion

    //endregion

    //endregion

    //region Private methods

    private Study convertRpbStudyToOcStudy(de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy) {
        Study converterStudy = null;

        // Complete hierarchical list of studies and sites
        List<StudyType> ocStudies = this.listAllStudies();

        // Extract collection of study sites from the complete list for each study
        List<de.dktk.dd.rpb.core.ocsoap.types.Study> studySites = new ArrayList<de.dktk.dd.rpb.core.ocsoap.types.Study>();

        for (StudyType ocStudy : ocStudies) {
            studySites.clear();

            if (ocStudy.getIdentifier().equals(rpbStudy.getOcStudyIdentifier())) {

                // Multi-centric study
                if (ocStudy.getSites() != null) {
                    for (SiteType ocStudySite : ocStudy.getSites().getSite()) {
                        de.dktk.dd.rpb.core.ocsoap.types.Study studySite = new de.dktk.dd.rpb.core.ocsoap.types.Study(ocStudy);
                        studySite.setSiteName(ocStudySite.getIdentifier());
                        studySite.setRealSiteName(ocStudySite.getName());
                        studySites.add(studySite);
                    }
                }
                // Mono-centric study
                else {
                    de.dktk.dd.rpb.core.ocsoap.types.Study study = new de.dktk.dd.rpb.core.ocsoap.types.Study(ocStudy);
                    study.setSiteName(study.getStudyIdentifier());
                    studySites.add(study);
                }

                break;
            }
        }

        // Active study/site
        for (de.dktk.dd.rpb.core.ocsoap.types.Study s : studySites) {
            if (s.getStudyIdentifier().equals(rpbStudy.getOcStudyIdentifier())) {
                converterStudy = s;
                break;
            }
        }

        return converterStudy;
    }

    @SuppressWarnings("unchecked")
    private ClientResponse getOcRestfulUrl(String format, String method) {
        Client client = Client.create();
        client.setFollowRedirects(true);

        MultivaluedMap loginFormData = new MultivaluedMapImpl();
        loginFormData.add(uname, UserContext.getUsername());
        loginFormData.add(passwd, UserContext.getClearPassword());

        WebResource webResource = client.resource(this.restBaseUrl + loginMethod);
        ClientResponse response = webResource
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, loginFormData);

        //TODO: very ugly find out other way of reading returned jsessionid
        String jsessionid = response.toString().replace("GET " + this.restBaseUrl + "MainMenu;jsessionid=", "").replace(" returned a response status of 200 OK", "");
        Cookie cookie = new Cookie("JSESSIONID", jsessionid);

        webResource = client.resource(this.restBaseUrl + "rest/" + CasebookDataType.CLINICALDATA.value + "/" + format + "/view/" + method);
        if (format.equals(CasebookFormat.JSON.value)) {
            response = webResource
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .cookie(cookie)
                    .get(ClientResponse.class);
        }
        else if (format.equals(CasebookFormat.XML.value)) {
            response = webResource
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .cookie(cookie)
                    .get(ClientResponse.class);
        }

        return response;
    }

    private ClientResponse restOcGet(String method) {
        Client client = Client.create();
        // TODO: use restBaseUrl in production
        WebResource webResource = client.resource("http://g40rpbtrials2.med.tu-dresden.de:8080/OpenClinica/" + method);
        return webResource
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);
    }

    //endregion

}
