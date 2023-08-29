package de.dktk.dd.rpb.api.v1.pacs;

import com.sun.jersey.api.client.ClientResponse;
import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.service.IConquestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1/pacs")
public class PacsDicomSeriesWebService  extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(PacsDicomSeriesWebService.class);

    //endregion

    /**
     * Loads a DICOM client response for specific patient and study and series.
     * Authentication via X-Api-Key value (API Key of the user) in the header.
     *
     * @param headers request header
     * @param dicomPatientId PID
     * @param studyInstanceUid UID of the DICOM study
     * @param seriesInstanceUid UID of the DICOM series
     * @return response from the PACS
     */

    @GET
    @Path("/subjects/{subjectid}/studies/{study}/series/{series}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDicomSeriesData(@Context HttpHeaders headers,
                                       @PathParam("subjectid") String dicomPatientId,
                                       @PathParam("study") String studyInstanceUid,
                                       @PathParam("series") String seriesInstanceUid) {

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

        IConquestService conquestService = this.createPacsConnection(apiKey);
        ClientResponse response;
        try {
            response = conquestService.loadStudySeriesResponse(dicomPatientId, studyInstanceUid, seriesInstanceUid);
        }
        catch (Exception err) {
            log.error(err.getMessage(),err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(response.getEntityInputStream()).build();

    }
}
