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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.ctms.CurriculumVitaeItem;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.ctms.IPersonRepository;
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
 * Bean for administration of CTMS person entities
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbPerson")
@Scope(value="view")
public class PersonBean extends CrudEntityViewModel<Person, Integer> {

    //region Injects

    //region Repository

    private final IPersonRepository repository;

    /**
     * Get PersonRepository
     * @return PersonRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IPersonRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public PersonBean(IPersonRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = new ArrayList<>();
        this.columnVisibilityList.add(Boolean.TRUE); // Surname
        this.columnVisibilityList.add(Boolean.TRUE); // Firstname
        this.columnVisibilityList.add(Boolean.FALSE); // Birthname
        this.columnVisibilityList.add(Boolean.TRUE); // Status
        this.columnVisibilityList.add(Boolean.FALSE); // Titles before
        this.columnVisibilityList.add(Boolean.FALSE); // Titles after

        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Commands

    //region CV Items

    public void addNewCvItem(CurriculumVitaeItem cvItem) {
        if (this.selectedEntity.addCurriculumVitaeItem(cvItem)) {
            this.doUpdateEntity();
        }
    }

    public void removeSelectedCvItem(CurriculumVitaeItem cvItem) {
        if (this.selectedEntity.removeCurriculumVitaeItem(cvItem)) {
            this.doUpdateEntity();
        }
    }

    //endregion

    //region StudyPerson

    public void doUpdateEntity(List<StudyPerson> selectedStudyPersonEntities, StudyPerson editMultiEntity) {
        if (this.selectedEntity.modifyStudyPersonnel(selectedStudyPersonEntities, editMultiEntity)) {
            this.doUpdateEntity();
        }
    }

    //endregion

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

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtEntities:colPersonSurname");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column1);
        sm1.setSortField("colPersonSurname");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        UIComponent column2 = viewRoot.findComponent(":form:tabView:dtEntities:colPersonFirstname");

        SortMeta sm2 = new SortMeta();
        sm2.setSortBy((UIColumn) column2);
        sm2.setSortField("colPersonFirstname");
        sm2.setSortOrder(SortOrder.ASCENDING);

        results.add(sm2);

        UIComponent column3 = viewRoot.findComponent(":form:tabView:dtEntities:colPersonStatus");

        SortMeta sm3 = new SortMeta();
        sm3.setSortBy((UIColumn) column3);
        sm3.setSortField("colPersonStatus");
        sm3.setSortOrder(SortOrder.ASCENDING);

        results.add(sm3);

        return results;
    }

    //endregion

}
