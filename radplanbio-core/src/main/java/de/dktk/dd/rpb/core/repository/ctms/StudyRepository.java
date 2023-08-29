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

package de.dktk.dd.rpb.core.repository.ctms;

import de.dktk.dd.rpb.core.dao.admin.StudyDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Default implementation of the {@link IStudyRepository} interface.
 *
 * The repository abstract data access to Study related part of domain model.
 * Study in this case is so call aggregate root and this repository handles the tasks related to study management.
 * Transactional annotation ensure that rollback when there is some problem on DB site.
 * It also ensure that everything is done in one transaction
 *
 * @see IStudyRepository
 *
 * @author tomas@skripcak.net
 * @since 17 Sep 2013
 */
@Named("studyRepository")
@Singleton
@SuppressWarnings("unused")
public class StudyRepository extends RepositoryImpl<Study, Integer> implements IStudyRepository {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(StudyRepository.class);

    //endregion

    //region Injects

    //region Study DAO

    protected StudyDao dao;

    @Inject
    public void setStudyDao(StudyDao value) {
        this.dao = value;
    }

    //endregion

    //endregion

    //region Overrides Study DAO access

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Study, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Study getNew() {
        return new Study();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Study getNewWithDefaults() {
        Study result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Study get(Study model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getOcStudyIdentifier())) {
            Study result = this.getByOcStudyIdentifier(model.getOcStudyIdentifier());
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    //endregion

    //region Aggregate methods

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Study getByOcStudyIdentifier(String ocStudyIdentifier) {

        Study study = new Study();
        study.setOcStudyIdentifier(ocStudyIdentifier);

        return findUniqueOrNone(study);
    }

    //endregion

}
