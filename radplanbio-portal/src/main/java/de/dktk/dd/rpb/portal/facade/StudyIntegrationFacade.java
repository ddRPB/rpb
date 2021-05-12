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

package de.dktk.dd.rpb.portal.facade;

import de.dktk.dd.rpb.core.domain.bio.AbstractSpecimen;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.DataTransformationService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import org.apache.log4j.Logger;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This is a facade which tries to simplify the access to RadPlanBio study data
 * RadPlanBio need to integrate the study entity defined in RadPlanBio with study entity
 * obtained from OpenClinica via SOAP web service and study entity obtained from OpenClinica while
 * directly accessing database storage.
 *
 * @author tomas@skripcak.net
 * @since 18 Feb 2014
 */
@Named("studyIntegrationFacade")
public class StudyIntegrationFacade {

    //region Finals

    private static final Logger log = Logger.getLogger(StudyIntegrationFacade.class);

    //endregion

    //region Injects

    //region StudyRepository

    @Inject
    private IStudyRepository studyRepository;

    //endregion

    //region DataTransformation

    @Inject
    protected DataTransformationService svcDataTransformation;

    //endregion

    //endregion

    //region Members

    // TODO: should be removed in a future, bean is only available in web GUI and not in web services access
    private MainBean mainBean;

    // When set to true, OC restful services are executed which have performance impact
    private Boolean retrieveStudySubjectOID;
    // Provided (init) by facade user
    private IOpenClinicaService openClinicaService;
    // Provided (init) by facade client
    private IOpenClinicaDataRepository openClinicaDataRepository;

    //endregion

    //region Constructors

    public StudyIntegrationFacade() {
        // NOOP
    }

    //endregion

    //region Properties

    public void setRetrieveStudySubjectOID(Boolean value) {
        this.retrieveStudySubjectOID = value;
    }

    public IOpenClinicaService getOpenClinicaService() {

        if (this.mainBean != null) {
            return this.mainBean.getOpenClinicaService();
        } else if (this.openClinicaService != null) {
            return this.openClinicaService;
        }

        return null;
    }

    //endregion

    //region Setup

    /**
     * Initialise facade with main bean initialised RPB services
     * Web GUI access (using bean member)
     *
     * @param mainBean mainBean have necessary resources to user specific services within RPB
     */
    public void init(MainBean mainBean) {
        this.mainBean = mainBean;

        // Initialise study repository from mainBean when it was not initialised by injection
        if (this.studyRepository == null) {
            this.studyRepository = this.mainBean.getStudyRepository();
        }

        // Default settings
        this.retrieveStudySubjectOID = Boolean.TRUE;
    }

    public void init(IOpenClinicaService openClinicaService) {
        this.openClinicaService = openClinicaService;

        // Default settings
        this.retrieveStudySubjectOID = Boolean.FALSE;
    }

    public void init(IOpenClinicaDataRepository openClinicaDataRepository) {
        this.openClinicaDataRepository = openClinicaDataRepository;
    }

    //endregion

    //region Facade methods

    //region Study

    /**
     * Load RPB persistent study domain entity according to current active EDC study (transient)
     * Web GUI access (using bean member)
     *
     * @return RPB Study entity
     */
    public Study loadStudy() {
        Study rpbStudy = null;

        // Load RadPlanBio study according to the user active study obtained from OC
        if (this.mainBean.getActiveStudy() != null) {
            rpbStudy = this.studyRepository.getByOcStudyIdentifier(
                    this.mainBean
                            .getActiveStudy()
                            .getOcStudyUniqueIdentifier()
            );
        }

        return rpbStudy;
    }

    /**
     * Load RPB persistent study domain entity according provided EDC study identifier
     *
     * @param studyIdentifier EDC study identifier
     * @return RPB study entity
     */
    public Study loadStudyByIdentifier(String studyIdentifier) {
        return this.studyRepository.getByOcStudyIdentifier(studyIdentifier);
    }

