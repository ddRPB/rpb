/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.ctms.StudyPhase;
import de.dktk.dd.rpb.core.repository.admin.ctms.IStudyPhaseRepository;
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
 * Bean for user administration of CTMS study phases
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbStudyPhase")
@Scope(value="view")
public class StudyPhaseBean extends CrudEntityViewModel<StudyPhase, Integer> {

    //region Injects

    //region Repository

    private IStudyPhaseRepository repository;

    /**
     * Get StudyPhaseRepository
     * @return StudyPhaseRepository
     */
    @Override
    public IStudyPhaseRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public StudyPhaseBean(IStudyPhaseRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = new ArrayList<>();
        this.columnVisibilityList.add(Boolean.TRUE); // Name
        this.columnVisibilityList.add(Boolean.TRUE); // Description

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
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colPhaseName", "colPhaseName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    //endregion

}