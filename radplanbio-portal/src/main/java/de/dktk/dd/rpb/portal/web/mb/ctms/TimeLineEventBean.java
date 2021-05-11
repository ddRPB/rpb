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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.ctms.TimeLineEvent;
import de.dktk.dd.rpb.core.repository.ctms.ITimeLineEventRepository;

import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for CTMS TimeLine Event
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbTimeLineEvent")
@Scope(value="view")
public class TimeLineEventBean extends CrudEntityViewModel<TimeLineEvent, Integer> {

    //region Injects

    //region Repository

    @Inject
    private ITimeLineEventRepository repository;

    /**
     * Get TimeLineEventRepository
     * @return TimeLineEventRepository
     */
    @Override
    public ITimeLineEventRepository getRepository() {
        return this.repository;
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
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return List of SortMeta elements
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtTimelineEntities:colEventStart", "colEventStart", SortOrder.ASCENDING);
        if (results != null) {

            List<SortMeta> nextResults = DataTableUtil.buildSortOrder(":form:tabView:dtTimelineEntities:colEventEnd", "colEventEnd", SortOrder.ASCENDING);
            if (nextResults != null) {
                results.addAll(nextResults);
            }

            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Need to build an initial column visibility list
     * @return List of Boolean visibility elements
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // name
        results.add(Boolean.FALSE); // description
        results.add(Boolean.TRUE); // type
        results.add(Boolean.TRUE); // startDate
        results.add(Boolean.TRUE); // endDate
        results.add(Boolean.TRUE); // organisation

        return results;
    }

    //endregion

}