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

package de.dktk.dd.rpb.portal.web.mb.lab;

import de.dktk.dd.rpb.core.domain.lab.Lab;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;

import de.dktk.dd.rpb.core.repository.lab.ILabRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.model.SortMeta;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of LAB entities
 *
 * @author tomas@skripcak.net
 * @since 30 Aug 2017
 */
@Named("mbLab")
@Scope(value="view")
public class LabBean extends CrudEntityViewModel<Lab, Integer> {

    //region Injects

    //region Repository

    private final ILabRepository repository;

    /**
     * Get LabRepository
     * @return LabRepository
     */
    @Override
    public ILabRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public LabBean(ILabRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Commands

    public void prepareNewEntity(PartnerSite site) {
        this.prepareNewEntity();
        if (site != null) {
            site.setLab(this.newEntity);
        }
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
        List<SortMeta> results = new ArrayList<>();

        return results;
    }

    //endregion

}