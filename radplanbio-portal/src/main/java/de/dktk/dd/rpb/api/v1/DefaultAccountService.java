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

package de.dktk.dd.rpb.api.v1;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    private static final Logger log = LoggerFactory.getLogger(DefaultAccountService.class);

    //endregion

    //region DefaultAccount

    //region GET

    @GET
    @Path("/{defaultAccountIdentifier}")
    @Produces({"application/vnd.defaultaccount.v1+json;charset=UTF-8"})
    public Response getDefaultAccount(@Context HttpHeaders headers,
                                      @PathParam("defaultAccountIdentifier") String defaultAccountIdentifier) {

        // ApiKey for authentication
        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
        if (apiKey == null || apiKey.equals("")) {
            log.info("Missing X-Api-Key, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Find user that corresponds to that specific apiKey
        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
        if (userAccount == null) {
            log.info("No ApiKey corresponding user, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        log.info("User with corresponding ApiKey was found.");

        Response notAuthorised = this.notAuthorisedResponse(defaultAccountIdentifier, userAccount);
        if (notAuthorised != null) {
            return notAuthorised;
        }

        log.info("User is authorised to retrieve his DefaultAccount data.");

        // For OC accounts load additional resources
        if (userAccount.hasOpenClinicaAccount()) {

            // Load OC password hash
            userAccount.setOcPasswordHash(
                this.openClinicaDataRepository.getUserAccountHash(
                        userAccount.getOcUsername()
                )
            );

            log.info("EDC User password hash loaded from EDC system");

            // Load OC active study
            userAccount.setActiveStudy(
                this.openClinicaDataRepository.getUserActiveStudy(
                        userAccount.getOcUsername()
                )
            );

            log.info("Active EDC study loaded for user.");
        }

        return javax.ws.rs.core.Response.status(200).entity(userAccount).build();
    }

    //endregion

    //endregion

    //region Study

    //region PUT

    @PUT
    @Path("/{defaultAccountIdentifier}/activestudy/{studyIdentifierParam}")
    public Response changeDefaultAccountActiveStudy(@Context HttpHeaders headers,
                                                    @PathParam("defaultAccountIdentifier") String defaultAccountIdentifier,
                                                    @PathParam("studyIdentifierParam") String studyIdentifierParam) {

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

        Response notAuthorised = this.notAuthorisedResponse(defaultAccountIdentifier, userAccount);
        if (notAuthorised != null) {
            return notAuthorised;
        }

        Study newActiveStudy = this.openClinicaDataRepository.getStudyByIdentifier(studyIdentifierParam);
        // Workaround for use in python client which sends primary key as identifier
        if (newActiveStudy == null) {
            newActiveStudy = this.openClinicaDataRepository.getStudyById(studyIdentifierParam);
        }

        if (this.openClinicaDataRepository.changeUserActiveStudy(
                userAccount.getOcUsername(),
                newActiveStudy
            )) {
            return javax.ws.rs.core.Response.status(204).build(); // OK, return no content code
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