    /**
     * Load RPB persistent study domain entity extended about transient EDC study (with metadata)
     * Web GUI access (using bean member)
     *
     * @return RPB Study entity
     */
    public Study loadStudyWithMetadata() {
        Study rpbStudy = this.loadStudy();

        if (rpbStudy != null) {
            Odm odm = this.getMetadataOdm();
            rpbStudy.setEdcStudy(
                    odm.findUniqueStudyOrNone(
                            this.mainBean
                                    .getActiveStudy()
                                    .getOcoid()
                    )
            );
        }

        return rpbStudy;
    }

    /**
     * Load RPB persistent study domain entity according provided EDC study identifier extended about transient EDC study (with metadata)
     * using the user account
     *
     * @param studyIdentifier EDC study identifier
     * @param siteIdentifier  EDC study site identifier
     * @return RPB Study entity
     */
    public Study loadStudyWithMetadataByIdentifier(String studyIdentifier, String siteIdentifier) {
        Study rpbStudy = this.loadStudyByIdentifier(studyIdentifier);

        Odm odm = this.getMetadataOdmByIdentifier(studyIdentifier, siteIdentifier);
        if (odm != null) {
            rpbStudy.setEdcStudy(odm.findFirstStudyOrNone());
        }

        return rpbStudy;
    }

    /**
     * Load RPB persistent study domain entity according provided EDC study identifier extended about transient EDC study (with metadata)
     * using the engine user account
     *
     * @param studyIdentifier EDC study identifier
     * @param siteIdentifier  EDC study site identifier
     * @return RPB Study entity
     */
    public Study loadStudyWithMetadataByIdentifierViaEngineUser(String studyIdentifier, String siteIdentifier) {
        Study rpbStudy = this.loadStudyByIdentifier(studyIdentifier);

        Odm odm = this.getMetadataOdmByIdentifierViaEngineUser(studyIdentifier, siteIdentifier);
        if (odm != null) {
            rpbStudy.setEdcStudy(odm.findFirstStudyOrNone());
        }

        return rpbStudy;
    }

    /**
     * Load RPB persistent study domain entities for all available EDC studies (transient)
     * Web GUI access (using bean member)
     *
     * @return List of RPB Study entities
     */
    public List<Study> loadStudies() {
        List<Study> rpbStudies = new ArrayList<>();

        // Load available EDC studies
        for (StudyType studyType : this.loadWebServicesStudies()) {

            // Find the corresponding RPB study
            Study rpbStudy = this.studyRepository.getByOcStudyIdentifier(
                    studyType.getIdentifier()
            );

            if (rpbStudy != null) {
                // Setup transient EDC study
                de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study(studyType);

                List<de.dktk.dd.rpb.core.domain.edc.Study> studySites = new ArrayList<>();

                // Multi-centre
                if (studyType.getSites() != null) {
                    for (SiteType siteType : studyType.getSites().getSite()) {
                        studySites.add(
                                new de.dktk.dd.rpb.core.domain.edc.Study(edcStudy, siteType)
                        );
                    }
                }
                // Mono-centre but keep depth of object graph for binding (create one abstract site)
                else {
                    studySites.add(edcStudy);
                }

                edcStudy.setStudySites(studySites);
                rpbStudy.setEdcStudy(edcStudy);

                rpbStudies.add(rpbStudy);
            }
        }

        return rpbStudies;
    }

    //endregion

    //region Subject/Patient/StudySubject

