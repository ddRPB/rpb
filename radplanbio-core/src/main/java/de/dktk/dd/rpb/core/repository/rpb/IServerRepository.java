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

import de.dktk.dd.rpb.core.domain.rpb.Server;
import de.dktk.dd.rpb.core.repository.support.Repository;

/**
 * The IServerRepository is a data-centric service for the {@link Server} aggregation root entity.
 * It provides the expected methods to get/delete a {@link Server} instance
 * plus some methods to perform searches.
 * <p>
 * Search logic is driven by 2 kinds of parameters: a {@link Server} instance used
 * as a properties holder against which the search will be performed and a {@link de.dktk.dd.rpb.core.dao.support.SearchParameters}
 * instance from where you can control your search options including the usage
 * of named queries.
 *
 * ServerRepository Interface
 *
 * @author tomas@skripcak.net
 * @since 28 Dec 2016
 */
public interface IServerRepository extends Repository<Server, Integer> {

}