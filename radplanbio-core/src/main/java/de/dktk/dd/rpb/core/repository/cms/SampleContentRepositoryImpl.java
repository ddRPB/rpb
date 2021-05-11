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

package de.dktk.dd.rpb.core.repository.cms;

import de.dktk.dd.rpb.core.dao.cms.SampleContentDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.cms.SampleContent;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link SampleContentRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see SampleContentRepository
 *
 * SampleContentRepository
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("sampleContentRepository")
@Singleton
public class SampleContentRepositoryImpl extends RepositoryImpl<SampleContent, Integer> implements SampleContentRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SampleContentRepositoryImpl.class);

    //endregion

    //region Injects

    protected SampleContentDao sampleContentDao;

    @Inject
    public void setSampleContentDao(SampleContentDao sampleContentDao) {
        this.sampleContentDao = sampleContentDao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<SampleContent, Integer> getDao() {
        return sampleContentDao;
    }

    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleContent getNew() {
        return new SampleContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleContent getNewWithDefaults() {
        SampleContent result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SampleContent get(SampleContent model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

}
