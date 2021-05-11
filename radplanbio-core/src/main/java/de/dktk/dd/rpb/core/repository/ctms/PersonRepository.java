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

package de.dktk.dd.rpb.core.repository.ctms;

import de.dktk.dd.rpb.core.dao.ctms.PersonDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * PersonRepository
 *
 * Default implementation of the {@link IPersonRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IPersonRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("personRepository")
@Singleton
public class PersonRepository extends RepositoryImpl<Person, Integer> implements IPersonRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PersonRepository.class);

    //endregion

    //region Injects

    protected PersonDao dao;

    @Inject
    public void setDao(PersonDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Person, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getNew() {
        return new Person();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getNewWithDefaults() {
        Person result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Person get(Person model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

    //endregion

}