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

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import de.dktk.dd.rpb.core.domain.admin.Portal;
import de.dktk.dd.rpb.core.dao.admin.PortalDao;

/**
 * Default implementation of the {@link PortalRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see RoleRepository
 *
 * PortalRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("portalRepository")
@Singleton
public class PortalRepositoryImpl extends RepositoryImpl<Portal, Integer> implements PortalRepository {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PortalRepositoryImpl.class);

    protected PortalDao portalDao;

    @Inject
    public void setRoleDao(PortalDao portalDao) {
        this.portalDao = portalDao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Portal, Integer> getDao() {
        return portalDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Portal getNew() {
        return new Portal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Portal getNewWithDefaults() {
        Portal result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Portal get(Portal model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

//        if (!isNotEmpty(model.getRoleName())) {
//            Role result = this.getByRoleName(model.getRoleName());
//            if (result != null) {
//                return result;
//            }
//        }

        return null;
    }


}