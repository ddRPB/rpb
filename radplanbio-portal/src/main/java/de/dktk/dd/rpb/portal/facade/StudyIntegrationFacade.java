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

package de.dktk.dd.rpb.portal.facade;

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.Subject;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;

import de.dktk.dd.rpb.core.service.DataTransformationService;

import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.portal.web.mb.MainBean;

import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;

import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private MainBean mainBean;
    private Boolean retreiveStudySubjectOID;

    //endregion

    //region Properties

    public Boolean getRetreiveStudySubjectOID() {
        return this.retreiveStudySubjectOID;
    }

    public void setRetreiveStudySubjectOID(Boolean value) {
        this.retreiveStudySubjectOID = value;
    }

    //endregion

    //region Setup

    public void init(MainBean mainBean) {
        this.mainBean = mainBean;
        this.retreiveStudySubjectOID = Boolean.TRUE;
    }

    //endregion

    //region Interface methods

    //region Study

    /**
     * Load RPB persistent study domain entity according to current active EDC study (transient)
     * @return RPB Study entity
     */
    public Study loadStudy() {
        Study rpbStudy;

        // Load RadPlanBio study according to active study obtained from OC
        if (this.mainBean.getActiveStudy().getParentStudy() != null) {
            rpbStudy = this.studyRepository.getByOcStudyIdentifier(this.mainBean.getActiveStudy().getParentStudy().getUniqueIdentifier());
        }
        else {
            rpbStudy = this.studyRepository.getByOcStudyIdentifier(this.mainBean.getActiveStudy().getUniqueIdentifier());
        }

        return rpbStudy;
    }

    /**
     * Load RPB persistent study domain entity extended about transtient EDC study (with metadata)
     * @return RPB Study entity
     */
    public Study loadStudyWithMetadata() {
        Study rpbStudy = this.loadStudy();
        Odm odm = this.getMetadataOdm();
        rpbStudy.setEdcStudy(
                odm.getStudyByOid(
                        this.mainBean.getActiveStudy().getOcoid()
                )
        );

        return rpbStudy;
    }

    //endregion

    //region Subject/Patient/StudySubject

    /**
     * Load RadPlanBio active study subjects (using multiple information sources)
     * @return List of Subject included in current user active study
     */
    public List<Subject> loadSubjects() throws OCConnectorException {

        List<Subject> subjects = new ArrayList<Subject>();

        // Create query study from the active study
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

        List<org.openclinica.ws.beans.StudySubjectWithEventsType> soapSubjects = this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(queryStudy);

        // StudySubjectOID is possible to retrieve only from RESTfull URL
        List<de.dktk.dd.rpb.core.ocsoap.types.StudySubject> restSubjects = null;
        if (this.retreiveStudySubjectOID) {
            restSubjects = this.mainBean.getOpenClinicaService().getStudyCasebookSubjects(this.mainBean.getActiveStudy().getOcoid());
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


    public List<StudySubject> loadStudySubjects() throws OCConnectorException {
        List<StudySubject> subjects = new ArrayList<StudySubject>();

        // Create query study from the active study
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

        List<org.openclinica.ws.beans.StudySubjectWithEventsType> soapSubjects = this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(queryStudy);

        // StudySubjectOID is possible to retrieve only from RESTfull URL
        List<de.dktk.dd.rpb.core.ocsoap.types.StudySubject> restSubjects = null;
        if (this.retreiveStudySubjectOID) {
            restSubjects = this.mainBean.getOpenClinicaService()
                    .getStudyCasebookSubjects(
                            this.mainBean.getActiveStudy().getOcoid()
                    );
        }

        for (org.openclinica.ws.beans.StudySubjectWithEventsType soapSubject : soapSubjects) {
            StudySubject subject = new StudySubject();
            subject.setStudySubjectId(soapSubject.getLabel());
            subject.setSecondaryId(soapSubject.getSecondaryLabel());

            if (soapSubject.getSubject() != null) {
                subject.setPid(soapSubject.getSubject().getUniqueIdentifier());
                subject.setSex(soapSubject.getSubject().getGender().value());
            }

            // Matching data about SOAP and REST subjects
            if (restSubjects != null) {
                for (de.dktk.dd.rpb.core.ocsoap.types.StudySubject restSubject : restSubjects) {
                    if (soapSubject.getLabel().equals(restSubject.getStudySubjectLabel())) {
                        subject.setSubjectKey(restSubject.getStudySubjectOID());
                    }
                }
            }

            subjects.add(subject);
        }

        return subjects;
    }

    public List<StudySubject> loadOdmStudySubjects() {
        String queryOdmXmlPath = this.mainBean.getActiveStudy().getOcoid() + "/*/*/*";

        List<StudySubject> result = this.mainBean.getOpenClinicaService().getStudyCasebookSubjects(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        return result;
    }

    public List<StudySubject> enrolSubjectsReturnFailed(List<StudySubject> subjects, StudyParameterConfiguration conf) {

        List<StudySubject> subjectFailedToImport = new ArrayList<StudySubject>();

        for (StudySubject ss : subjects) {
            de.dktk.dd.rpb.core.ocsoap.types.StudySubject wsSubject = this.createWsStudySubject(ss, conf);
            if (!this.mainBean.getOpenClinicaService().createNewStudySubject(wsSubject)) {
                subjectFailedToImport.add(ss);
            }
        }

        return subjectFailedToImport;
    }

    public Person fetchPatientStudySubjects(Person patient) {
        // Reset before fetch
        if (patient != null) {
            patient.getStudySubjects().clear();
        }

        if (this.mainBean.getRootOpenClinicaService() != null) {
            for (StudyType st : this.mainBean.getRootOpenClinicaService().listAllStudies()) {
                de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = new de.dktk.dd.rpb.core.ocsoap.types.Study(st);

                List<StudySubjectWithEventsType> sswets = null;
                try {
                    sswets = this.mainBean.getRootOpenClinicaService().listAllStudySubjectsByStudy(queryStudy);
                } catch (Exception err) {
                    err.printStackTrace();
                }

                // Both PID without and with partner site identifier
                String siteIdentifier = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();
                for (StudySubjectWithEventsType sswet : sswets) {
                    String pid;
                    if (sswet.getSubject().getUniqueIdentifier().contains(siteIdentifier) &&
                        sswet.getSubject().getUniqueIdentifier().startsWith(siteIdentifier)) {
                        pid = sswet.getSubject().getUniqueIdentifier().replace(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-", "");
                    }
                    else {
                        pid = sswet.getSubject().getUniqueIdentifier();
                    }

                    if (pid.equals(patient.getPid())) {
                        patient.getStudySubjects().add(
                            new StudySubject(sswet, st)
                        );

                        break;
                    }
                }
            }
        }

        return patient;
    }

    public Person fetchPatientDicomStudies(Person patient) {
        // Reset before fetch
        if (patient != null) {
            patient.getDicomStudies().clear();
        }

        if (this.mainBean.getPacsService() != null) {

            List<DicomStudy> dcmList = this.mainBean.getPacsService().loadPatientStudies(patient.getPid());
            if (dcmList != null) {
                for (DicomStudy dcm : dcmList) {
                    patient.getDicomStudies().add(
                            dcm
                    );
                }
            }
            dcmList= this.mainBean.getPacsService().loadPatientStudies(
                    this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-" + patient.getPid()
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

    //endregion

    //region Events

    public List<de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent> loadSubjectEvents(Subject rpbSubject) throws OCConnectorException {

        // Get the current user active study directly from open clinica database
        // Create query study from the active study
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

        // Event with OC RESTFull URL
        String studySubjectIdentifier = this.retreiveStudySubjectOID ? rpbSubject.getOid() : rpbSubject.getStudySubjectId();
        List<EventDefinition> restEvents =  this.mainBean.getOpenClinicaService()
                .getStudyCasebookEvents(
                        this.mainBean.getActiveStudy().getOcoid(),
                        studySubjectIdentifier
                );

        // Fetch the study metadata
        MetadataODM metadata = this.mainBean.getOpenClinicaService().getStudyMetadata(queryStudy);

        // Find me selected study subjects
        for (org.openclinica.ws.beans.StudySubjectWithEventsType ocWsStudySubject : this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(queryStudy)) {

            if (ocWsStudySubject.getLabel().equals(rpbSubject.getStudySubjectId())) {

                // And construct list of scheduled events
                ArrayList<de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent> scheduledEvents = new ArrayList<de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent>();
                if (ocWsStudySubject.getEvents() != null) {


                    for (org.openclinica.ws.beans.EventType eventType : ocWsStudySubject.getEvents().getEvent()) {

                        de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent newEvent = null;
                        try {
                            newEvent = new de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent(eventType);
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }

                        // Enhance with information from metadata
                        EventDefinition ed;
                        try {
                            ed = metadata.getStudyEventDef(newEvent.getEventOID());
                            newEvent.setEventName(ed.getName());
                            newEvent.setDescription(ed.getDescription());
                            newEvent.setCategory(ed.getCategory());
                            newEvent.setType(ed.getType());
                            newEvent.setIsRepeating(ed.getIsRepeating());
                        }
                        catch (Exception err) {
                            err.printStackTrace();
                        }

                        // Enhance information from REST json metadata
                        for (EventDefinition restEvent : restEvents) {
                            // Matching events (use REST info to enhance the event data)
                            // Depending on the name and date, time is not considered (multiple events in one day not supported for now)
                            if (newEvent.getEventOID().equals(restEvent.getOid()) &&
                                newEvent.getStartDate().getYear() == restEvent.getStartDate().getYear() &&
                                newEvent.getStartDate().getMonth() == restEvent.getStartDate().getMonth() &&
                                newEvent.getStartDate().getDay() == restEvent.getStartDate().getDay()) {

                                newEvent.setStatus(restEvent.getStatus());
                                newEvent.setStudyEventRepeatKey(restEvent.getStudyEventRepeatKey());
                            }
                        }

                        scheduledEvents.add(newEvent);
                    }

                    return scheduledEvents;
                }
            }
        }

        return null;
    }

    public void scheduleStudyEvents(List<StudySubject> studySubjects, Odm metadataOdm) {
        try {
            for (StudySubject ss : studySubjects) {

                // Subjects could be excluded from schedule events job
                if (ss.getIsEnabled()) {
                    for (EventDefinition ed : metadataOdm.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions()) {

                        //TODO: if the event is repeating, it makes sense to check how many repeats are present in study subject data and schedule it as many times as necessary

                        // Event
                        ScheduledEvent newEvent = new ScheduledEvent();
                        newEvent.setEventOID(ed.getOid());

                        // Scheduling date
                        GregorianCalendar c = new GregorianCalendar();
                        c.setTime(new Date());
                        XMLGregorianCalendar dateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                        newEvent.setStartDate(dateXML);

                        de.dktk.dd.rpb.core.ocsoap.types.StudySubject subject = this.createWsStudySubject(ss, metadataOdm.getStudyDetails().getStudyParameterConfiguration());

                        // Schedule
                        this.mainBean.getOpenClinicaService().scheduleStudyEvent(subject, newEvent);
                    }
                }
            }
        }
        catch (Exception err) {
            // NOOP
        }
    }

    //endregion

    //region Metadata

    public Odm getMetadataOdm() {

        Odm result = null;

        if (this.mainBean.getActiveStudy() != null) {

            // Create query study from the active study
            de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

            // Fetch the study metadata
            MetadataODM metadata = this.mainBean.getOpenClinicaService().getStudyMetadata(queryStudy);

            // XML to DomainObjects
            result = metadata.unmarshallOdm();
            result.updateHierarchy();
        }

        return result;
    }

    //endregion

    //region Data

    public void importData(Odm odmData, Integer subjectsPerOdm) throws OCConnectorException {
        if (odmData != null) {

            odmData.cleanAttributesUnnecessaryForImport();
            List<Odm> odmList =  odmData.splitToList(subjectsPerOdm);

            for (Odm odm : odmList) {
                String odmString = this.svcDataTransformation.transformOdmToString(odm);
                this.mainBean.getOpenClinicaService().importData(odmString);
            }
        }
    }

    public List<File> createImportFiles(Odm odmData, Integer subjectsPerOdm) {
        List<File> results = new ArrayList<File>();

        if (odmData != null) {

            odmData.cleanAttributesUnnecessaryForImport();
            List<Odm> odmList =  odmData.splitToList(subjectsPerOdm);

            int i = 0;
            for (Odm odm: odmList) {
                File file = this.svcDataTransformation.transformOdmToXmlFile(odm, String.valueOf(i) + ".xml");
                results.add(file);
                i++;
            }
        }

        return results;
    }

    //endregion

    //region Fields

    public List<ItemDefinition> getDicomStudyFields(String eventOid, List<ItemDefinition> dicomFieldOids) {
        List<ItemDefinition> items = new ArrayList<ItemDefinition>();

        // Create query study from the active study
        de.dktk.dd.rpb.core.ocsoap.types.Study queryStudy = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());

        // Fetch the study metadata
        MetadataODM metadata = this.mainBean.getOpenClinicaService().getStudyMetadata(queryStudy);

        // Fetch DICOM specific eCRF fields according to annotation
        for (ItemDefinition itemOid : dicomFieldOids) {
            ItemDefinition item;
            try {
                item = metadata.getStudyItemDef(itemOid.getOid());
                if (item != null) {
                    item.setValue(itemOid.getValue());
                    items.add(item);
                }
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }

        return items;
    }

    /**
     * Get all item definition withing study
     * @return all item definitions withing study
     */
    public List<ItemDefinition> getAllFields() {
        List<ItemDefinition> items = new ArrayList<ItemDefinition>();

        Odm odm = this.getMetadataOdm();

        // Collect items from all events and forms
        if (odm != null) {
            for (EventDefinition e : odm.getStudyByOid(this.mainBean.getActiveStudy().getOcoid()).getMetaDataVersion().getStudyEventDefinitions()) {
                for (FormDefinition f : e.getFormDefs()) {
                    for (ItemGroupDefinition ig : f.getItemGroupDefs()) {
                        items.addAll(ig.getItemDefs());
                    }
                }
            }
        }

        return items;
    }

    //endregion

    //endregion

    //region Private methods

    private de.dktk.dd.rpb.core.ocsoap.types.Study createWebServicesQueryStudy(de.dktk.dd.rpb.core.domain.edc.Study ocDbActiveStudy) {

        String ocActiveStudyIdentifier;

        if (ocDbActiveStudy.getParentStudy() != null) {
            ocActiveStudyIdentifier = ocDbActiveStudy.getParentStudy().getUniqueIdentifier();
        }
        else {
            ocActiveStudyIdentifier = ocDbActiveStudy.getUniqueIdentifier();
        }

        // Complete hierarchical list of studies and sites
        List<org.openclinica.ws.beans.StudyType> ocStudies = this.mainBean.getOpenClinicaService().listAllStudies();

        for (org.openclinica.ws.beans.StudyType wsOcStudy : ocStudies) {

            if (wsOcStudy.getIdentifier().equals(ocActiveStudyIdentifier)) {

                // Multi-centric study
                if (wsOcStudy.getSites() != null) {
                    for (org.openclinica.ws.beans.SiteType wsOcStudySite : wsOcStudy.getSites().getSite()) {

                        if (wsOcStudySite.getIdentifier().equals(ocDbActiveStudy.getUniqueIdentifier())) {
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

    private de.dktk.dd.rpb.core.ocsoap.types.StudySubject createWsStudySubject(StudySubject odmSubjectData, StudyParameterConfiguration conf) {

        de.dktk.dd.rpb.core.ocsoap.types.StudySubject studySubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject();
        de.dktk.dd.rpb.core.ocsoap.types.Study study = this.createWebServicesQueryStudy(this.mainBean.getActiveStudy());
        studySubject.setStudy(study);

        try {
            // Whether StudySubjectId is required depends on the study configuration
            if (conf.getStudySubjectIdGeneration() != null) {
                if (conf.getStudySubjectIdGeneration() == StudyParameterConfiguration.StudySubjectIdGeneration.auto) {
                    studySubject.setStudySubjectLabel(""); // Event if auto I have to provide at least empty string
                }
                else if (conf.getStudySubjectIdGeneration() == StudyParameterConfiguration.StudySubjectIdGeneration.manual) {
                    if (odmSubjectData.getStudySubjectId() != null) {
                        studySubject.setStudySubjectLabel(odmSubjectData.getStudySubjectId());
                    }
                    //else {
                        // missing StudySubjectId, throw exception
                    //}
                }
            }
            else {
                studySubject.setStudySubjectLabel(""); // Could not read this information from parameters, use empty string because something is required
            }

            // When study is configured to save PID, make sure that there is a PID
            if (conf.getPersonIdRequired() != null) {
                if (conf.getPersonIdRequired() != StudyParameterConfiguration.RON.not_used) {
                    if (odmSubjectData.getPid() != null) {
                        // Use PartnerSite identifier as a prefix in case of multi-centre study
                        if (study.isMulticentric()) {
                            studySubject.setPersonID(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-" + odmSubjectData.getPid());
                        }
                        else {
                            studySubject.setPersonID(odmSubjectData.getPid()); // Use just PID for mono-centric study
                        }
                    }
                }
            }

            // Gender is always required (there is a bug in OC web service)
            studySubject.setSex(odmSubjectData.getSex());

            // Calendar
            GregorianCalendar c = new GregorianCalendar();

            // Whether day of birth will be collected depends on the study configuration
            if (conf.getCollectSubjectDayOfBirth()) {
               if (odmSubjectData.getDateOfBirth() != null) {
                   DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
                   Date date = format.parse(odmSubjectData.getDateOfBirth());
                   c.setTime(date);
                   XMLGregorianCalendar birthdateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                   studySubject.setDateOfBirth(birthdateXML);
               }
            }

            // Always collect enrollment date for subject (the current date)
            c.setTime(new Date());
            XMLGregorianCalendar enrollmentdateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            studySubject.setDateOfRegistration(enrollmentdateXML);
        }
        catch (Exception err) {
            // NOOP
        }

        return studySubject;
    }

    //endregion

}
