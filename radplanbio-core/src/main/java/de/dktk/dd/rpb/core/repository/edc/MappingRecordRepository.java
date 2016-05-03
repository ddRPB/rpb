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

import de.dktk.dd.rpb.core.dao.edc.MappingRecordDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link IMappingRecordRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IMappingRecordRepository
 *
 * @author tomas@skripcak.net
 * @since 18 March 2015
 */
@Named("mappingRecordRepository")
@Singleton
public class MappingRecordRepository extends RepositoryImpl<MappingRecord, Integer> implements IMappingRecordRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MappingRecordRepository.class);

    //endregion

    //region Injects

    protected MappingRecordDao dao;

    @Inject
    public void setMappedItemDao(MappingRecordDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<MappingRecord, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingRecord getNew() {
        return new MappingRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingRecord getNewWithDefaults() {
        return new MappingRecord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public MappingRecord get(MappingRecord model) {
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
