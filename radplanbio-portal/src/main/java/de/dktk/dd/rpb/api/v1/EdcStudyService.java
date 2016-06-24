package de.dktk.dd.rpb.api.v1;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.EmailService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Service handling EDC Studies as aggregate root resources (EDC study centric)
 */
@Component
@Path("/v1/edcstudies")
public class EdcStudyService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(EdcStudyService.class);

    //endregion

    //region Injects

    //region AuditService

    @Inject
    private AuditLogService auditLogService;

    @SuppressWarnings("unused")
    public void setAuditLogService(AuditLogService svc) {
        this.auditLogService = svc;
    }

    @Inject
    private EmailService emailService;

    @SuppressWarnings("unused")
    public void setEmailService(EmailService svc) {
        this.emailService = svc;
    }

    @Inject
    private IOpenClinicaDataRepository ocRepository;

    @SuppressWarnings("unused")
    public void setOcRepository(IOpenClinicaDataRepository ocRepository) {
        this.ocRepository = ocRepository;
    }

    //endregion

    //endregion

    //region Study

    //region GET

    @GET
    @Path("/{param}")
    @Produces({"application/vnd.edcstudy.v1+json"})
    public Response getEdcStudy(@Context HttpHeaders headers,
                                @PathParam("param") String edcStudyIdentifier) {

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

        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.ocRepository.getStudyByIdentifier(edcStudyIdentifier);

        if (edcStudy != null) {
            return javax.ws.rs.core.Response.status(200).entity(edcStudy).build();
        }
        else {
            return javax.ws.rs.core.Response.status(404).build(); // Not found
        }
    }

    //endregion

    //endregion

}
