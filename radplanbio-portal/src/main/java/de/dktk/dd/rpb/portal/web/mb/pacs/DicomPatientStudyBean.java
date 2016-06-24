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

package de.dktk.dd.rpb.portal.web.mb.pacs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.Subject;

import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;

import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;

import org.apache.maven.artifact.versioning.ComparableVersion;

import org.primefaces.component.api.UIColumn;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;
import org.springframework.context.annotation.Scope;

/**
 * Bean for navigating RadPlanBio DICOM patient studies (OpenClinica EDC link to Conquest PACS)
 *
 * @author tomas@skripcak.net
 * @since 07 August 2013
 */
@Named("mbDicomStudies")
@Scope("view")
public class DicomPatientStudyBean implements Serializable {

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

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    @SuppressWarnings("unused")
    public void setStudyIntegrationFacade(StudyIntegrationFacade value) {
        this.studyIntegrationFacade = value;
    }

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    private List<SortMeta> subjectsPreSortOrder;
    private List<SortMeta> eventsPreSortOrder;
    private List<SortMeta> dicomStudiesPreSortOrder;

    // StudySubjects Data Table
    private List<Subject> studySubjectsList;
    private List<Subject> filteredStudySubjects;
    private Subject selectedStudySubject;
    private List<Boolean> subjectsColumnVisibilityList;

    // ScheduledEvents Data Table
    private List<ScheduledEvent> scheduledEventList;
    private List<ScheduledEvent> filteredScheduledEvents;
    private ScheduledEvent selectedScheduledEvent;
    private List<Boolean> eventsColumnVisibilityList;

    // DICOM studies Data Table
    private List<DicomStudy> studyList;
    private List<DicomStudy> filteredStudies;
    private DicomStudy selectedStudy;
    private List<Boolean> dicomStudyColumnVisibilityList;

    private int activeTabIndex;
    private String patientId;

    //endregion

    //region Properties

    //region ActiveTabIndex

    public int getActiveTabIndex() {
        return this.activeTabIndex;
    }

    public void setActiveTabIndex(int value) {
        this.activeTabIndex = value;
    }

    //endregion