    /**
     * Load RadPlanBio active study subjects (using multiple information sources)
     *
     * @return List of Subject included in current user active study
     */
    public List<Subject> loadSubjects() throws OCConnectorException {

        List<Subject> subjects = new ArrayList<>();

        // Create query study from the active study
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

        List<org.openclinica.ws.beans.StudySubjectWithEventsType> soapSubjects = this.getOpenClinicaService().listAllStudySubjectsByStudy(queryStudy);

        // StudySubjectOID is possible to retrieve only from restful URL
        List<de.dktk.dd.rpb.core.ocsoap.types.StudySubject> restSubjects = null;
        if (this.retrieveStudySubjectOID) {
            restSubjects = this.getOpenClinicaService().getStudyCasebookSubjects(this.mainBean.getActiveStudy().getOcoid());
        }

        for (org.openclinica.ws.beans.StudySubjectWithEventsType soapSubject : soapSubjects) {
            Subject subject = new Subject();
            subject.setStudySubjectId(soapSubject.getLabel());
            subject.setSecondaryId(soapSubject.getSecondaryLabel());

            if (soapSubject.getSubject() != null) {
                subject.setUniqueIdentifier(soapSubject.getSubject().getUniqueIdentifier());
                subject.setGender(soapSubject.getSubject().getGender().value());
                subject.setEnrollmentDate(soapSubject.getEnrollmentDate());
            }

            // Matching data about SOAP and REST subjects
            if (restSubjects != null) {
                for (de.dktk.dd.rpb.core.ocsoap.types.StudySubject restSubject : restSubjects) {
                    if (soapSubject.getLabel().equals(restSubject.getStudySubjectLabel())) {
                        subject.setOid(restSubject.getStudySubjectOID());
                    }
                }
            }

            subjects.add(subject);
        }

        return subjects;
    }

    public StudySubject loadStudySubjectWithEvents(String studySubjectIdentifier) {
        return this.loadStudySubjectWithEvents(this.mainBean.getActiveStudy(), studySubjectIdentifier);
    }

    public StudySubject loadStudySubjectWithEvents(de.dktk.dd.rpb.core.domain.edc.Study edcStudy, String studySubjectIdentifier) {
        StudySubject result = null;

        // TODO: if we want to support fetch by parent study identifier, we need to implement different SQL
        // if active study in parent study (usually there are no patients enrolled on parent study lvl) -> empty list
        String studyUniqueIdentifier = "";
        if (edcStudy != null) {
            studyUniqueIdentifier = edcStudy.getUniqueIdentifier();
        }

        // Invoked from GUI context
        if (this.mainBean != null) {
            result = this.mainBean.getOpenClinicaDataRepository().getStudySubjectWithEvents(studyUniqueIdentifier, studySubjectIdentifier);
        }
        // Invoked from API context
        else if (this.openClinicaDataRepository != null) {
            result = this.openClinicaDataRepository.getStudySubjectWithEvents(studyUniqueIdentifier, studySubjectIdentifier);
        }

        return result;
    }

    public StudySubject loadStudySubjectWithEventsWithForms(String studySubjectIdentifier) {
        return this.loadStudySubjectWithEventsWithForms(this.mainBean.getActiveStudy(), studySubjectIdentifier);
    }

    public StudySubject loadStudySubjectWithEventsWithForms(de.dktk.dd.rpb.core.domain.edc.Study edcStudy, String studySubjectIdentifier) {
        StudySubject result = null;

        // TODO: if we want to support fetch by parent study identifier, we need to implement different SQL
        // if active study in parent study (usually there are no patients enrolled on parent study lvl) -> empty list
        String studyUniqueIdentifier = "";
        if (edcStudy != null) {
            studyUniqueIdentifier = edcStudy.getUniqueIdentifier();
        }

        // Invoked from GUI context
        if (this.mainBean != null) {
            result = this.mainBean.getOpenClinicaDataRepository().getStudySubjectWithEventsWithForms(studyUniqueIdentifier, studySubjectIdentifier);
        }
        // Invoked from API context
        else if (this.openClinicaDataRepository != null) {
            result = this.openClinicaDataRepository.getStudySubjectWithEventsWithForms(studyUniqueIdentifier, studySubjectIdentifier);
        }

        return result;
    }

    public List<StudySubject> loadStudySubjects() {
        return this.loadStudySubjects(this.mainBean.getActiveStudy());
    }

    /**
     * Load subjects of study zero if the study zero exists
     *
     * @return List of StudySubjects
     */
    public List<StudySubject> loadStudyZeroSubjects() {
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study();
        edcStudy.setUniqueIdentifier(Constants.study0Identifier);
        return this.loadStudySubjects(edcStudy);
    }

