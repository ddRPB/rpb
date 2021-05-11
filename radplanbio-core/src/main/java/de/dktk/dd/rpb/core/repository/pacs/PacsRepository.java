/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.repository.pacs;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import de.dktk.dd.rpb.core.domain.pacs.Pacs;
import de.dktk.dd.rpb.core.dao.pacs.PacsDao;

/**
 * Default implementation of the {@link Pacs} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IPacsRepository
 *
 * IPacsRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("pacsRepository")
@Singleton
public class PacsRepository extends RepositoryImpl<Pacs, Integer> implements IPacsRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PacsRepository.class);

    //endregion

    //region Injects

    private PacsDao pacsDao;

    @Inject
    public void setPacsDao(PacsDao pacsDao) {
        this.pacsDao = pacsDao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Pacs, Integer> getDao() {
        return pacsDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pacs getNew() {
        return new Pacs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pacs getNewWithDefaults() {
        Pacs result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Pacs get(Pacs model) {
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