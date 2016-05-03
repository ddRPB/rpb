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

import de.dktk.dd.rpb.core.dao.admin.PidDao;
import de.dktk.dd.rpb.core.dao.admin.PortalDao;
import de.dktk.dd.rpb.core.dao.admin.ServerDao;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.dao.admin.PartnerSiteDao;
import de.dktk.dd.rpb.core.dao.admin.PacsDao;
import de.dktk.dd.rpb.core.dao.admin.EdcDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;

import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link PartnerSite} interface.
 * Note: you may use multiple DAO from this layer.
 * @see PartnerSiteRepository
 *
 * PartnerSiteRepository interface implementation
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("partnerSiteRepository")
@Singleton
public class PartnerSiteRepositoryImpl extends RepositoryImpl<PartnerSite, Integer> implements PartnerSiteRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PartnerSiteRepositoryImpl.class);

    //endregion

    //region Injects

    protected PartnerSiteDao partnerSiteDao;

    @Inject
    public void setPartnerSiteDao(PartnerSiteDao value) {
        this.partnerSiteDao = value;
    }

    // It is used when repository transaction with PACS is necessary
    protected PacsDao pacsDao;

    @Inject
    public void setPacsDao(PacsDao value) {
        this.pacsDao = value;
    }

    // It is used when repository transaction with EDC is necessary
    protected EdcDao edcDao;

    @Inject
    public void setEdcDao(EdcDao value) {
        this.edcDao = value;
    }

    // It is used when repository transaction with PID is necessary
    protected PidDao pidDao;

    @Inject
    public void setPidDao(PidDao value) {
        this.pidDao = value;
    }

    // It is used when repository transaction with Portal is necessary
    protected PortalDao portalDao;

    @Inject
    public void setPortalDao(PortalDao value) {
        this.portalDao = value;
    }

    // It is used when repository transaction with Server is necessary
    protected ServerDao serverDao;

    @Inject
    public void setServerDao(ServerDao value) {
        this.serverDao = value;
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

        if (!isNotEmpty(model.getSiteName())) {
            PartnerSite result = getBySiteName(model.getSiteName());
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
        site.setSiteName(_siteName);
        return findUniqueOrNone(site);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteBySiteName(String siteName) {
        delete(getBySiteName(siteName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void savePartnerSitePacs(PartnerSite partnerSite) {
        if (partnerSite.getPacs().isIdSet()) {
            this.pacsDao.merge(partnerSite.getPacs());
        }
        else {
            this.pacsDao.save(partnerSite.getPacs());
        }

        if (partnerSite.isIdSet()) {
            // I am saving PACS but if other elements are not stored they are just transcient new objects
            // for UI binding so null them before the merge to partnerSite is made
            if (partnerSite.getEdc() != null && !partnerSite.getEdc().isIdSet()) {
                partnerSite.setEdc(null);
            }
            if (partnerSite.getPid() != null &&!partnerSite.getPid().isIdSet()) {
                partnerSite.setPid(null);
            }
            if (partnerSite.getPortal() != null && !partnerSite.getPortal().isIdSet()) {
                partnerSite.setPortal(null);
            }
            if (partnerSite.getServer() != null && !partnerSite.getServer().isIdSet()) {
                partnerSite.setServer(null);
            }
            this.merge(partnerSite);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void savePartnerSiteEdc(PartnerSite partnerSite) {
        if (partnerSite.getEdc().isIdSet()) {
            this.edcDao.merge(partnerSite.getEdc());
        }
        else {
            this.edcDao.save(partnerSite.getEdc());
        }

        if (partnerSite.isIdSet()) {
            // I am saving EDC but if other elements are not stored they are just transcient new objects
            // for UI binding so null them before the merge to partnerSite is made
            if (partnerSite.getPacs() != null && !partnerSite.getPacs().isIdSet()) {
                partnerSite.setPacs(null);
            }
            if (partnerSite.getPid() != null &&!partnerSite.getPid().isIdSet()) {
                partnerSite.setPid(null);
            }
            if (partnerSite.getPortal() != null && !partnerSite.getPortal().isIdSet()) {
                partnerSite.setPortal(null);
            }
            if (partnerSite.getServer() != null && !partnerSite.getServer().isIdSet()) {
                partnerSite.setServer(null);
            }
            this.merge(partnerSite);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void savePartnerSitePid(PartnerSite partnerSite) {
        if (partnerSite.getPid().isIdSet()) {
            this.pidDao.merge(partnerSite.getPid());
        }
        else {
            this.pidDao.save(partnerSite.getPid());
        }

        if (partnerSite.isIdSet()) {
            // I am saving PID but if other elements are not stored they are just transcient new objects
            // for UI binding so null them before the merge to partnerSite is made
            if (partnerSite.getPacs() != null && !partnerSite.getPacs().isIdSet()) {
                partnerSite.setPacs(null);
            }
            if (partnerSite.getEdc() != null &&!partnerSite.getEdc().isIdSet()) {
                partnerSite.setEdc(null);
            }
            if (partnerSite.getPortal() != null && !partnerSite.getPortal().isIdSet()) {
                partnerSite.setPortal(null);
            }
            if (partnerSite.getServer() != null && !partnerSite.getServer().isIdSet()) {
                partnerSite.setServer(null);
            }
            this.merge(partnerSite);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void savePartnerSitePortal(PartnerSite partnerSite) {
        if (partnerSite.getPortal().isIdSet()) {
            this.portalDao.merge(partnerSite.getPortal());
        }
        else {
            this.portalDao.save(partnerSite.getPortal());
        }

        if (partnerSite.isIdSet()) {
            // I am saving Portal but if other elements are not stored they are just transcient new objects
            // for UI binding so null them before the merge to partnerSite is made
            if (partnerSite.getPacs() != null && !partnerSite.getPacs().isIdSet()) {
                partnerSite.setPacs(null);
            }
            if (partnerSite.getEdc() != null &&!partnerSite.getEdc().isIdSet()) {
                partnerSite.setEdc(null);
            }
            if (partnerSite.getPid() != null && !partnerSite.getPid().isIdSet()) {
                partnerSite.setPid(null);
            }
            if (partnerSite.getServer() != null && !partnerSite.getServer().isIdSet()) {
                partnerSite.setServer(null);
            }
            this.merge(partnerSite);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void savePartnerSiteServer(PartnerSite partnerSite) {
        if (partnerSite.getServer().isIdSet()) {
            this.serverDao.merge(partnerSite.getServer());
        }
        else {
            this.serverDao.save(partnerSite.getServer());
        }

        if (partnerSite.isIdSet()) {
            // I am saving Server but if other elements are not stored they are just transcient new objects
            // for UI binding so null them before the merge to partnerSite is made
            if (partnerSite.getPacs() != null && !partnerSite.getPacs().isIdSet()) {
                partnerSite.setPacs(null);
            }
            if (partnerSite.getEdc() != null &&!partnerSite.getEdc().isIdSet()) {
                partnerSite.setEdc(null);
            }
            if (partnerSite.getPid() != null && !partnerSite.getPid().isIdSet()) {
                partnerSite.setPid(null);
            }
            if (partnerSite.getPortal() != null && !partnerSite.getPortal().isIdSet()) {
                partnerSite.setPortal(null);
            }
            this.merge(partnerSite);
        }
    }

    //endregion

}