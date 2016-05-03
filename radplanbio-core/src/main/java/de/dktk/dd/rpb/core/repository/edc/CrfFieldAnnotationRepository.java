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

package de.dktk.dd.rpb.core.repository.edc;

import de.dktk.dd.rpb.core.dao.edc.CrfFieldAnnotationDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link ICrfFieldAnnotationRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ICrfFieldAnnotationRepository
 *
 * @author tomas@skripcak.net
 * @since 15 July 2015
 */
@Named("crfFieldAnnotationRepository")
@Singleton
public class CrfFieldAnnotationRepository extends RepositoryImpl<CrfFieldAnnotation, Integer> implements ICrfFieldAnnotationRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(CrfFieldAnnotationRepository.class);

    //endregion

    //region Injects

    protected CrfFieldAnnotationDao dao;

    @Inject
    public void setDao(CrfFieldAnnotationDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<CrfFieldAnnotation, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CrfFieldAnnotation getNew() {
        return new CrfFieldAnnotation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CrfFieldAnnotation getNewWithDefaults() {
        return new CrfFieldAnnotation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CrfFieldAnnotation get(CrfFieldAnnotation model) {
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
