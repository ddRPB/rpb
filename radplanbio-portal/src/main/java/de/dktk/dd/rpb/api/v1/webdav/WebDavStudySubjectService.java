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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.OK;
import static net.java.dev.webdav.jaxrs.Headers.*;
import static net.java.dev.webdav.jaxrs.ResponseStatus.MULTI_STATUS;
import static net.java.dev.webdav.jaxrs.xml.properties.ResourceType.COLLECTION;

/**
 * Service handling WebDAV requests to access RPB data
 */
@Component
@Path("/v1/webdav/studies/{studyIdentifier}/sites/{siteIdentifier}/subjects")
public class WebDavStudySubjectService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavStudySubjectService.class);

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
            @PathParam("siteIdentifier") final String siteIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        String subjectsUrl = uriInfo.getRequestUri().toString();
        if (!subjectsUrl.endsWith("/")) {
            subjectsUrl += "/";
        }
        
        final Response studySubjectsFolder = new Response(
            new HRef(subjectsUrl),
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

        // This is the top level study subjects root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(studySubjectsFolder)).build();
        }
        // Otherwise expand
        final Collection<Response> responses = new LinkedList<>(singletonList(studySubjectsFolder));

        // Init the facade with OC data service with access to OC database
        this.studyIntegrationFacade.init(this.openClinicaDataRepository);

        // Define EDC study for query
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study();
        // Query for siteIdentifier (in mono-centre it is the same as parent study)
        edcStudy.setUniqueIdentifier(siteIdentifier);

        try {

            List <de.dktk.dd.rpb.core.domain.edc.StudySubject> studySubjects = this.studyIntegrationFacade.loadStudySubjects(edcStudy);

            if (studySubjects != null) {
                for (de.dktk.dd.rpb.core.domain.edc.StudySubject studySubject : studySubjects) {

                    String studySubjectNameIdentifier = WebDavUtils.buildStudySubjectIdentifier(studySubject);
                    Date convertEnrollmentDate = studySubject.getDateEnrollment();
                    Response studySubjectFolder = new Response(
                        new HRef(subjectsUrl + studySubjectNameIdentifier),
                        null,
                        null,
                        null,
                        new PropStat(
                            new Prop(
                                new DisplayName(studySubjectNameIdentifier),
                                new CreationDate(convertEnrollmentDate),
                                new GetLastModified(convertEnrollmentDate),
                                COLLECTION
                            ),
                            new Status((javax.ws.rs.core.Response.StatusType) OK)
                        )
                    );

                    responses.add(studySubjectFolder);
                }
            }

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        catch (Exception err) {
            log.error(err);
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    @PROPFIND
    @Path("{studySubjectIdentifier}")
    public final javax.ws.rs.core.Response propfind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        String subjectUrl = uriInfo.getRequestUri().toString();
        if(!subjectUrl.endsWith("/")) {
            subjectUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty() &&
            studySubjectIdentifier != null && !studySubjectIdentifier.isEmpty()) {

            final Response studySubjectFolder = new Response(
                new HRef(subjectUrl),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName(studySubjectIdentifier),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );

            final Collection<Response> responses = new LinkedList<>(singletonList(studySubjectFolder));

            //TODO: load this flag from user profile settings that should be persisted in DB
            boolean showDicomEvents = false;
            if (showDicomEvents) {
                Response studyEventFolder = new Response(
                    new HRef(subjectUrl + "events"),
                    null,
                    null,
                    null,
                    new PropStat(
                        new Prop(
                            new DisplayName("events"),
                            new CreationDate(new Date()),
                            new GetLastModified(new Date()),
                            COLLECTION
                        ),
                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                    )
                );

                responses.add(studyEventFolder);

                return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
            }
            else {
                Response studyEventFolder = new Response(
                    new HRef(subjectUrl + "dcmstudies"),
                    null,
                    null,
                    null,
                    new PropStat(
                        new Prop(
                            new DisplayName("dcmstudies"),
                            new CreationDate(new Date()),
                            new GetLastModified(new Date()),
                            COLLECTION
                        ),
                        new Status((javax.ws.rs.core.Response.StatusType) OK)
                    )
                );

                responses.add(studyEventFolder);

                return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
            }

        }
        else {
            log.error("WebDAV: Requested study/site/subject identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    //endregion

    //endregion
    
}