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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.repository.edc.ICrfFieldAnnotationRepository;

import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of RadPlanBio study eCRF annotations
 *
 * eCRF annotations are used to define special semantic meaning withing RadPlanBio for some eCRF fields
 * As an example of such a semantics, one can annotate specific fields in eCRF which stores DICOM study UIDs
 * in order to query PACS subsystem in RadPlanBio
 *
 * @author tomas@skripcak.net
 * @since 01 Oct 2013
 */
@Named("mbCrfAnnotation")
@Scope("view")
public class CrfAnnotationBean extends CrudEntityViewModel<CrfFieldAnnotation, Integer> {

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

    @SuppressWarnings("unused")
    public void setStudyRepository(IStudyRepository value) {
        this.studyRepository = value;
    }

    //endregion

    //region Repository

    @Inject
    private ICrfFieldAnnotationRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public ICrfFieldAnnotationRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(ICrfFieldAnnotationRepository repository) {
        this.repository = repository;
    }

    //endregion

    //endregion

    //region Members

    private de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;

    private List<EventDefinition> eventDefinitionList;
    private EventDefinition selectedEventDefinition;
    private FormDefinition selectedFormDefinition;
    private ItemGroupDefinition selectedItemGroupDefinition;

    private DualListModel<ItemDefinition> itemDefinitions;

    //endregion

    //region Properties

    //region RadPlanBio Study

    public Study getRpbStudy() {
        return this.rpbStudy;
    }

    public void setRpbStudy(Study value) {
        this.rpbStudy = value;
    }

    //endregion

    //region Event Definition List

    public List<EventDefinition> getEventDefinitionList() {
        return this.eventDefinitionList;
    }

    public void setEventDefinitionList(List<EventDefinition> list) {
        this.eventDefinitionList = list;
    }

    //endregion

    //region Selected Event Definition

    public EventDefinition getSelectedEventDefinition() {
        return this.selectedEventDefinition;
    }

    public void setSelectedEventDefinition(EventDefinition value) {
        this.selectedEventDefinition = value;
    }

    //endregion

    //region Selected Form Definition

    public FormDefinition getSelectedFormDefinition() {
        return this.selectedFormDefinition;
    }

    public void setSelectedFormDefinition(FormDefinition value) {
        this.selectedFormDefinition = value;
    }

    //endregion

    //region Selected Item Group Definition

    public ItemGroupDefinition getSelectedItemGroupDefinition() {
        return this.selectedItemGroupDefinition;
    }

    public void setSelectedItemGroupDefinition(ItemGroupDefinition value) {
        this.selectedItemGroupDefinition = value;
    }

    //endregion

    //region Item PickList

    public DualListModel<ItemDefinition> getItemDefinitions() {
        return  this.itemDefinitions;
    }

    public void setItemDefinitions(DualListModel<ItemDefinition> model) {
        this.itemDefinitions = model;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        // Load data
        this.loadCrfFieldAnnotations();
        this.loadStudyMetadata();
    }

    //endregion

    //region Commands

    //region  Select One Menu Helpers

