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

import de.dktk.dd.rpb.core.domain.ctms.Organisation;
import de.dktk.dd.rpb.core.repository.ctms.IOrganisationRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.primefaces.component.api.UIColumn;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of CTMS organisations
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbOrganisation")
@Scope(value="view")
public class OrganisationBean extends CrudEntityViewModel<Organisation, Integer> {

    //region Injects

    //region Repository

    @Inject
    private IOrganisationRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IOrganisationRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IOrganisationRepository repository) {
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

    //region Commands

    public void loadTree() {
        try {
            this.root = new DefaultTreeNode();
            List<Organisation> entitiesWithoutParent = new ArrayList<Organisation>();
            for (Organisation o : getRepository().find()) {
                if (o.getParent() == null) {
                    entitiesWithoutParent.add(o);
                }
            }
            this.buildChildrenNodes(root, entitiesWithoutParent);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
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
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtEntities:colOrganisationName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column1);
        sm1.setSortField("colOrganisationName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    /**
     * Create list of booleans determining visibility of columns
     * @return list of booleans for visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        results.add(Boolean.TRUE); // Name
        results.add(Boolean.TRUE); // Parent

        return results;
    }

    //endregion

    //region Private

    /**
     * Recursively build the tree structure
     * @param parent parent node in graph
     * @param children node children
     */
    private void buildChildrenNodes(TreeNode parent, List<Organisation> children) {
        for (Organisation o : children) {
            TreeNode tn = new DefaultTreeNode(o, parent);

            if (o.getChildren() != null) {
                this.buildChildrenNodes(tn, o.getChildren());
            }
        }
    }

    //endregion

}