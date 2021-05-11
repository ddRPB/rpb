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

package de.dktk.dd.rpb.core.repository.admin;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import javax.inject.Inject;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import de.dktk.dd.rpb.core.dao.admin.RtStructTypeDao;
import de.dktk.dd.rpb.core.domain.admin.RtStructType;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link RtStructType} interface.
 * Note: you may use multiple DAO from this layer.
 * @see RtStructTypeRepository
 *
 * RtStructTypeRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 27 Nov 2014
 */
@Named("rtStructTypeRepository")
@Singleton
public class RtStructTypeRepositoryImpl extends RepositoryImpl<RtStructType, Integer> implements RtStructTypeRepository {

    //region Finals

    private static final Logger log = Logger.getLogger(RtStructTypeRepositoryImpl.class);

    //endregion

    //region Injects

    protected RtStructTypeDao rtStructTypeDao;

    @Inject
    public void setRtStructTypeDao(RtStructTypeDao value) {
        this.rtStructTypeDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<RtStructType, Integer> getDao() {
        return this.rtStructTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RtStructType getNew() {
        return new RtStructType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RtStructType getNewWithDefaults() {
        RtStructType result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public RtStructType get(RtStructType model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            RtStructType result = this.getByName(model.getName());
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
    public RtStructType getByName(String name) {
        RtStructType structType = new RtStructType();
        structType.setName(name);

        return findUniqueOrNone(structType);
    }

    //endregion

}
