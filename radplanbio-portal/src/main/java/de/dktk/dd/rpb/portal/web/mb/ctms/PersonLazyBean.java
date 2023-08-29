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

import de.dktk.dd.rpb.core.dao.support.OrderByDirection;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.PersonStatus;
import de.dktk.dd.rpb.core.domain.ctms.Person_;
import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.admin.ctms.IPersonStatusRepository;
import de.dktk.dd.rpb.core.repository.ctms.IPersonRepository;
import de.dktk.dd.rpb.core.repository.rpb.IRadPlanBioDataRepository;
import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of CTMS person entities
 *
 * @author tomas@skripcak.net
 * @since 25 May 2020
 */
@Named("mbPersonLazy")
@Scope(value = "view")
public class PersonLazyBean extends GenericLazyDataModel<Person, Integer> {

    //region Members

    private final IPersonStatusRepository personStatusRepository;
    private final IRadPlanBioDataRepository radPlanBioDataRepository;
    private final int maxSearchResults = 25;
    private List<Person> personList = new ArrayList<>();

    //endregion

    //region Constructors

    @Inject
    public PersonLazyBean(
            IPersonRepository repository,
            IPersonStatusRepository personStatusRepository,
            IRadPlanBioDataRepository radPlanBioDataRepository
    ) {
        super(repository);

        this.personStatusRepository = personStatusRepository;
        this.radPlanBioDataRepository = radPlanBioDataRepository;
    }

    //endregion


    public List<Person> getPersonList() {
        return personList;
    }

    public int getMaxSearchResults() {
        return maxSearchResults;
    }


    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = this.buildColumnVisibilityList();
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

    public List<Person> getMatchingPersonList(String searchString) {
        this.personList = this.radPlanBioDataRepository.getPersonsWithMatchingName(searchString.trim(), maxSearchResults + 1);
        return this.personList;
    }

    //endregion

    //region Overrides

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

    @Override
    protected Person getEntity(Map<String, Object> filters) {
        Person example = this.repository.getNew();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "surname":
                    example.setSurname(entry.getValue().toString());
                    break;
                case "firstname":
                    example.setFirstname(entry.getValue().toString());
                    break;
                case "birthname":
                    example.setBirthname(entry.getValue().toString());
                    break;
                case "status.name":
                    example.setStatus(this.personStatusRepository.getNew());
                    example.getStatus().setName(((PersonStatus) entry.getValue()).getName());
                    break;
                case "titlesBefore":
                    example.setTitlesBefore(entry.getValue().toString());
                    break;
                case "titlesAfter":
                    example.setTitlesAfter(entry.getValue().toString());
                    break;
            }
        }

        return example;
    }

    @Override
    protected SearchParameters searchParameters() {
        return new SearchParameters() //
                .anywhere() //
                .caseInsensitive();
    }

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters.orderBy(Person_.surname, OrderByDirection.ASC);
    }

    //endregion

}
