/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;

import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.DisplayName;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;

import org.apache.log4j.Logger;
import org.openclinica.ws.beans.*;
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

        // Authentication
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IOpenClinicaService svcEdc = this.createEdcConnection(defaultAccount);

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

        // Create example to query parent study
        de.dktk.dd.rpb.core.ocsoap.types.Study selectedStudy = new Study();
        selectedStudy.setStudyIdentifier(studyIdentifier);

        // When study and site identifier are not the same it is multi-centre study
        if (!studyIdentifier.equals(siteIdentifier)) {
            // Define which site to query
            selectedStudy.setSiteName(siteIdentifier);
        }

        try {
            // With too many parallel requests SOAP listing can fail
            List<StudySubjectWithEventsType> soapSubjects = svcEdc.listAllStudySubjectsByStudy(selectedStudy);
            // 15 seconds - hardcoded for now
            int retryTimeout = 15000;
            long endTime = System.currentTimeMillis() + retryTimeout;
            while (soapSubjects == null && System.currentTimeMillis() < endTime) {
                soapSubjects = svcEdc.listAllStudySubjectsByStudy(selectedStudy);
            }

            if (soapSubjects != null) {
                for (StudySubjectWithEventsType ocSubject : soapSubjects) {

                    String studySubjectNameIdentifier = WebDavUtils.buildStudySubjectIdentifier(ocSubject);
                    Date convertEnrollmentDate = ocSubject.getEnrollmentDate().toGregorianCalendar().getTime();
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
            else {
                log.error("WebDAV: OC SOAP list all subjects failed, probably too many parallel requests.");
            }

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        catch (OCConnectorException e) {
            log.error(e);
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