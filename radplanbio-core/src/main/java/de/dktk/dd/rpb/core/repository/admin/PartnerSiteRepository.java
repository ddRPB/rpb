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

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.support.Repository;

/**
 * The PartnerSiteRepository is a data-centric service for the {@link PartnerSite} entity.
 * It provides the expected methods to get/delete a {@link PartnerSite} instance
 * plus some methods to perform searches.
 * <p>
 * Search logic is driven by 2 kinds of parameters: a {@link PartnerSite} instance used
 * as a properties holder against which the search will be performed and a {@link de.dktk.dd.rpb.core.dao.support.SearchParameters}
 * instance from where you can control your search options including the usage
 * of named queries.
 *
 * PartnerSiteRepository Interface
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
public interface PartnerSiteRepository extends Repository<PartnerSite, Integer> {

    /**
     * Return the persistent instance of {@link PartnerSite} with the given unique property value siteName,
     * or null if there is no such persistent instance.
     *
     * @param siteName the unique value
     * @return the corresponding {@link PartnerSite} persistent instance or null
     */
    PartnerSite getBySiteName(String siteName);

    /**
     * Delete a {@link PartnerSite} using the unique column siteName
     *
     * @param siteName the unique value
     */
    void deleteBySiteName(String siteName);

    /**
     * Save a {@link PartnerSite} and its Pacs (if new insert if exits update)
     *
     * @param partnerSite the partnerSite object for save
     */
    void savePartnerSitePacs(PartnerSite partnerSite);

    /**
     * Save a {@link PartnerSite} and its Edc (if new insert if exits update)
     *
     * @param partnerSite the partnerSite object for save
     */
    void savePartnerSiteEdc(PartnerSite partnerSite);

    /**
     * Save a {@link PartnerSite} and its Pid (if new insert if exits update)
     *
     * @param partnerSite the partnerSite object for save
     */
    void savePartnerSitePid(PartnerSite partnerSite);

    /**
     * Save a {@link PartnerSite} and its Portal (if new insert if exits update)
     *
     * @param partnerSite the partnerSite object for save
     */
    void savePartnerSitePortal(PartnerSite partnerSite);

    /**
     * Save a {@link PartnerSite} and its Server (if new insert if exits update)
     *
     * @param partnerSite the partnerSite object for save
     */
    void savePartnerSiteServer(PartnerSite partnerSite);

}