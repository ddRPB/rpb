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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.repository.edc.ICrfFieldAnnotationRepository;

import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;

import org.primefaces.model.DualListModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
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

    //region Members

    private ICrfFieldAnnotationRepository repository;

    private List<EventDefinition> eventDefinitionList;
    private EventDefinition selectedEventDefinition;
    private FormDefinition selectedFormDefinition;
    private ItemGroupDefinition selectedItemGroupDefinition;

    private DualListModel<ItemDefinition> itemDefinitions;

    //endregion

    //region Constructors

    @Inject
    public CrfAnnotationBean(ICrfFieldAnnotationRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Properties

    //region Repository

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @Override
    public ICrfFieldAnnotationRepository getRepository() {
        return this.repository;
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
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
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
        this.itemDefinitions = new DualListModel<>(this.selectedItemGroupDefinition.getItemDefs(), new ArrayList<ItemDefinition>());
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

    public void prepareNewEntity(List<EventDefinition> eventDefinitions) {
        this.eventDefinitionList = eventDefinitions;
        this.prepareNewEntity();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colCrfAnnotationEventDefOid", "colCrfAnnotationEventDefOid", SortOrder.ASCENDING);
        List<SortMeta> results1 = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colCrfAnnotationItemDefOid", "colCrfAnnotationItemDefOid", SortOrder.ASCENDING);
        if (results1 != null) {
            if (results != null) {
                results.addAll(results1);
                return results;
            }
            else {
                return results1;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // Event
        result.add(Boolean.FALSE); // Form
        result.add(Boolean.FALSE); // Group
        result.add(Boolean.TRUE); //  Item
        result.add(Boolean.TRUE); // Annotation Type

        return result;
    }

    //endregion

}