    //region DICOM Patient ID

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String value) {
        this.patientId = value;
    }

    //endregion

    //region DICOM Subjects DataTable Properties

    //region StudySubjectsList Property

    /**
     * Get Study Subjects List
     *
     * @return List - Study Subjects List
     */
    public List<Subject> getStudySubjectsList() {
        return this.studySubjectsList;
    }

    /**
     * Set Study Subjects List
     *
     * @param studySubjectsList - Study Subjects List
     */
    public void setStudySubjectsList(List<Subject> studySubjectsList) {
        this.studySubjectsList = studySubjectsList;
    }

    //endregion

    //region FilteredStudySubjects Property

    /**
     * Get Filtered StudySubjects List
     *
     * @return List - StudySubjects List
     */
    public List<Subject> getFilteredStudySubjects() {
        return this.filteredStudySubjects;
    }

    /**
     * Set Filtered Study Subjects List
     *
     * @param filteredStudySubjects - Study Subjects List
     */
    public void setFilteredStudySubjects(List<Subject> filteredStudySubjects) {
        this.filteredStudySubjects = filteredStudySubjects;
    }

    //endregion

    //region SelectedStudySubject Property

    /**
     * Get Selected Study Subject
     * @return SelectedStudySubject - StudySubject
     */
    public Subject getSelectedStudySubject() {
        return this.selectedStudySubject;
    }

    /**
     * Set Selected Study Subject
     * @param subject - StudySubject
     */
    public void setSelectedStudySubject(Subject subject) {
        // Nothing was selected before
        if (this.selectedStudySubject == null && subject != null) {
            this.selectedStudySubject = subject;

            // Subject was selected so try to load scheduled events
            this.reloadSubjectEvents();
        }
        // Different entity was selected before
        else if (this.selectedStudySubject != null && !this.selectedStudySubject.equals(subject)) {
           this.selectedStudySubject = subject;

           // Subject was selected so try to load scheduled events
           this.reloadSubjectEvents();
        }
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

    //region DICOM Event DataTable Properties

    //region ScheduledEventList Property

    /**
     * Get Scheduled Event List
     *
     * @return List - Scheduled Event List
     */
    public List<ScheduledEvent> getScheduledEventList() {
        return this.scheduledEventList;
    }

    /**
     * Set Scheduled Event List
     *
     * @param value - Scheduled Event List
     */
    public void setScheduledEventList(List<ScheduledEvent> value) {
        this.scheduledEventList = value;
    }

    //endregion

    //region Filtered Scheduled Events Property

    /**
     * Get Filtered ScheduledEvent List
     *
     * @return List - Scheduled Even  List
     */
    public List<ScheduledEvent> getFilteredScheduledEvents() {
        return this.filteredScheduledEvents;
    }

    /**
     * Set Scheduled Filtered Event List
     *
     * @param value - Scheduled Event List
     */
    public void setFilteredScheduledEvents(List<ScheduledEvent> value) {
        this.filteredScheduledEvents = value;
    }

    //endregion

    //region SelectedScheduledEvent Property

    /**
     * Get Selected Scheduled Event
     * @return SelectedScheduledEvent - ScheduledEvent
     */
    public ScheduledEvent getSelectedScheduledEvent() {
        return this.selectedScheduledEvent;
    }

    /**
     * Set Selected Scheduled Event
     * @param event - ScheduledEvent
     */
    public void setSelectedScheduledEvent(ScheduledEvent event) {
        // Nothing was selected before
        if (this.selectedScheduledEvent == null && event != null) {
            this.selectedScheduledEvent = event;

            // Event was selected so try to load DICOM annotated study items
            this.reloadDicomAnnotatedStudyItem();
        }
        // Different entity was selected before
        if (this.selectedScheduledEvent != null && !this.selectedScheduledEvent.equals(event)) {
            this.selectedScheduledEvent = event;

            // Event was selected so try to load DICOM annotated study items
            this.reloadDicomAnnotatedStudyItem();
        }
    }

    //endregion

    //endregion

    //region EventsColumnVisibilityList

    public List<Boolean> getEventsColumnVisibilityList() {
        return this.eventsColumnVisibilityList;
    }

    public void setEventsColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.eventsColumnVisibilityList = columnVisibilityList;
    }

    //endregion

    //region DICOM Study DataTable Properties

    //region StudyList Property

    /**
     * Get Study List
     *
     * @return List - Study List
     */
    public List<DicomStudy> getStudyList() {
        return this.studyList;
    }

    /**
     * Set Study List
     *
     * @param studyList - Study List
     */
    public void setStudyList(List<DicomStudy> studyList) {
        this.studyList = studyList;
    }

    //endregion

    //region FilteredStudies Property

    /**
     * Get Filtered Study List
     *
     * @return List - Study List
     */
    public List<DicomStudy> getFilteredStudies() {
        return this.filteredStudies;
    }

    /**
     * Set Filtered Study List
     *
     * @param filteredStudies - Study List
     */
    public void setFilteredStudies(List<DicomStudy> filteredStudies) {
        this.filteredStudies = filteredStudies;
    }

    //endregion

    //region SelectedStudy Property

    /**
     * Get Selected Study
     * @return SelectedStudy - DicomStudy
     */
    public DicomStudy getSelectedStudy() {
        return this.selectedStudy;
    }

    /**
     * Set Selected Study
     * @param study - DicomStudy
     */
    public void setSelectedStudy(DicomStudy study) {
        // Nothing was selected before
        if (this.selectedStudy == null && study != null) {
            this.selectedStudy = study;
        }
        // Different entity was selected before
        else if (this.selectedStudy != null && !this.selectedStudy.equals(study)) {
            this.selectedStudy = study;
        }
    }

    //endregion

    //endregion

    //region DicomStudyColumnVisibilityList

    public List<Boolean> getDicomStudyColumnVisibilityList() {
        return this.dicomStudyColumnVisibilityList;
    }

    public void setDicomStudyColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.dicomStudyColumnVisibilityList = columnVisibilityList;
    }

    //endregion

    //region Sorting

    public List<SortMeta> getSubjectsPreSortOrder() {
        return this.subjectsPreSortOrder;
    }

    public void setSubjectsPreSortOrder(List<SortMeta> sortList) {
        this.subjectsPreSortOrder = sortList;
    }

    public List<SortMeta> getEventsPreSortOrder() {
        return this.eventsPreSortOrder;
    }

    public void setEventsPreSortOrder(List<SortMeta> sortList) {
        this.eventsPreSortOrder = sortList;
    }

    public List<SortMeta> getDicomStudiesPreSortOrder() {
        return this.dicomStudiesPreSortOrder;
    }

    public void setDicomStudiesPreSortOrder(List<SortMeta> sortList) {
        this.dicomStudiesPreSortOrder = sortList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setSubjectColumnVisibilityList(
                this.buildSubjectsVisibilityList()
        );
        this.setEventsColumnVisibilityList(
                this.buildEventsVisibilityList()
        );
        this.setDicomStudyColumnVisibilityList(
                this.buildDicomStudiesVisibilityList()
        );
        this.setSubjectsPreSortOrder(
                this.buildSubjectsSortOrder()
        );
        this.setEventsPreSortOrder(
                this.buildEventsSortOrder()
        );
        this.setDicomStudiesPreSortOrder(
                this.buildDicomStudiesSortOrder()
        );
        // RadPlanBio study subjects
        this.reloadSubjects();
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

    public void onDicomStudyEntityToggle(ToggleEvent e) {
        try {
            this.dicomStudyColumnVisibilityList.set(((Integer) e.getData() - 1), e.getVisibility() == Visibility.VISIBLE);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Commands

    //region Subjects

    public void resetSubjects() {
        this.studySubjectsList = null;
        this.selectedStudySubject = null;
    }

    /**
     * Reload RadPlanBio subjects
     */
    public void reloadSubjects() {
        this.resetSubjects();
        this.resetSubjectEvents();
        this.resetDicomStudies();

        try {
            this.studyIntegrationFacade.init(this.mainBean);

            // Faster load with new OC REST APIs
            if (new ComparableVersion(
                        this.mainBean.getMyAccount().getPartnerSite().getEdc().getVersion()
                   )
                   .compareTo(
                           new ComparableVersion("3.7")
                   ) >= 0) {

                this.studyIntegrationFacade.setRetreiveStudySubjectOID(Boolean.FALSE);
            }

            this.studySubjectsList = this.studyIntegrationFacade.loadSubjects();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Events

    public void resetSubjectEvents() {
        this.scheduledEventList = null;
        this.selectedScheduledEvent = null;
    }

    /**
     * Reload RadPlanBio subject events
     */
    public void reloadSubjectEvents() {
        this.resetSubjectEvents();
        this.resetDicomStudies();

        try {
            if (this.selectedStudySubject != null) {
                this.studyIntegrationFacade.init(this.mainBean);
                // Faster load with new OC REST APIs
                if (new ComparableVersion(
                        this.mainBean.getMyAccount().getPartnerSite().getEdc().getVersion()
                        )
                        .compareTo(
                                new ComparableVersion("3.7")
                        ) >= 0) {

                    this.studyIntegrationFacade.setRetreiveStudySubjectOID(Boolean.FALSE);
                }
                this.scheduledEventList = this.studyIntegrationFacade.loadSubjectEvents(this.selectedStudySubject);
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region DICOM Studies

    public void resetDicomStudies() {
        this.studyList = null;
        this.selectedStudy = null;
    }

    /**
     * Reload DICOM studies in selected study/subject/event based on annotated CRF values
     */
    public void reloadDicomAnnotatedStudyItem() {
        this.resetDicomStudies();

        try {
            if (this.selectedScheduledEvent != null) {
                this.studyIntegrationFacade.init(this.mainBean);

                // Read PatientID and Studie UIDs from DICOM study CRFs
                List<ItemDefinition> dicomCrfFields = this.prepareDicomUidForQuery();

                List<ItemDefinition> dicomEcrfItemList = this.studyIntegrationFacade.getDicomStudyFields(this.selectedScheduledEvent.getEventOID(), dicomCrfFields);

                if (this.mainBean.getPacsService() != null) {
                    this.studyList = this.mainBean.getPacsService().loadPatientStudies(this.patientId, dicomCrfFields);

                    for (DicomStudy dcmStudy : this.studyList) {
                        for (ItemDefinition itemDef : dicomEcrfItemList)
                            if (dcmStudy.getStudyInstanceUID().equals(itemDef.getValue())) {
                                dcmStudy.seteCrfLabel(itemDef.getLabel());
                            }
                    }
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Private methods

    /**
     * Read DICOM patient ID and study UIDs from OpenClinica CRF
     *
     * @return list of DICOM study UIDs to load from PACS
     * @throws Exception
     */
    private List<ItemDefinition> prepareDicomUidForQuery() throws Exception {
        List<ItemDefinition> dcmCrfFields = new ArrayList<ItemDefinition>();

        this.studyIntegrationFacade.init(this.mainBean);
        Study rpbStudy = this.studyIntegrationFacade.loadStudy();

        if (rpbStudy == null) {
            throw new Exception("There is no RadPlanBio study associated to your current active OpenClinica study!");
        }

        for (CrfFieldAnnotation annotation : rpbStudy.getCrfFieldAnnotations()) {
            // Collect associated DICOM patient ID from the CRF
            if (annotation
                    .getAnnotationType()
                    .getName()
                    .equals("DICOM_PATIENT_ID") &&
                    annotation
                            .getEventDefinitionOid()
                            .equals(this.selectedScheduledEvent.getEventOID())
                    ) {

                this.patientId = this.mainBean.getSvcRpb().loadCrfItemValue(
                        this.mainBean.getActiveStudy().getOcoid(),
                        this.selectedStudySubject.getUniqueIdentifier(),
                        annotation.getEventDefinitionOid(),
                        this.selectedScheduledEvent.getStudyEventRepeatKey(),
                        annotation.getFormOid(),
                        annotation.getCrfItemOid()
                );
            }
            // Collect associated DICOM study UID from the CRF
            else if (annotation
                    .getAnnotationType()
                    .getName()
                    .equals("DICOM_STUDY_INSTANCE_UID") &&
                    annotation
                            .getEventDefinitionOid()
                            .equals(this.selectedScheduledEvent.getEventOID())
                    ) {

                String dicomStudyUid = this.mainBean.getSvcRpb().loadCrfItemValue(
                        this.mainBean.getActiveStudy().getOcoid(),
                        this.selectedStudySubject.getUniqueIdentifier(),
                        annotation.getEventDefinitionOid(),
                        this.selectedScheduledEvent.getStudyEventRepeatKey(),
                        annotation.getFormOid(),
                        annotation.getCrfItemOid()
                );

                ItemDefinition itemDef = new ItemDefinition();
                itemDef.setOid(annotation.getCrfItemOid());
                itemDef.setValue(dicomStudyUid);
                dcmCrfFields.add(itemDef);
            }
        }

        return dcmCrfFields;
    }

    private List<Boolean> buildSubjectsVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // StudySubjectID
        results.add(Boolean.TRUE); // PID
        results.add(Boolean.FALSE); // Secondary ID
        results.add(Boolean.TRUE); // Gender
        results.add(Boolean.TRUE); // Enrollment date

        return results;
    }

    private List<Boolean> buildEventsVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // Name
        results.add(Boolean.FALSE); // Description
        results.add(Boolean.TRUE); // Category
        results.add(Boolean.FALSE); // Type
        results.add(Boolean.TRUE); // Repeating
        results.add(Boolean.TRUE); // StartDate
        results.add(Boolean.FALSE); // Status

        return results;
    }

    private List<Boolean> buildDicomStudiesVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // eCRF Item label
        results.add(Boolean.FALSE); // DICOM Study Description
        results.add(Boolean.TRUE); // Type (modality)
        results.add(Boolean.TRUE); // Study Date
        results.add(Boolean.FALSE); // Study UID

        return results;
    }

    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtDicomSubjects:colStudySubjectId");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudySubjectId");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    private List<SortMeta> buildEventsSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtDicomEvents:colEventName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colEventName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    private List<SortMeta> buildDicomStudiesSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtDicomStudies:colDicomStudyLabel");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colDicomStudyLabel");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}

