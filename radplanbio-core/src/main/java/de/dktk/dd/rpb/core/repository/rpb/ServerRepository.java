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

package de.dktk.dd.rpb.core.repository.rpb;

import de.dktk.dd.rpb.core.dao.rpb.ServerDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.rpb.Server;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link IServerRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IPortalRepository
 *
 * PortalRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("serverRepository")
@Singleton
public class ServerRepository extends RepositoryImpl<Server, Integer> implements IServerRepository {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ServerRepository.class);

    private ServerDao dao;

    @Inject
    public void setDao(ServerDao dao) {
        this.dao = dao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Server, Integer> getDao() {
        return this.dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server getNew() {
        return new Server();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server getNewWithDefaults() {
        Server result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Server get(Server model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

}
