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

import de.dktk.dd.rpb.core.dao.edc.MappingDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link IMappingRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IMappingRepository
 *
 * @author tomas@skripcak.net
 * @since 18 March 2015
 */
@Named("mappingRepository")
@Singleton
public class MappingRepository extends RepositoryImpl<Mapping, Integer> implements IMappingRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MappingRepository.class);

    //endregion

    //region Injects

    protected MappingDao dao;

    @Inject
    public void setMappedItemDao(MappingDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Mapping, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapping getNew() {
        return new Mapping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapping getNewWithDefaults() {
        return new Mapping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Mapping get(Mapping model) {
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