    /**
     *
     */
    public void reloadEventForms() {
        try {
            for (EventDefinition ed: this.eventDefinitionList) {
                if (ed.getOid().equals(this.newEntity.getEventDefinitionOid())) {
                    this.setSelectedEventDefinition(ed);
                    break;
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     *
     */
    public void reloadFormGroups() {
        try {
            for (FormDefinition fd: this.selectedEventDefinition.getFormDefs()) {
                if (fd.getOid().equals(this.newEntity.getFormOid())) {
                    this.setSelectedFormDefinition(fd);
                    break;
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     *
     */
    public void reloadGroupItems() {
        try {
            for (ItemGroupDefinition igd: this.selectedFormDefinition.getItemGroupDefs()) {
                if (igd.getOid().equals(this.newEntity.getGroupOid())) {
                    this.setSelectedItemGroupDefinition(igd);
                    break;
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     *
     *
     */
    public void reloadItems() {

        // Prepare model for PickList
        this.itemDefinitions = new DualListModel<ItemDefinition>(this.selectedItemGroupDefinition.getItemDefs(), new ArrayList<ItemDefinition>());
    }

    //endregion

    //region  Reload

    /**
     * Reload eCRF annotation data for active study for
     */
    public void loadCrfFieldAnnotations() {
        try {
            // Reload eCRF annotations and their possible types form DB
            if (this.mainBean.getActiveStudy().getParentStudy() != null) {
                this.rpbStudy = this.studyRepository.getByOcStudyIdentifier((this.mainBean.getActiveStudy().getParentStudy().getUniqueIdentifier()));
            }
            else {
                this.rpbStudy = this.studyRepository.getByOcStudyIdentifier(this.mainBean.getActiveStudy().getUniqueIdentifier());
            }
            if (this.rpbStudy == null) {
                throw new Exception("There is no RadPlanBio study associated to your current active OpenClinica study!");
            }

            // Reset selected
            this.selectedEventDefinition = null;
            this.selectedFormDefinition = null;
            this.selectedItemGroupDefinition = null;
            this.itemDefinitions = null;

        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Reload study metadata from EDC OpenClinica
     */
    public void loadStudyMetadata() {
        try {
            // Get ODM metadata for user's active study
            MetadataODM metadata =  this.mainBean.getOpenClinicaService()
                    .getStudyMetadata(
                            this.mainBean.getActiveStudy().getUniqueIdentifier()
                    );

            // XML to DomainObjects
            Odm odm = metadata.unmarshallOdm();
            odm.updateHierarchy();
            this.eventDefinitionList = odm.getStudyByOid(this.mainBean.getActiveStudy().getOcoid())
                    .getMetaDataVersion()
                    .getStudyEventDefinitions();
        }
        catch (Exception err) {
            messageUtil.error(err);
        }
    }

    //endregion

    //region eCRF annotation CRUD

    /**
     * Create a new eCRF field annotation for a study
     */
    public void doCreateAnnotation() {
        try {

            // Collect selection
            for (ItemDefinition selectedItem : this.itemDefinitions.getTarget()) {
                this.newEntity = new CrfFieldAnnotation(this.newEntity);
                this.newEntity.setStudy(this.rpbStudy);
                this.newEntity.setCrfItemOid(selectedItem.getOid());
                this.rpbStudy.addCrfFieldAnnotation(this.newEntity);
            }

            // Commit
            this.studyRepository.merge(this.rpbStudy);

            // Info
            this.messageUtil.infoText("Insert Successful: eCRF annotation(s): " +
                    newEntity.getAnnotationType().getName() +
                    " successfuly assigned to eCRF field: " +
                    newEntity.getCrfItemOid() +
                    " of " + this.rpbStudy.getOcStudyIdentifier() + " study.");

            // Reload annotations form DB
            this.loadCrfFieldAnnotations();

            // Prepare new
            this.setNewEntity(new CrfFieldAnnotation());
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Update existing eCRF field annotation
     * @param actionEvent action event
     */
    @SuppressWarnings("unused")
    public void doUpdateAnnotation(ActionEvent actionEvent){
        try {
            this.studyRepository.merge(this.rpbStudy);

            this.messageUtil.infoText("Edit Successful: Annotation for eCRF field: " +
                    this.selectedEntity.getCrfItemOid() +
                    " successfuly updated.");

            // Reload annotations form DB
            this.loadCrfFieldAnnotations();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete existing eCRF field annotation
     * @param actionEvent action event
     */
    @SuppressWarnings("unused")
    public void doDeleteAnnotation(ActionEvent actionEvent){
        try {
            this.rpbStudy.removeCrfFieldAnnotation(this.selectedEntity);
            this.studyRepository.merge(this.rpbStudy);

            this.messageUtil.infoText("Delete Successful: Annotation for eCRF field: " +
                    this.selectedEntity.getCrfItemOid() +
                    " successfuly deleted.");

            // Reload annotations form DB
            this.loadCrfFieldAnnotations();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtEntities:colCrfAnnotationEventDefOid");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column1);
        sm1.setSortField("colCrfAnnotationEventDefOid");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        UIComponent column2 = viewRoot.findComponent(":form:tabView:dtEntities:colCrfAnnotationItemDefOid");

        SortMeta sm2 = new SortMeta();
        sm2.setSortBy((UIColumn) column2);
        sm2.setSortField("colCrfAnnotationItemDefOid");
        sm2.setSortOrder(SortOrder.ASCENDING);

        results.add(sm2);

        return results;
    }

    //endregion

}
