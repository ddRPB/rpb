/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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
import java.math.BigInteger;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.ctms.Person;

import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;

import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.Event;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;

import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import de.dktk.dd.rpb.portal.web.util.TabBean;
import org.json.JSONObject;
import org.openclinica.ws.beans.*;

import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;
import org.primefaces.model.chart.*;
import org.springframework.context.annotation.Scope;

import org.apache.commons.lang.math.NumberUtils;

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

    private MainBean mainBean;
    private AuditLogService auditLogService;
    private MessageUtil messageUtil;

    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Members

    private TabBean tab = new TabBean();

    // GUI
    private List<Boolean> subjectsColumnVisibilityList;
    private List<Boolean> eventsColumnVisibilityList;

    private List<SortMeta> sitesPreSortOrder;
    private List<SortMeta> subjectsPreSortOrder;

    // RPB study
    private de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;
    private StudyParameterConfiguration studyConfiguration;

    // Parent study
    private List<StudyType> studyTypeList;
    private StudyType selectedStudyType;

    // StudySite
    private List<Study> studyList;
    private List<Study> filteredStudies;
    private Study selectedStudy;

    // StudySubject
    private List<StudySubject> studySubjectsList;
    private List<StudySubject> filteredStudySubjects;
    private StudySubject selectedStudySubject;

    //TODO: this should be the main study subject type used in this bean
    private de.dktk.dd.rpb.core.domain.edc.StudySubject rpbSelectedStudySubject;

    // Scheduled StudyEvent
    private ScheduledEvent selectedScheduledEvent;

    //TODO: this should be the main study event type used in this bean
    private EventData selectedEventData;

    // Subject
    private Subject newSubject;
    private Subject selectedSubject;

    private static Map<String, Object> genderValues;
    private boolean isSure;
    private boolean usePidGenerator;

    // StudyEvent
    private List<Event> eventList;
    private String selectedEvent;
    private Date newEventDate;
    private ScheduledEvent newEvent;
    private Integer originalEventRepeatKey;

    // QueryStrings
    private String studyParam;
    private String studySubjectParam;

    // Graphs
    private LineChartModel siteEnrolmentGraphModel;

    public LineChartModel getSiteEnrolmentGraphModel() {
        return this.siteEnrolmentGraphModel;
    }

    //endregion

    //region Constructors

    @Inject
    public OCStudyBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade, AuditLogService auditLogService, MessageUtil messageUtil) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
        this.messageUtil = messageUtil;
    }

    //endregion

    //region Properties

    //region Tab

    public TabBean getTab() {
        return this.tab;
    }

    //endregion

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

    //region SelectedStudyType

    public StudyType getSelectedStudyType() {
        return selectedStudyType;
    }

    public void setSelectedStudyType(StudyType selectedStudyType) {
        this.selectedStudyType = selectedStudyType;
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
    
    public de.dktk.dd.rpb.core.domain.edc.StudySubject getRpbSelectedStudySubject() {
        return rpbSelectedStudySubject;
    }

    public void setRpbSelectedStudySubject(de.dktk.dd.rpb.core.domain.edc.StudySubject rpbSelectedStudySubject) {
        this.rpbSelectedStudySubject = rpbSelectedStudySubject;
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

    //region StudyEvent
    
    //region SelectedEvent

    public ScheduledEvent getSelectedScheduledEvent() {
        return this.selectedScheduledEvent;
    }

    public void setSelectedScheduledEvent(ScheduledEvent value) {
        this.selectedScheduledEvent = value;
    }

    public EventData getSelectedEventData() {
        return this.selectedEventData;
    }

    public void setSelectedEventData(EventData selectedEventData) {
        this.selectedEventData = selectedEventData;
    }

    //endregion

    //region OriginalEventRepeatKey

    public Integer getOriginalEventRepeatKey() {
        return this.originalEventRepeatKey;
    }

    public void setOriginalEventRepeatKey(Integer originalEventRepeatKey) {
        this.originalEventRepeatKey = originalEventRepeatKey;
    }

    //endregion

    //endregion

    //region RadPlanBio Study

    public de.dktk.dd.rpb.core.domain.ctms.Study getRpbStudy() {
        return this.rpbStudy;
    }

    //endregion

    //region StudyParameterConfiguration

    public StudyParameterConfiguration getStudyConfiguration() {
        return this.studyConfiguration;
    }

    //endregion

    //region Subject

    public Subject getNewSubject() {
        return this.newSubject;
    }

    public void setNewSubject(Subject value) {
        this.newSubject = value;
    }

    public Subject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(Subject value) {
        this.selectedSubject = value;
    }


    //endregion

    //region GenderValues

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

    //region CanReIdentifyPatient

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

    //region StudyParam

    public String getStudyParam() {
        return studyParam;
    }

    public void setStudyParam(String studyParam) {
        this.studyParam = studyParam;
    }

    //endregion

    //region StudySubjectParam

    public String getStudySubjectParam() {
        return studySubjectParam;
    }

    public void setStudySubjectParam(String studySubjectParam) {
        this.studySubjectParam = studySubjectParam;
    }

    //endregion

    //endregion

    //region Init

    /**
     * Initialisation of the bean
     */
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

            // PID generator is not required
            else if (!this.mainBean.getMyAccount().getPartnerSite().hasEnabledPid()) {
                this.usePidGenerator = false;
            }

            if (!this.mainBean.getMyAccount().hasOpenClinicaAccount()) {
                throw new Exception("There is now OpenClinica user account for you.");
            }

            this.setSubjectColumnVisibilityList(
                    this.buildSubjectsVisibilityList()
            );
            this.setSitesPreSortOrder(
                    this.buildSitesSortOrder()
            );
            this.setSubjectsPreSortOrder(
                    this.buildSubjectsSortOrder()
            );
            
            // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
            this.studyIntegrationFacade.init(this.mainBean);
            this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

            // Load initial data
            this.reloadStudies();
            this.selectedParentStudyChanged();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * After initialisation, in case of pid query parameter present initialise the search
     */
    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (this.studyParam != null && this.studySubjectParam != null) {
                if (!"".equals(this.studyParam) && !"".equals(this.studySubjectParam)) {
                    this.selectedStudyType = this.studyTypeLookup(this.studyParam);
                    if (this.selectedStudyType != null) {
                        this.selectedParentStudyChanged();

                        this.tab.setActiveIndex(1);
                    }
                }
            }
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

    public void onStudySubjectIdChange() {
        // Only apply this pseudonym generation strategy when PID generator is not used and PID is required
        if (!this.usePidGenerator &&
            this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.REQUIRED)) {
            
            if (this.newSubject != null) {
                this.newSubject.generateUniqueIdentifier(
                        this.rpbStudy,
                        this.mainBean.getMyAccount().getPartnerSite()
                );
            }
        }
    }

    //endregion

    //region Commands

    //region Clear

    /**
     * Set all selected entities to null
     */
    public void clearSelection() {
        this.setSelectedStudy(null);
        this.setSelectedSubject(null);
        this.setSelectedStudySubject(null);
        this.setStudySubjectsList(null);
        this.setSelectedScheduledEvent(null);
    }

    //endregion

    //region Study

    private StudyType studyTypeLookup(String studyIdentifier) {
        if (studyTypeList != null) {
            for (StudyType st : this.studyTypeList) {
                // Multi-centric study
                if (st.getSites() != null) {
                    for (SiteType site : st.getSites().getSite()) {
                        if (site.getIdentifier().equals(studyIdentifier)) {
                            // Select the master study in combo box
                            return st;
                        }
                    }
                }

                // Try to mach parent study also for Mono-centric study
                if (st.getIdentifier().equals(studyIdentifier)) {
                    // Select the master study in combo box
                    return st;
                }
            }
        }

        return null;
    }

    /**
     * Reload studies from OpenClinica
     * This fills the StudyType list (this represents the hierarchy study/sites)
     * It also automatically select active study of user
     */
    public void reloadStudies() {
        try {
            // EDC services available
            if (this.mainBean.getOpenClinicaService() != null) {

                // Load open clinica studies
                this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();

                // Automatically select active study/site from OC database
                if (this.mainBean.getActiveStudy() != null) {
                    this.selectedStudyType = this.studyTypeLookup(this.mainBean.getActiveStudy().getUniqueIdentifier());
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Selected parent EDC study changed
     */
    public void selectedParentStudyChanged() {
        this.reloadStudySites();
        this.clearSelection();

        // Auto select site
        if (this.studyList != null && this.studyList.size() > 0) {
            // Mono-centre or multi-centre with non recruiting partner site (no study site)
            this.selectedStudy = this.studyList.get(0);
            // Multi-centre with recruiting site -> search for site according identifier
            if (this.studyList.size() > 1) {
                for (Study s : this.studyList) {
                    if (s.getSiteName().startsWith(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + Constants.RPB_IDENTIFIERSEP)) {
                        this.selectedStudy = s;
                        break;
                    }
                }
            }
        }

        // Load corresponding RPB study entity
        if (this.selectedStudy != null) {
            this.rpbStudy = this.mainBean.getStudyRepository().getByOcStudyIdentifier(this.selectedStudy.getStudyIdentifier());

            // Need to have this study as active otherwise cannot use restful URLs
            //this.changeActiveStudy();
        }

        // Reload study configuration details
        this.reloadParentStudyConfiguration();
        // Reload study subjects
        this.reloadStudySubjects();
    }

    /**
     * Reload Study Sites depending on selected study name
     */
    public void reloadStudySites() {
        try {
            if (this.selectedStudyType != null) {
                for (StudyType st : this.studyTypeList) {
                    if (st.getName().equals(this.selectedStudyType.getName())) {

                        // Representing collection of Study Sites
                        ArrayList<Study> tempStudyList = new ArrayList<>();

                        // Multi-centric study
                        if (st.getSites() != null) {
                            for (SiteType site : st.getSites().getSite()) {
                                Study studySite = new Study(st);
                                studySite.setSiteName(site.getIdentifier());
                                studySite.setRealSiteName(site.getName());
                                studySite.setSiteOID(site.getOid());
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
        this.reloadStudySubjects();
    }

    /**
     * Load study configuration information of selected study/site from metadata
     */
    public void reloadParentStudyConfiguration() {
        try {
            // Always read the parent study metadata
            MetadataODM studyMetadata = this.mainBean
                    .getOpenClinicaService()
                    .getStudyMetadata(
                        this.selectedStudy.getStudyIdentifier()
                    );
            // What to do if user does not have access to parent study? OC bug: site metadata are reporting wrong studySubjectID generation
            // Use the root access to the parent study metadata
            if (studyMetadata == null) {
                studyMetadata = this.mainBean
                        .getRootOpenClinicaService()
                        .getStudyMetadata(
                            this.selectedStudy.getStudyIdentifier()
                        );
            }

            // Setup study configuration from metadata
            if (studyMetadata != null) {
                this.studyConfiguration.setCollectSubjectDob(
                        studyMetadata.getCollectDobStudyParameter()
                );
                this.studyConfiguration.setSexRequired(
                        studyMetadata.getCollectSexStudyParameter()
                );
                this.studyConfiguration.setAllowDiscrepancyManagement(
                        studyMetadata.getAllowDiscrepancyManagementStudyParameter()
                );
                this.studyConfiguration.setStudySubjectIdGeneration(
                        studyMetadata.getSubjectIdGenerationStudyParameter()
                );
                this.studyConfiguration.setPersonIdRequired(
                        studyMetadata.getSubjectPersonIdRequiredStudyParameter()
                );
                this.studyConfiguration.setSecondaryLabelViewable(
                        studyMetadata.getSecondaryLabelViewableStudyParameter()
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
            this.mainBean.changeUserActiveStudy(
                this.mainBean.getSvcWebApi().loadEdcStudy(
                    this.mainBean.getMyAccount().getApiKey(),
                    this.selectedStudy.getSiteName()
                )
            );

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

        return new ArrayList<>();
    }

    //endregion

    //region StudySubject

    public boolean canEnrollIntoSelectedTreatmentArm() {
        // Check whether enrollment is possible (enrollment rules)
        if (this.rpbStudy != null && this.rpbStudy.getIsEnrollmentArmAssignmentRequired() != null &&
            this.rpbStudy.getIsEnrollmentArmAssignmentRequired()){
            if (this.getNewSubject().getArm() == null) {
                return false;
            }
            else if (this.getNewSubject().getArm().getIsEnabled() == null ||
                    !this.getNewSubject().getArm().getIsEnabled()) {
                FacesContext.getCurrentInstance().validationFailed();
                this.messageUtil.warningText("Subject assignment to selected treatment arm is disabled.");
                return false;
            }
        }

        return true;
    }

    private StudySubject studySubjectLookup(String studySubjectId) {
        if (this.studySubjectsList != null) {
            for (StudySubject studySubject : this.studySubjectsList) {
                if (studySubject.getStudySubjectLabel().equals(studySubjectId)) {
                    return studySubject;
                }
            }
        }

        return null;
    }

    /**
     * Create a new study subject in OC using web service
     */
    public void createNewStudySubject() {
        try {
            // Check whether enrollment is possible (enrollment rules)
            if (this.rpbStudy != null && this.rpbStudy.getIsEnrollmentArmAssignmentRequired() != null &&
                this.rpbStudy.getIsEnrollmentArmAssignmentRequired()){

                if (this.getNewSubject().getArm() == null) {
                    throw new Exception("Enrollment arm assignment is required!");
                }
                else {
                    if (!this.getNewSubject().getArm().getIsEnabled()) {
                        throw new Exception("Subject assignment to selected treatment arm is disabled.");
                    }
                }
            }

            // Prepare SOAP StudySubject from Subject data
            StudySubject studySubject = new StudySubject();

            // Whether StudySubjectID is provided depends on study configuration
            if (this.studyConfiguration.getStudySubjectIdGeneration() != null) {

                // Auto
                if (this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.AUTO)) {
                    studySubject.setStudySubjectLabel(""); // Event if auto I have to provide at least empty string
                }
                // Manual
                else if (this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.MANUAL)) {

                    // Manually entered StudySubjectID should never be number, because it hinders the automatic StudySubjectID generation
                    if (NumberUtils.isNumber(this.getNewSubject().getStudySubjectId())) {
                        throw new Exception("Number is not allowed for manually entered StudySubjectID!");
                    }
                    else {
                        studySubject.setStudySubjectLabel(
                                this.getNewSubject().getStudySubjectId()
                        );
                    }
                }
            }
            // Could not read this information from parameters, use empty string because something is required
            else {
                studySubject.setStudySubjectLabel("");
            }

            // When PID is required
            if (this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.REQUIRED)) {

                // Determine whether subject has PID assigned
                if (this.getNewSubject().getUniqueIdentifier() == null ||
                    this.getNewSubject().getUniqueIdentifier().equals("")) {

                    throw new Exception("Patient PID (pseudonym) should not be empty!");
                }

                // Use partner site prefix and pure PID to create cross site unique Person ID
                if (this.usePidGenerator) {
                    studySubject.setPersonID(
                            this.mainBean.constructMySubjectFullPid(
                                    this.getNewSubject().getUniqueIdentifier()
                            )
                    );
                }
                // Use manually assigned PID
                else {
                    studySubject.setPersonID(
                            this.getNewSubject().getUniqueIdentifier()
                    );
                }
            }
            else if (this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.OPTIONAL)) {

                // Determine whether subject has PID assigned
                if (this.getNewSubject().getUniqueIdentifier() != null &&
                    !this.getNewSubject().getUniqueIdentifier().equals("")) {

                    // Use partner site prefix and pure PID to create cross site unique Person ID
                    if (this.usePidGenerator) {
                        studySubject.setPersonID(
                                this.mainBean.constructMySubjectFullPid(
                                        this.getNewSubject().getUniqueIdentifier()
                                )
                        );
                    }
                    // Use manually assigned PID
                    else {
                        studySubject.setPersonID(
                                this.getNewSubject().getUniqueIdentifier()
                        );
                    }
                }
            }

            // If secondary ID is provided use it
            if (this.getNewSubject().getSecondaryId() != null && !this.getNewSubject().getSecondaryId().equals("")) {
                studySubject.setStudySubjectSecondaryLabel(this.getNewSubject().getSecondaryId());
            }

            if (this.selectedStudy != null) {
                studySubject.setStudy(this.selectedStudy);
            }
            else {
                throw new Exception("Study site has to be selected!");
            }

            // Gender is always required (there is a bug in OC web service)
            studySubject.setSex(this.getNewSubject().getGender());

            // Calendar
            GregorianCalendar c = new GregorianCalendar();

            // Whether day of birth will be collected for subject depend on study configuration
            if (this.studyConfiguration.getCollectSubjectDob().equals(EnumCollectSubjectDob.YES)) {
                c.setTime(this.getNewSubject().getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                studySubject.setDateOfBirth(birthDateXML);
            }
            else if (this.studyConfiguration.getCollectSubjectDob().equals(EnumCollectSubjectDob.ONLY_YEAR)) {
                c.setTime(this.getNewSubject().getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                studySubject.setYearOfBirth(new BigInteger(String.valueOf(birthDateXML.getYear())));
            }

            // Always collect enrollment date for subject
            studySubject.setDateOfRegistration(this.getNewSubject().getEnrollmentDate());

            // Enroll subject into EDC study
            String ssid = this.mainBean.getOpenClinicaService().createNewStudySubject(studySubject);
            if (ssid != null) {
                this.auditLogService.event(
                        AuditEvent.EDCStudySubjectEnrollment,
                        studySubject.getPersonID(), // PID
                        this.selectedStudy.getStudyIdentifier(), // Study
                        ssid // SSID
                );
                this.messageUtil.infoText("Study Subject: " + ssid + " successfully created.");
                this.reloadStudySubjects();
            }
            else {
                throw new Exception("EDC web-service could not create a new study subject.");
            }

            // Register enrolled study subject into BIO bank
            // TODO: automatic registering in BIO bank is disabled for now (need more time for testing)
//            if (ssid != null && Boolean.valueOf(this.rpbStudy.getTagValue("BIO")) && this.mainBean.getSvcBio() != null) {
//
//                boolean bioRegistrationResult = false;
//
//                String bioStudyIdType = Constants.CXX_ID + Constants.CXX_CODESEP + this.rpbStudy.getTagValue("BIO-code");
//
//                de.dktk.dd.rpb.core.domain.edc.StudySubject ss = new de.dktk.dd.rpb.core.domain.edc.StudySubject();
//                ss.setStudySubjectId(ssid);
//                ss.setPid(studySubject.getPersonID());
//                ss.setSex(studySubject.getSex());
//
//                // For principal site patients
//                if (this.mainBean.isPrincipalSiteSubject(studySubject.getPersonID())) {
//
//                    // Fetch IDAT
//                    Person person = this.mainBean.getSvcPid().loadPatient(
//                            this.mainBean.extractMySubjectPurePid(
//                                    studySubject.getPersonID()
//                            )
//                    );
//                    // Fetch StudySubjects
//                    ss.setPerson(
//                            this.studyIntegrationFacade.fetchPatientStudySubjects(person)
//                    );
//
//                    // Patient MPI
//                    String mpi = ss.extractStudy0PureSSID();
//
//                    // Determine whether patient with MPI exists in BIO
//                    Person bioMpiPatient = null;
//                    if (mpi != null) {
//                        bioMpiPatient = this.mainBean.getSvcBio().loadPatient(mpi);
//                    }
//
//                    //Patient with MPI that does not exists in BIO
//                    if (mpi != null && bioMpiPatient == null) {
//
//                        // Create patient with MPI, PID, IDAT, SSID
//                        bioRegistrationResult = this.mainBean.getSvcBio().createPatient(
//                                bioStudyIdType,
//                                ss,
//                                this.mainBean.getMyAccount().getPartnerSite(),
//                                true,
//                                true,
//                                true
//                        );
//                    }
//                    // Patient with MPI that exists in BIO
//                    else if (mpi != null) {
//
//                        //  Update patient with MPI and provide additional PID, SSID
//                        bioRegistrationResult = this.mainBean.getSvcBio().createPatient(
//                                bioStudyIdType,
//                                ss,
//                                this.mainBean.getMyAccount().getPartnerSite(),
//                                true,
//                                true,
//                                false
//                        );
//                    }
//                    // Patient is not in Study0
//                    else {
//                        this.messageUtil.info("Principal site patient is not enrolled in Study0. In order to create patient in BIO it first need to be enrolled in Study0.");
//                    }
//                }
//                // For other site patients
//                else {
//
//                    // Create patient with PID, SSID
//                    bioRegistrationResult = this.mainBean.getSvcBio().createPatient(
//                            bioStudyIdType,
//                            ss,
//                            this.mainBean.getMyAccount().getPartnerSite(),
//                            false,
//                            false,
//                            false
//                    );
//                }
//
//                if (bioRegistrationResult) {
//
//                    // Audit BIO bank patient registration
//                    this.auditLogService.event(
//                            AuditEvent.BIOPatientCreation,
//                            studySubject.getPersonID(), // PID
//                            this.rpbStudy.getTagValue("BIO-code"), // Study
//                            ssid // SSID
//                    );
//                    this.messageUtil.infoText("Study Subject: " + ssid + " successfully registered in BIO bank.");
//                }
//            }

            // Prepare data structure for the new study subject object binding
            Person person = new Person();
            this.newSubject = new Subject();
            this.newSubject.setPerson(person);
            this.isSure = true;
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.error(err);
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
                    List<StudySubject> subjectList = new ArrayList<>();

                    for (StudySubjectWithEventsType sswe : this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(this.selectedStudy)) {
                        StudySubject ss = new StudySubject(this.selectedStudy, sswe);

                        // Assign scheduled events
                        ArrayList<ScheduledEvent> scheduledEvents = new ArrayList<>();
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

            // Enrollment graph
            this.reloadSitePatientEnrollmentLineModel();
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

            // TODO: refactor Event available for scheduling
            this.loadSelectedSubjectDetails();
            //this.reloadStudyEvents();
        }
    }

    /**
     * Reload view models for study partner site enrollment graphs
     */
    public void reloadSitePatientEnrollmentLineModel() {

        // Model
        this.siteEnrolmentGraphModel = new LineChartModel();
        this.siteEnrolmentGraphModel.setTitle(this.selectedStudy.getSiteName() + " Enrollment Progress");
        this.siteEnrolmentGraphModel.setLegendPosition("ne");
        this.siteEnrolmentGraphModel.setZoom(Boolean.TRUE);

        // Monthly bar series
        BarChartSeries series1 = new BarChartSeries();
        series1.setLabel("monthly");
        series1.setXaxis(AxisType.X);
        series1.setYaxis(AxisType.Y);

        CategoryAxis xAxis = new CategoryAxis("Months");
        xAxis.setTickAngle(-50);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.X, xAxis);
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setLabel("Per Month");
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setTickFormat("%d");
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setMin(0);

        // Daily line series
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("total");
        series2.setXaxis(AxisType.X);
        series2.setYaxis(AxisType.Y2);

        Axis y2Axis = new LinearAxis("Sum");
        y2Axis.setTickFormat("%d");
        y2Axis.setMin(0);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.X, xAxis);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.Y2, y2Axis);

        // Only if some patients are enrolled
        if (this.studySubjectsList != null) {

            // These will serve as base data structures for graphs models
            HashMap<String, Integer> sumProgress = new HashMap<>();
            HashMap<String, Integer> monthlyProgress = new HashMap<>();

            // Initialise hash maps for
            for (StudySubject ss : this.studySubjectsList) {

                // Sum enrollment report
                if (sumProgress.get(ss.getDateOfRegistration().getYear() + "-" + String.format("%02d", ss.getDateOfRegistration().getMonth())) != null) {
                    Integer value = sumProgress.get(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()));
                    sumProgress.put(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()), value + 1);
                }
                else {
                    sumProgress.put(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()), 1);
                }

                // Monthly enrollment report
                if (monthlyProgress.get(ss.getDateOfRegistration().getYear() + "-" + String.format("%02d", ss.getDateOfRegistration().getMonth())) != null) {
                    Integer value = monthlyProgress.get(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()));
                    monthlyProgress.put(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()), value + 1);
                }
                else {
                    monthlyProgress.put(ss.getDateOfRegistration().getYear() + "-" +
                            String.format("%02d", ss.getDateOfRegistration().getMonth()), 1);
                }
            }

            // Sort monthly by key
            TreeMap<String, Integer> sortedMonthly = new TreeMap<>(monthlyProgress);

            if (!sortedMonthly.isEmpty()) {
                int countingYear = Integer.parseInt(sortedMonthly.firstKey().substring(0, 4));
                int countingMonth = Integer.parseInt(sortedMonthly.firstKey().substring(5, 7));
                for (String key : sortedMonthly.keySet()) {

                    // Current year-month bar
                    int year = Integer.parseInt(key.substring(0, 4));
                    int month = Integer.parseInt(key.substring(5, 7));

                    // If there is a gap in recruitment
                    if (year > countingYear || month < countingMonth) {

                        // Fill the gap for missing years
                        for (int i = countingYear; i < year; i++) {
                            // Fill the gap in recruitment until the end of year with zero bars
                            for (int j = countingMonth; j <= 12; j++) {
                                series1.set(i + "-" + String.format("%02d", j), 0);
                            }

                            countingMonth = 1;
                        }

                        countingYear = year;

                        // Fill the gap for missing months with zero bars
                        for (int i = countingMonth; i < month; i++) {
                            series1.set(year + "-" + String.format("%02d", i), 0);
                        }

                        countingMonth = month;
                    }
                    // Gab is within a same year
                    else if (year == countingYear) {
                        if (month > countingMonth) {
                            // Fill the gap in recruitment with zero bars
                            for (int i = countingMonth; i < month; i++) {
                                series1.set(year + "-" + String.format("%02d", i), 0);
                            }

                            countingMonth = month;
                        }
                    }

                    // And always create the current recruitment bar
                    series1.set(key, monthlyProgress.get(key));

                    // Setup what should be the following year-month key
                    if (countingMonth == 12) {
                        countingMonth = 1;
                        countingYear += 1;
                    } else {
                        countingMonth += 1;
                        countingYear = year;
                    }
                }

                // Scale for X axis
                xAxis.setMin(sortedMonthly.firstKey());
                xAxis.setMax(sortedMonthly.lastKey());
            }

            // Sort sum be key
            TreeMap<String, Integer> sortedSum = new TreeMap<>(sumProgress);

            if (!sortedSum.isEmpty()) {
                int sum = 0;
                for (String key : sortedSum.keySet()) {
                    sum += sortedSum.get(key);
                    series2.set(key, sum);
                }
            }

            this.siteEnrolmentGraphModel.addSeries(series1);
            this.siteEnrolmentGraphModel.addSeries(series2);
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

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    private List<Boolean> buildSubjectsVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // StudySubjectID
        results.add(Boolean.TRUE); // PID
        results.add(Boolean.FALSE); // Secondary ID
        results.add(Boolean.TRUE); // Gender
        results.add(Boolean.FALSE); // Date of Birth
        results.add(Boolean.TRUE); // Enrollment date

        return results;
    }

    //endregion

    //region Patient

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
                finalResult = this.mainBean.getSvcPid().createSurePatientJson(tokenId, person);
            } else {
                finalResult = this.mainBean.getSvcPid().getCreatePatientJsonResponse(tokenId, person);
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
                this.auditLogService.event(AuditEvent.PIDUnsure, this.newSubject.getPerson().toString());

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
            // Pure PID (without partner site identifier)
            String pid = this.mainBean.extractMySubjectPurePid(
                this.selectedStudySubject.getPersonID()
            );

            // Fetch IDAT
            Person patient = this.mainBean.getSvcPid().loadPatient(pid);
            this.getSelectedSubject().setPerson(patient);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);
            this.messageUtil.infoText("Patient identity data for " + pid + " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //TODO: need to change the view model to work with RPB StudySubject that can hold the reference to person
    public void depseudonymiseAllPids() {
        try {
//            List<Person> pseudonymisedPatients = this.mainBean.getSvcPid().getAllPatientPIDs();
//            this.entityList = this.mainBean.getSvcPid().getPatientListByPIDs(pseudonymisedPatients);
//
//            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, "All PIDs");
//            this.messageUtil.infoText("Patient identity data for " + entityList.size() + " pseudonyms loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Study Event

    public void resetSubjectEvents() {
        this.selectedEventData = null;
    }

    public void loadSelectedSubjectDetails() {

        // Only load details if new subject is selected
        if (this.rpbSelectedStudySubject == null ||
            this.selectedStudySubject != null && !this.rpbSelectedStudySubject.getStudySubjectId().equals(this.selectedStudySubject.getStudySubjectLabel())) {

            // Reset
            this.rpbSelectedStudySubject = null;

            // Clear loaded collections
            this.resetSubjectEvents();
            //this.resetSubjectCrfs();

            try {
                // Load ODM resource for selected study subject
                String soid = this.selectedStudy.getSiteOID() != null ? this.selectedStudy.getSiteOID() : this.selectedStudy.getStudyOID();
                String queryOdmXmlPath = soid + "/" + this.selectedStudySubject.getStudySubjectLabel() + "/*/*";
                if (this.mainBean.getEngineOpenClinicaService() != null) {

                    Odm selectedStudySubjectOdm = this.mainBean.getEngineOpenClinicaService().getStudyCasebookOdm(
                            OpenClinicaService.CasebookFormat.XML,
                            OpenClinicaService.CasebookMethod.VIEW,
                            queryOdmXmlPath
                    );

                    // Crate Defs from Refs
                    selectedStudySubjectOdm.updateHierarchy();

                    // Replace selected SOAP subject with REST subject that has clinical data
                    this.rpbSelectedStudySubject = selectedStudySubjectOdm.findUniqueStudySubjectOrNone(
                            this.selectedStudySubject.getStudySubjectLabel()
                    );

                    // Link clinical data with definitions from ODM
                    this.rpbSelectedStudySubject.linkOdmDefinitions(selectedStudySubjectOdm);
                }
                else {
                    this.messageUtil.warning("RPB iengine EDC service is not initiated (most likely iengine user does not exist).");
                }
            }
            catch (Exception err) {
                this.messageUtil.error(err);
            }
        }
    }

    public void reloadStudyEvents() {
        try {
            List<Event> eventDefinitions = null;
            this.eventList = new ArrayList<>();

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
            String scheduledRepeatKey = this.mainBean.getOpenClinicaService().scheduleStudyEvent(this.selectedStudySubject, this.newEvent);
            if (scheduledRepeatKey != null && !scheduledRepeatKey.isEmpty()) {
                this.auditLogService.event(
                    AuditEvent.EDCStudyEventSchedule,
                    this.newEvent.getEventOID() + "["+ scheduledRepeatKey +"]",
                    selectedStudySubject.getStudySubjectLabel()
                );
            }

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

    public void selectedEventChanged(EventData eventData) {
        // Reset
        if (eventData != null) {
            this.originalEventRepeatKey = eventData.getStudyEventRepeatKeyInteger();
        }
    }

    public void assignNewStudyEventRepeatKey(int newStudyEventRepeatKey) {
        if (this.selectedEventData != null) {
            this.selectedEventData.setStudyEventRepeatKey(String.valueOf(newStudyEventRepeatKey));
        }
    }

    public void updateStudyEvent() {
        try {
            // Update OC database
            boolean modified = this.mainBean.getOpenClinicaDataRepository().swapEventDataOrder(
                    this.rpbSelectedStudySubject,
                    this.selectedEventData,
                    this.originalEventRepeatKey
            );

            // When successful create an audit log entry
            if (modified) {
                this.auditLogService.event(
                    AuditEvent.EDCDataModification,
                    "StudyEventData",
                    this.selectedStudyType.getOid() + "/" + this.rpbSelectedStudySubject.getSubjectKey() + "/" + this.selectedEventData.getEventDefinition().getOid(),
                    "studyEventRepeatKey changed from [" + this.originalEventRepeatKey + "] to [" + this.selectedEventData.getStudyEventRepeatKey() + "]"
                );

                this.messageUtil.infoText("Study Event Data: successfully saved.");

                // Reload
                this.rpbSelectedStudySubject = null;
                this.loadSelectedSubjectDetails();
                this.selectedEventData = null;
                this.originalEventRepeatKey = null;
            }
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

}