    public List<StudySubject> loadStudySubjects(de.dktk.dd.rpb.core.domain.edc.Study edcStudy) {

        List<StudySubject> results = new ArrayList<>();

        // TODO: if we want to support fetch by parent study identifier, we need to implement different SQL
        // if active study in parent study (usually there are no patients enrolled on parent study lvl) -> empty list
        String studyUniqueIdentifier = "";
        if (edcStudy != null) {
            studyUniqueIdentifier = edcStudy.getUniqueIdentifier();
        }

        // Invoked from GUI context
        if (this.mainBean != null) {
            results = this.mainBean.getOpenClinicaDataRepository().findStudySubjectsByStudy(studyUniqueIdentifier);
        }
        // Invoked from API context
        else if (this.openClinicaDataRepository != null) {
            results = this.openClinicaDataRepository.findStudySubjectsByStudy(studyUniqueIdentifier);
        }

        return results;
    }

    // TODO: this is used currently in Matrix beans, should be replaced with DB access eventually
    public List<StudySubject> loadOdmStudySubjects() {
        String queryOdmXmlPath = this.mainBean.getActiveStudy().getOcoid() + "/*/*/*";

        List<StudySubject> result = this.getOpenClinicaService().getStudyCasebookSubjects(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        return result;
    }

    public List<StudySubject> loadStudySubjectsWithEvents() {
        return this.loadStudySubjectsWithEvents(this.mainBean.getActiveStudy());
    }

    public List<StudySubject> loadStudySubjectsWithEvents(de.dktk.dd.rpb.core.domain.edc.Study edcStudy) {
        List<StudySubject> results = new ArrayList<>();

        // TODO: if we want to support fetch by parent study identifier, we need to implement different SQL
        // if active study in parent study (usually there are no patients enrolled on parent study lvl) -> empty list
        String studyUniqueIdentifier = "";
        if (edcStudy != null) {
            studyUniqueIdentifier = edcStudy.getUniqueIdentifier();
        }

        // Invoked from GUI context
        if (this.mainBean != null) {
            results = this.mainBean.getOpenClinicaDataRepository().findStudySubjectsWithEvents(studyUniqueIdentifier);
        }
        // Invoked from API context
        else if (this.openClinicaDataRepository != null) {
            results = this.openClinicaDataRepository.findStudySubjectsWithEvents(studyUniqueIdentifier);
        }

        return results;
    }

    public List<StudySubject> enrolSubjectsReturnFailed(List<StudySubject> subjects, StudyParameterConfiguration conf) throws OCConnectorException {
        String parentStudyUniqueIdentifier = getParentStudyUniqueStudyIdentifier(this.mainBean.getActiveStudy());
        String siteIdentifier = this.mainBean.getActiveStudy().getUniqueIdentifier();
        List<StudySubject> subjectFailedToImport = new ArrayList<>();

        for (StudySubject ss : subjects) {

            String ssid = null;
            try {
                ssid = createNewOpenClinicaStudySubject(parentStudyUniqueIdentifier, siteIdentifier, ss, conf);
            } catch (DatatypeConfigurationException e) {
                log.error(e);
            }
            if (ssid == null) {
                subjectFailedToImport.add(ss);
            }
        }

        return subjectFailedToImport;
    }

    /**
     * Creates a new study subject within the specific study in OpenClinica
     *
     * @param studyIdentifier             String unique identifier of the parent study
     * @param siteIdentifier              String unique identifier for the site specific study
     * @param studySubject                EDC StudySubject
     * @param studyParameterConfiguration EDC StudyParameterConfiguration to validate that the provided studySubject
     *                                    fulfills the criteria of overall the study configuration
     * @return StudySubjectId of the created subject or null
     */
    public String createNewOpenClinicaStudySubject(String studyIdentifier, String siteIdentifier, StudySubject studySubject, StudyParameterConfiguration studyParameterConfiguration) throws DatatypeConfigurationException, OCConnectorException {
        de.dktk.dd.rpb.core.ocsoap.types.Study ocsoapStudy = this.createWebServicesQueryStudyByIdentifiers(studyIdentifier, siteIdentifier);
        de.dktk.dd.rpb.core.ocsoap.types.StudySubject ocsopStudySubject = this.createWsStudySubject(studySubject, studyParameterConfiguration, ocsoapStudy);
        return this.getOpenClinicaService().createNewStudySubject(ocsopStudySubject);
    }

    public Person fetchPatientStudySubjects(Person patient) {

        if (patient != null) {

            // Clear assigned StudySubjects before fetching new
            if (patient.getStudySubjects() != null) {
                patient.getStudySubjects().clear();
            }

            // Only search in EDC studies where user has access to
            if (this.getOpenClinicaService() != null) {

                List<StudyType> studyTypeList = this.getOpenClinicaService().listAllStudies();
                if (studyTypeList != null) {

                    String pid = this.mainBean.constructMySubjectFullPid(patient.getPid());

                    // Fetch from DB - old approach used web services but that is not scaling properly
                    patient.getStudySubjects().addAll(
                            this.mainBean.getOpenClinicaDataRepository().findStudySubjectsByPseudonym(pid, studyTypeList)
                    );
                }
            }
        }

        // Person record enhanced about studySubjects data
        return patient;
    }

    public Person fetchPatientDicomStudies(Person patient) {
        // Reset before fetch
        if (patient != null) {
            patient.getDicomStudies().clear();
        }

        if (this.mainBean.getPacsService() != null) {

            List<DicomStudy> dcmList = this.mainBean.getPacsService().loadPatientStudies(
                    this.mainBean.constructMySubjectFullPid(patient.getPid())
            );
            if (dcmList != null) {
                for (DicomStudy dcm : dcmList) {
                    patient.getDicomStudies().add(
                            dcm
                    );
                }
            }
        }

        return patient;
    }

    public Person fetchPatientSpecimens(Person patient) {

        if (patient != null) {

            // Reset before fetch
            patient.getBioSpecimens().clear();

            // Biobank is activated
            if (this.mainBean.getSvcBio() != null) {

                // Patient from other sites are not in study-0
                String his = patient.findStudySubjectId(Constants.study0Identifier);
                if (!"".equals(his)) {

                    Person bioPatient = this.mainBean.getSvcBio().loadPatient(
                            Constants.CXX_HIS,
                            his.replace(Constants.HISprefix, "")
                    );
                    if (bioPatient != null) {
                        for (AbstractSpecimen s : bioPatient.getBioSpecimens()) {
                            patient.addBioSpecimen(s);
                        }
                    }
                }
                //TODO: load biospecimens for patients from other sites (base on what identifier?)
            }
        }

        return patient;
    }

    //endregion

    //region Events

    public List<EventData> loadStudySubjectEvents(String studyIdentifier, String studySubjectIdentifier) {

        List<EventData> result = new ArrayList<>();

        // Load ODM resource for selected study subject
        String queryOdmXmlPath = studyIdentifier + "/" + studySubjectIdentifier + "/*/*";
        Odm selectedStudySubjectOdm = this.getOpenClinicaService().getStudyCasebookOdm(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        // If study subject data exists
        if (selectedStudySubjectOdm != null) {

            // Create Definitions from References in metadata
            selectedStudySubjectOdm.updateHierarchy();

            // Find SubjectData from ODM
            StudySubject studySubject = selectedStudySubjectOdm.findUniqueStudySubjectOrNone(
                    studySubjectIdentifier
            );

            // Link metadata to selectedStudySubject clinical data
            if (studySubject != null) {
                studySubject.linkOdmDefinitions(selectedStudySubjectOdm);
                result = studySubject.getStudyEventDataList();
            }
        }

        return result;
    }

    public List<String> scheduleStudyEvents(List<StudySubject> studySubjects, Odm metadataOdm) {
        List<String> ocResponseList = new ArrayList<>();
        try {
            de.dktk.dd.rpb.core.ocsoap.types.Study study = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());
            for (StudySubject ss : studySubjects) {
                // Subjects could be excluded from schedule events job
                if (ss.getIsEnabled()) {
                    // Schedule only events that have data
                    for (EventData ed : ss.getEventOccurrences()) {
                        // WS event for scheduling
                        ScheduledEvent newEvent = new ScheduledEvent(ed);

                        // WS subject to associate the event with
                        de.dktk.dd.rpb.core.ocsoap.types.StudySubject subject = this.createWsStudySubject(ss, metadataOdm.getStudyDetails().getStudyParameterConfiguration(), study);

                        // Schedule
                        String ocResponse = this.getOpenClinicaService().scheduleStudyEvent(subject, newEvent);
                        ocResponseList.add(ocResponse);
                        log.debug("Scheduling event: " + ed.getStudyEventOid() + " with original RepeatKey: " + ed.getStudyEventRepeatKey() + " on OpenClinica with RepeatKey: " + ocResponse);
                    }
                }
            }
        } catch (Exception err) {
            log.error(err);
        }
        return ocResponseList;
    }

    //endregion

    //region Metadata

    /**
     * Get active study metadata as ODM domain resource via SOAP web services
     * Web GUI access (using bean member)
     *
     * @return ODM EDC study metadata domain entity
     */
    public Odm getMetadataOdm() {
        Odm result = null;

        if (this.mainBean.getActiveStudy() != null) {

            // Create query study from the active study
            de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());
            if (queryStudy != null) {
                // Fetch the study metadata
                MetadataODM metadata = this.getOpenClinicaService()
                        .getStudyMetadata(queryStudy);

                // XML to DomainObjects
                result = metadata.unmarshallOdm();
                result.updateHierarchy();
            }
        }

        return result;
    }

