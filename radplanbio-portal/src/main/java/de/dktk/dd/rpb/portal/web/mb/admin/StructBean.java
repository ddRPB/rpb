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

import de.dktk.dd.rpb.core.domain.admin.RtStruct;
import de.dktk.dd.rpb.core.repository.admin.RtStructRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
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
 * Bean for administration of RtStruct
 *
 * @author tomas@skripcak.net
 * @since 27 Nov 2014
 */
@Named("mbStruct")
@Scope(value="view")
public class StructBean extends CrudEntityViewModel<RtStruct, Integer> {

    //region Injects

    //region Repository

    @Inject
    private RtStructRepository repository;

    /**
     * Get StructRepository
     * @return StructRepository
     */
    @SuppressWarnings("unused")
    @Override
    public RtStructRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructRepository
     * @param repository StructRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(RtStructRepository repository) {
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
     * Prepare new transient entity object for UI binding
     */
    public void prepareNewEntity() {
        this.newEntity = new RtStruct();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colStructIdentifier");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStructIdentifier");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<Boolean>();
        result.add(Boolean.TRUE); // Identifier
        result.add(Boolean.TRUE); // Name
        result.add(Boolean.FALSE); // Description
        result.add(Boolean.TRUE); // Type

        return result;
    }

    //endregion

}