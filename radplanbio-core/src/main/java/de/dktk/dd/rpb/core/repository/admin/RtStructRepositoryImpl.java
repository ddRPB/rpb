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

import javax.inject.Inject;

import de.dktk.dd.rpb.core.dao.admin.RtStructDao;
import de.dktk.dd.rpb.core.domain.admin.RtStruct;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link RtStruct} interface.
 * Note: you may use multiple DAO from this layer.
 * @see RtStructRepository
 *
 * RtStructRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 27 Nov 2014
 */
@Named("rtStructRepository")
@Singleton
public class RtStructRepositoryImpl extends RepositoryImpl<RtStruct, Integer> implements RtStructRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RtStructRepositoryImpl.class);

    //endregion

    //region Injects

    protected RtStructDao rtStructDao;

    @Inject
    public void setRtStructDao(RtStructDao value) {
        this.rtStructDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<RtStruct, Integer> getDao() {
       return this.rtStructDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RtStruct getNew() {
        return new RtStruct();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RtStruct getNewWithDefaults() {
        RtStruct result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public RtStruct get(RtStruct model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

//        if (!isNotEmpty(model.getName())) {
//            RtStruct result = this.getByName(model.getName());
//            if (result != null) {
//                return result;
//            }
//        }

        return null;
    }

    //endregion

}
