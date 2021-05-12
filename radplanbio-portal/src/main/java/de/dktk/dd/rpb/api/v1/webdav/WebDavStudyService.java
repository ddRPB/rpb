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
import de.dktk.dd.rpb.core.service.IOpenClinicaService;

import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.DisplayName;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;

import org.apache.log4j.Logger;
import org.openclinica.ws.beans.StudyType;
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
@Path("/v1/webdav/studies")
public class WebDavStudyService extends BaseService  {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavStudyService.class);

    //endregion

    //region Resources

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
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers ,
            @Context HttpServletRequest httpServletRequest){

        // Authentication
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IOpenClinicaService svcEdc = this.createEdcConnection(defaultAccount);

        // Make sure that studies URL ends with slash
        String studiesUrl = uriInfo.getRequestUri().toString();
        if (!studiesUrl.endsWith("/")) {
            studiesUrl += "/";
        }

        // Studies root folder for back navigation
        final Response studiesFolder = new Response(
            new HRef(studiesUrl),
            null,
            null,
            null,
            new PropStat(
                new Prop(
                    new DisplayName("studies"),
                    new CreationDate(new Date()),
                    new GetLastModified(new Date()),
                    COLLECTION
                ),
                new Status((javax.ws.rs.core.Response.StatusType) OK)
            )
        );

        // This is the top level studies root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(studiesFolder)).build();
        }
        // Otherwise expand
        final Collection<Response> responses = new LinkedList<>(singletonList(studiesFolder));

        // With too many parallel requests SOAP listing can fail
        List<StudyType> soapStudies = svcEdc.listAllStudies();
        // 15 seconds - hardcoded for now
        int retryTimeout = 15000;
        long endTime = System.currentTimeMillis() + retryTimeout;
        while (soapStudies == null && System.currentTimeMillis() < endTime) {
            this.sleepSecond();
            soapStudies = svcEdc.listAllStudies();
        }

        if (soapStudies != null) {
            for (StudyType ocStudy : soapStudies) {
                Response studyFolder = new Response(
                    new HRef(studiesUrl + ocStudy.getIdentifier()),
                    null,
                    null,
                    null,
                    new PropStat(
                        new Prop(
                            new DisplayName(ocStudy.getIdentifier()),
                            new CreationDate(new Date()),
                            new GetLastModified(new Date()),
                            COLLECTION
                        ),
                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                    )
                );
                
                responses.add(studyFolder);
            }
        }
        else {
            log.error("WebDAV: OC SOAP list all studies failed, probably too many parallel requests.");
        }
        
        return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
    }

    @PROPFIND
    @Path("{studyIdentifier}")
    public final javax.ws.rs.core.Response propfind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers ,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that selected study URL ends with slash
        String studyUrl = uriInfo.getRequestUri().toString();
        if (!studyUrl.endsWith("/")) {
            studyUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty()) {

            // Selected study folder for back navigation
            final Response studyFolder = new Response(
                new HRef(studyUrl),
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

            final Collection<Response> responses = new LinkedList<>(singletonList(studyFolder));

            Response studySiteFolder = new Response(
                new HRef(studyUrl + "sites"),
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

            responses.add(studySiteFolder);

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: Requested study identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }
    
    //endregion

    //endregion

}
