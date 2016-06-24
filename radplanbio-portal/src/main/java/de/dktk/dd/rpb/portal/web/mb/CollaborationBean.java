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

package de.dktk.dd.rpb.portal.web.mb;

import java.io.*;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.PartnerSiteRepository;
import de.dktk.dd.rpb.core.util.ResourcesUtil;

import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.springframework.context.annotation.Scope;

/**
 * ViewModel for collaboration View
 *
 * @author tomas@skripcak.net
 * @since 17 Jun 2015
 */
@Named("mbCollaboration")
@Scope(value="view")
public class CollaborationBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Resources

    @Inject
    @SuppressWarnings("unused")
    protected ResourcesUtil resourcesUtil;

    //endregion

    //region Messages

    @Inject
    @SuppressWarnings("unused")
    protected MessageUtil messageUtil;

    //endregion

    //region PartnerSite repository

    @Inject
    private PartnerSiteRepository partnerSiteRepository;

    /**
     * Set StudyRepository
     *
     * @param value - StudyRepository
     */
    @SuppressWarnings("unused")
    public void setPartnerSiteRepositoryRepository(PartnerSiteRepository value) {
        this.partnerSiteRepository = value;
    }

    //endregion

    //endregion

    //region Members

    private MapModel simpleModel;
    private Marker marker;

    //endregion

    //region Properties

    public MapModel getSimpleModel() {
        return simpleModel;
    }

    @SuppressWarnings("unused")
    public Marker getMarker() {
        return marker;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        simpleModel = new DefaultMapModel();

        List<PartnerSite> partnerSites = this.partnerSiteRepository.find();

        for (PartnerSite site : partnerSites) {
            if (site.getIsEnabled()) {

                LatLng coord = null;
                if (site.getLatitude() != null && site.getLongitude() != null) {
                    coord = new LatLng(site.getLatitude(), site.getLongitude());
                }

                if (coord != null) {
                    simpleModel.addOverlay(new Marker(coord, site.getDescription()));
                }
            }
        }
    }

    //endregion

    //region EventHandlers

    public void onMarkerSelect(OverlaySelectEvent event) {
        marker = (Marker)event.getOverlay();
        this.messageUtil.infoText(this.marker.getTitle());
    }

    //endregion

}
