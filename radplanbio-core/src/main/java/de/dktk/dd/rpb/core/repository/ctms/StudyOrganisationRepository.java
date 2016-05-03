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

import de.dktk.dd.rpb.core.dao.ctms.StudyOrganisationDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyOrganisation;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * StudyOrganisationRepository
 *
 * Default implementation of the {@link IStudyOrganisationRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyOrganisationRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("studyOrganisationRepository")
@Singleton
public class StudyOrganisationRepository extends RepositoryImpl<StudyOrganisation, Integer> implements IStudyOrganisationRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyOrganisationRepository.class);

    //endregion

    //region Injects

    protected StudyOrganisationDao dao;

    @Inject
    public void setDao(StudyOrganisationDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyOrganisation, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyOrganisation getNew() {
        return new StudyOrganisation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyOrganisation getNewWithDefaults() {
        StudyOrganisation result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyOrganisation get(StudyOrganisation model) {
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