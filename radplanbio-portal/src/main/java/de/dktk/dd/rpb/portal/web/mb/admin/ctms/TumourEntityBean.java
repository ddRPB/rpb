/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.admin.ctms;

import de.dktk.dd.rpb.core.domain.ctms.TumourEntity;
import de.dktk.dd.rpb.core.repository.admin.ctms.ITumourEntityRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for user administration of CTMS tumour entities
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbTumourEntity")
@Scope(value="view")
public class TumourEntityBean extends CrudEntityViewModel<TumourEntity, Integer> {

    //region Injects

    //region Repository

    @Inject
    private ITumourEntityRepository repository;

    /**
     * Get TumourEntityRepository
     * @return TumourEntityRepository
     */
    @Override
    public ITumourEntityRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(ITumourEntityRepository repository) {
        this.repository = repository;
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
        
        this.load();
    }

    //endregion

    //region Overrides

    /**
     * Load tree structure from entities (need to have parent -> children relationship)
     */
    @Override
    public void load() {
        super.load();
        this.loadTree();
    }

    /**
     * Insert a new entity into repository
     */
    @Override
    public void doCreateEntity() {
        super.doCreateEntity();
        this.loadTree();
    }

    /**
     * Update selected entity in repository
     */
    @Override
    public void doUpdateEntity() {
        super.doUpdateEntity();
        this.loadTree();
    }

    /**
     * Delete selected entity from repository
     */
    @Override
    public void doDeleteEntity() {
        super.doDeleteEntity();
        this.loadTree();
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of SortMeta for data table sorting
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<>();
        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        // TumourEntity administration View
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colTumourEntityName");
        if (column != null) {
            results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colTumourEntityName", "colTumourEntityName", SortOrder.ASCENDING);
            if (results != null) {
                return results;
            }
        }

        // Study TumourEntities View
        column = viewRoot.findComponent(":form:tabView:dtTumourEntities:colTumourEntityName");
        if (column != null) {
            results = DataTableUtil.buildSortOrder(":form:tabView:dtTumourEntities:colTumourEntityName", "colTumourEntityName", SortOrder.ASCENDING);
            if (results != null) {
                return results;
            }
        }

        return results;
    }

    /**
     * Create list of booleans determining visibility of columns
     * @return list of booleans for visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // Name
        result.add(Boolean.TRUE); // Description
        result.add(Boolean.TRUE); // Parent

        return result;
    }

    //endregion

    //region Private

    /**
     * Load tree structure from entities (need to have parent -> children relationship)
     */
    private void loadTree() {
        try {
            this.root = new DefaultTreeNode();
            List<TumourEntity> entitiesWithoutParent = new ArrayList<>();
            for (TumourEntity te : getRepository().find()) {
                if (te.getParent() == null) {
                    entitiesWithoutParent.add(te);
                }
            }
            this.buildChildrenNodes(root, entitiesWithoutParent);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Recursively build the tree structure
     * @param parent parent node in graph
     * @param children node children
     */
    private void buildChildrenNodes(TreeNode parent, List<TumourEntity> children) {
        for (TumourEntity te : children) {
            TreeNode tn = new DefaultTreeNode(te, parent);

            if (te.getChildren() != null) {
                this.buildChildrenNodes(tn, te.getChildren());
            }
        }
    }

    //endregion

}