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

import de.dktk.dd.rpb.core.dao.ctms.StudyTagTypeDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyTagType;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * StudyTagTypeRepository
 *
 * Default implementation of the {@link IStudyTagTypeRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyTagTypeRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("studyTagTypeRepository")
@Singleton
public class StudyTagTypeRepository extends RepositoryImpl<StudyTagType, Integer> implements IStudyTagTypeRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyTagTypeRepository.class);

    //endregion

    //region Injects

    protected StudyTagTypeDao studyTagTypeDao;

    @Inject
    public void setTypeDao(StudyTagTypeDao value) {
        this.studyTagTypeDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyTagType, Integer> getDao() {
        return this.studyTagTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyTagType getNew() {
        return new StudyTagType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyTagType getNewWithDefaults() {
        StudyTagType result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyTagType get(StudyTagType model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            StudyTagType result = this.getByName(model.getName());
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
    public StudyTagType getByName(String name) {
        StudyTagType entity = new StudyTagType();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}