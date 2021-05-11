/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.dktk.dd.rpb.core.dao.support.OrderBy;
import de.dktk.dd.rpb.core.dao.support.OrderByDirection;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import de.dktk.dd.rpb.portal.web.util.TabBean;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;

/**
 * Extends PrimeFaces {@link LazyDataModel} to provide Generic CRUD lazy view model
 */
public abstract class GenericLazyDataModel<E extends Identifiable<PK>, PK extends Serializable> extends LazyDataModel<E> {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //region Members

    private boolean bypassFirstOffset = true;
    private int size = 0;

    protected List<Boolean> columnVisibilityList;
    protected E selectedEntity;
    protected E newEntity;
    protected E[] selectedEntities;

    protected Repository<E, PK> repository;

    protected TabBean tab = new TabBean();

    //endregion

    //region Constructors

    public GenericLazyDataModel(Repository<E, PK> repository) {
        this.repository = repository;
    }

    //endregion

    //region Properties

    //region Selected Entity Property

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

    //region Selected Entities Property

    public E[] getSelectedEntities() {
        return this.selectedEntities;
    }

    public void setSelectedEntities(E[] selectedEntities) {
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

    //region Size

    public Integer getSize() {
        return size;
    }

    //endregion

    //region ColumnVisibilityList

    public List<Boolean> getColumnVisibilityList() {
        return columnVisibilityList;
    }

    //endregion

    //region Tab

    public TabBean getTab() {
        return this.tab;
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

    /**
     * React to mouse click on row
     */
    public void onRowSelect(SelectEvent event) {
        if (event.getObject() != null) {
            this.setSelectedEntity((E) event.getObject());
        }
    }

    //endregion

    //region Overrides

    @Override
    public String getRowKey(E item) {
        return String.valueOf(item.hashCode());
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getRowData(String rowKey) {
        for (E item : ((List<E>) getWrappedData())) {
            if (rowKey.equals(getRowKey(item))) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        // Create example entity according to filters
        E example = this.getEntity(filters);

        // Construct search parameters
        SearchParameters sp = this.populateSearchParameters(first, pageSize, sortField, sortOrder, filters);

        // Determine how many records conform provided search criteria
        this.setRowCount(this.repository.findCount(example, sp));

        return this.repository.find(example, sp);
    }

    @Override
    public List<E> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        // Create example entity according to filters
        E example = this.getEntity(filters);

        // TODO: implement multi sort criteria for populating
        // Construct search parameters
        SearchParameters sp = this.populateSearchParameters(first, pageSize, "", SortOrder.ASCENDING, filters);

        // Determine how many records conform provided search criteria
        this.setRowCount(this.repository.findCount(example, sp));

        return this.repository.find(example, sp);
    }

    @Override
    public void setRowCount(int rowCount) {
        super.setRowCount(rowCount);
        this.size = rowCount;
    }

    //endregion

    //region Commands

    /**
     * Prepare new entity for binding
     */
    public void doPrepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Prepare new entity with defaults for binding
     */
    public void doPrepareNewEntityWithDefaults() {
        this.newEntity = this.repository.getNewWithDefaults();
    }

    /**
     * Insert a new entity into repository
     */
    public void doCreateEntity() {
        try {
            // Persist
            this.repository.save(this.newEntity);
            this.selectedEntity = this.newEntity;
            this.repository.refresh(this.selectedEntity);

            this.messageUtil.infoEntity("status_saved_ok", this.newEntity);
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
            this.selectedEntity = this.repository.merge(
                    this.selectedEntity
            );
            this.messageUtil.infoEntity("status_saved_ok", this.selectedEntity);
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
            this.repository.delete(selectedEntity);

            this.messageUtil.infoEntity("status_deleted_ok", this.selectedEntity);
            this.selectedEntity = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Abstract Methods

    /**
     * Get entity example which will be use for query
     * @param filters filter values from viewModel
     * @return entity for query by example
     */
    protected abstract E getEntity(Map<String, Object> filters);

    /**
     * Get newly initialised SearchParameters for certain viewModel
     * @return searchParameters
     */
    protected abstract SearchParameters searchParameters();

    /**
     * Extent provided searchParameters about default order from certain viewModel
     * @param searchParameters provided searchParameters
     * @return searchParameters extended about default order
     */
    protected abstract SearchParameters defaultOrder(SearchParameters searchParameters);

    //endregion

    //region Helpers

    /**
     * Convert PrimeFaces {@link SortOrder} to our {@link OrderByDirection}.
     */
    protected OrderByDirection convert(SortOrder order) {
        return order == SortOrder.DESCENDING ? OrderByDirection.DESC : OrderByDirection.ASC;
    }

    /**
     * Applies the passed parameters to the passed SearchParameters.
     * @return the passed searchParameters
     */
    protected SearchParameters populateSearchParameters(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        SearchParameters sp = this.searchParameters();
        sp.setFirstResult(this.bypassFirstOffset ? 0 : first);
        this.bypassFirstOffset = false;
        sp.setMaxResults(pageSize);

        // Sort field defined via UI
        if (isNotEmpty(sortField)) {
            return sp.orderBy(new OrderBy(sortField, this.convert(sortOrder)));
        }
        // Otherwise use the default order for specific entity
        else {
            sp = this.defaultOrder(sp);
        }

        return sp;
    }

    /**
     * _HACK_
     * Call it from your view when a <code>search</code> event is triggered to bypass offset sent by primefaces paginator.
     */
    public void onSearch() {
        bypassFirstOffset = true;
    }

    //endregion

}
