/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Service handling DefaultAccounts as aggregate root resources
 */
@Component
@Path("/v1/defaultaccounts")
public class DefaultAccountService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(DefaultAccountService.class);

    //endregion

    //region Injects

    @Inject
    private IOpenClinicaDataRepository ocRepository;

    @SuppressWarnings("unused")
    public void setOcRepository(IOpenClinicaDataRepository ocRepository) {
        this.ocRepository = ocRepository;
    }

    //endregion

    //region DefaultAccount

    //region GET

    @GET
    @Path("/{param}")
    @Produces({"application/vnd.defaultaccount.v1+json"})
    public Response getDefaultAccount(@Context HttpHeaders headers,
                                      @PathParam("param") String identifier) {

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

        Response notAuthorised = this.notAuthorisedResponse(identifier, userAccount);
        if (notAuthorised != null) {
            return notAuthorised;
        }

        // For OC accounts load additional resources
        if (userAccount.hasOpenClinicaAccount()) {

            // Load OC password hash
            userAccount.setOcPasswordHash(
                this.ocRepository.getUserAccountHash(
                        userAccount.getOcUsername()
                )
            );

            // Load OC active study
            userAccount.setActiveStudy(
                this.ocRepository.getUserActiveStudy(
                        userAccount.getOcUsername()
                )
            );
        }

        return javax.ws.rs.core.Response.status(200).entity(userAccount).build();
    }

    //endregion

    //endregion

    //region Study

    //region PUT

    @PUT
    @Path("/{param}/activestudy/{studyIdentifierParam}")
    public Response changeDefaultAccountActiveStudy(@Context HttpHeaders headers, @PathParam("param") String identifier, @PathParam("studyIdentifierParam") String studyIdentifierParam) {

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

        Response notAuthorised = this.notAuthorisedResponse(identifier, userAccount);
        if (notAuthorised != null) {
            return notAuthorised;
        }

        Study newActiveStudy = this.ocRepository.getStudyByIdentifier(studyIdentifierParam);

        if (this.ocRepository.changeUserActiveStudy(
                userAccount.getOcUsername(),
                newActiveStudy)) {
            return javax.ws.rs.core.Response.status(204).build(); // OK, retrun no content code
        }
        else {
            return javax.ws.rs.core.Response.status(403).build(); // Something failed return forbidden code
        }
    }

    //endregion

    //endregion

    //region Private

    private Response notAuthorisedResponse(String defaultAccountIdentifier, DefaultAccount authenticatedUser) {
        // Detect what identifier is provided (id or username)
        boolean isId = defaultAccountIdentifier.matches("^[+-]?\\d+$");

        // Only allow to retrieve data of account of authenticated user
        if (isId) {
            if (!authenticatedUser.getId().toString().equals(defaultAccountIdentifier)) {
                log.info("Attempt to retrieve different than authenticated user, unauthorised");
                return javax.ws.rs.core.Response.status(401).build();
            }
        }
        else {
            if (!authenticatedUser.getUsername().equals(defaultAccountIdentifier)) {
                log.info("Attempt to retrieve different than authenticated user, unauthorised");
                return javax.ws.rs.core.Response.status(401).build();
            }
        }

        return null;
    }

    //endregion

}
