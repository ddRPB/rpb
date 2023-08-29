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

import de.dktk.dd.rpb.core.dao.ctms.TrialSubjectDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.randomisation.TrialSubject;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link ITrialSubjectRepository} interface.
 * Note: you may use multiple DAO from this layer
 *
 * @author tomas@skripcak.net
 * @since 6 Feb 2014
 */
@Named("trialSubjectRepository")
@Singleton
public class TrialSubjectRepository extends RepositoryImpl<TrialSubject, Integer> implements ITrialSubjectRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TrialSubjectRepository.class);

    //endregion

    //region Injects

    protected TrialSubjectDao dao;

    @Inject
    public void setTrialSubjectDao(TrialSubjectDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<TrialSubject, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TrialSubject getNew() {
        return new TrialSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TrialSubject getNewWithDefaults() {
        TrialSubject result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TrialSubject get(TrialSubject model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

//        if (!isNotEmpty(model.getPacsBaseUrl())) {
//            Pacs result = getByPacsBaseUrl(model.getPacsBaseUrl());
//            if (result != null) {
//                return result;
//            }
//        }

        return null;
    }

    //endregion

}
