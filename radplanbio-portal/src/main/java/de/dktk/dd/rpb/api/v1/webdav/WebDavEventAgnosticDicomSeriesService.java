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
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.IConquestService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;

import de.dktk.dd.rpb.core.util.Constants;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.elements.Status;
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
@Path("/v1/webdav/studies/{studyIdentifier}/sites/{siteIdentifier}/subjects/{studySubjectIdentifier}/dcmstudies/{dicomStudyIdentifier}/dcmseries")
public class WebDavEventAgnosticDicomSeriesService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavEventAgnosticDicomSeriesService.class);

    //endregion

    //region Resources

    //region OPTION

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
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("dicomStudyIdentifier") final String dicomStudyIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier, dicomStudyIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Authenticate user
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IOpenClinicaService svcEdc = this.createEdcConnection(defaultAccount);
        IConquestService svcPacs = this.createPacsConnection(defaultAccount);

        // Make sure that Dicom series URL ends with slash
        String dicomSeriesUrl = uriInfo.getRequestUri().toString();
        if (!dicomSeriesUrl.endsWith("/")) {
            dicomSeriesUrl += "/";
        }

        final Response seriesFolder = new Response(
            new HRef(dicomSeriesUrl),
            null,
            null,
            null,
            new PropStat(
                new Prop(
                    new DisplayName("dcmseries"),
                    new CreationDate(new Date()),
                    new GetLastModified(new Date()),
                    COLLECTION
                ),
                new Status((javax.ws.rs.core.Response.StatusType) OK)
            )
        );

        // This is the top level study events root return
        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(seriesFolder)).build();
        }
        // Expand
        final Collection<Response> responses = new LinkedList<>(singletonList(seriesFolder));

        // Create example to query parent study
        de.dktk.dd.rpb.core.ocsoap.types.Study selectedStudy = new Study();
        selectedStudy.setStudyIdentifier(studyIdentifier);

        // When study and site identifier are not the same it is multi-centre study
        if (!studyIdentifier.equals(siteIdentifier)) {
            // Define which site to study
            selectedStudy.setSiteName(siteIdentifier);
        }

        // Create example to query the study subject
        de.dktk.dd.rpb.core.ocsoap.types.StudySubject selectedSubject = new de.dktk.dd.rpb.core.ocsoap.types.StudySubject();
        String trimStudySubjectIdentifier = WebDavUtils.trimStudySubjectIdentifier(studySubjectIdentifier);
        selectedSubject.setStudySubjectLabel(trimStudySubjectIdentifier);

        // Check whether requested study subject exists within requested study
        if (svcEdc.studySubjectExistsInStudy(selectedSubject, selectedStudy)) {
            
            // Trim identifiers
            String dicomPatientId = WebDavUtils.trimDicomPatientPseudonymIdentifier(studySubjectIdentifier);
            String trimDicomStudyIdentifier = WebDavUtils.trimDicomStudyNameIdentifier(dicomStudyIdentifier);

            // Decode identifier
            String dicomStudyUid = WebDavUtils.decodeDicomUid(trimDicomStudyIdentifier);

            // Load DICOM study from PACS
            DicomStudy dicomStudy = svcPacs.loadPatientStudy(dicomPatientId, dicomStudyUid);
            if (dicomStudy != null) {
                for (DicomSeries dicomSeries : dicomStudy.getStudySeries()) {
                    
                    String dicomSeriesNameIdentifier = dicomSeries.getSeriesModality() +
                            Constants.RPB_IDENTIFIERSEP +
                            WebDavUtils.encodeDicomUid(dicomSeries.getSeriesInstanceUID());

                    // Combine date and time of DICOM study and DICOM series
                    Date seriesCombineDate = WebDavUtils.combineDateTime(dicomStudy.getDateStudy(), dicomSeries.getTimeSeries());
                    Response dicomSeriesFolder = new Response(
                        new HRef(dicomSeriesUrl + dicomSeriesNameIdentifier),
                        null,
                        null,
                        null,
                        new PropStat(
                            new Prop(
                                new DisplayName(dicomSeriesNameIdentifier),
                                new CreationDate(seriesCombineDate),
                                new GetLastModified(seriesCombineDate),
                                COLLECTION
                            ),
                            new Status((javax.ws.rs.core.Response.StatusType) OK)
                        )
                    );
                    
                    responses.add(dicomSeriesFolder);
                }
                
                return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
            }
            else {
                log.warn("WebDAV: Requested DICOM study does not exist.");
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        else {
            log.warn("WebDAV: Requested study subject does not exist withing requested study.");
            return javax.ws.rs.core.Response.status(404).build();
        }
    }

    @PROPFIND
    @Path("{dicomSeriesIdentifier}")
    public final javax.ws.rs.core.Response profind(
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("dicomStudyIdentifier") final String dicomStudyIdentifier,
            @PathParam("dicomSeriesIdentifier") final String dicomSeriesIdentifier,
            @Context final UriInfo uriInfo,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier,  dicomStudyIdentifier, dicomSeriesIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Make sure that Dicom series URL ends with slash
        String dicomSeriesUrl = uriInfo.getRequestUri().toString();
        if (!dicomSeriesUrl.endsWith("/")) {
            dicomSeriesUrl += "/";
        }

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty() &&
            studySubjectIdentifier != null && !studySubjectIdentifier.isEmpty() &&
            dicomStudyIdentifier != null && !dicomStudyIdentifier.isEmpty() &&
            dicomSeriesIdentifier != null && !dicomSeriesIdentifier.isEmpty()) {

            final Response dicomSeriesFolder = new Response(
                new HRef(dicomSeriesUrl),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName(dicomSeriesIdentifier),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );

            final Collection<Response> responses = new LinkedList<>(singletonList(dicomSeriesFolder));
            Response dicomFilesFolder = new Response(
                new HRef(dicomSeriesUrl + "dcm"),
                null,
                null,
                null,
                new PropStat(
                    new Prop(
                        new DisplayName("dcm"),
                        new CreationDate(new Date()),
                        new GetLastModified(new Date()),
                        COLLECTION
                    ),
                    new Status((javax.ws.rs.core.Response.StatusType) OK)
                )
            );
            
            responses.add(dicomFilesFolder);

            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new Response[0]))).build();
        }
        else {
            log.error("WebDAV: Requested study/site/subject/dcmstudy/dcmseries identifier is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    //endregion

    //endregion

}
