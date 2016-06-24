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

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;

/**
 * Extends PrimeFaces {@link LazyDataModel}
 */
public abstract class GenericLazyDataModel<E extends Identifiable<PK>, PK extends Serializable> extends LazyDataModel<E> {

    // region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //region Members

    private boolean bypassFirstOffset = true;
    private int size = 0;

    protected E selectedEntity;
    protected E newEntity;
    protected E[] selectedRows;

    protected Repository<E, PK> repository;

    protected List<Boolean> columnVisibilityList;

    //endregion

    //region Constructors

    public GenericLazyDataModel(Repository<E, PK> repository) {
        this.repository = repository;
    }

    //endregion

    //region Properties

    //region Entity DataTable Properties

    //region Selected Entity Property

    /**
     * Get Selected Entity
     * @return selected entity - E
     */
    @SuppressWarnings("unused")
    public E getSelectedEntity() {
        return this.selectedEntity;
    }

    /**
     * Set Selected Entity
     * @param entity - E
     */
    @SuppressWarnings("unused")
    public void setSelectedEntity(E entity) {
        this.selectedEntity = entity;
    }

    //endregion

    //region Selected Entities Property

    @SuppressWarnings("unused")
    public void setSelectedRows(E[] selectedRows) {
        this.selectedRows = selectedRows;
    }

    @SuppressWarnings("unused")
    public E[] getSelectedRows() {
        return selectedRows;
    }

    //endregion

    //endregion

    //region New Entity

    /**
     * Get New Entity
     * @return NewRole - Entity
     */
    @SuppressWarnings("unused")
    public E getNewEntity() {
        return this.newEntity;
    }

    /**
     * Set New Entity
     * @param entity - Entity
     */
    @SuppressWarnings("unused")
    public void setNewSoftware(E entity) {
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

//    /**
//     * Action invoked from sub search pages used to select the target of an association.
//     */
//    public String select() {
//        return select(getRowData());
//    }
//
//    protected String select(E selectedRow) {
//        return getCallBack().selected(selectedRow);
//    }
//
//    /**
//     * React to mouse click and force the navigation depending on the context.
//     * When in sub mode, the select action is invoked otherwise the edit action is invoked.
//     */
//    public void onRowSelect(SelectEvent event) {
//        E selected = getSelectedRow();
//        if (selected != null) {
//            if (getCurrentConversation().getCurrentContext().isSub()) {
//                Faces.navigate(controller.select(selected));
//            } else if (controller.getPermission().canEdit(selected)) {
//                Faces.navigate(controller.edit(selected));
//            } else {
//                Faces.navigate(controller.view(selected));
//            }
//        }
//    }

    //endregion

    //region Abstract Methods

    protected abstract E getEntity(Map<String, Object> filters);

    protected abstract SearchParameters defaultOrder(SearchParameters searchParameters);

    //endregion

    //region Helpers

    /**
     * Default search parameters
     */
    public SearchParameters searchParameters() {
        return new SearchParameters() //
                .anywhere() //
                .caseInsensitive();
    }

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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public void onSearch() {
        bypassFirstOffset = true;
    }

    //endregion

}
