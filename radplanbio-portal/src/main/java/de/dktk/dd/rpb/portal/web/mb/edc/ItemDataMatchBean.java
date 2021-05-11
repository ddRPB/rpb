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

package de.dktk.dd.rpb.portal.web.mb.edc;


import de.dktk.dd.rpb.core.domain.edc.ItemDataMatch;
import de.dktk.dd.rpb.core.repository.edc.IItemDataMatchRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for ItemDataMatch
 *
 * @author tomas@skripcak.net
 * @since 30 Jun 2016
 */
@Named("mbItemDataMatch")
@Scope(value="view")
public class ItemDataMatchBean extends CrudEntityViewModel<ItemDataMatch, Integer> {

    //region Injects

    //region Repository - Dummy

    private IItemDataMatchRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IItemDataMatchRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IItemDataMatchRepository repository) {
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
//        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudySubjects:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
//        if (results != null) {
//            return results;
//        }

        return new ArrayList<>();
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
//        results.add(Boolean.TRUE); // SSID
//        results.add(Boolean.FALSE); // SecondaryID
//        results.add(Boolean.TRUE); // Gender
//        results.add(Boolean.TRUE); // EnrollmentDate
//        results.add(Boolean.TRUE); // StudyName

        return results;
    }

    //endregion

}