    /**
     * Get specified study metadata as ODM domain resource via SOAP web services using the user account
     *
     * @param studyIdentifier EDC study identifier
     * @param siteIdentifier  EDC study site identifier
     * @return ODM EDC study metadata domain entity
     */
    public Odm getMetadataOdmByIdentifier(String studyIdentifier, String siteIdentifier) {
        Odm result = null;

        // Query
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(studyIdentifier, siteIdentifier);

        // Fetch the study metadata
        MetadataODM metadataOdm = this.getOpenClinicaService().getStudyMetadata(queryStudy);
        if (metadataOdm != null) {

            // XML to DomainObjects
            result = metadataOdm.unmarshallOdm();
            result.updateHierarchy();
        } else {
            log.error("Facade: OC SOAP failed, studyIdentifier: " + studyIdentifier);
        }

        return result;
    }

    /**
     * Get specified study metadata as ODM domain resource via SOAP web services using the Engine user account
     *
     * @param studyIdentifier EDC study identifier
     * @param siteIdentifier  EDC study site identifier
     * @return ODM EDC study metadata domain entity
     */
    public Odm getMetadataOdmByIdentifierViaEngineUser(String studyIdentifier, String siteIdentifier) {
        Odm result = null;

        // Query
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(studyIdentifier, siteIdentifier);

        // Fetch the study metadata
        MetadataODM metadataOdm = this.mainBean.getEngineOpenClinicaService().getStudyMetadata(queryStudy);
        if (metadataOdm != null) {

            // XML to DomainObjects
            result = metadataOdm.unmarshallOdm();
            result.updateHierarchy();
        } else {
            log.error("Facade: OC SOAP failed, studyIdentifier: " + studyIdentifier);
        }

        return result;
    }

