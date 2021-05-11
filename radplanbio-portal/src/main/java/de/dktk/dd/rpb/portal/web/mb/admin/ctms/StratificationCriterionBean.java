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

import de.dktk.dd.rpb.core.domain.criteria.DichotomousCriterion;
import de.dktk.dd.rpb.core.repository.ctms.IDichotomousCriterionRepository;
import de.dktk.dd.rpb.core.repository.support.Repository;
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
 * Bean for user administration of CTMS study stratification criteria
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbStratificationCriterion")
@Scope(value="view")
public class StratificationCriterionBean extends CrudEntityViewModel<DichotomousCriterion,Integer> {

    //region Injects

    //region Repository

    private IDichotomousCriterionRepository repository;

    /**
     * Get DichotomousCriterionRepository
     * @return DichotomousCriterionRepository
     */
    @Override
    public Repository<DichotomousCriterion, Integer> getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Members

    private List<String> criteriaList;
    private String selectedCrit;
    private DichotomousCriterion dichotomousCriterion;

    //endregion

    //region Constructors

    @Inject
    public StratificationCriterionBean(IDichotomousCriterionRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Properties

    public List<String> getCriteriaList() {
        return this.criteriaList;
    }

    public void setCriteriaList( List<String> crit) {
        this.criteriaList = crit;
    }

    public String getSelectedCrit() {
        return this.selectedCrit;
    }

    public void setSelectedCrit(String crit) {
        this.selectedCrit = crit;

        if ("DichotomousConstraint".equals(crit)) {
            this.dichotomousCriterion = new DichotomousCriterion();
            this.newEntity = dichotomousCriterion;
        }
    }

    public DichotomousCriterion getDichotomousCriterion() {
        return this.dichotomousCriterion;
    }

    public void setDichotomousCriterion(DichotomousCriterion dichotomousCriterion) {
        this.dichotomousCriterion = dichotomousCriterion;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        // Criteria list for binding (integer, double, string, date, option)
        this.criteriaList = new ArrayList<>();
        this.criteriaList.add("DichotomousConstraint");

        this.load();
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtCriteria:colCritName", "colCritName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    //endregion

}