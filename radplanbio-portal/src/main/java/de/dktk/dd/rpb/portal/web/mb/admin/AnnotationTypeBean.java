/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.edc.AnnotationType;
import de.dktk.dd.rpb.core.repository.admin.AnnotationTypeRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of RadPlanBio annotation types
 *
 * These annotation types are used when defining eCRF annotations
 * Annotations give a specific meaning to rather flexible eCRF item fields
 *
 * @author tomas@skripcak.net
 * @since 13 Sep 2013
 */
@Named("mbAnnotationType")
@Scope("view")
public class AnnotationTypeBean extends CrudEntityViewModel<AnnotationType, Integer> {

    //region Injects

    //region Repository

    @Inject
    private AnnotationTypeRepository repository;

    /**
     * Get Repository
     * @return Repository
     */
    @SuppressWarnings("unused")
    @Override
    public AnnotationTypeRepository getRepository() {
        return this.repository;
    }

    /**
     * Set Repository
     * @param repository Repository
     */
    @SuppressWarnings("unused")
    public void setRepository(AnnotationTypeRepository repository) {
        this.repository = repository;
    }

    //endregion

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

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}
