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

package de.dktk.dd.rpb.portal.web.mb.admin.ctms;

import de.dktk.dd.rpb.core.domain.ctms.CurriculumVitaeItemType;
import de.dktk.dd.rpb.core.repository.admin.ctms.ICurriculumVitaeItemTypeRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
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
 * Bean for user administration of CTMS CV item types
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbCurriculumVitaeItemType")
@Scope(value="view")
public class CurriculumVitaeItemTypeBean extends CrudEntityViewModel<CurriculumVitaeItemType, Integer> {

    //region Injects

    //region Repository

    @Inject
    private ICurriculumVitaeItemTypeRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public ICurriculumVitaeItemTypeRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(ICurriculumVitaeItemTypeRepository repository) {
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
        this.loadTree();
    }

    //endregion

    //region Commands

    /**
     * Load tree structure from entities (need to have parent -> children relationship)
     */
    public void loadTree() {
        try {
            this.root = new DefaultTreeNode();
            List<CurriculumVitaeItemType> entitiesWithoutParent = new ArrayList<CurriculumVitaeItemType>();
            for (CurriculumVitaeItemType cvit : getRepository().find()) {
                if (cvit.getParent() == null) {
                    entitiesWithoutParent.add(cvit);
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
     * Prepare new transient entity object for UI binding
     */
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
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colTypeName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colTypeName");
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
        results.add(Boolean.TRUE); // Description
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
    private void buildChildrenNodes(TreeNode parent, List<CurriculumVitaeItemType> children) {
        for (CurriculumVitaeItemType cvit : children) {
            TreeNode tn = new DefaultTreeNode(cvit, parent);

            if (cvit.getChildren() != null) {
                this.buildChildrenNodes(tn, cvit.getChildren());
            }
        }
    }

    //endregion

}