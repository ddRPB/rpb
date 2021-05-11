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

package de.dktk.dd.rpb.portal.web.mb.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.inject.Inject;
import javax.inject.Named;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;

import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

/**
 * Bean for administration of PartnerSites
 *
 * @author tomas@skripcak.net
 * @since 07 August 2013
 */
@Named("mbPartnerSite")
@Scope("view")
public class PartnerSiteBean extends CrudEntityViewModel<PartnerSite, Integer> {

    //region Injects

    //region Repository

    IPartnerSiteRepository repository;

    public IPartnerSiteRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public PartnerSiteBean(IPartnerSiteRepository IPartnerSiteRepository) {
        this.repository = IPartnerSiteRepository;
    }

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
     * @return List of SortMeta objects
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colSiteIdentifier", "colSiteIdentifier", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }
        results = DataTableUtil.buildSortOrder(":form:tabView:dtTrialSites:colSiteIdentifier", "colSiteIdentifier", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // Identifier
        result.add(Boolean.TRUE); // Name
        result.add(Boolean.TRUE); // IsEnabled
        result.add(Boolean.FALSE); // Latitude
        result.add(Boolean.FALSE); // Longitude

        return result;
    }

    //endregion

}