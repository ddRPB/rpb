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

package de.dktk.dd.rpb.portal.web.mb.support;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.Named;
import de.dktk.dd.rpb.core.domain.Personed;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import de.dktk.dd.rpb.portal.web.util.TabBean;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.TreeNode;
import org.primefaces.model.Visibility;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Simple generic ViewModel for single domain entity which allows basic CRUD operations
 *
 * @author tomas@skripcak.net
 * @since 27 Nov 2014
 */
public abstract class CrudEntityViewModel<E extends Identifiable<PK>, PK extends Serializable>  implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    protected TreeNode root; // Data container for tree table

    protected List<E> entityList;
    protected List<E> filteredEntities;

    protected E selectedEntity;
    protected List<E> selectedEntities;

    protected E newEntity;
    protected E editMultiEntity;

    protected List<Boolean> columnVisibilityList;
    protected List<SortMeta> preSortOrder;

    protected TabBean tab = new TabBean();

    //endregion

    //region Properties

    //region DataTable - Entity

    //region Root - TreeView

    public TreeNode getRoot() {
        return this.root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    //endregion

    //region EntityList

    /**
     * Get Entity List
     * List is sorted according to name when E implements Named interface
     * List is sorted according to surname and firstname when E implements Personed interface
     * Sorting is useful when loading entities into simple UI element like combobox
     * @return List - Entity List
     */
    public List<E> getEntityList() {
        if (this.entityList != null && this.entityList.size() > 0) {
            E entity = this.entityList.get(0);

            // Named entities sort by name
            if (entity instanceof Named) {
                Collections.sort(this.entityList, new Comparator<E>() {
                    public int compare(E s1, E s2) {
                        String name1 = ((Named) s1).getName() != null ? ((Named) s1).getName() : "";
                        String name2 = ((Named) s2).getName() != null ? ((Named) s2).getName() : "";

                        return (name1.compareToIgnoreCase(name2));
                    }
                });
            }
            // Personed entities sort by surname and firstname
            else if (entity instanceof Personed) {
                Collections.sort(this.entityList, new Comparator<E>() {
                    public int compare(E s1, E s2) {
                        String surname1 = ((Personed) s1).getSurname() != null ? ((Personed) s1).getSurname() : "";
                        String surname2 = ((Personed) s2).getSurname() != null ? ((Personed) s2).getSurname() : "";
                        int surnameComp = surname1.compareTo(surname2);

                        if (surnameComp != 0) {
                            return surnameComp;
                        }
                        else {
                            String firstname1 = ((Personed) s1).getFirstname() != null ? ((Personed) s1).getFirstname() : "";
                            String firstname2 = ((Personed) s2).getFirstname() != null ? ((Personed) s2).getFirstname() : "";

                            return firstname1.compareTo(firstname2);
                        }
                    }
                });
            }
        }

        return this.entityList;
    }

    /**
     * Set Entity List
     * @param list - Entity List
     */
    public void setEntityList(List<E> list) {
        this.entityList = list;
    }

    //endregion

    //region FilteredEntities

    /**
     * Get Filtered Entities List
     * @return List - Entities List
     */
    public List<E> getFilteredEntities() {
        return this.filteredEntities;
    }

    /**
     * Set Filtered Entities List
     * @param list - Entities List
     */
    public void setFilteredEntities(List<E> list) {
        this.filteredEntities = list;
    }

    //endregion

    //region SelectedEntity

    /**
     * Get Selected Entity
     * @return selected entity - E
     */
    public E getSelectedEntity() {
        return this.selectedEntity;
    }

    /**
     * Set Selected Entity
     * @param entity - E
     */
    public void setSelectedEntity(E entity) {
        this.selectedEntity = entity;
    }

    //endregion

    //region SelectedEntities

    public List<E> getSelectedEntities() {
        return this.selectedEntities;
    }

    public void setSelectedEntities(List<E> selectedEntities) {
        this.selectedEntities = selectedEntities;
    }

    //endregion

    //region New Entity

    /**
     * Get New Entity
     * @return NewRole - Entity
     */
    public E getNewEntity() {
        return this.newEntity;
    }

    /**
     * Set New Entity
     * @param entity - Entity
     */
    public void setNewEntity(E entity) {
        this.newEntity = entity;
    }

    //endregion

    // region Edit MultiEntity

    public E getEditMultiEntity() {
        return this.editMultiEntity;
    }

    public void setEditMultiEntity(E entity) {
        this.editMultiEntity = entity;
    }

    //endregion

    //region ColumnVisibilityList

    public List<Boolean> getColumnVisibilityList() {
        return columnVisibilityList;
    }

    public void setColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.columnVisibilityList = columnVisibilityList;
    }

    //endregion

    //endregion

    //region Tab

    public TabBean getTab() {
        return this.tab;
    }

    //endregion

    //region PreSortOrder

    public List<SortMeta> getPreSortOrder() {
        return this.preSortOrder;
    }

    public void setPreSortOrder(List<SortMeta> sortOrder) {
        this.preSortOrder = sortOrder;
    }

    //endregion

    //endregion

    //region EventHandlers

    public void onEntityToggle(ToggleEvent e) {
        try {
            this.columnVisibilityList.set(((Integer) e.getData() - 1), e.getVisibility() == Visibility.VISIBLE);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Commands

    /**
     * Load entity list and store into ViewModel for binding
     */
    public void load() {
        try {
            // Load fresh list of entities from repository
            this.entityList = getRepository().find();

            this.messageUtil.info("status_loaded_ok");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Insert a new entity into repository
     */
    public void doCreateEntity() {
        try {
            // Persist
            this.getRepository().save(this.newEntity);
            this.selectedEntity = this.newEntity;
            this.getRepository().refresh(this.selectedEntity);

            this.messageUtil.infoEntity("status_saved_ok", this.newEntity);

            // Reload
            this.entityList = getRepository().find();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Update selected entity in repository
     */
    public void doUpdateEntity() {
        try {
            // Commit changes
            this.selectedEntity = this.getRepository().merge(this.selectedEntity);
            
            this.messageUtil.infoEntity("status_saved_ok", this.selectedEntity);

            // Reload
            this.entityList = getRepository().find();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete selected entity from repository
     */
    public void doDeleteEntity() {
        try {
            // Delete
            this.getRepository().delete(selectedEntity);

            this.messageUtil.infoEntity("status_deleted_ok", this.selectedEntity);
            this.selectedEntity = null;

            // Reload
            this.entityList = getRepository().find();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Methods

//    /**
//     * Load the passed 'x-to-many' association.
//     */
//    protected void loadCollection(Collection<?> collection) {
//        if (collection != null) {
//            collection.size();
//        }
//    }
//
//    /**
//     * Load the passed 'x-to-one' association.
//     */
//    protected void loadSingular(Object association) {
//        if (association != null) {
//            association.toString();
//        }
//    }

    //endregion

    //region Abstract Methods

    /**
     * Get Repository
     * @return repository
     */
    protected abstract Repository<E, PK> getRepository();

    /**
     * Prepare new entity
     */
    public abstract void prepareNewEntity();

    /**
     * Need to build an initial sort order for data table multi sort
     */
    protected abstract List<SortMeta> buildSortOrder();

    //endregion

}
