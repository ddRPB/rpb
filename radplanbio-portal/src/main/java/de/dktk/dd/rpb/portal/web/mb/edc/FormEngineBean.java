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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.edc.FormEngine;
import de.dktk.dd.rpb.core.repository.edc.IFormEngineRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.primefaces.model.SortMeta;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of FormEngine entities
 *
 * @author tomas@skripcak.net
 * @since 14 Sep 2017
 */
@Named("mbFormEngine")
@Scope(value="view")
public class FormEngineBean extends CrudEntityViewModel<FormEngine, Integer> {

    //region Injects

    //region Repository

    private final IFormEngineRepository repository;

    /**
     * Get FormEngineRepository
     * @return FormEngineRepository
     */
    @Override
    public IFormEngineRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public FormEngineBean(IFormEngineRepository repository) {
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
            site.setFormEngine(this.newEntity);
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