    //endregion

    //region Data

    public void importData(Odm odmData, Integer subjectsPerOdm) throws OCConnectorException {
        if (odmData != null) {

            odmData.cleanAttributesUnnecessaryForImport();
            List<Odm> odmList = odmData.splitToList(subjectsPerOdm);

            for (Odm odm : odmList) {
                String odmString = this.svcDataTransformation.transformOdmToString(odm);
                this.getOpenClinicaService().importData(odmString);
            }
        }
    }

    public List<File> createImportFiles(Odm odmData, Integer subjectsPerOdm) {
        List<File> results = new ArrayList<>();

        if (odmData != null) {

            odmData.cleanAttributesUnnecessaryForImport();
            List<Odm> odmList = odmData.splitToList(subjectsPerOdm);

            for (Odm odm : odmList) {

                String filename = "";
                if (odm.getClinicalDataList().get(0).getStudySubjects().size() == 1) {
                    filename = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getSubjectKey() + ".xml";
                } else if (odm.getClinicalDataList().get(0).getStudySubjects().size() > 1) {
                    filename = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getSubjectKey() +
                            "-" +
                            odm.getClinicalDataList().get(0).getStudySubjects().get(subjectsPerOdm - 1).getSubjectKey() + ".xml";
                }

                File file = this.svcDataTransformation.transformOdmToXmlFile(
                        odm,
                        filename
                );
                results.add(file);
            }
        }

        return results;
    }

