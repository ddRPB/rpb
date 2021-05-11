/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

package de.dktk.dd.rpb.core.repository.admin.ctms;

import de.dktk.dd.rpb.core.dao.ctms.StudyPhaseDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyPhase;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * StudyPhaseRepository
 *
 * Default implementation of the {@link IStudyPhaseRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyPhaseRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("studyPhaseRepository")
@Singleton
public class StudyPhaseRepository extends RepositoryImpl<StudyPhase, Integer> implements IStudyPhaseRepository {

    //region Finals

    private static final Logger log = Logger.getLogger(StudyPhaseRepository.class);

    //endregion

    //region Injects

    protected StudyPhaseDao studyPhaseDao;

    @Inject
    public void setTypeDao(StudyPhaseDao value) {
        this.studyPhaseDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyPhase, Integer> getDao() {
        return this.studyPhaseDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPhase getNew() {
        return new StudyPhase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPhase getNewWithDefaults() {
        StudyPhase result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyPhase get(StudyPhase model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            StudyPhase result = this.getByName(model.getName());
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyPhase getByName(String name) {
        StudyPhase entity = new StudyPhase();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}