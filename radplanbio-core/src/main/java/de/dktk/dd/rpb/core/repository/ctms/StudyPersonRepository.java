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

package de.dktk.dd.rpb.core.repository.ctms;

import de.dktk.dd.rpb.core.dao.ctms.StudyPersonDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyPerson;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * StudyPersonRepository
 *
 * Default implementation of the {@link IStudyPersonRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyPersonRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("studyPersonRepository")
@Singleton
public class StudyPersonRepository extends RepositoryImpl<StudyPerson, Integer> implements IStudyPersonRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyPersonRepository.class);

    //endregion

    //region Injects

    protected StudyPersonDao dao;

    @Inject
    public void setDao(StudyPersonDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyPerson, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPerson getNew() {
        return new StudyPerson();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPerson getNewWithDefaults() {
        StudyPerson result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyPerson get(StudyPerson model) {
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