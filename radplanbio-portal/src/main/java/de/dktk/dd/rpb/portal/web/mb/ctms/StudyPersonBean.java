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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.ctms.IStudyPersonRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
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

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyPersonRepository repository) {
        this.repository = repository;
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
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        // Study Personnel View
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colPersonSurname");
        if (column1 != null) {
            SortMeta sm1 = new SortMeta();
            sm1.setSortBy((UIColumn) column1);
            sm1.setSortField("colPersonSurname");
            sm1.setSortOrder(SortOrder.ASCENDING);

            results.add(sm1);

            UIComponent column2 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colPersonFirstname");

            SortMeta sm2 = new SortMeta();
            sm2.setSortBy((UIColumn) column2);
            sm2.setSortField("colPersonFirstname");
            sm2.setSortOrder(SortOrder.ASCENDING);

            results.add(sm2);

            UIComponent column3 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colPersonRole");

            SortMeta sm3 = new SortMeta();
            sm3.setSortBy((UIColumn) column3);
            sm3.setSortField("colPersonRole");
            sm3.setSortOrder(SortOrder.ASCENDING);

            results.add(sm3);
            return results;
        }

        // Person Studies View
        column1 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colStudyTitle");
        if (column1 != null) {
            SortMeta sm1 = new SortMeta();
            sm1.setSortBy((UIColumn) column1);
            sm1.setSortField("colStudyTitle");
            sm1.setSortOrder(SortOrder.ASCENDING);

            results.add(sm1);

            UIComponent column2 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colPersonRole");

            SortMeta sm2 = new SortMeta();
            sm2.setSortBy((UIColumn) column2);
            sm2.setSortField("colPersonRole");
            sm2.setSortOrder(SortOrder.ASCENDING);

            results.add(sm2);

            return results;
        }

        return results;
    }

    /**
     * Need to build an initial visibility for columns
     * @return list of Boolean variables
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

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
        column1 = viewRoot.findComponent(":form:tabView:dtPersonnelEntities:colStudyTitle");
        if (column1 != null) {
            results.add(Boolean.TRUE); // Study Title
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
