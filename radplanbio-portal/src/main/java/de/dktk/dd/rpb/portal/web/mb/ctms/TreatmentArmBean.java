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

import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.repository.ctms.ITreatmentArmRepository;
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
 * Bean for administration of CTMS study treatment arms
 * arms are used as subject groups but also for randomisation
 *
 * @author tomas@skripcak.net
 * @since 07 August 2015
 */
@Named("mbTreatmentArm")
@Scope(value="view")
public class TreatmentArmBean extends CrudEntityViewModel<TreatmentArm, Integer> {

    //region Injects

    //region Repository

    @Inject
    private ITreatmentArmRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public ITreatmentArmRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(ITreatmentArmRepository repository) {
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
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtArms:colArmName", "colArmName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // Name
        results.add(Boolean.FALSE); // Description
        results.add(Boolean.TRUE); // Planned Nr. of subjects
        results.add(Boolean.TRUE); // IsEnabled

        return results;
    }

    //endregion

}