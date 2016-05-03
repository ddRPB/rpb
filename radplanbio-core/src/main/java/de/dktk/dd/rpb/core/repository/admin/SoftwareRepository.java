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

import de.dktk.dd.rpb.core.dao.admin.SoftwareDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.admin.Software;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link ISoftwareRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see ISoftwareRepository
 *
 * SoftwareRepository
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("softwareRepository")
@Singleton
public class SoftwareRepository extends RepositoryImpl<Software, Integer> implements ISoftwareRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SoftwareRepository.class);

    //endregion

    //region Injects

    protected SoftwareDao dao;

    @Inject
    public void setSampleContentDao(SoftwareDao dao) {
        this.dao = dao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Software, Integer> getDao() {
        return dao;
    }

    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public Software getNew() {
        return new Software();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Software getNewWithDefaults() {
        Software result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Software get(Software model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

}
