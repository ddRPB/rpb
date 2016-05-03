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
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.dao.admin.DefaultAccountDao;

/**
 * Default implementation of the {@link DefaultAccountRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see DefaultAccountRepository
 *
 * DefaultAccountRepository
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("defaultAccountRepository")
@Singleton
public class DefaultAccountRepositoryImpl extends RepositoryImpl<DefaultAccount, Integer> implements DefaultAccountRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(DefaultAccountRepositoryImpl.class);

    //endregion

    //region Injects

    protected DefaultAccountDao defaultAccountDao;

    @Inject
    public void setAccountDao(DefaultAccountDao value) {
        this.defaultAccountDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<DefaultAccount, Integer> getDao() {
        return this.defaultAccountDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultAccount getNew() {
        return new DefaultAccount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultAccount getNewWithDefaults() {
        DefaultAccount result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DefaultAccount get(DefaultAccount model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getUsername())) {
            DefaultAccount result = getByUsername(model.getUsername());
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
    public DefaultAccount getByUsername(String username) {
        DefaultAccount account = new DefaultAccount();
        account.setUsername(username);
        return findUniqueOrNone(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DefaultAccount getByOcUsername(String ocUsername) {
        DefaultAccount account = new DefaultAccount();
        account.setOcUsername(ocUsername);
        return findUniqueOrNone(account);
    }

    //endregion

}