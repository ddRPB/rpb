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

import de.dktk.dd.rpb.core.dao.edc.MappedItemDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.edc.mapping.AbstractMappedItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedCsvItem;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link IMappedItemRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IMappedItemRepository
 *
 * @author tomas@skripcak.net
 * @since 17 March 2015
 */
@Named("mappedItemRepository")
@Singleton
public class MappedItemRepository extends RepositoryImpl<AbstractMappedItem, Integer> implements IMappedItemRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MappedItemRepository.class);

    //endregion

    //region Injects

    protected MappedItemDao mappedItemDao;

    @Inject
    public void setMappedItemDao(MappedItemDao dao) {
        this.mappedItemDao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<AbstractMappedItem, Integer> getDao() {
        return this.mappedItemDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractMappedItem getNew() {
        // Should not matter which type... take CSV
        return new MappedCsvItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractMappedItem getNewWithDefaults() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AbstractMappedItem get(AbstractMappedItem model) {
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
