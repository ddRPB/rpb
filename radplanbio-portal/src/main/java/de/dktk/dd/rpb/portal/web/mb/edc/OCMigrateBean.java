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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * ViewModel bean for EDC cross study data migration target <- source
 *
 * @author tomas@skripcak.net
 * @since 26 Nov 2019
 */
@Named("mbMigrateData")
@Scope("view")
public class OCMigrateBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Main bean

    private MainBean mainBean;

    public MainBean getMainBean() {
        return this.mainBean;
    }

    //endregion

    //region StudyIntegrationFacade

    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Messages

    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    // Target
    private Study study;
    private List<StudySubject> subjectList;
    private StudySubject selectedSubject;
    private List<EventData> subjectEventList;
    private EventData selectedSubjectEvent; // Event occurrence need to be scheduled
    private FormDefinition selectedEventForm; // Form defined in EventDefinition

    // Source
    private List<Study> sourceStudies;
    private Study selectedSourceStudy;
    private de.dktk.dd.rpb.core.domain.edc.Study selectedSourceStudySite;
    private List<StudySubject> sourceSubjectList;
    private StudySubject selectedSourceSubject;
    private List<EventData> sourceSubjectEventList;
    private EventData selectedSourceSubjectEvent; // Event occurrence need to have data
    private FormData selectedSourceEventForm; // Form data need to exist
    
    //endregion

    //region Constructors

    @Inject
    public OCMigrateBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade, MessageUtil messageUtil) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.messageUtil = messageUtil;
    }

    //endregion

    //region Properties

    //region Study

    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    //region SubjectList

    public List<StudySubject> getSubjectList() {
        return this.subjectList;
    }

    public void setSubjectList(List<StudySubject> subjectList) {
        this.subjectList = subjectList;
    }

    //endregion

    //region SelectedSubject

    public StudySubject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(StudySubject selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    //endregion

    //region SubjectEventList

    public List<EventData> getSubjectEventList() {
        return this.subjectEventList;
    }

    public void setSubjectEventList(List<EventData> subjectEventList) {
        this.subjectEventList = subjectEventList;
    }

    //endregion

    //region SelectedSubjectEvent

    public EventData getSelectedSubjectEvent() {
        return this.selectedSubjectEvent;
    }

    public void setSelectedSubjectEvent(EventData selectedSubjectEvent) {
        this.selectedSubjectEvent = selectedSubjectEvent;
    }

    //endregion

    //region SelectedEventFrom

    public FormDefinition getSelectedEventForm() {
        return this.selectedEventForm;
    }

    public void setSelectedEventForm(FormDefinition selectedEventForm) {
        this.selectedEventForm = selectedEventForm;
    }

    //endregion

    //region SourceStudies

    public List<Study> getSourceStudies() {
        return this.sourceStudies;
    }

    public void setSourceStudies(List<Study> sourceStudies) {
        this.sourceStudies = sourceStudies;
    }

    //endregion

    //region SelectedSourceStudy

    public Study getSelectedSourceStudy() {
        return this.selectedSourceStudy;
    }

    public void setSelectedSourceStudy(Study selectedSourceStudy) {
        this.selectedSourceStudy = selectedSourceStudy;
    }

    //endregion

    //region SelectedSourceStudySite

    public de.dktk.dd.rpb.core.domain.edc.Study getSelectedSourceStudySite() {
        return this.selectedSourceStudySite;
    }

    public void setSelectedSourceStudySite(de.dktk.dd.rpb.core.domain.edc.Study selectedSourceStudySite) {
        this.selectedSourceStudySite = selectedSourceStudySite;
    }

    //endregion

    //region SourceSubjectsList

    public List<StudySubject> getSourceSubjectList() {
        return this.sourceSubjectList;
    }

    public void setSourceSubjectList(List<StudySubject> list) {
        this.sourceSubjectList = list;
    }

    //endregion

    //region SelectedSourceSubject

    public StudySubject getSelectedSourceSubject() {
        return this.selectedSourceSubject;
    }

    public void setSelectedSourceSubject(StudySubject subject) {
        this.selectedSourceSubject = subject;
    }

    //endregion

    //region SourceSubjectEventList

    public List<EventData> getSourceSubjectEventList() {
        return this.sourceSubjectEventList;
    }

    public void setSourceSubjectEventList(List<EventData> sourceSubjectEventList) {
        this.sourceSubjectEventList = sourceSubjectEventList;
    }

    //endregion

    //region SelectedSourceSubjectEvent

    public EventData getSelectedSourceSubjectEvent() {
        return selectedSourceSubjectEvent;
    }

    public void setSelectedSourceSubjectEvent(EventData selectedSourceSubjectEvent) {
        this.selectedSourceSubjectEvent = selectedSourceSubjectEvent;
    }

    //endregion

    //region SelectedSourceEventForm

    public FormData getSelectedSourceEventForm() {
        return this.selectedSourceEventForm;
    }

    public void setSelectedSourceEventForm(FormData selectedSourceEventForm) {
        this.selectedSourceEventForm = selectedSourceEventForm;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {

        // Load initial data
        this.load();
    }

    //endregion

    //region Commands

    /**
     * Reload user's active study metadata metadata
     */
    public void load() {
        try {
            this.studyIntegrationFacade.init(this.mainBean);

            // Target
            this.study = this.studyIntegrationFacade.loadStudy();
            this.loadStudySubjects();

            // Source
            this.sourceStudies = this.studyIntegrationFacade.loadStudies();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Reload study subjects from active target study site
     */
    public void loadStudySubjects() {
        try {
            if (this.getStudy() != null) {
                this.subjectList = this.studyIntegrationFacade.loadStudySubjects();
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Reload study subjects from selected source study site
     */
    public void loadSourceStudySubjects() {
        try {
            if (this.getSelectedSourceStudySite() != null) {
                this.sourceSubjectList = this.studyIntegrationFacade.loadStudySubjects(this.selectedSourceStudySite);
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void loadSubjectEvents() {
        try {
            if (this.getSelectedSubject() != null) {

                this.subjectEventList = this.studyIntegrationFacade.loadStudySubjectEvents(
                        this.mainBean.getActiveStudy().getOcoid(),
                        this.selectedSubject.getStudySubjectId()
                );
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void loadSourceSubjectEvents() {
        try {
            if (this.getSelectedSourceStudySite() != null && this.selectedSourceSubject != null) {

                this.sourceSubjectEventList = this.studyIntegrationFacade.loadStudySubjectEvents(
                        this.selectedSourceStudySite.getOid(),
                        this.selectedSourceSubject.getStudySubjectId()
                );
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void importData() {
        // TODO: Create valid CDISC ODM for selected target from source data
    }

    //endregion

    //region Private methods

    

    //endregion

}
