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

package de.dktk.dd.rpb.api.support;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Base service
 */
public class BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(BaseService.class);

    //endregion

    //region Injects

    //region Repository

    @Inject
    private DefaultAccountRepository userRepository;

    protected DefaultAccountRepository getUserRepository() {
        return this.userRepository;
    }

    protected void setUserAccountRepository(DefaultAccountRepository value) {
        this.userRepository = value;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Find user that corresponds to that specific apiKey
     * @param apiKey userAccount associated API-Key
     * @return DefaultAccount entity
     */
    protected DefaultAccount getAuthenticatedUser(String apiKey) {

        DefaultAccount userAccount = new DefaultAccount();
        userAccount.setApiKey(apiKey);
        userAccount = this.userRepository.findUniqueOrNone(userAccount);

        return userAccount;
    }

    //endregion

}
