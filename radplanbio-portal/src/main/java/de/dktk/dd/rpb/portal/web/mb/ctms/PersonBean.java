/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.ctms.IPersonRepository;
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

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        
        this.load();
    }

    //endregion

    //region Commands

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

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // Surname
        results.add(Boolean.TRUE); // Firstname
        results.add(Boolean.FALSE); // Birthname
        results.add(Boolean.TRUE); // Status
        results.add(Boolean.FALSE); // Titles before
        results.add(Boolean.FALSE); // Titles after

        return results;
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colPersonSurname", "colPersonSurname", SortOrder.ASCENDING);

        if (results != null) {

            List<SortMeta> sm2 = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colPersonFirstname", "colPersonFirstname", SortOrder.ASCENDING);
            if (sm2 != null) {
                results.addAll(sm2);
            }

            List<SortMeta> sm3 = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colPersonStatus", "colPersonStatus", SortOrder.ASCENDING);
            if (sm3 != null) {
                results.addAll(sm3);
            }
            
            return results;
        }

        return new ArrayList<>();
    }

    //endregion

}
