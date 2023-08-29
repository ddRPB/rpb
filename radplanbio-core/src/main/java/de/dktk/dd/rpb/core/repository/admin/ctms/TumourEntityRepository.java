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

import de.dktk.dd.rpb.core.dao.ctms.TumourEntityDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.ctms.TumourEntity;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * TumourEntityRepository
 *
 * Default implementation of the {@link ITumourEntityRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ITumourEntityRepository
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("tumourEntityRepository")
@Singleton
public class TumourEntityRepository extends RepositoryImpl<TumourEntity, Integer> implements ITumourEntityRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TumourEntityRepository.class);

    //endregion

    //region Injects

    protected TumourEntityDao dao;

    @Inject
    public void setTypeDao(TumourEntityDao value) {
        this.dao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<TumourEntity, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TumourEntity getNew() {
        return new TumourEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TumourEntity getNewWithDefaults() {
        TumourEntity result = getNew();
        result.initDefaultValues();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TumourEntity get(TumourEntity model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            TumourEntity result = this.getByName(model.getName());
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
    public TumourEntity getByName(String name) {
        TumourEntity entity = new TumourEntity();
        entity.setName(name);

        return findUniqueOrNone(entity);
    }

    //endregion

}
