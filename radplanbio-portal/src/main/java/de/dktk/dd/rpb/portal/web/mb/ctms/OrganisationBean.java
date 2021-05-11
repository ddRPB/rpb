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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.ctms.Organisation;
import de.dktk.dd.rpb.core.repository.ctms.IOrganisationRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

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
     * Get OrganisationRepository
     * @return OrganisationRepository
     */
    @Override
    public IOrganisationRepository getRepository() {
        return this.repository;
    }

    /**
     * Set OrganisationRepository
     * @param repository OrganisationRepository
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
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colOrganisationName", "colOrganisationName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create list of booleans determining visibility of columns
     * @return list of booleans for visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // Name
        results.add(Boolean.TRUE); // Parent

        return results;
    }

    //endregion

    //region Private

    /**
     * Load tree structure from entities (need to have parent -> children relationship)
     */
    private void loadTree() {
        try {
            this.root = new DefaultTreeNode();
            List<Organisation> entitiesWithoutParent = new ArrayList<>();
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