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

package de.dktk.dd.rpb.api.v1;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * Service handling PartnerSites as aggregate root resources
 *
 * This is important as RPB allows to define different backend systems per partner site,
 * This means that for de-central RPB to RPB communication the RPB API should be used to create abstraction
 * on top of local RPB to module system communication
 */
@Component
@Path("/v1/partnersites")
public class PartnerSiteService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(PartnerSiteService.class);

    //endregion

    //region Members

    private IPartnerSiteRepository partnerSiteRepository;

    //endregion

    //region Constructors

    @Inject
    public PartnerSiteService(IPartnerSiteRepository partnerSiteRepository) {
        this.partnerSiteRepository = partnerSiteRepository;
    }

    //endregion

    //region Resource Methods

    //region GET

    @GET
    @Path("/{partnerSiteIdentifier}/")
    @Produces({"application/vnd.partnersite.v1+json"})
    public Response readPartnerSite(@Context HttpHeaders headers,
                                    @PathParam("partnerSiteIdentifier") String partnerSiteIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // OK
        return javax.ws.rs.core.Response.status(200).build();
    }

    @GET
    @Produces({"application/vnd.partnersite.v1+json"})
    public Response readPartnerSites(@Context HttpHeaders headers) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Set logged user name for auditing
        this.auditLogService.setUsername(userAccount.getUsername());

        // OK
        return javax.ws.rs.core.Response.status(200).build();
    }

    //endregion

    //endregion

    //region PACS - this should go to its own resource

    //region DICOM viewers

    @GET
    @Path("/{partnerSiteIdentifier}/pacs/dicomviewers/html5/patients/{patientId}/studies/{studyInstanceUid}/series/{seriesInstanceUid}/")
    public Response getHtml5DicomSeriesViewer(@Context HttpHeaders headers,
                                         @PathParam("partnerSiteIdentifier") String partnerSiteIdentifier,
                                         @PathParam("patientId") String patientId,
                                         @PathParam("studyInstanceUid") String studyInstanceUid,
                                         @PathParam("seriesInstanceUid") String seriesInstanceUid) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        PartnerSite partnerSiteExample = new PartnerSite();
        partnerSiteExample.setIdentifier(partnerSiteIdentifier);
        PartnerSite partnerSite = this.partnerSiteRepository.get(partnerSiteExample);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write("test");
                writer.flush();
            }
        };

        return Response.ok(stream).build();

//            response.setContentType("text/html");
//            PrintWriter out = response.getWriter();
//
//            String cgiUrl = this.getStudySite().getPacs().getPacsBaseUrl();
//            String mode = "";
//
//            // These are the identifications according to which we are going to browse the PACS server
//            String patientIdMatch = "";
//            String seriesUID = "";
//            String queryStrings = "";
//
//            // Public URL for proxy (this URL is used in lua code in Conquest in order to generate javascript)
//            String proxy = this.getStudySite().getPortal().getPublicUrl();
//
//            // Look in the query string to find which mode to use for ConQuest
//            if (request.getParameter("mode") != null) {
//                mode = request.getParameter("mode");
//            }
//
//            // Look for query strings which are necessary for DICOM viewer
//            if (mode.equals("radplanbioview") || mode.equals("radplanbiosimpleview")) {
//                if (request.getParameter("patientId") != null) {
//                    patientIdMatch = request.getParameter("patientId");
//                }
//                if (request.getParameter("seriesUID") != null) {
//                    seriesUID =  request.getParameter("seriesUID");
//                }
//
//                if (!this.viewerHasSupportForBrowser(request)) {
//                    mode = "radplanbioviewold";
//                }
//
//                queryStrings = "?mode=" + mode +
//                        "&patientidmatch=" + patientIdMatch +
//                        "&seriesUID=" + seriesUID +
//                        "&proxy=" + proxy;
//            }
//
//            // Create a URL which will be requested from ConQuest web server
//            URL url = new URL(cgiUrl + queryStrings);
//
//            URLConnection connection = url.openConnection();
//            connection.setDoOutput(true);
//            connection.connect();
//
//            // Reading output from URL
//            DataInputStream inputStream = new DataInputStream(connection.getInputStream());
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//            // Print everything to web page (It is just a viewer)
//            String inputLine;
//            while ((inputLine = bufferedReader.readLine()) != null) {
//                out.println(inputLine);
//            }
    }

    //endregion

    //region DICOMweb

    @GET
    @Path("/{partnerSiteIdentifier}/pacs/dicomweb/patients/{patientId}/studies/{studyInstanceUid}/series/{seriesInstanceUid}/instances/{sopInstanceUid}")
    public Response getDicomInstance(@Context HttpHeaders headers,
                                     @PathParam("partnerSiteIdentifier") String partnerSiteIdentifier,
                                     @PathParam("patientId") String patientId,
                                     @PathParam("studyInstanceUid") String studyInstanceUid,
                                     @PathParam("seriesInstanceUid") String seriesInstanceUid,
                                     @PathParam("sopInstanceUid") String sopInstanceUid) {

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write("test");
                writer.flush();
            }
        };

        return Response.ok(stream).build();
    }

    //endregion

    //endregion

}
