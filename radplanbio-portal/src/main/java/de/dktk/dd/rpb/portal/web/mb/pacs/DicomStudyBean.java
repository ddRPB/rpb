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

package de.dktk.dd.rpb.portal.web.mb.pacs;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.repository.pacs.IDicomStudyRepository;
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
 * Bean for DicomStudy
 *
 * @author tomas@skripcak.net
 * @since 02 Nov 2015
 */
@Named("mbDicomStudy")
@Scope(value="view")
public class DicomStudyBean extends CrudEntityViewModel<DicomStudy, Integer> {

    //region Injects

    //region Repository - Dummy

    private IDicomStudyRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IDicomStudyRepository getRepository() {
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

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results =  DataTableUtil.buildSortOrder(":form:tabView:dtDicomStudies:colDicomStudyUid", "colDicomStudyUid", SortOrder.ASCENDING);

        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // UID
        results.add(Boolean.TRUE); // Description
        results.add(Boolean.TRUE); // Type
        results.add(Boolean.TRUE); // Date

        return results;
    }

    //endregion

}