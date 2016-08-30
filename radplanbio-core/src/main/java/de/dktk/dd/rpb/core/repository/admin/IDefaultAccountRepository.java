/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2014  Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.support.Repository;

/**
 * The DefaultAccountRepository is a data-centric service for the {@link DefaultAccount} entity.
 * It provides the expected methods to get/delete a {@link DefaultAccount} instance
 * plus some methods to perform searches.
 * <p>
 * Search logic is driven by 2 kinds of parameters: a {@link DefaultAccount} instance used
 * as a properties holder against which the search will be performed and a {@link de.dktk.dd.rpb.core.dao.support.SearchParameters}
 * instance from where you can control your search options including the usage
 * of named queries.
 *
 * DefaultAccountRepository Interfaces
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
public interface IDefaultAccountRepository extends Repository<DefaultAccount, Integer> {

    /**
     * Return the persistent instance of {@link DefaultAccount} with the given unique property value username,
     * or null if there is no such persistent instance.
     *
     * @param username the unique value
     * @return the corresponding {@link DefaultAccount} persistent instance or null
     */
    DefaultAccount getByUsername(String username);

    /**
     * Return the persistent instance of {@link DefaultAccount} with the given unique property value ocusername,
     * or null if there is no such persistent instance.
     *
     * @param ocUsername the unique value
     * @return the corresponding {@link DefaultAccount} persistent instance or null
     */
    DefaultAccount getByOcUsername(String ocUsername);

}