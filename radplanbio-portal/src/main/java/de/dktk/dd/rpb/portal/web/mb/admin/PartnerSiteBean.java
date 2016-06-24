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

package de.dktk.dd.rpb.portal.web.mb.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.dktk.dd.rpb.core.domain.admin.Pid;
import de.dktk.dd.rpb.core.domain.admin.Portal;
import de.dktk.dd.rpb.core.domain.admin.Server;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.admin.Pacs;
import de.dktk.dd.rpb.core.domain.edc.Edc;

import de.dktk.dd.rpb.core.repository.admin.PartnerSiteRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

/**
 * Bean for administration of PartnerSites
 *
 * @author tomas@skripcak.net
 * @since 07 August 2013
 */
@Named("mbPartnerSite")
@Scope("view")
public class PartnerSiteBean extends CrudEntityViewModel<PartnerSite, Integer> {

    //region Injects

    //region Repository

    @Inject
    PartnerSiteRepository repository;

    public PartnerSiteRepository getRepository() {
        return this.repository;
    }

    /**
     * Set PartnerSiteRepository
     *
     * @param value - PartnerSiteRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(PartnerSiteRepository value) {
        this.repository = value;
    }

    //endregion

    //endregion

    //region Properties

    //region PartnerSite PACS

    public Pacs getSelectedSitePacs() {
        if (this.selectedEntity != null && this.selectedEntity.getPacs() != null) {
            return this.selectedEntity.getPacs();
        }
        else if (this.selectedEntity != null) {
            this.selectedEntity.setPacs(new Pacs());
            return this.selectedEntity.getPacs();
        }

        return null;
    }

    public void setSelectedSitePacs(Pacs value) {
        if (this.selectedEntity != null) {
            this.selectedEntity.setPacs(value);
        }
    }

    //endregion

    //region PartnerSite EDC

    public Edc getSelectedSiteEdc() {
        if (this.selectedEntity != null && this.selectedEntity.getEdc() != null) {
            return this.selectedEntity.getEdc();
        }
        else if (this.selectedEntity != null) {
            this.selectedEntity.setEdc(new Edc());
            return this.selectedEntity.getEdc();
        }

        return null;
    }

    public void setSelectedSiteEdc(Edc value) {
        if (this.selectedEntity != null) {
            this.selectedEntity.setEdc(value);
        }
    }

    //endregion

    //region PartnerSite PID

    public Pid getSelectedSitePid() {
        if (this.selectedEntity != null && this.selectedEntity.getPid() != null) {
            return this.selectedEntity.getPid();
        }
        else if (this.selectedEntity != null) {
            this.selectedEntity.setPid(new Pid());
            return this.selectedEntity.getPid();
        }

        return null;
    }

    public void setSelectedSitePid(Pid value) {
        if (this.selectedEntity != null) {
            this.selectedEntity.setPid(value);
        }
    }

    //endregion

    //region PartnerSite Portal

    public Portal getSelectedSitePortal() {
        if (this.selectedEntity != null && this.selectedEntity.getPortal() != null) {
            return this.selectedEntity.getPortal();
        }
        else if (this.selectedEntity != null) {
            this.selectedEntity.setPortal(new Portal());
            return this.selectedEntity.getPortal();
        }

        return null;
    }

    public void setSelectedSitePortal(Portal value) {
        if (this.selectedEntity != null) {
            this.selectedEntity.setPortal(value);
        }
    }

    //endregion

    //region PartnerSite Server

    public Server getSelectedSiteServer() {
        if (this.selectedEntity != null && this.selectedEntity.getServer() != null) {
            return this.selectedEntity.getServer();
        }
        else if (this.selectedEntity != null) {
            this.selectedEntity.setServer(new Server());
            return this.selectedEntity.getServer();
        }

        return null;
    }

    public void setSelectedSiteServer(Server value) {
        if (this.selectedEntity != null) {
            this.selectedEntity.setServer(value);
        }
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();
    }

    //endregion

    //region Commands

    public void doUpdatePortal() {
        this.repository.savePartnerSitePortal(this.selectedEntity);
        this.load();

        this.messageUtil.infoText("Portal for site: " + this.selectedEntity.getSiteName() + " successfuly saved.");
    }

    public void doUpdateEdc() {
        this.repository.savePartnerSiteEdc(this.selectedEntity);
        this.load();

        this.messageUtil.infoText("EDC for site: " + this.selectedEntity.getSiteName() + " successfuly saved.");
    }

    public void doUpdatePacs() {
        this.repository.savePartnerSitePacs(this.selectedEntity);
        this.load();

        this.messageUtil.infoText("PACS for site: " + this.selectedEntity.getSiteName() + " successfuly saved.");
    }

    public void doUpdatePid() {
        this.repository.savePartnerSitePid(this.selectedEntity);
        this.load();

        this.messageUtil.infoText("PID generator for site: " + this.selectedEntity.getSiteName() + " successfuly saved.");
    }

    public void doUpdateIEServer() {
        this.repository.savePartnerSiteServer(this.selectedEntity);
        this.load();

        this.messageUtil.infoText("Import/Export Server for site: " + this.selectedEntity.getSiteName() + " successfuly saved.");
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
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();

        // AdminPartnerSites View
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colSiteIdentifier");
        if (column != null) {
            SortMeta sm1 = new SortMeta();
            sm1.setSortBy((UIColumn) column);
            sm1.setSortField("colSiteIdentifier");
            sm1.setSortOrder(SortOrder.ASCENDING);

            results.add(sm1);

            return results;
        }

        // AdminPartnerSites View
        column = viewRoot.findComponent(":form:tabView:dtTrialSites:colSiteIdentifier");
        if (column != null) {
            SortMeta sm1 = new SortMeta();
            sm1.setSortBy((UIColumn) column);
            sm1.setSortField("colSiteIdentifier");
            sm1.setSortOrder(SortOrder.ASCENDING);

            results.add(sm1);

            return results;
        }

        return results;
    }

    //endregion

}

