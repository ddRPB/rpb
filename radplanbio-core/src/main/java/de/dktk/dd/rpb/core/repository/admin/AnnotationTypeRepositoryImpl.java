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

package de.dktk.dd.rpb.core.repository.admin;

import de.dktk.dd.rpb.core.dao.edc.AnnotationTypeDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.edc.AnnotationType;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Default implementation of the {@link AnnotationTypeRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see AnnotationTypeRepository
 *
 * AnnotationTypeRepository
 *
 * @author tomas@skripcak.net
 * @since 13 Sep 2013
 */
@Named("annotationTypeRepository")
@Singleton
public class AnnotationTypeRepositoryImpl extends RepositoryImpl<AnnotationType, Integer> implements AnnotationTypeRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AnnotationTypeRepositoryImpl.class);

    //endregion

    //region Injects

    protected AnnotationTypeDao annotatioTypeDao;

    @Inject
    public void setTypeDao(AnnotationTypeDao value) {
        this.annotatioTypeDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<AnnotationType, Integer> getDao() {
        return this.annotatioTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationType getNew() {
        return new AnnotationType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationType getNewWithDefaults() {
        AnnotationType result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AnnotationType get(AnnotationType model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            AnnotationType result = this.getByName(model.getName());
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
    public AnnotationType getByName(String name) {
        AnnotationType type = new AnnotationType();
        type.setName(name);
        return findUniqueOrNone(type);
    }

    //endregion

}
