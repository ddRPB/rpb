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

import de.dktk.dd.rpb.core.domain.ctms.CurriculumVitaeItem;
import de.dktk.dd.rpb.core.repository.ctms.ICurriculumVitaeItemRepository;
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
 * Bean for administration of CTMS organisations
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbCurriculumVitaeItem")
@Scope(value="view")
public class CurriculumVitaeItemBean extends CrudEntityViewModel<CurriculumVitaeItem, Integer> {

    //region Injects

    //region Repository

    @Inject
    private ICurriculumVitaeItemRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public ICurriculumVitaeItemRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(ICurriculumVitaeItemRepository repository) {
        this.repository = repository;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = new ArrayList<Boolean>();
        this.columnVisibilityList.add(Boolean.TRUE); // Name
        this.columnVisibilityList.add(Boolean.TRUE); // Type
        this.columnVisibilityList.add(Boolean.TRUE); // Start date
        this.columnVisibilityList.add(Boolean.TRUE); // End date
        this.columnVisibilityList.add(Boolean.TRUE); // Employer
        this.columnVisibilityList.add(Boolean.FALSE); // Workplace

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
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtCvEntities:colItemStartDate");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column1);
        sm1.setSortField("colItemStartDate");
        sm1.setSortOrder(SortOrder.DESCENDING);

        results.add(sm1);


        return results;
    }

    //endregion

}