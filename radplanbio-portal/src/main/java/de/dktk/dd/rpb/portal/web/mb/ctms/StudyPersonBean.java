/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.ctms.IStudyPersonRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import org.springframework.context.annotation.Scope;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of CTMS study personnel
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbStudyPerson")
@Scope(value="view")
public class StudyPersonBean extends CrudEntityViewModel<StudyPerson, Integer> {

    //region Injects

    //region Repository

    @Inject
    private IStudyPersonRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudyPersonRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {

        // Build Visibility List according to View
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );

        // Build Sort Order according to View
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();
    }

    //endregion

    //region Commands

    public void prepareEditMultiEntity() {
        this.editMultiEntity = this.repository.getNew();
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
     * @return List of SortMeta entities
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results;

        // Study Personnel View
        results = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colPersonSurname", "colPersonSurname", SortOrder.ASCENDING);
        if (results != null) {

            List<SortMeta> results1 = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colPersonFirstname", "colPersonFirstname", SortOrder.ASCENDING);
            if (results1 != null) {
                results.addAll(results1);

                List<SortMeta> results2 = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colPersonRole", "colPersonRole", SortOrder.ASCENDING);
                if (results2 != null) {
                    results.addAll(results2);
                }
            }

            return results;
        }

        // Person Studies View
        results = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colStudyName", "colStudyName", SortOrder.ASCENDING);
        if (results != null) {

            List<SortMeta> results1 = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colPersonStudyStart", "colPersonStudyStart", SortOrder.ASCENDING);
            if (results1 != null) {
                results.addAll(results1);

                List<SortMeta> results2 = DataTableUtil.buildSortOrder(":form:tabView:dtPersonnelEntities:colPersonStudyEnd", "colPersonStudyEnd", SortOrder.ASCENDING);

                if (results2 != null) {
                    results.addAll(results2);
                }
            }

            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Need to build an initial visibility for columns
     * @return list of Boolean variables
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        // Study Personnel View
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colPersonSurname");
        if (column1 != null) {
            results.add(Boolean.TRUE); // Surname
            results.add(Boolean.FALSE); // FirstName
            results.add(Boolean.TRUE); // Study person role
            results.add(Boolean.TRUE); // Can obtain informed consent
            results.add(Boolean.TRUE); // Start
            results.add(Boolean.TRUE); // End

            return results;
        }

        // Person Studies View
        column1 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colStudyName");
        if (column1 != null) {
            results.add(Boolean.TRUE); // Study name
            results.add(Boolean.TRUE); // Study status
            results.add(Boolean.TRUE); // Study person role
            results.add(Boolean.TRUE); // Can obtain informed consent
            results.add(Boolean.TRUE); // Start
            results.add(Boolean.TRUE); // End

            return results;
        }

        return results;
    }

    //endregion

}
