/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.api.v1.webdav;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.api.support.WebDavUtils;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;

import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.DisplayName;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import java.util.*;
import java.util.Collection;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.OK;
import static net.java.dev.webdav.jaxrs.Headers.*;
import static net.java.dev.webdav.jaxrs.ResponseStatus.MULTI_STATUS;
import static net.java.dev.webdav.jaxrs.xml.properties.ResourceType.COLLECTION;

/**
 * Service handling WebDAV requests to access RPB data
 */
@Component
@Path("/v1/webdav/studies/{studyIdentifier}/sites")
public class WebDavStudySiteService  extends BaseService{

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavStudySiteService.class);

    //endregion

    //region Resource Methods

    //region OPTIONS

    @OPTIONS
    public final javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.noContent()
                .header(DAV, "1, 2")
                .header("Allow", "PROPFIND,OPTIONS")
                .build();
    }

    //endregion

    //region PROPFIND

    @PROPFIND
    public final javax.ws.rs.core.Response propfind(
            @Context final UriInfo uriInfo,
            @PathParam("studyIdentifier") final String studyIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Authentication
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IOpenClinicaService svcEdc = this.createEdcConnection(defaultAccount);

        String sitesUrl = uriInfo.getRequestUri().toString();
        if (!sitesUrl.endsWith("/")) {
            sitesUrl += "/";
        }

        // Sites root folder for back navigation
        final Response sitesFolder = new Response(
            new HRef(sitesUrl),
            null,
            null,
            null,
            new PropStat(
                new Prop(
                    new DisplayName("sites"),
                    new CreationDate(new Date()),
                    new GetLastModified(new Date()),
                    COLLECTION
                ),
                new Status((javax.ws.rs.core.Response.StatusType) OK)
            )
        );

        // This is the top level studies root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(sitesFolder)).build();
        }
        // Otherwise expand
        final Collection<Response> responses = new LinkedList<>(singletonList(sitesFolder));

        // Metadata
        MetadataODM metadataOdm = svcEdc.getStudyMetadata(studyIdentifier);

        // XML to DomainObjects
        Odm odm = null;
        if (metadataOdm != null) {
            odm = metadataOdm.unmarshallOdm();
            odm.updateHierarchy();
        }

        if (odm != null && odm.getStudies() != null) {
            // Multi-centre
            if (odm.getStudies().size() > 1) {
                String parentStudyProtocolId = "";
                for (int i = 0; i < odm.getStudies().size(); i++) {

                    Study site = odm.getStudies().get(i);
                    if (i == 0) {
                        parentStudyProtocolId = site.getGlobalVariables().getProtocolName();
                    }
                    else {
                        String siteIdentifier = site.extractStudySiteIdentifier(parentStudyProtocolId);

                        Response siteFolder = new Response(
                            new HRef(sitesUrl + siteIdentifier),
                            null,
                            null,
                            null,
                            new PropStat(
                                new Prop(
                                    new DisplayName(siteIdentifier),
                                    new CreationDate(new Date()),
                                    new GetLastModified(new Date()),
                                    COLLECTION
                                ),
                                new Status((javax.ws.rs.core.Response.StatusType) OK)
                            )
                        );

                        responses.add(siteFolder);
                    }
                }
            }
            // Mono-centre: create artificial site from study identifier
            else {
                Response artificialSiteFolder = new Response(
                    new HRef(sitesUrl + studyIdentifier),
                    null,
                    null,
                    null,
                    new PropStat(
                        new Prop(
                            new DisplayName(studyIdentifier),
                            new CreationDate(new Date()),
                            new GetLastModified(new Date()),
                            COLLECTION
                        ),
                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                    )
                );

                responses.add(artificialSiteFolder);
            }

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: OC SOAP failed, studyIdentifier: " + studyIdentifier);
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    @PROPFIND
    @Path("{siteIdentifier}")
    public final javax.ws.rs.core.Response propfind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that selected site URL ends with slash
        String siteUrl = uriInfo.getRequestUri().toString();
        if (!siteUrl.endsWith("/")) {
            siteUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty()) {

            // Selected site folder for back navigation
            final Response siteFolder = new Response(
                new HRef(siteUrl),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName(siteIdentifier),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );
            
            final Collection<Response> responses = new LinkedList<>(singletonList(siteFolder));
            Response studySubjectFolder = new Response(
                    new HRef(siteUrl + "subjects"),
                    null,
                    null,
                    null,
                    new PropStat(
                        new Prop(
                            new DisplayName("subjects"),
                            new CreationDate(new Date()),
                            new GetLastModified(new Date()),
                            COLLECTION
                        ),
                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                    )
            );

            responses.add(studySubjectFolder);

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: Requested study/site identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    //endregion

    //endregion
}
