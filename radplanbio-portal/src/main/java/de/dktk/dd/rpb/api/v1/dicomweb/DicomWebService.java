/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

package de.dktk.dd.rpb.api.v1.dicomweb;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.service.CtpService;

import org.apache.log4j.Logger;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service handling DICOM Web Services aka DICOM web (PS3.18)
 * It creates clinical trial agnostic endpoint for DICOM web client
 */
@Component
@Path("/v1/dicomweb")
public class DicomWebService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(DicomWebService.class);

    //endregion

    //region Members
    
    private CtpService svcCtp;

    //endregion

    //region Constructors

    @Inject
    public DicomWebService(CtpService svcCtp) {
        this.svcCtp = svcCtp;
    }

    //endregion

    //region Resource Methods

    //region OPTIONS

    @OPTIONS
    public Response options() {
        return Response.status(400).build();
    }

    //endregion

    //region POST (STOW-RS)

    @POST
    @Path("/studies")
    @Consumes("multipart/related")
    public Response storeDicomStudy(@Context HttpHeaders headers,
                                    MimeMultipart multi) {
        return this.storeDicomStudy(headers, null, multi);
    }

    @POST
    @Path("/studies/{dicomStudyIdentifier}")
    @Consumes("multipart/related")
    public Response storeDicomStudy(@Context HttpHeaders headers,
                                    @PathParam("dicomStudyIdentifier") String dicomStudyIdentifier,
                                    MimeMultipart multi) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        String username = this.radPlanBioDataRepository.getDefaultAccountUsernameByApiKey(apiKey);
        if (username == null || username.isEmpty()) {
            log.info("No apiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Supported mime type (also parse multi value content type)
        String contentType = headers.getRequestHeader("content-type").get(0);
        String type = "";
        String boundaryMessage = "";
        if (contentType.contains(";")) {
            String[] params = contentType.split(";", -1);
            for (String param1 : params) {
                String param = param1.trim();
                // dcm4che stowrs client does put quotes around type
                if (param.startsWith("type=\"")) {
                    int index = param.indexOf("type=\"");
                    type = param.substring(index + 6, param.length() - 1);
                }
                // accept also client calls that do not have quotes around type
                else if (param.startsWith("type=")) {
                    int index = param.indexOf("type=");
                    type = param.substring(index + 5);
                }
                else if (param.startsWith("boundary=")) {
                    int index = param.indexOf("boundary=");
                    boundaryMessage = param.substring(index + 9);
                }
            }
        }
        else {
            type = contentType;
        }

        // Type
        if (type.isEmpty()) {
            log.info("STOWRS bad request (syntax)");
            return javax.ws.rs.core.Response.status(400).build();
        }

        // Boundary message (separator)
        if (boundaryMessage.isEmpty()) {
            log.info("STOWRS bad request (syntax)");
            return javax.ws.rs.core.Response.status(400).build();
        }

        // Specifies that the post is PS3.10 binary instances
        switch (type) {
            case "application/dicom":
                try {
                    // Read the parts from the multipart message
                    int parts = multi.getCount();
                    int importedParts = 0;
                    for (int i = 0; i < parts; i++) {
                        BodyPart part = multi.getBodyPart(i);
                        String partType = part.getContentType();

                        if (partType != null && partType.equals("application/dicom")) {

                            // Read DICOM
                            Attributes dcmAttributes = null;
                            DicomInputStream din = null;
                            InputStream is = part.getInputStream();

                            // Make a deep copy of input stream so that it can be used multiple times
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) > -1 ) {
                                baos.write(buffer, 0, len);
                            }
                            baos.flush();

                            // One input for DICOM reader
                            InputStream dis = new ByteArrayInputStream(baos.toByteArray());
                            // One input for http import
                            InputStream iis = new ByteArrayInputStream(baos.toByteArray());

                            try {
                                din = new DicomInputStream(dis);
                                dcmAttributes = din.readDataset(-1, -1);
                            }
                            catch (IOException e) {
                                log.error(e);
                            }
                            finally {
                                try {
                                    if (din != null) {
                                        din.close();
                                    }
                                }
                                catch (IOException err) {
                                    log.error(err);
                                }
                            }

                            String patientId = "";
                            String studyInstanceUid = "";
                            String seriesInstanceUid = "";
                            String instanceUid = "";
                            if (dcmAttributes != null) {
                                patientId = dcmAttributes.getString(Tag.PatientID);
                                studyInstanceUid = dcmAttributes.getString(Tag.StudyInstanceUID);
                                seriesInstanceUid = dcmAttributes.getString(Tag.SeriesInstanceUID);
                                instanceUid = dcmAttributes.getString(Tag.SOPInstanceUID);
                            }

                            // Everything received in multipart has to have the specified dicom study instance UID
                            boolean dicomStudyIdentifierMatch = true;
                            boolean dicomStudyIdentifierProvided = dicomStudyIdentifier != null && !dicomStudyIdentifier.equals("");
                            if (dicomStudyIdentifierProvided && studyInstanceUid != null && !studyInstanceUid.isEmpty()) {
                                dicomStudyIdentifierMatch = dicomStudyIdentifier.equals(studyInstanceUid);
                            }
                            // Skip this part -> it will lead to partially stored response (202)
                            if (dicomStudyIdentifierProvided && !dicomStudyIdentifierMatch) {
                                continue;
                            }
                            
                            boolean importSuccessful;
                            if (this.svcCtp != null) {
                                importSuccessful = this.svcCtp.httpImportDicom(iis);
                            }
                            else {
                                log.error("CTP service is not defined.");
                                return Response.status(400).build();
                            }

                            // Part stored
                            if (importSuccessful) {
                                importedParts++;
                            }
                        }
                        else {
                            log.info("STOWRS part " + i + ". unsupported media type");
                        }
                    }

                    // All parts stored = OK
                    if (importedParts == parts) {
                        return Response.status(200).build();
                    }
                    // Nothing stored = Conflict
                    else if (importedParts == 0) {
                        return Response.status(409).build();
                    }
                    // Partially stored = Accepted
                    else if (importedParts < parts) {
                        return Response.status(202).build();
                    }
                    else {
                        return Response.status(400).build();
                    }
                }
                catch (Exception err) {
                    log.error(err);
                    return Response.status(400).build();
                }
                // Specifies that the post is PS3.19 XML metadata and bulk data
            case "application/dicom+xml":
                // Unsupported media type
                return Response.status(415).build();
            // Specifies that the post is DICOM JSON metadata and bulk data
            case "application/dicom+json":
                // Unsupported media type
                return Response.status(415).build();
            // Content Type was not specified but is mandatory
            default:
                // Unsupported media type
                return Response.status(415).build();
        }
    }

    //endregion

    //endregion

}
