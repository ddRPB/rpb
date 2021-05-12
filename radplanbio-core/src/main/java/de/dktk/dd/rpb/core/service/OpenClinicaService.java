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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.UserAccount;
import de.dktk.dd.rpb.core.ocsoap.connect.ConnectInfo;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.connect.OCWebServices;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.Event;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.util.CacheUtil;
import de.dktk.dd.rpb.core.util.Constants;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;
import org.openclinica.ws.data.v1.ImportResponse;
import org.openclinica.ws.event.v1.ScheduleResponse;
import org.openclinica.ws.study.v1.ListAllResponse;
import org.openclinica.ws.studysubject.v1.CreateResponse;
import org.openclinica.ws.studysubject.v1.IsStudySubjectResponse;
import org.openclinica.ws.studysubject.v1.ListAllByStudyResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenClinica service implements client calls to SOAP and REST based web-service endpoints provided by the EDC system
 *
 * It provides the possibility to cache the ws responses for situation where the amount of data that is within a response
 * causes big delays from usability perspective (e.g. study-0 subject list)
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

    //endregion

    //region Members

    private String ocUsername;
    private String ocPassword;
    private String restBaseUrl;

    private OCWebServices ocws;

    private Boolean cacheIsEnabled;

    private CacheUtil cacheUtil;

    //endregion

    //region Properties

    public Boolean isConnected() {
        return this.ocws != null;
    }

    public Boolean getCacheIsEnabled() {
        return this.cacheIsEnabled;
    }

    public void setCacheIsEnabled(Boolean value) {
        this.cacheIsEnabled = value;
    }

    public CacheUtil getCacheUtil() {
        if (this.cacheUtil == null) {
            this.cacheUtil = CacheUtil.getInstance();
        }

        return this.cacheUtil;
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
            this.cacheIsEnabled = Boolean.FALSE;
            this.ocUsername = username;
            this.ocPassword = password;

            ConnectInfo ci = new ConnectInfo(wsBaseUrl, this.ocUsername);
            ci.setPassword(this.ocPassword);
            this.ocws = OCWebServices.getInstance(
                    ci,
                    false, // logging
                    true // forceInstantiation, I am forcing because otherwise it is holding the old one
            );
            this.restBaseUrl = restBaseUrl;
        }
        catch (MalformedURLException | ParserConfigurationException | DatatypeConfigurationException err) {
            log.error(err);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectWithHash(String username, String passwordHash, String wsBaseUrl, String restBaseUrl) {
        try {
            this.cacheIsEnabled = Boolean.FALSE;
            this.ocUsername = username;

            ConnectInfo ci = new ConnectInfo(wsBaseUrl, this.ocUsername, passwordHash);
            this.ocws = OCWebServices.getInstance(
                    ci,
                    false, // logging
                    true // forceInstantiation, I am forcing because otherwise it is holding the old one
            );
            this.restBaseUrl = restBaseUrl;
        }
        catch (MalformedURLException | ParserConfigurationException | DatatypeConfigurationException err) {
            log.error(err);
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

                // Load study metadata from cache if available
                if (this.cacheIsEnabled) {

                    // Check if it is multi-centre or mono-centre study
                    String identifier = study.isMulticentric() ? study.getSiteName() : study.getStudyIdentifier();

                    String key = this.ocws.getBaseURL() + "studies/" + identifier + "/metadata";
                    Element element = this.getCacheUtil().getMetadataCacheElement(key);
                    if (element != null) {
                        metadata = (MetadataODM) element.getObjectValue();
                    }
                    else {
                        metadata = this.ocws.fetchStudyMetadata(study);
                        this.getCacheUtil().setMetadataCacheElement(new Element(key, metadata));
                    }
                }
                // Otherwise load from EDC server
                else {
                    metadata = this.ocws.fetchStudyMetadata(study);
                }
            }
        }
        catch (OCConnectorException err) {
            log.error(err);
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

                // Load study metadata from cache if available
                if (this.cacheIsEnabled) {

                    String key = this.ocws.getBaseURL() + "studies/" + identifier + "/metadata";
                    Element element = this.getCacheUtil().getMetadataCacheElement(key);
                    if (element != null) {
                        metadata = (MetadataODM) element.getObjectValue();
                    }
                    else {
                        metadata = this.ocws.fetchMetadataByIdentifier(identifier);
                        this.getCacheUtil().setMetadataCacheElement(new Element(key, metadata));
                    }
                }
                // Otherwise load from EDC server
                else {
                    metadata = this.ocws.fetchMetadataByIdentifier(identifier);
                }
            }
        }
        catch (OCConnectorException err) {
            log.error(err);
        }

        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshStudyMetadataCache(String identifier) {
        try {
            if (this.isConnected()) {

                // Refresh study metadata cache
                if (this.cacheIsEnabled) {

                    String key = this.ocws.getBaseURL() + "studies/" + identifier + "/metadata";
                    
                    MetadataODM metadata = this.ocws.fetchMetadataByIdentifier(identifier);
                    this.getCacheUtil().setMetadataCacheElement(new Element(key, metadata));
                }
            }
        }
        catch (OCConnectorException err) {
            log.error(err);
        }
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
        }
        catch (OCConnectorException err) {
            log.error(err);
        }

        return allStudies != null ? allStudies.getStudies().getStudy() : null;
    }

    //endregion

    //region Study Subject

    /**
     * {@inheritDoc}
     */
    @Override
    public String createNewStudySubject(StudySubject studySubject) throws OCConnectorException {
        String result = null;

        try {
            if (this.isConnected()) {
                CreateResponse response = this.ocws.createStudySubject(studySubject);
                if (response != null) {
                    if (response.getResult().equals(Constants.OC_SUCCESS)) {
                        if (response.getLabel() != null && !response.getLabel().equals(""))
                            result = response.getLabel();
                    }
                }
            }
        } catch (DatatypeConfigurationException err) {
            log.error(err);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean studySubjectExistsInStudy(StudySubject studySubject, Study study) {
        Boolean result = Boolean.FALSE;

        if (studySubject.getStudy() == null || !studySubject.getStudy().equals(study)) {
            studySubject.setStudy(study);
        }

        try {
            // False because I am not going to provide enrolment date
            IsStudySubjectResponse response = this.ocws.isStudySubject(studySubject, Boolean.FALSE);
            if (response != null) {
                result = response.getResult().equals(Constants.OC_SUCCESS);
            }
        }
        catch (OCConnectorException err) {
            // For OC reported fail
            if (err.getMessage().contains(Constants.OC_FAIL)) {
                // For locked studies - OC returns wrong status
                // Workaround: consider study subject as existing for locked study
                if (err.getMessage().contains(Constants.OC_ERROR_WRONGSTATUS)) {
                    result = Boolean.TRUE;
                    log.info(err);
                }
                // Subject does not exists
                else {
                    log.warn(err);
                }
            }
            // Other error with unrecognised code
            else {
                log.error(err);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudySubjectWithEventsType> listAllStudySubjectsByStudy(Study study) throws OCConnectorException {

        ListAllByStudyResponse studySubjectsResponse;

        // For study-0 look whether there is cached response available
        if (this.cacheIsEnabled &&
            study.getStudyIdentifier().equals(Constants.study0Identifier)) {

            String key = this.ocws.getBaseURL() + "studies/" + study.getStudyIdentifier() + "/studysubjects";
            Element element = this.getCacheUtil().getSubjectsCacheElement(key);
            if (element != null) {
                studySubjectsResponse = (ListAllByStudyResponse) element.getObjectValue();
            }
            else {
                studySubjectsResponse = this.ocws.listAllByStudy(study);
                this.getCacheUtil().setSubjectsCacheElement(new Element(key, studySubjectsResponse));
            }
        }
        // Otherwise load from EDC server
        else {
            studySubjectsResponse = this.ocws.listAllByStudy(study);
        }

        log.info("EDC: listAllStudySubject: " + studySubjectsResponse.getResult());

        return studySubjectsResponse.getStudySubjects().getStudySubject();
    }

    //TODO: unused?, probably we can deprecate this method (using direct access to DB, that returns StudySubjects
    @Override
    public List<StudySubject> getSubjectsByStudy(de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy) throws OCConnectorException {

        List<StudySubject> subjectList = new ArrayList<>();

        Study ocStudy = this.convertRpbStudyToOcStudy(rpbStudy);

        for (StudySubjectWithEventsType sswe : this.listAllStudySubjectsByStudy(ocStudy)) {
            StudySubject ss = null;

            try {
                ss = new StudySubject(ocStudy, sswe);
            }
            catch (DatatypeConfigurationException err) {
                log.error(err);
            }

            // Assign events
            ArrayList<ScheduledEvent> scheduledEvents = new ArrayList<>();
            if (sswe.getEvents() != null) {
                for (EventType eventType : sswe.getEvents().getEvent()) {

                    ScheduledEvent newEvent = null;
                    try {
                        newEvent = new ScheduledEvent(eventType);
                    }
                    catch (DatatypeConfigurationException err) {
                        log.error(err);
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
        }
        catch (OCConnectorException | DatatypeConfigurationException err) {
            log.error(err);
        }

        return events;
    }

    @Override
    public String scheduleStudyEvent(StudySubject subject, ScheduledEvent event) throws OCConnectorException {
        ScheduleResponse response = this.ocws.scheduleEvent(subject, event);
        log.info(response.getResult());

        return response.getStudyEventOrdinal();
    }

    //endregion

    //region Data

    public void importData(String odmXmlData) throws OCConnectorException {
        if (this.isConnected()) {

            // When using OC SOAP ws the xml should start with ODM element as root
            String cleanOdmXmlData = odmXmlData.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
            cleanOdmXmlData = cleanOdmXmlData.replace(" xmlns:ns2=\"http://www.openclinica.org/ns/odm_ext_v130/v3.1\"", "");
            cleanOdmXmlData = cleanOdmXmlData.replace(" xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"", "");

            // Import
            ImportResponse response = this.ocws.importODM(cleanOdmXmlData);
            log.info(response.getResult());
        }
    }

    //endregion

    //endregion

    //region REST

    //region EDC User Account

    public UserAccount loginUserAccount(String username, String password) {
        UserAccount account = null;

        String method = "pages/accounts/login";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        Client client = Client.create();
        WebResource webResource = client.resource(this.restBaseUrl + method);

        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, body);

        int status = response.getStatus();
        if (status == 200) {
            String output = response.getEntity(String.class);

            try {
                JSONObject jsonObject = new JSONObject(output);

                account = new UserAccount();
                account.setUserName(jsonObject.getString("username"));
                account.setFirstName(jsonObject.getString("firstName"));
                account.setLastName(jsonObject.getString("lastName"));
                account.setPassword(jsonObject.getString("password"));
                account.setApiKey(jsonObject.optString("apiKey", ""));

                // TODO: implement parsing later when roles are needed
//                JSONArray rolesArray = jsonObject.getJSONArray("roles");
//                for (int i = 0; i < rolesArray.length(); i++) {
//                    JSONObject role = rolesArray.getJSONObject(i);
//
//
//
//                }

            }
            catch (JSONException err) {
                log.error(err);
            }
        }
        // 401 Bad Credentials
        else if (status == 401) {
            log.info("Bed credentials.");
        }

        return account;
    }

    public UserAccount createUserAccount() {
        UserAccount createdAccount = null;

        // TODO: to be implemented

        return createdAccount;
    }

    public UserAccount loadUserAccount() {
        UserAccount resultAccount = null;

        // TODO: to be implemented

        return resultAccount;
    }

    public UserAccount loadParticipantUserAccount() {
        UserAccount resultAccount = null;

        // TODO: to be implemented

        return resultAccount;
    }

    public UserAccount loadParticipantUserAccountByAccessCode() {
        UserAccount resultAccount = null;

        // TODO: to be implemented

        return resultAccount;
    }

    //endregion

    //region ODM

    /**
     * {@inheritDoc}
     */
    @Override
    public Odm getStudyCasebookOdm(CasebookFormat format, CasebookMethod method, String queryOdmXmlPath) {
        Odm odm = null;

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(
                this.ocUsername,
                this.ocPassword,
                format.value,
                queryOdmXmlPath
        );

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            try {
                // Unmarshall XML
                if (format == CasebookFormat.XML) {
                    JAXBContext context = JAXBContext.newInstance(Odm.class);
                    Unmarshaller un = context.createUnmarshaller();
                    StringReader reader = new StringReader(output);
                    odm = (Odm) un.unmarshal(reader);
                }
                else if (format == CasebookFormat.JSON) {
                    // TODO: how to fast unmarshall JSON
                }
            }
            catch (Exception err) {
                log.error(err);
            }
        }

        return odm;
    }

    //endregion

    //region Study Subject

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudySubject> getStudyCasebookSubjects(String studyOid) {
        List<StudySubject> results = new ArrayList<>();

        String method = studyOid + "/*/*/*";
        String format = "json";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(
                this.ocUsername,
                this.ocPassword,
                format,
                method
        );

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
            catch (JSONException err) {
                log.error(err);
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

        ClientResponse response = this.getOcRestfulUrl(
                this.ocUsername,
                this.ocPassword,
                format,
                method
        );

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
            catch (JSONException err) {
                log.error(err);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudyCasebookSubjects(CasebookFormat format, CasebookMethod method, String queryOdmXmlPath) {

        List<de.dktk.dd.rpb.core.domain.edc.StudySubject> results = new ArrayList<>();

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        ClientResponse response = this.getOcRestfulUrl(
                this.ocUsername,
                this.ocPassword,
                format.value,
                queryOdmXmlPath
        );

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
                log.error(err);
            }
        }

        return results;
    }

    //endregion

    //region ePRO

    public List<EventData> loadParticipateEvents(String studyOid, String studySubjectOid) {
        List<EventData> results = new ArrayList<>();

        String method = "pages/odmk/study/" + studyOid + "/studysubject/" + studySubjectOid + "/events";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        Client client = Client.create();
        WebResource webResource = client.resource(this.restBaseUrl + method);

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() == 200) {
            String output = response.getEntity(String.class);

            // TODO: this is ugly, it need to be fixed in OC or I have to find better way how to specify namespaces for unmarshalling
            // Change the namespaces to make unmarshalling to our ODM domain model possible
            // http://www.openclinica.org/ns/odm_ext_v130/v3.1-api -> http://www.openclinica.org/ns/odm_ext_v130/v3.1
            output = output.replace("ns6:", "ns2:");
            // Change the namespaces to make unmarshalling to our ODM domain model possible
            // http://www.cdisc.org/ns/odm/v1.3-api -> http://www.cdisc.org/ns/odm/v1.3
            output = output.replace("ns5:", "");

            InputStream xmlStream = new ByteArrayInputStream(output.getBytes());

            Odm odm = null;
            try {
                JAXBContext context = JAXBContext.newInstance(Odm.class);
                Unmarshaller un = context.createUnmarshaller();
                odm = (Odm) un.unmarshal(xmlStream);
            }
            catch (Exception err) {
                log.error(err);
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

    public String loadEditableUrl(String ocUrl, String returnUrl) {
        String result = "";

        //TODO: this is stupid check to fix the production, try to find better solution
        // If the editable is actually new Enketo form (data entry was started in EDC)
        if (ocUrl.indexOf(":9005/::") == -1) {

            Client client = Client.create();
            WebResource webResource = client.resource(ocUrl);

            ClientResponse response = webResource
                    .get(ClientResponse.class);

            // Accepted
            if (response.getStatus() == 202) {
                result = response.getEntity(String.class);
                result = result.replace("\"", "");

                // Adapt return URL to be URL of my participate
                // &returnUrl=http%3A%2F%2Fstudy1.mystudy.me%3A8080%2F%23%2Fevent%2FSS_SUB001%2Fdashboard&ecid=
                int startIndex = result.indexOf("&returnUrl");
                startIndex = startIndex + 11;
                int endIndex = result.indexOf("&ecid");
                String oldReturnUrlParameter = result.substring(startIndex, endIndex);
                result = result.replace(oldReturnUrlParameter, returnUrl);
            }
        }
        else {
            result = ocUrl;
        }

        return result;
    }

    public boolean completeParticipantEvent(String studySubjectIdentifier, String studyEventDefinitionOid, String eventRepeatKey, String apiKey) {
        String method = "pages/auth/api/v1/studyevent/studysubject/" + studySubjectIdentifier + "/studyevent/" + studyEventDefinitionOid + "/ordinal/" + eventRepeatKey + "/complete";

        if (!this.restBaseUrl.endsWith("/")) {
            this.restBaseUrl += "/";
        }

        Client client = Client.create();
        WebResource webResource = client.resource(this.restBaseUrl + method);

        ClientResponse response = webResource
                .header("api_key", apiKey)
                .put(ClientResponse.class);

        int status = response.getStatus();

        return status == 200 || status == 401;
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
        List<de.dktk.dd.rpb.core.ocsoap.types.Study> studySites = new ArrayList<>();

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
    private ClientResponse getOcRestfulUrl(String ocUsername, String ocClearPassword, String format, String method) {
        Client client = Client.create();
        client.setFollowRedirects(true);

        MultivaluedMap loginFormData = new MultivaluedMapImpl();
        if (ocUsername != null && !ocUsername.equals("")) {
            loginFormData.add(Constants.OC_UNAMEPARAM, ocUsername);
        }
        else {
            loginFormData.add(Constants.OC_UNAMEPARAM, UserContext.getUsername());
        }
        if (ocClearPassword != null && !ocClearPassword.equals("")) {
            loginFormData.add(Constants.OC_PASSWDPARAM, ocClearPassword);
        }
        else {
            loginFormData.add(Constants.OC_PASSWDPARAM, UserContext.getClearPassword());
        }

        WebResource webResource = client.resource(this.restBaseUrl + Constants.OC_LOGINMETHOD);
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

    //endregion

}