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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.AbstractConstraint;
import de.dktk.dd.rpb.core.domain.criteria.constraints.DichotomousConstraint;
import de.dktk.dd.rpb.core.domain.ctms.*;
import de.dktk.dd.rpb.core.domain.randomisation.AbstractRandomisationData;
import de.dktk.dd.rpb.core.domain.randomisation.BlockRandomisationConfiguration;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.repository.admin.ctms.IStudyTagTypeRepository;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for user administration of CTMS studies
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbCtmsStudy")
@Scope(value="view")
public class StudyBean extends CrudEntityViewModel<Study, Integer> {

    //region Injects

    //region Repository

    @Inject
    private IStudyRepository repository;

    /**
     * Get StudyRepository
     * @return StudyRepository
     */
    @Override
    public IStudyRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StudyRepository
     * @param repository StudyRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyRepository repository) {
        this.repository = repository;
    }

    @Inject
    IStudyTagTypeRepository tagTypeRepository;

    @SuppressWarnings("unused")
    public void setTagTypeRepository(IStudyTagTypeRepository tagTypeRepository) {
        this.tagTypeRepository = tagTypeRepository;
    }

    //endregion

    //endregion

    //region Members

    // region Study

    private List<StudyTag> tagSearchCriteria;
    private StudyTag newTag;
    private List<String> tagBooleanList;

    private List<TumourEntity> assignedTumourEntities;

    //endregion

    //region Randomisation

    private BlockRandomisationConfiguration randomisationConf;

    //endregion

    //endregion

    //region Properties

    //region AssignedTumourEntities

    public List<TumourEntity> getAssignedTumourEntities() {
        return this.assignedTumourEntities;
    }

    public void setAssignedTumourEntities(List<TumourEntity> tes) {
        this.assignedTumourEntities = tes;
    }

    //endregion

    //region TagSearchCriteria

    public List<StudyTag> getTagSearchCriteria() {
        return this.tagSearchCriteria;
    }

    public void setTagSearchCriteria(List<StudyTag> criteria) {
        this.tagSearchCriteria = criteria;
    }

    //endregion

    //region NewStudyTag

    public StudyTag getNewTag() {
        return this.newTag;
    }

    public void setNewTag(StudyTag tag) {
        this.newTag = tag;
    }

    //endregion

    //region TagBooleanList

    public List<String> getTagBooleanList() {
        return this.tagBooleanList;
    }

    //endregion

    //region ProtocolTypes

    public EnumProtocolType[] getProtocolTypes() {
        return EnumProtocolType.values();
    }

    //endregion

    //region TimeLineEventsModel

    public TimelineModel getTimeLineEventsModel() {
        TimelineModel model = new TimelineModel();

        if (this.selectedEntity != null && this.selectedEntity.getTimeLineEvents() != null) {
            for (TimeLineEvent tle : this.selectedEntity.getTimeLineEvents()) {
                model.add(
                    new TimelineEvent(
                            tle,
                            tle.getStartDate(),
                            tle.getEndDate()
                    )
                );
            }
        }

        return model;
    }

    //endregion

    //region Randomisation

    public BlockRandomisationConfiguration getRandomisationConf() {
        if (this.selectedEntity != null && this.selectedEntity.getRandomisationConfiguration() != null ) {
            this.randomisationConf = (BlockRandomisationConfiguration)this.selectedEntity.getRandomisationConfiguration();
        }
        else if (this.selectedEntity != null && this.selectedEntity.getRandomisationConfiguration() == null) {
            this.randomisationConf = new BlockRandomisationConfiguration();
            this.randomisationConf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
        }

        return this.randomisationConf;
    }

    public void setRandomisationConf(BlockRandomisationConfiguration conf) {
        this.randomisationConf = conf;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        // Initialise study tag search criteria
        this.tagSearchCriteria = new ArrayList<StudyTag>();

        // To enabled editing in cell PrimeFaces requires drop down list component
        this.tagBooleanList = new ArrayList<String>();
        this.tagBooleanList.add(Boolean.FALSE.toString().toLowerCase());
        this.tagBooleanList.add(Boolean.TRUE.toString().toLowerCase());

        // Setup main DataTable column visibility
        this.setColumnVisibilityList(
            this.buildColumnVisibilityList()
        );
        // Setup main DataTable ordering (necessary for multi sort)
        this.setPreSortOrder(
            this.buildSortOrder()
        );

        // Load studies
        this.load();
    }

    //endregion

    //region Commands

    //region TumourEntities

    public void prepareNewTumourEntities() {
        this.assignedTumourEntities = new ArrayList<TumourEntity>();
    }

    public void doAddTumourEntities(List<TumourEntity> tumourEntities) {
        this.selectedEntity.addTumourEntities(tumourEntities);
        this.doUpdateEntity();
    }

    public void doRemoveTumourEntity(TumourEntity te) {
        this.selectedEntity.removeTumourEntity(te);
        this.doUpdateEntity();
    }

    //endregion

    //region StudyTags

    public void prepareNewStudyTag() {
        this.newTag = new StudyTag();
    }

    public void saveNewStudyTag() {
        try {
            if (this.newEntity != null) {
                this.newEntity.addTag(this.newTag);
            }
            else {
                this.selectedEntity.addTag(this.newTag);
            }
            this.newTag = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void cancelNewStudyTag() {
        this.newTag = null;
    }

    public void saveNewTagTypeCriteria() {
        try {
            this.tagSearchCriteria.add(this.newTag);
            this.newTag = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void searchStudy() {
        // Query by example cannot handle this object graph, that is why I filter it manually
        if (this.entityList == null) {
            this.entityList = this.repository.find();
        }

        // Implementing filter with AND connections
        List<Study> studyFilter;

        // Run it on filtered entities when filter is present
        if (this.filteredEntities != null) {
            studyFilter = this.buildAndStudyFilter(this.filteredEntities);
            this.filteredEntities = studyFilter;
            this.entityList = studyFilter;
        }
        // Run it on all entities
        else {
            studyFilter = this.buildAndStudyFilter(this.entityList);
            this.entityList = studyFilter;
        }
    }

    public void initiliseMissingRequiredTags(List<StudyTagType> requiredStudyTagTypes) {
        this.selectedEntity.initialiseAllRequiredTags(
                requiredStudyTagTypes
        );
    }

    //endregion

    //region TimeLineEvents

    public void doAddNewEvent(TimeLineEvent event) {
        this.selectedEntity.addTimeLineEvent(event);
        this.doUpdateEntity();
    }

    public void doRemoveSelectedEvent(TimeLineEvent event) {
        this.selectedEntity.removeTimeLineEvent(event);
        this.doUpdateEntity();
    }

    //endregion

    //region StudyPersonnel

    public void doAddNewPresonnel(StudyPerson studyPerson) {
        this.selectedEntity.addStudyPersonnel(studyPerson);
        this.doUpdateEntity();
    }

    public void doRemoveSelectedPersonnel(StudyPerson studyPerson) {
        this.selectedEntity.removeStudyPersonnel(studyPerson);
        this.doUpdateEntity();
    }

    //endregion

    //region StudyOrganisations

    public void doAddNewOrganisation(StudyOrganisation studyOrganisation) {
        this.selectedEntity.addStudyOrganisation(studyOrganisation);
        this.doUpdateEntity();
    }

    public void doRemoveSelectedOrganisation(StudyOrganisation studyOrganisation) {
        this.selectedEntity.removeStudyOrganisation(studyOrganisation);
        this.doUpdateEntity();
    }

    //endregion

    //region TreatmentArms

    public void doAddNewTreatmentArm(TreatmentArm arm) {
        this.selectedEntity.addTreatmentArm(arm);
        this.doUpdateEntity();
    }

    public void doRemoveSelectedTreatmentArm(TreatmentArm arm) {
        this.selectedEntity.removeTreatmentArm(arm);
        this.doUpdateEntity();
    }

    //endregion

    //region StratificationCriteria

    /**
     * Create a new criteria the study
     */
    public void doAddNewCriterion(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> newCriterion) {
        try {
            // Take options from criterion
            for (Object option : newCriterion.getConfiguredValues()) {
                // Setup constraints for criterion from each option
                List<String> constraintValue = new ArrayList<String>();
                constraintValue.add(option.toString());
                DichotomousConstraint co = new DichotomousConstraint(constraintValue);

                // And add the constraint to the criterion strata
                newCriterion.addStrata(co);
            }

            // Let study aggregate root take care about persisting this
            this.selectedEntity.addSubjectCriterion(newCriterion);
            this.doUpdateEntity();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete criteria from the study
     */
    public void doRemoveCriteria(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> selectedCriterion) {
        this.selectedEntity.removeSubjectCriterion(selectedCriterion);
        this.doUpdateEntity();
    }

    //endregion

    //region TrialSites

    /**
     * Assign new participating site to the study
     */
    public void doAddNewTrialSite(PartnerSite newSite) {
        this.selectedEntity.addParticipatingSite(newSite);
        this.doUpdateEntity();
    }

    /**
     * Delete participating site from the study
     */
    public void doRemoveTrialSite(PartnerSite site) {
        this.selectedEntity.removeParticipatingSite(site);
        this.doUpdateEntity();
    }

    //endregion

    //region Randomisation

    /**
     * Update randomisation setup for study
     */
    public void doUpdateRandomisation() {
        this.selectedEntity.setRandomisationConfiguration(this.randomisationConf);
        AbstractRandomisationData randData = this.randomisationConf.getData();
        this.doUpdateEntity();
    }

    //endregion

    //region StudyDocuments

    /**
     * After the document is uploaded the location can be persisted to the database
     */
    public void doAddNewStudyDoc(StudyDoc newStudyDoc) {
        this.selectedEntity.addDoc(newStudyDoc);
        this.doUpdateEntity();
    }

    /**
     * Delete selected study documentation form the study
     */
    public void doRemoveSelectedStudyDoc(StudyDoc selectedStudyDoc) {
        // Remove from DB
        this.selectedEntity.removeDoc(selectedStudyDoc);
        this.doUpdateEntity();

        // Try to remove from file system
        try {
            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
            File file = new File(extContext.getRealPath("//WEB-INF//files//" + "//" + this.selectedEntity.getId() + "//" + selectedStudyDoc.getFilename()));
            if (!file.delete()) {
                throw new Exception("Delete failed while deleting file from file system.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region EventHandlers

    public String onFlowProcess(FlowEvent event) {
//        if(skip) {
//            skip = false;   //reset in case user goes back
//            return "confirm";
//        }
//        else {
        return event.getNewStep();
        //}
    }

    public void onCellEdit(CellEditEvent event) {
        try {
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();
            if (newValue != null && !newValue.equals(oldValue)) {
                this.messageUtil.infoText(oldValue + " -> " + newValue);
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Overrides

    /**
     * Insert a new entity into repository
     */
    @Override
    public void doCreateEntity() {
        Boolean isValid = Boolean.TRUE;

        try {
            if (!this.newEntity.areTagsValid()) {
                isValid = Boolean.FALSE;
                throw new Exception("One or multiple of provided (required) StudyTags are empty.");
            }
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.errorText(err.getMessage());
        }

        if (isValid) {
            super.doCreateEntity();
        }
    }

    /**
     * Update selected entity in repository
     */
    @Override
    public void doUpdateEntity() {
        Boolean isValid = Boolean.TRUE;

        try {
            if (!this.selectedEntity.areTagsValid()) {
                isValid = Boolean.FALSE;
                throw new Exception("One or multiple of provided (required) StudyTags are empty.");
            }
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.errorText(err.getMessage());
        }

        if (isValid) {
            super.doUpdateEntity();
        }
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    public void prepareNewEntity(List<StudyTagType> requiredStudyTagTypes) {
        this.newEntity = this.repository.getNew();

        // Initialise all required tags
        for (StudyTagType stt : requiredStudyTagTypes) {
            this.newEntity.addTag(
                    new StudyTag(stt)
            );
        }
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colStudyTitle", "colStudyTitle", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<SortMeta>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<Boolean>();
        result.add(Boolean.TRUE); // short title
        result.add(Boolean.FALSE); // title
        result.add(Boolean.TRUE); // tumour entities
        result.add(Boolean.TRUE); // phase
        result.add(Boolean.TRUE); // sponsoring type
        result.add(Boolean.TRUE); // status
        result.add(Boolean.TRUE); // protocol type

        // All dynamic study tags are hidden
        int tagTypeCount = this.tagTypeRepository.find().size();
        for (int j = 0; j < tagTypeCount; j++) {
            result.add(Boolean.FALSE);
        }

        return result;
    }

    //endregion

    //region Private

    private List<Study> buildAndStudyFilter(List<Study> studies) {
        List<Study> studyFilter = new ArrayList<Study>();

        for (Study s : studies) {
            List<StudyTag> criteriaToMatch = new ArrayList<StudyTag>();
            criteriaToMatch.addAll(this.tagSearchCriteria);

            for (StudyTag st : s.getTags()) {
                for (StudyTag filterSt : this.tagSearchCriteria) {
                    if (st.getType().getId().equals(filterSt.getType().getId())) {
                        if (st.getValue().equals(filterSt.getValue())) {

                            StudyTag tagToRemove = null;
                            for (StudyTag ttr : criteriaToMatch) {
                                if (ttr.getType().getId().equals(st.getType().getId())) {
                                    tagToRemove = ttr;
                                    break;
                                }
                            }

                            if (tagToRemove != null) {
                                criteriaToMatch.remove(tagToRemove);
                            }
                        }
                    }
                }
            }

            if (criteriaToMatch.size() == 0) {
                studyFilter.add(s);
            }
        }

        return studyFilter;
    }

    //endregion

}