/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.domain.rpb.Software;
import de.dktk.dd.rpb.core.domain.rpb.Portal;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.repository.rpb.ISoftwareRepository;
import de.dktk.dd.rpb.core.repository.rpb.IPortalRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;

import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of software which RadPlanBio portal of specific partner site is providing for download
 *
 * @author tomas@skripcak.net
 * @since 25 Nov 2014
 */
@Named("mbSoftware")
@Scope("view")
public class SoftwareBean extends CrudEntityViewModel<Software, Integer> {

    //region Injects

    //region Repository

    private ISoftwareRepository repository;
    private IPortalRepository portalRepository;
    private IPartnerSiteRepository partnerSiteRepository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public ISoftwareRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public SoftwareBean(ISoftwareRepository repository, IPartnerSiteRepository partnerSiteRepository, IPortalRepository portalRepository) {
        this.repository = repository;
        this.partnerSiteRepository = partnerSiteRepository;
        this.portalRepository = portalRepository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.loadSoftware();
    }

    //endregion

    //region Commands

    /**
     * Load all software associated with admin partner site portal
     */
    public void loadSoftware() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // Load fresh portal entity to get list of software
            PartnerSite partnerSite = new PartnerSite();
            partnerSite.setIdentifier(this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier"));
            Portal portal = this.partnerSiteRepository.findUnique(partnerSite).getPortal();
            this.entityList = portal.getSoftware();
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    /**
     * Insert a new software into repository
     */
    public void doCreateSoftware() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // Persist
            PartnerSite partnerSite = new PartnerSite();
            partnerSite.setIdentifier(this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier"));
            Portal portal = this.partnerSiteRepository.findUnique(partnerSite).getPortal();
            portal.addSoftware(this.newEntity);
            this.portalRepository.merge(portal);
            context.addMessage(null, new FacesMessage("Insert Successful", "Software: " + this.newEntity.getName() + " successfully saved."));

            this.newEntity = null;
            this.selectedEntity = null;

            // Reload and prepare new
            this.loadSoftware();
        }
        catch (Exception err) {
            context.validationFailed();
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    /**
     * Update selected software in repository
     */
    public void doUpdateSoftware(){
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // Commit changes
            PartnerSite partnerSite = new PartnerSite();
            partnerSite.setIdentifier(this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier"));
            Portal portal = this.partnerSiteRepository.findUnique(partnerSite).getPortal();
            portal.removeSoftware(this.selectedEntity);
            portal.addSoftware(this.selectedEntity);
            this.portalRepository.merge(portal);
            context.addMessage(null, new FacesMessage("Edit Successful", "Software: " + this.selectedEntity.getName() + " successfully saved."));

            // Reload actual
            this.loadSoftware();
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    /**
     * Delete selected software from repository
     */
    public void doDeleteSoftware(){
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // Delete
            PartnerSite partnerSite = new PartnerSite();
            partnerSite.setIdentifier(this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier"));
            Portal portal = this.partnerSiteRepository.findUnique(partnerSite).getPortal();
            portal.removeSoftware(this.selectedEntity);
            this.portalRepository.merge(portal);

            context.addMessage(null, new FacesMessage("Delete Successful", "Software: " + this.selectedEntity.getName() + " successfully deleted."));

            this.selectedEntity = null;

            // Reload actual
            this.loadSoftware();
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colSoftwareName", "colSoftwareName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    //endregion

}