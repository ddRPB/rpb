/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.edc.EventData;

import de.dktk.dd.rpb.core.repository.edc.IStudyEventRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel bean for StudyEvent
 *
 * @author tomas@skripcak.net
 * @since 19 Apr 2017
 */
@Named("mbStudyEvents")
@Scope(value="view")
public class StudyEventBean extends CrudEntityViewModel<EventData, Integer> {

    //region Injects

    //region Repository - Dummy

    private IStudyEventRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudyEventRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyEventRepository repository) {
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

    @Override
    public void load() {
        // NOOP
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }
    
    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudyEvents:colEventStartDate", "colEventStartDate", SortOrder.ASCENDING);
        if (results != null) {
            results = DataTableUtil.addSortOrder(results, ":form:tabView:dtStudyEvents:colEventName", "colEventName", SortOrder.ASCENDING);

            return results;
        }

        return new ArrayList<>();
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // Name
        results.add(Boolean.FALSE); // Description
        results.add(Boolean.TRUE); // Category
        results.add(Boolean.FALSE); // Type
        results.add(Boolean.TRUE); // Repeating
        results.add(Boolean.TRUE); // StartDate
        results.add(Boolean.FALSE); // Status

        return results;
    }

    //endregion

}