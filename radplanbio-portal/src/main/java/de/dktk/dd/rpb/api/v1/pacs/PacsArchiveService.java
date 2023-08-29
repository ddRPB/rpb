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

package de.dktk.dd.rpb.api.v1.pacs;

import com.sun.jersey.core.header.ContentDisposition;
import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.EnumConquestMode;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.IConquestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Date;

/**
 * Service handling PACS specific archiving API that RPB exposes
 */
@Component
@Path("/v1/pacs/archive")
public class PacsArchiveService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(PacsArchiveService.class);

    //endregion

    //region GET

    //TODO: @RolesAllowed("ROLE_PACS_DOWNLOAD")
    @GET
    @Path("/{pacsObjectIdentifier}")
    @Produces("application/zip")
    public Response createArchive(@PathParam("pacsObjectIdentifier") String pacsObjectIdentifier,
                                  @QueryParam("mode") String zipMode,
                                  @QueryParam("verifyCount") Integer verifyCount,
                                  @Context HttpHeaders headers) {

        //TODO: authentication and authorization should be done in filter
        //TODO: can filter initialise defaultAccount as authenticated authorised user that can be used in the service?

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // ApiKey for authentication
        DefaultAccount account = this.getAuthenticatedUser(apiKey);
        if (account == null) {
            log.info("Missing or incorrect X-Api-Key, unauthorised");
            return Response.status(401).build();
        }

        if (!account.hasRoleName("ROLE_PACS_DOWNLOAD")) {
            log.info("Insufficient role, forbidden");
            return Response.status(403).build();
        }

        IConquestService svcPacs = this.createPacsConnection(account.getApiKey());

        if (pacsObjectIdentifier == null || pacsObjectIdentifier.isEmpty()) {
            log.info("PACS object identifier is null or empty, bad request");
            return Response.status(400).build();
        }
        if (zipMode == null || zipMode.isEmpty()) {
            log.info("ZIP mode query parameter is null or empty, bad request");
            return Response.status(400).build();
        }

        // Pseudonym:StudyInstanceUID
        int index = pacsObjectIdentifier.indexOf(":");
        String dicomPatientId = pacsObjectIdentifier.substring(0, index);

        InputStream is = null;
        
        if (zipMode.equalsIgnoreCase(EnumConquestMode.ZIP_STUDY.toString())) {

            String studyInstanceUid = pacsObjectIdentifier.substring(index + 1);

            if (verifyCount == null) {
                log.info("Study verifyCount query parameter is null or empty, bad request");
                return Response.status(400).build();
            }

            try {
                // Make sure that data is available within DICOM proxy cache (if used) before archiving attempt
                svcPacs.cacheDicomStudy(dicomPatientId, studyInstanceUid, verifyCount);
                is = svcPacs.archivePatientStudy(dicomPatientId, studyInstanceUid);

                // Audit the trigger of download of DICOM study
                this.auditLogService.setUsername(account.getUsername());
                this.auditLogService.event(
                        AuditEvent.PACSDataDownload,
                        "DicomStudy",
                        dicomPatientId,
                        studyInstanceUid
                );

            } catch(Exception err) {
                log.error(err.getMessage(), err);
                return Response.status(500).build();
            }

        } else if (zipMode.equalsIgnoreCase(EnumConquestMode.ZIP_SERIES.toString())) {

            // Pseudonym:StudyInstanceUID:SeriesInstanceUID
            int secIndex = pacsObjectIdentifier.indexOf(":", index + 1);
            String studyInstanceUid = pacsObjectIdentifier.substring(index + 1, secIndex);
            String seriesInstanceUid = pacsObjectIdentifier.substring(secIndex + 1);

            try {

                // When number of files in series not provided it need to be fetched before download
                int filesCount = 0;
                if (verifyCount == -1) {
                    DicomSeries dicomSeries = svcPacs.loadStudySeries(dicomPatientId, studyInstanceUid, seriesInstanceUid);

                    if (dicomSeries.getSeriesImages() != null) {
                        filesCount = dicomSeries.getSeriesImages().size();
                    }
                }

                // Make sure that data is available within DICOM proxy cache (if used) before archiving attempt
                svcPacs.cacheDicomSeries(dicomPatientId, studyInstanceUid, seriesInstanceUid, filesCount);
                is = svcPacs.archivePatientSeries(dicomPatientId, seriesInstanceUid);

                // Audit the trigger of download of DICOM series
                this.auditLogService.setUsername(account.getUsername());
                this.auditLogService.event(
                        AuditEvent.PACSDataDownload,
                        "DicomSeries",
                        dicomPatientId,
                        seriesInstanceUid
                );

            } catch(Exception err) {
                log.error(err.getMessage(), err);
                return Response.status(500).build();
            }
        }

        ContentDisposition contentDisposition = ContentDisposition
                .type("attachment")
                .fileName(pacsObjectIdentifier + ".zip")
                .creationDate(new Date())
                .build();

        return javax.ws.rs.core.Response
                .ok(is)
                .header("Content-Disposition", contentDisposition)
                .build();
    }

    //endregion

}