    //endregion

    //endregion

    //region Private

    private List<StudyType> loadWebServicesStudies() {
        List<StudyType> studyTypeList = new ArrayList<>();

        // Load when EDC services are available
        if (this.getOpenClinicaService() != null) {
            studyTypeList = this.getOpenClinicaService().listAllStudies();
        }

        return studyTypeList;
    }

    private de.dktk.dd.rpb.core.ocsoap.types.Study createWebServicesQueryStudy(de.dktk.dd.rpb.core.domain.edc.Study ocDbActiveStudy) {
        String parentStudyUniqueIdentifier = getParentStudyUniqueStudyIdentifier(ocDbActiveStudy);
        String siteIdentifier = ocDbActiveStudy.getUniqueIdentifier();
        return createWebServicesQueryStudyByIdentifiers(parentStudyUniqueIdentifier, siteIdentifier);
    }

    private de.dktk.dd.rpb.core.ocsoap.types.Study createWebServicesQueryStudyByIdentifiers(String parentStudyUniqueIdentifier, String siteIdentifier) {

        // Complete hierarchical list of studies and sites
        List<org.openclinica.ws.beans.StudyType> ocStudies = this.getOpenClinicaService().listAllStudies();

        for (org.openclinica.ws.beans.StudyType wsOcStudy : ocStudies) {

            if (wsOcStudy.getIdentifier().equals(parentStudyUniqueIdentifier)) {

                // Multi-centric study
                if (wsOcStudy.getSites() != null) {
                    for (org.openclinica.ws.beans.SiteType wsOcStudySite : wsOcStudy.getSites().getSite()) {

                        if (wsOcStudySite.getIdentifier().equals(siteIdentifier)) {
                            de.dktk.dd.rpb.core.ocsoap.types.Study studySite = new de.dktk.dd.rpb.core.ocsoap.types.Study(wsOcStudy);

                            studySite.setSiteName(wsOcStudySite.getIdentifier());
                            studySite.setRealSiteName(wsOcStudySite.getName());

                            return studySite;
                        }
                    }
                    // Parent study is active
                    de.dktk.dd.rpb.core.ocsoap.types.Study study = new de.dktk.dd.rpb.core.ocsoap.types.Study(wsOcStudy);
                    study.setSiteName(study.getStudyIdentifier());

                    return study;
                }
                // Mono-centric study
                else {
                    de.dktk.dd.rpb.core.ocsoap.types.Study study = new de.dktk.dd.rpb.core.ocsoap.types.Study(wsOcStudy);
                    study.setSiteName(study.getStudyIdentifier());

                    return study;
                }
            }
        }

        return null;
    }

    /***
     * Finds the unique identifier for the parent study (multi centric approach) or returns the unique identifier if the
     * study is mono centric and has no parent
     *
     * @param ocDbActiveStudy EDC Study
     * @return String uniqueIdentifier
     */
    private String getParentStudyUniqueStudyIdentifier(de.dktk.dd.rpb.core.domain.edc.Study ocDbActiveStudy) {
        String ocActiveStudyIdentifier;
        if (ocDbActiveStudy.getParentStudy() != null) {
            ocActiveStudyIdentifier = ocDbActiveStudy.getParentStudy().getUniqueIdentifier();
        } else {
            ocActiveStudyIdentifier = ocDbActiveStudy.getUniqueIdentifier();
        }
        return ocActiveStudyIdentifier;
    }

