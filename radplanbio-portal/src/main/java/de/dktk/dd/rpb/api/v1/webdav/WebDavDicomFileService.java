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

import com.sun.jersey.core.header.ContentDisposition;
import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.api.support.WebDavUtils;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.IConquestService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.*;
import net.java.dev.webdav.jaxrs.xml.properties.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.OK;
import static net.java.dev.webdav.jaxrs.Headers.*;
import static net.java.dev.webdav.jaxrs.ResponseStatus.MULTI_STATUS;
import static net.java.dev.webdav.jaxrs.xml.properties.ResourceType.COLLECTION;

/**
 * Service handling WebDAV requests to access RPB data
 */
@Component
@Path("/v1/webdav/studies/{studyIdentifier}/sites/{siteIdentifier}/subjects/{studySubjectIdentifier}/events/{eventIdentifier}/dcmstudies/{dicomStudyIdentifier}/dcmseries/{dicomSeriesIdentifier}/dcm")
public class WebDavDicomFileService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavDicomFileService.class);

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
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("eventIdentifier") final String eventIdentifier,
            @PathParam("dicomStudyIdentifier") final String dicomStudyIdentifier,
            @PathParam("dicomSeriesIdentifier") final String dicomSeriesIdentifier,
            @DefaultValue(DEPTH_INFINITY) @HeaderParam(DEPTH) final String depth,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(studyIdentifier, siteIdentifier, studySubjectIdentifier, eventIdentifier, dicomStudyIdentifier, dicomSeriesIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        // Authenticate user
        DefaultAccount defaultAccount = this.defaultAccountAuthentication(httpServletRequest);
        IOpenClinicaService svcEdc = this.createEdcConnection(defaultAccount);
        IConquestService svcPacs = this.createPacsConnection(defaultAccount);

        // Make sure that selected dicom files URL ends with slash
        String dicomFilesUrl = uriInfo.getRequestUri().toString();
        if (!dicomFilesUrl.endsWith("/")) {
            dicomFilesUrl += "/";
        }

        final Response filesFolder = new Response(
            new HRef(dicomFilesUrl),
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

        // This is the top level study events root return

        if (depth.equals(DEPTH_0)) {
            return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(filesFolder)).build();
        }
        // Expand files
        final Collection<Response> responses = new LinkedList<>(singletonList(filesFolder));

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
            String trimDicomSeriesIdentifier = WebDavUtils.trimDicomSeriesNameIdentifier(dicomSeriesIdentifier);

            // Decode identifier
            String dicomStudyUid = WebDavUtils.decodeDicomUid(trimDicomStudyIdentifier);
            String dicomSeriesUid = WebDavUtils.decodeDicomUid(trimDicomSeriesIdentifier);

            // Load selected DICOM series from PACS
            DicomSeries dicomSeries = svcPacs.loadStudySeries(dicomPatientId, dicomStudyUid, dicomSeriesUid);
            
            if (dicomSeries != null) {

                for (DicomImage dicomImage : dicomSeries.getSeriesImages()) {

                    String dicomImageNameIdentifier = WebDavUtils.encodeDicomUid(dicomImage.getSopInstanceUID()) + ".dcm";
                    int size = dicomImage.getSize() != null ? dicomImage.getSize() : 1024;

                    Response fileResponse = new net.java.dev.webdav.jaxrs.xml.elements.Response(
                            new HRef(dicomFilesUrl + dicomImageNameIdentifier),
                            null,
                            null,
                            null,
                            new PropStat(
                            new Prop(
                                new DisplayName(dicomImageNameIdentifier),
                                new CreationDate(new Date()),
                                new GetLastModified(new Date()),
                                new GetContentLength(size),
                                new GetContentType(MediaType.APPLICATION_OCTET_STREAM)
                            ),
                            new Status((javax.ws.rs.core.Response.StatusType) OK)
                        )
                    );
                    responses.add(fileResponse);
                }

                return javax.ws.rs.core.Response.status(MULTI_STATUS).entity(new MultiStatus(responses.toArray(new net.java.dev.webdav.jaxrs.xml.elements.Response[0]))).build();
            }
            else {
                log.warn("WebDAV: Requested DICOM series does not exist.");
                return javax.ws.rs.core.Response.status(404).build();
            }
        }
        else {
            log.warn("WebDAV: Requested study subject does not exist within requested study.");
            return javax.ws.rs.core.Response.status(404).build();
        }
    }

    //endregion

    //region GET

    @GET
    @Path("/{fileIdentifier:.*}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public  final javax.ws.rs.core.Response getFile(
            @Context final UriInfo uriInfo,
            @PathParam("studyIdentifier") final String studyIdentifier,
            @PathParam("siteIdentifier") final String siteIdentifier,
            @PathParam("studySubjectIdentifier") final String studySubjectIdentifier,
            @PathParam("eventIdentifier") final String eventIdentifier,
            @PathParam("dicomStudyIdentifier") final String dicomStudyIdentifier,
            @PathParam("dicomSeriesIdentifier") final String dicomSeriesIdentifier,
            @PathParam("fileIdentifier") final String fileIdentifier,
            @Context final Providers providers,
            @Context HttpServletRequest httpServletRequest) {

        // Workaround for windows native client that tries to lookup for things that do not exists in RPB
        if (WebDavUtils.isWindowsFileSystemIdentifier(dicomStudyIdentifier, dicomSeriesIdentifier, fileIdentifier)) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        String apiKey = this.extractApiKey(httpServletRequest);
        IConquestService svcPacs = this.createPacsConnection(apiKey);

        if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
            siteIdentifier != null && !siteIdentifier.isEmpty() &&
            studySubjectIdentifier != null && !studySubjectIdentifier.isEmpty() &&
            eventIdentifier != null && !eventIdentifier.isEmpty() &&
            dicomStudyIdentifier != null && !dicomStudyIdentifier.isEmpty() &&
            dicomSeriesIdentifier != null && !dicomSeriesIdentifier.isEmpty() &&
            fileIdentifier != null && !fileIdentifier.isEmpty()) {

            // Trim  all identifiers (remove the all prefix from identifiers )
            String trimDicomFileIdentifier = WebDavUtils.trimDicomInstanceNameIdentifier(fileIdentifier);
            String trimDicomSeriesIdentifier = WebDavUtils.trimDicomSeriesNameIdentifier(dicomSeriesIdentifier);
            String trimDicomStudyIdentifier = WebDavUtils.trimDicomStudyNameIdentifier(dicomStudyIdentifier);

            // Decode identifiers
            String dicomStudyUid = WebDavUtils.decodeDicomUid(trimDicomStudyIdentifier);
            String dicomSeriesUid = WebDavUtils.decodeDicomUid(trimDicomSeriesIdentifier);
            String dicomSopUid = WebDavUtils.decodeDicomUid(trimDicomFileIdentifier);

            // Load Attributes from PACS
            InputStream in = svcPacs.loadWadoDicomStream(dicomStudyUid, dicomSeriesUid, dicomSopUid);

            ContentDisposition contentDisposition = ContentDisposition
                    .type("attachment")
                    .fileName(fileIdentifier)
                    .creationDate(new Date())
                    .build();

            return javax.ws.rs.core.Response
                    .ok(in)
                    .header("Content-Disposition", contentDisposition)
                    .build();
        }
        else {
            log.error("WebDAV: Requested study/site/subject/event/dcmstudy/dcmseries/dcm identifier?cgi is null or empty.");
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    // endregion

    //endregion

}
