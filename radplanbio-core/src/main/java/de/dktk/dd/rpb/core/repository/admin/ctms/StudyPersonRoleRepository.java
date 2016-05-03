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

package de.dktk.dd.rpb.core.repository.admin.ctms;

import de.dktk.dd.rpb.core.dao.ctms.StudyPersonRoleDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyPersonRole;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * StudyPersonRoleRepository
 *
 * Default implementation of the {@link IStudyPersonRoleRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyPersonRoleRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("studyPersonRoleRepository")
@Singleton
public class StudyPersonRoleRepository extends RepositoryImpl<StudyPersonRole, Integer> implements IStudyPersonRoleRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyPersonRoleRepository.class);

    //endregion

    //region Injects

    protected StudyPersonRoleDao dao;

    @Inject
    public void setTypeDao(StudyPersonRoleDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyPersonRole, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPersonRole getNew() {
        return new StudyPersonRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyPersonRole getNewWithDefaults() {
        StudyPersonRole result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyPersonRole get(StudyPersonRole model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            StudyPersonRole result = this.getByName(model.getName());
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
    public StudyPersonRole getByName(String name) {
        StudyPersonRole entity = new StudyPersonRole();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}