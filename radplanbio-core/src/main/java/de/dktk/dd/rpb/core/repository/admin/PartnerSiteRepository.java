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

package de.dktk.dd.rpb.core.repository.admin;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import javax.inject.Inject;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.dao.ctms.PartnerSiteDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link PartnerSite} interface.
 * Note: you may use multiple DAO from this layer.
 * @see IPartnerSiteRepository
 *
 * IPartnerSiteRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("partnerSiteRepository")
@Singleton
public class PartnerSiteRepository extends RepositoryImpl<PartnerSite, Integer> implements IPartnerSiteRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PartnerSiteRepository.class);

    //endregion

    //region Injects

    protected PartnerSiteDao partnerSiteDao;

    @Inject
    public void setPartnerSiteDao(PartnerSiteDao value) {
        this.partnerSiteDao = value;
    }

    //endregion

    //region Overrides

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<PartnerSite, Integer> getDao() {
        return this.partnerSiteDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerSite getNew() {
        return new PartnerSite();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerSite getNewWithDefaults() {
        PartnerSite result = getNew();
        result.initDefaultValues();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PartnerSite get(PartnerSite model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        if (!isNotEmpty(model.getName())) {
            PartnerSite result = getBySiteName(model.getName());
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
    public PartnerSite getBySiteName(String _siteName) {
        PartnerSite site = new PartnerSite();
        site.setName(_siteName);
        return findUniqueOrNone(site);
    }

    //endregion

}