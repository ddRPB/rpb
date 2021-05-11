/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for querying data from RadPlanBio
 * (cross study annotated data)
 *
 * @author tomas@skripcak.net
 * @since 28 Sep 2017
 */
@Named("mbQueryData")
@Scope("view")
public class OCQueryBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    public MainBean getMainBean() {
        return this.mainBean;
    }

    //endregion

    //region StudyIntegrationFacade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    private List<Study> studies;
    private Study selectedStudy;
    private List<CrfFieldAnnotation> selectedAnnotations;

    private de.dktk.dd.rpb.core.domain.edc.Study selectedStudySite;

    private List<StudySubject> baseStudySubjects;
    private List<StudySubject> studySubjects;
    private List<StudySubject> filteredStudySubjects;

    private List<Boolean> subjectsColumnVisibilityList;
    private List<SortMeta> subjectsPreSortOrder;
    
    //endregion

    //region Properties

    public List<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    public Study getSelectedStudy() {
        return this.selectedStudy;
    }

    public void setSelectedStudy(Study study) {
        this.selectedStudy = study;
    }

    public de.dktk.dd.rpb.core.domain.edc.Study getSelectedStudySite() {
        return this.selectedStudySite;
    }

    public void setSelectedStudySite(de.dktk.dd.rpb.core.domain.edc.Study selectedStudySite) {
        this.selectedStudySite = selectedStudySite;
    }

    public List<CrfFieldAnnotation> getSelectedAnnotations() {
        return this.selectedAnnotations;
    }

    public void setSelectedAnnotations(List<CrfFieldAnnotation> selectedAnnotations) {
        this.selectedAnnotations = selectedAnnotations;
    }

    public List<StudySubject> getStudySubjects() {
        return this.studySubjects;
    }

    public void setStudySubjects(List<StudySubject> studySubjects) {
        this.studySubjects = studySubjects;
    }

    public List<StudySubject> getFilteredStudySubjects() {
        return this.filteredStudySubjects;
    }

    public void setFilteredStudySubjects(List<StudySubject> filteredStudySubjects) {
        this.filteredStudySubjects = filteredStudySubjects;
    }

    //region Sorting

    public List<SortMeta> getSubjectsPreSortOrder() {
        return this.subjectsPreSortOrder;
    }

    public void setSubjectsPreSortOrder(List<SortMeta> sortList) {
        this.subjectsPreSortOrder = sortList;
    }

    //endregion

    //region ColumnVisibilityList

    public List<Boolean> getSubjectsColumnVisibilityList() {
        return subjectsColumnVisibilityList;
    }

    public void setSubjectsColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.subjectsColumnVisibilityList = columnVisibilityList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {

        this.setSubjectsColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setSubjectsPreSortOrder(
                this.buildSubjectsSortOrder()
        );

        // Load initial data
        this.load();
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

    //endregion

    //region Commands
    
    public void load() {
        try {
            // Reload study metadata
            this.studyIntegrationFacade.init(this.mainBean);
            this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);
            this.studies = this.studyIntegrationFacade.loadStudies();
            this.baseStudySubjects = this.studyIntegrationFacade.loadStudySubjects();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void queryData() {
        try {
            // Change active study site for REST query to work (otherwise it will lookup in currently active study)
            de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.selectedStudySite != null ? this.selectedStudySite : this.selectedStudy.getEdcStudy();
            this.mainBean.changeUserActiveStudy(edcStudy);

            // Load cross study subjects
            List<StudySubject> crossStudySubjects = this.studyIntegrationFacade.loadStudySubjects(edcStudy);

            for (StudySubject baseStudySubject : this.baseStudySubjects) {
                for (StudySubject crossStudySubject: crossStudySubjects) {
                    // Match subjects
                    if (baseStudySubject.getPid().equals(crossStudySubject.getPid())) {

                        // Cross study detailed query
                        StudySubject studySubjectOdm = this.studyIntegrationFacade.loadOdmStudySubject(
                                this.selectedStudy.getEdcStudy().getOid(),
                                crossStudySubject.getStudySubjectId()
                        );

                        // When subject exists
                        if (studySubjectOdm != null) {
                            for (CrfFieldAnnotation annotation : this.selectedAnnotations) {
                                // When the cross study subject has annotated ItemData
                                List<ItemData> itemData = studySubjectOdm.findAnnotatedItemData(annotation);
                                if (itemData != null && itemData.size() > 0) {
                                    // Add cross study subject event occurrences to base study subject
                                    for (EventData ed: studySubjectOdm.getEventOccurrences()) {
                                        baseStudySubject.addStudyEventData(ed);
                                    }
                                }
                            }
                        }

                        break;
                    }
                }
            }

            this.studySubjects = this.baseStudySubjects;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Private methods

    /**
     * Need to build an initial sort order for data table multi sort
     * @return List of SortMeta objects
     */
    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results =  DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);

        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();
        
        result.add(Boolean.TRUE); // PID
        result.add(Boolean.TRUE); // StudySubjectID
        result.add(Boolean.FALSE); // SubjectKey
        result.add(Boolean.TRUE); // Firstname
        result.add(Boolean.TRUE); // Surname
        result.add(Boolean.TRUE); // Birthdate
        result.add(Boolean.FALSE); // Birthname
        result.add(Boolean.FALSE); // City of residence
        result.add(Boolean.FALSE); // ZIP

        return result;
    }

    //endregion
}
