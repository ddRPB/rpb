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

import de.dktk.dd.rpb.core.dao.admin.StudyDocDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.StudyDoc;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Default implementation of the {@link IStudyDocRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IStudyDocRepository
 *
 * DocTypeRepository
 *
 * @author tomas@skripcak.net
 * @since 17 Sep 2013
 */
@Named("studyDocRepository")
@Singleton
public class StudyDocRepository extends RepositoryImpl<StudyDoc, Integer> implements IStudyDocRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyDocRepository.class);

    //endregion

    //region Injects

    protected StudyDocDao dao;

    @Inject
    public void setStudyDocDao(StudyDocDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<StudyDoc, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyDoc getNew() {
        return new StudyDoc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyDoc getNewWithDefaults() {
        StudyDoc result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public StudyDoc get(StudyDoc model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getFilename())) {
            StudyDoc result = this.getByFilename(model.getFilename());
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
    public StudyDoc getByFilename(String name) {
        StudyDoc type = new StudyDoc();
        type.setFilename(name);
        return findUniqueOrNone(type);
    }

    //endregion

}