    private de.dktk.dd.rpb.core.ocsoap.types.Study createWebServicesQueryStudy(String studyIdentifier, String siteIdentifier) {
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy;

        // Multi-centric study
        if (!studyIdentifier.equals(siteIdentifier)) {
            queryStudy = new de.dktk.dd.rpb.core.ocsoap.types.Study();
            queryStudy.setStudyIdentifier(studyIdentifier);
            queryStudy.setSiteName(siteIdentifier);
        }
        // Mono-centric study
        else {
            queryStudy = new de.dktk.dd.rpb.core.ocsoap.types.Study();
            queryStudy.setStudyIdentifier(studyIdentifier);
        }

        return queryStudy;
    }

    private de.dktk.dd.rpb.core.ocsoap.types.StudySubject createWsStudySubject(StudySubject odmSubjectData, StudyParameterConfiguration conf, de.dktk.dd.rpb.core.ocsoap.types.Study study) throws DatatypeConfigurationException {

        de.dktk.dd.rpb.core.ocsoap.types.StudySubject studySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject();
        studySubject.setStudy(study);

        try {
            // Whether StudySubjectId is required depends on the study configuration
            if (conf.getStudySubjectIdGeneration() != null) {
                if (conf.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.AUTO)) {
                    if (odmSubjectData.getStudySubjectId() != null && !odmSubjectData.getStudySubjectId().isEmpty()) {
                        studySubject.setStudySubjectLabel(odmSubjectData.getStudySubjectId());
                    } else {
                        studySubject.setStudySubjectLabel(""); // Event if auto I have to provide at least empty string
                    }
                } else if (conf.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.MANUAL)) {
                    if (odmSubjectData.getStudySubjectId() != null) {
                        studySubject.setStudySubjectLabel(odmSubjectData.getStudySubjectId());
                    }
                }
            } else {
                studySubject.setStudySubjectLabel(""); // Could not read this information from parameters, use empty string because something is required
            }

            // When study is configured to save PID, make sure that there is a PID
            if (conf.getPersonIdRequired() != null && !conf.getPersonIdRequired().equals(EnumRequired.NOT_USED)) {
                if (odmSubjectData.getPid() != null && !odmSubjectData.getPid().isEmpty()) {
                    studySubject.setPersonID(
                            this.mainBean.constructMySubjectFullPid(odmSubjectData.getPid())
                    );
                }
            }

            // If secondary ID is provided use it
            if (odmSubjectData.getSecondaryId() != null && !odmSubjectData.getSecondaryId().isEmpty()) {
                studySubject.setStudySubjectSecondaryLabel(odmSubjectData.getSecondaryId());
            }

            // Gender is always required (there is a bug in OC web service)
            studySubject.setSex(odmSubjectData.getSex());

            // Calendar
            GregorianCalendar c = new GregorianCalendar();

            // Whether day of birth will be collected depends on the study configuration
            if (conf.getCollectSubjectDob().equals(EnumCollectSubjectDob.YES)) {
                if (odmSubjectData.getPerson() != null && odmSubjectData.getPerson().getBirthdate() != null) {
                    c.setTime(odmSubjectData.getPerson().getBirthdate());
                    XMLGregorianCalendar birthdateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    studySubject.setDateOfBirth(birthdateXML);
                }
            } else if (conf.getCollectSubjectDob().equals(EnumCollectSubjectDob.ONLY_YEAR)) {
                if (odmSubjectData.getPerson() != null && odmSubjectData.getPerson().getBirthdate() != null) {
                    c.setTime(odmSubjectData.getPerson().getBirthdate());
                    XMLGregorianCalendar birthdateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    studySubject.setYearOfBirth(new BigInteger(String.valueOf(birthdateXML.getYear())));
                } else {
                    if (odmSubjectData.getYearOfBirth() != null) {
                        studySubject.setYearOfBirth(new BigInteger(String.valueOf(odmSubjectData.getYearOfBirth())));
                    }
                }
            }

            if (odmSubjectData.getEnrollmentDate() != null) {
                c.setTime(odmSubjectData.getDateEnrollment());
            } else {
                // default is the current date)
                c.setTime(new Date());
            }
            XMLGregorianCalendar enrollmentdateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            studySubject.setDateOfRegistration(enrollmentdateXML);

        } catch (Exception err) {
            log.error("There was a problem creating the WsStudySubject object", err);
            throw err;
        }

        return studySubject;
    }

    //endregion

}
