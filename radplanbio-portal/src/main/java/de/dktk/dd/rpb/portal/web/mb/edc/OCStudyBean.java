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

package de.dktk.dd.rpb.portal.web.mb.edc;

import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;

import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.Event;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;

import org.json.JSONObject;
import org.openclinica.ws.beans.*;

import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;
import org.springframework.context.annotation.Scope;

/**
 * OpenClinica Study Managed Bean
 *
 * This is a ViewModel for ecrfStudies.faces View
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
@Named("mbStudy")
@Scope("view")
public class OCStudyBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    /**
     * Set MainBean
     *
     * @param bean MainBean
     */
    @SuppressWarnings("unused")
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
    }

    //endregion

    //region Study repository

    @Inject
    private IStudyRepository studyRepository;

    /**
     * Set StudyRepository
     * @param repository StudyRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyRepository repository) {
        this.studyRepository = repository;
    }

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //region AuditService

    @Inject
    private AuditLogService auditLogService;

    @SuppressWarnings("unused")
    public void setAuditLogService(AuditLogService svc) {
        this.auditLogService = svc;
    }

    //endregion

    //endregion

    //region Members

    protected List<Boolean> subjectsColumnVisibilityList;
    protected List<Boolean> eventsColumnVisibilityList;

    private List<SortMeta> sitesPreSortOrder;
    private List<SortMeta> subjectsPreSortOrder;
    private List<SortMeta> eventsPreSortOrder;

    private de.dktk.dd.rpb.core.domain.ctms.Study radPlanBioStudy;
    private StudyParameterConfiguration studyConfiguration;

    private List<StudyType> studyTypeList;
    private String selectedStudyName;

    private List<Study> studyList;
    private List<Study> filteredStudies;
    private Study selectedStudy;

    private List<StudySubject> studySubjectsList;
    private List<StudySubject> filteredStudySubjects;
    private StudySubject selectedStudySubject;

    private List<ScheduledEvent> filteredScheduledEvents;
    private ScheduledEvent selectedScheduledEvent;

    private Subject newSubject;
    private Subject selectedSubject;

    private static Map<String, Object> genderValues;
    private boolean isSure;
    private boolean usePidGenerator;

    private List<Event> eventList;
    private String selectedEvent;
    private Date newEventDate;
    private ScheduledEvent newEvent;

    //endregion

    //region Properties

    //region StudyTypeList

    /**
     * Get Study Type List
     *
     * @return List - Study Type List
     */
    public List<StudyType> getStudyTypeList() {
        // Use lambda exp after upgrade to Java8 to sort that stuff
        //Collections.sort(this.studyTypeList, (s1, s2) -> s1.getName().compareTo(s2.getName()));

        if (this.studyTypeList != null) {
            Collections.sort(this.studyTypeList, new Comparator<StudyType>() {
                public int compare(StudyType s1, StudyType s2) {
                    String name1 = s1.getName();
                    String name2 = s2.getName();

                    return (name1.compareToIgnoreCase(name2));
                }
            });
        }

        return this.studyTypeList;
    }

    /**
     * Set Study Type List
     *
     * @param value - Study Type List
     */
    public void setStudyTypeList(List<StudyType> value) {
        this.studyTypeList = value;
    }

    //endregion

    //region SelectedStudyName

    public String getSelectedStudyName() {
        return this.selectedStudyName;
    }

    public void setSelectedStudyName(String value) {
        this.selectedStudyName = value;
    }

    //endregion

    //region StudySite DataTable Properties

    //region StudyList Property

    public List<Study> getStudyList() {
        return this.studyList;
    }

    public void setStudyList(List<Study> studyList) {
        this.studyList = studyList;
    }

    //endregion

    //region FilteredStudies Property

    public List<Study> getFilteredStudies() {
        return this.filteredStudies;
    }

    public void setFilteredStudies(List<Study> value) {
        this.filteredStudies = value;
    }

    //endregion

    //region SelectedStudy Property

    public Study getSelectedStudy() {
        return this.selectedStudy;
    }

    public void setSelectedStudy(Study selectedStudy) {
        this.selectedStudy = selectedStudy;
    }

    //endregion

    //region PreSortOrder

    public List<SortMeta> getSitesPreSortOrder() {
        return this.sitesPreSortOrder;
    }

    public void setSitesPreSortOrder(List<SortMeta> sortOrder) {
        this.sitesPreSortOrder = sortOrder;
    }

    //endregion

    //endregion

    //region StudySubject DataTable Properties

    //region StudySubjectsList Property

    public List<StudySubject> getStudySubjectsList() {
        return this.studySubjectsList;
    }

    public void setStudySubjectsList(List<StudySubject> studySubjectsList) {
        this.studySubjectsList = studySubjectsList;
    }

    //endregion

    //region FilteredStudySubjects Property

    public List<StudySubject> getFilteredStudySubjects() {
        return this.filteredStudySubjects;
    }

    public void setFilteredStudySubjects(List<StudySubject> filteredStudySubjects) {
        this.filteredStudySubjects = filteredStudySubjects;
    }

    //endregion

    //region SelectedStudySubject Property

    public StudySubject getSelectedStudySubject() {
        return this.selectedStudySubject;
    }

    public void setSelectedStudySubject(StudySubject selectedStudySubject) {
        this.selectedStudySubject = selectedStudySubject;

        this.selectedStudySubjectChanged();
    }

    //endregion

    //region PreSortOrder

    public List<SortMeta> getSubjectsPreSortOrder() {
        return this.subjectsPreSortOrder;
    }

    public void setSubjectsPreSortOrder(List<SortMeta> sortOrder) {
        this.subjectsPreSortOrder = sortOrder;
    }

    //endregion

    //endregion

    //region SubjectsColumnVisibilityList

    public List<Boolean> getSubjectColumnVisibilityList() {
        return this.subjectsColumnVisibilityList;
    }

    public void setSubjectColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.subjectsColumnVisibilityList = columnVisibilityList;
    }

    //endregion

    //region ScheduledEvent DataTable Properties

    //region Filtered Scheduled Events Property

    public List<ScheduledEvent> getFilteredScheduledEvents() {
        return this.filteredScheduledEvents;
    }

    public void setFilteredScheduledEvents(List<ScheduledEvent> value) {
        this.filteredScheduledEvents = value;
    }

    //endregion

    //region SelectedScheduledEvent Property

    public ScheduledEvent getSelectedScheduledEvent() {
        return this.selectedScheduledEvent;
    }

    public void setSelectedScheduledEvent(ScheduledEvent value) {
        this.selectedScheduledEvent = value;
    }

    //endregion

    //region PreSortOrder

    public List<SortMeta> getEventsPreSortOrder() {
        return this.eventsPreSortOrder;
    }

    public void setEventsPreSortOrder(List<SortMeta> sortOrder) {
        this.eventsPreSortOrder = sortOrder;
    }

    //endregion

    //endregion

    //region RadPlanBio Study

    public de.dktk.dd.rpb.core.domain.ctms.Study getRadPlanBioStudy() {
        return this.radPlanBioStudy;
    }

    //endregion

    //region Study Parameter Configuration

    public StudyParameterConfiguration getStudyConfiguration() {
        return this.studyConfiguration;
    }

    //endregion

    //region New Subject

    public Subject getNewSubject() {
        return this.newSubject;
    }

    public void setNewSubject(Subject value) {
        this.newSubject = value;
    }

    //endregion

    //region SelectedSubject

    public Subject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(Subject value) {
        this.selectedSubject = value;
    }


    //endregion

    //region Gender Values

    static {
        genderValues = new LinkedHashMap<String,Object>();
        genderValues.put("Male", "m"); //label, value
        genderValues.put("Female", "f");
    }

    public Map<String,Object> getGenderValues() {
        return genderValues;
    }

    //endregion

    //region IsSure

    public boolean getIsSure() {
        return this.isSure;
    }

    public void setIsSure(boolean value) {
        this.isSure = value;
    }

    //endregion

    //region UsePidGenerator

    public boolean getUsePidGenerator() {
        return this.usePidGenerator;
    }

    public void setUsePidGenerator(boolean value) {
        this.usePidGenerator = value;
    }

    //endregion

    //region CanRe-IdentifyPatient

    public boolean getCanDepseudonymisePatient() {
        if (this.selectedStudy != null && this.selectedStudy.isMulticentric()) {
            return this.selectedStudy.getSiteName().contains(this.mainBean.getMyAccount().getPartnerSite().getIdentifier()) &&
                    this.selectedStudy.getSiteName().startsWith(this.mainBean.getMyAccount().getPartnerSite().getIdentifier());
        }

        return true;
    }

    //endregion

    //region EventList

    public List<Event> getEventList() {
        return this.eventList;
    }

    public void setEventList(List<Event> events) {
        this.eventList = events;
    }

    //endregion

    //region SelectedEvent

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String event) {
        this.selectedEvent = event;
    }

    //endregion

    //region NewEventDate

    public Date getNewEventDate() {
        return this.newEventDate;
    }

    public void setNewEventDate(Date value) {
        this.newEventDate = value;
    }

    //endregion

    //region NewEvent

    @SuppressWarnings("unused")
    public ScheduledEvent getNewEvent() {
        return this.newEvent;
    }

    @SuppressWarnings("unused")
    public void setScheduledEvent(ScheduledEvent value) {
        this.newEvent = value;
    }

    //endregion

    //region EventsColumnVisibilityList

    public List<Boolean> getEventsColumnVisibilityList() {
        return eventsColumnVisibilityList;
    }

    public void setEventsColumnVisibilityList(List<Boolean> eventsColumnVisibilityList) {
        this.eventsColumnVisibilityList = eventsColumnVisibilityList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        // Initialize new patient for dialog binding
        Person person = new Person();
        this.newSubject = new Subject();
        this.newSubject.setPerson(person);

        this.isSure = true;
        this.usePidGenerator = true;

        // Study configuration according to metadata
        this.studyConfiguration = new StudyParameterConfiguration();

        try {
            if (this.mainBean.getMyAccount().getPartnerSite() == null) {
                throw new Exception("Your account is not associated with any partner site.");
            }
            else if (this.mainBean.getMyAccount().getPartnerSite().getEdc() == null) {
                throw new Exception("Your partner site does not have associated OpenClinica EDC");
            }
            else if (this.mainBean.getMyAccount().getPartnerSite().getPid() == null) {
                throw new Exception("Your partner site does not have associated PID generator.");
            }

            if (!this.mainBean.getMyAccount().hasOpenClinicaAccount()) {
                throw new Exception("There is now OpenClinica user account for you.");
            }

            this.setSubjectColumnVisibilityList(
                    this.buildSubjectsVisibilityList()
            );
            this.setEventsColumnVisibilityList(
                    this.buildEventsVisibilityList()
            );

            this.setSitesPreSortOrder(
                    this.buildSitesSortOrder()
            );
            this.setSubjectsPreSortOrder(
                    this.buildSubjectsSortOrder()
            );
            this.setEventsPreSortOrder(
                    this.buildEventsSortOrder()
            );

            // Load initial data
            this.reloadStudies();
            this.reloadStudySites();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region EventHandlers

    public void onSubjectEntityToggle(ToggleEvent e) {
        try {
            this.subjectsColumnVisibilityList.set(((Integer) e.getData() - 1), e.getVisibility() == Visibility.VISIBLE);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void onEventEntityToggle(ToggleEvent e) {
        try {
            this.eventsColumnVisibilityList.set(((Integer) e.getData() - 1), e.getVisibility() == Visibility.VISIBLE);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Commands

    //region Study

    public void clearSelection() {
        this.setSelectedStudy(null);
        this.setSelectedSubject(null);
        this.setSelectedStudySubject(null);
        this.setStudySubjectsList(null);
        this.setSelectedScheduledEvent(null);
    }

    /**
     * Reload studies from OpenClinica
     * This fills the StudyType list (this represents the hierarchy study/sites)
     * It also automatically select active study of user
     */
    public void reloadStudies() {
        try {
            if (this.mainBean.getOpenClinicaService() != null) {
                // Load open clinica studies
                this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();

                // Automatically select active study/site from OC database
                if (this.mainBean.getActiveStudy() != null) {
                    for (StudyType st : this.getStudyTypeList()) {

                        // Multi-centric study
                        if (st.getSites() != null) {
                            for (SiteType site : st.getSites().getSite()) {
                                if (site.getIdentifier().equals(
                                        this.mainBean.getActiveStudy().getUniqueIdentifier())) {
                                    // Select the master study in combo box
                                    this.selectedStudyName = st.getName();
                                    return;
                                }
                            }
                        }
                        // Mono-centric study
                        else {
                            if (st.getIdentifier().equals(
                                    this.mainBean.getActiveStudy().getUniqueIdentifier())) {
                                // Select the master study in combo box
                                this.selectedStudyName = st.getName();
                                return;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Selected parent study changed
     */
    public void selectedParentStudyChanged() {
        this.reloadStudySites();
        this.clearSelection();

        // Automatically re-select a first site in a list
        if (this.studyList != null && this.studyList.size() > 0) {
            this.selectedStudy = this.studyList.get(0);
            //this.selectedStudySiteChanged();
        }
    }

    /**
     * Reload Study Sites depending on selected study name
     */
    public void reloadStudySites() {
        try {
            if (this.selectedStudyName != null) {

                for (StudyType st : this.studyTypeList) {
                    if (st.getName().equals(this.selectedStudyName)) {

                        // Representing collection of Study Sites
                        ArrayList<Study> tempStudyList = new ArrayList<Study>();

                        // Multi-centric study
                        if (st.getSites() != null) {
                            for (SiteType site : st.getSites().getSite()) {
                                Study studySite = new Study(st);
                                studySite.setSiteName(site.getIdentifier());
                                studySite.setRealSiteName(site.getName());
                                tempStudyList.add(studySite);
                            }
                        }
                        // Mono-centric study
                        else {
                            Study study = new Study(st);
                            study.setSiteName(study.getStudyIdentifier());
                            tempStudyList.add(study);
                        }

                       this.studyList = tempStudyList;
                    }
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Selected study site changed
     */
    public void selectedStudySiteChanged() {
        this.reloadStudySiteDetails();
        this.reloadStudySubjects();
    }

    /**
     * Load study configuration information of selected study/site from metadata
     */
    public void reloadStudySiteDetails() {
        try {
            // Always read the parent study metadata (site metadata does not always provide valid settings)
            MetadataODM parentStudyMetadata = this.mainBean.getOpenClinicaService().getStudyMetadata(this.selectedStudy.getStudyIdentifier());

            // Setup study configuration from metadata
            if (parentStudyMetadata != null) {
                this.studyConfiguration.setCollectSubjectDayOfBirth(
                        parentStudyMetadata.getCollectDobStudyParameter()
                );
                this.studyConfiguration.setSexRequired(
                        parentStudyMetadata.getCollectSexStudyParameter()
                );
                this.studyConfiguration.setAllowDiscrepancyManagement(
                        parentStudyMetadata.getAllowDiscrepancyManagementStudyParameter()
                );
                this.studyConfiguration.setStudySubjectIdGeneration(
                        parentStudyMetadata.getSubjectIdGenerationStudyParameter()
                );
                this.studyConfiguration.setPersonIdRequired(
                        parentStudyMetadata.getSubjectPersonIdRequiredStudyParameter()
                );
                this.studyConfiguration.setSecondaryLabelViewable(
                        parentStudyMetadata.getSecondaryLabelViewableStudyParameter()
                );
            }

            // Associate RadPlanBio study (study management data)
            this.loadRadPlanBioStudy();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load RadPlanBio study with study documents according to selected OC study
     */
    public void loadRadPlanBioStudy() {
        try {
            if (this.studyRepository != null && this.selectedStudy != null) {
                this.radPlanBioStudy = this.studyRepository.getByOcStudyIdentifier(
                        this.selectedStudy.getStudyIdentifier()
                );
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     *  Change active study in open clinica for current user
     */
    public void changeActiveStudy() {
        try {
            if (this.mainBean.getMyAccount().isLdapUser()) {
                this.mainBean.changeUserActiveStudy(
                        this.mainBean.getSvcWebApi()
                                .loadEdcStudy(
                                        this.selectedStudy.getSiteName()
                                )
                );
            }
            else {
                this.mainBean.changeUserActiveStudy(
                        this.mainBean.getSvcRpb()
                                .loadOCStudyByIdentifier(
                                        this.selectedStudy.getSiteName()
                                )
                );
            }
            this.mainBean.refreshActiveStudy();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    private List<SortMeta> buildSitesSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudies:colSiteIdentifier", "colSiteIdentifier", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<SortMeta>();
    }

    //endregion

    //region Study Subject

    /**
     * Create a new study subject in OC using web service
     */
    public void createNewStudySubject() {
        try {
            // Prepare StudySubject from Subject data
            StudySubject studySubject = new StudySubject();

            // Whether study subject id is provided depends on study configuration
            if (this.studyConfiguration.getStudySubjectIdGeneration() != null) {
                if (this.studyConfiguration.getStudySubjectIdGeneration() == StudyParameterConfiguration.StudySubjectIdGeneration.auto) {
                    studySubject.setStudySubjectLabel(""); // Event if auto I have to provide at least empty string
                }
                else if (this.studyConfiguration.getStudySubjectIdGeneration() == StudyParameterConfiguration.StudySubjectIdGeneration.manual) {
                 studySubject.setStudySubjectLabel(this.getNewSubject().getStudySubjectId());
                }
            }
            // Could not read this information from parameters, use empty string because something is required
            else {
                studySubject.setStudySubjectLabel("");
            }

            // When study is configured to save PID, make sure that there is a PID
            if (this.studyConfiguration.getPersonIdRequired() != StudyParameterConfiguration.RON.not_used) {
                if (this.getNewSubject().getUniqueIdentifier() == null) {
                    throw new Exception("Patient PID (pseudonym) should not be empty");
                }
                else if (this.getNewSubject().getUniqueIdentifier().equals("")) {
                    throw new Exception("Patient PID (pseudonym) should not be empty");
                }

                // Use partner site prefix and PID when to create cross site unique Person ID
                studySubject.setPersonID(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-" + this.getNewSubject().getUniqueIdentifier());
            }

            // If secondary ID is provided use it
            if (this.getNewSubject().getSecondaryId() != null && !this.getNewSubject().getSecondaryId().equals("")) {
                studySubject.setStudySubjectSecondaryLabel(this.getNewSubject().getSecondaryId());
            }

            if (this.selectedStudy != null) {
                studySubject.setStudy(this.selectedStudy);
            }
            else {
                throw new Exception("Study site has to be selected");
            }

            // Gender is always required (there is a bug in OC web service)
            studySubject.setSex(this.getNewSubject().getGender());

            // Calendar
            GregorianCalendar c = new GregorianCalendar();

            // Whether day of birth will be collected for subject depend on study configuration
            if (this.studyConfiguration.getCollectSubjectDayOfBirth()) {
                c.setTime(this.getNewSubject().getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                studySubject.setDateOfBirth(birthDateXML);
            }

            // Always collect enrollment date for subject
            studySubject.setDateOfRegistration(this.getNewSubject().getEnrollmentDate());

            boolean result = this.mainBean.getOpenClinicaService().createNewStudySubject(studySubject);
            if (result) {
                this.auditLogService.event(AuditEvent.EDCStudySubjectEnrollment, studySubject.getPersonID());
                this.messageUtil.infoText("Study subject: " + studySubject.getPersonID() + " successfully created.");
                this.reloadStudySubjects();
            }
            else {
                throw new Exception("Web service cannot create a new subject.");
            }

            // Prepare data structure for the new study subject object binding
            Person person = new Person();
            this.newSubject = new Subject();
            this.newSubject.setPerson(person);
            this.isSure = true;
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            messageUtil.error(err);
        }
    }

    /**
     * Reset new study subject attributes (also with generated PID withing Subject entity)
     */
    public void resetNewStudySubject() {
        if (this.newSubject != null) {
            this.newSubject.initDefaultValues();
        }

        RequestContext.getCurrentInstance().reset(":newSubjectForm");
    }

    /**
     * Reload study subject registered for selected study/site from OC using web service
     */
    public void reloadStudySubjects() {
        try {
            if (this.mainBean.getOpenClinicaService() != null) {
                // Study was selected
                if (this.selectedStudy != null) {
                    List<StudySubject> subjectList = new ArrayList<StudySubject>();

                    for (StudySubjectWithEventsType sswe : this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(this.selectedStudy)) {
                        StudySubject ss = new StudySubject(this.selectedStudy, sswe);

                        // Assign scheduled events
                        ArrayList<ScheduledEvent> scheduledEvents = new ArrayList<ScheduledEvent>();
                        if (sswe.getEvents() != null) {
                            for (EventType eventType : sswe.getEvents().getEvent()){
                                ScheduledEvent newEvent = new ScheduledEvent(eventType);
                                scheduledEvents.add(newEvent);
                            }

                            ss.setScheduledEvents(scheduledEvents);
                        }

                        subjectList.add(ss);
                    }

                    this.studySubjectsList = subjectList;
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Selected study subject changed
     */
    public void selectedStudySubjectChanged() {
        if (this.selectedStudySubject != null) {

            // Prepare subject object for UI data binding
            Subject s = new Subject();
            s.setUniqueIdentifier(this.selectedStudySubject.getPersonID());
            s.setGender(this.selectedStudySubject.getSex());
            this.selectedSubject = s;

            // Event available for scheduling
            this.reloadStudyEvents();
        }
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudySubjects:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<SortMeta>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    private List<Boolean> buildSubjectsVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // StudySubjectID
        results.add(Boolean.TRUE); // PID
        results.add(Boolean.FALSE); // Secondary ID
        results.add(Boolean.TRUE); // Gender
        results.add(Boolean.TRUE); // Enrollment date

        return results;
    }

    //endregion

    //region Person = Patient - identity

    /**
     * Use patient personal information to generate pseudonym PID
     */
    public void generateNewPid() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = this.mainBean.getSvcPid().newPatientToken(sessionId);

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("tokenId");
            }

            Person person = this.newSubject.getPerson();

            if (this.newSubject.getPerson().getIsSure()) {
                finalResult = this.mainBean.getSvcPid().createSurePatienJson(tokenId, person);
            } else {
                finalResult = this.mainBean.getSvcPid().createPatientJson(tokenId, person);
            }

            String newId;
            Boolean tentative;

            if (finalResult != null) {
                newId = finalResult.optString("newId");
                if (!newId.isEmpty()) {
                    tentative = finalResult.getBoolean("tentative");

                    this.auditLogService.event(AuditEvent.PIDCreation, newId);

                    this.messageUtil.infoText("PID generated value: " + newId + " tentative: " + tentative);
                }

                this.newSubject.setUniqueIdentifier(newId);
            }

            // Delete session if it exists (however session cleanup can be also automaticaly done by Mainzelliste)
            this.mainBean.getSvcPid().deleteSession(sessionId);
        }
        catch (Exception err) {
            this.messageUtil.error(err);

            // Unsure patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.newSubject.getPerson().setIsSure(Boolean.FALSE);
                this.setIsSure(Boolean.FALSE);
            }
        }
    }

    /**
     * Take selected subject PID and get back patient personal information
     * Depseudonymization
     */
    public void getPersonalInformation() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            // If there is a study site specific identifier in PID remove it and use clear PID to deanonymise patient
            String pid;
            if (this.selectedStudySubject.getPersonID().contains(this.mainBean.getMyAccount().getPartnerSite().getIdentifier()) &&
                    this.selectedStudySubject.getPersonID().startsWith(this.mainBean.getMyAccount().getPartnerSite().getIdentifier())) {
                pid = this.selectedStudySubject.getPersonID().replace(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-", "");
            }
            // Otherwise a clear PID  should be there, use this one
            else {
                pid = this.selectedStudySubject.getPersonID();
            }

            finalResult = this.mainBean.getSvcPid().readPatientToken(
                    sessionId,
                    pid
            );

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("id");
            }

            Person p = this.mainBean.getSvcPid().getPatient(tokenId);
            this.getSelectedSubject().setPerson(p);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);
            this.messageUtil.infoText("Patient idenity data for " + pid + " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Study Event

    public void reloadStudyEvents() {
        try {
            List<Event> eventDefinitions = null;
            this.eventList = new ArrayList<Event>();

            // Load event definitions according to study setup
            if (this.mainBean.getOpenClinicaService() != null && this.selectedStudy != null) {
                eventDefinitions = this.mainBean
                        .getOpenClinicaService()
                        .listAllEventDefinitionsByStudy(
                                this.selectedStudy
                        );
            }

            // Use only not scheduled events and scheduled repeating events
            if (eventDefinitions != null) {
                for (Event e : eventDefinitions) {
                    boolean isScheduled = false;

                    for (ScheduledEvent se : this.selectedStudySubject.getScheduledEvents()) {
                        if (se.getEventOID().equals(e.getEventOID())) {
                            isScheduled = true;
                            if (e.getIsRepeating()) {
                                this.eventList.add(e);
                            }
                            break;
                        }
                    }

                    if (!isScheduled){
                        this.eventList.add(e);
                    }
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Schedule new study event occurrence for selected study subject
     */
    public void scheduleNewStudyEvent() {
        try {
            // New scheduled event for EDC
            this.newEvent = new ScheduledEvent();

            // Event OID
            this.newEvent.setEventOID(this.selectedEvent);

            // Calendar
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(this.newEventDate);
            XMLGregorianCalendar dateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            newEvent.setStartDate(dateXML);

            // Schedule
            this.mainBean.getOpenClinicaService().scheduleStudyEvent(this.selectedStudySubject, this.newEvent);
            this.auditLogService.event(AuditEvent.EDCStudyEventScheduled, this.newEvent.getEventOID());

            // Reload scheduled events
            this.reloadStudySubjects();
            this.selectedStudySubjectChanged();

            // Reset new event
            this.newEventDate = null;
            this.selectedEvent = "";
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    private List<Boolean> buildEventsVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // Oid
        results.add(Boolean.TRUE); // Start

        return results;
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    private List<SortMeta> buildEventsSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtScheduledEvents:colEventOid", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<SortMeta>();
    }

    //endregion

    //endregion

}