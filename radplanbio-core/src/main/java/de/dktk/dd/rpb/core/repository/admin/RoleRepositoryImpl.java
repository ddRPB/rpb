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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import de.dktk.dd.rpb.core.domain.admin.Role;
import de.dktk.dd.rpb.core.dao.admin.RoleDao;

/**
 * Default implementation of the {@link RoleRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see RoleRepository
 *
 * RoleRepository Interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("roleRepository")
@Singleton
public class RoleRepositoryImpl extends RepositoryImpl<Role, Integer> implements RoleRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoleRepositoryImpl.class);

    //endregion

    protected RoleDao roleDao;

    @Inject
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<Role, Integer> getDao() {
        return this.roleDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getNew() {
        return new Role();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getNewWithDefaults() {
        Role result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Role get(Role model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            Role result = this.getByRoleName(model.getName());
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
    public Role getByRoleName(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return findUniqueOrNone(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteByRoleName(String roleName) {
        delete(getByRoleName(roleName));
    }
}