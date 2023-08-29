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

package de.dktk.dd.rpb.core.repository.pid;

import de.dktk.dd.rpb.core.dao.pid.PidDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.pid.Pid;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link Pid} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IPidRepository
 *
 * IPidRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 28 Dec 2016
 */
@Named("pidRepository")
@Singleton
public class PidRepository extends RepositoryImpl<Pid, Integer> implements IPidRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PidRepository.class);

    //endregion

    //region Injects

    private PidDao dao;

    @Inject
    public void setPacsDao(PidDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Pid, Integer> getDao() {
        return dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pid getNew() {
        return new Pid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pid getNewWithDefaults() {
        Pid result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Pid get(Pid model) {
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
