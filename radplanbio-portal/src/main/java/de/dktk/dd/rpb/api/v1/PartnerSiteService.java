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

/**
 * Service handling PartnerSites as aggregate root resources
 *
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
    @Path("/{partnerSiteIdentifier}")
    @Produces({"application/vnd.partnersite.v1+json;charset=UTF-8"})
    public Response getPartnerSite(@Context HttpHeaders headers,
                                   @PathParam("partnerSiteIdentifier") String partnerSiteIdentifier)  {

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

        // Allow only for administrators
        if (!userAccount.hasRoleName("ROLE_ADMIN")) {
            log.info("User has to be administrator, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        if (partnerSiteIdentifier == null || partnerSiteIdentifier.isEmpty()) {
            // Bad request
            return javax.ws.rs.core.Response.status(400).build();
        }

        // TODO: implemented advanced detection, identifier can be primary key or string identifier

        PartnerSite example = new PartnerSite();
        example.setIdentifier(partnerSiteIdentifier);

        PartnerSite partnerSite = this.partnerSiteRepository.findUniqueOrNone(example);

        // OK
        return javax.ws.rs.core.Response.status(200).entity(partnerSite).build();
    }

    //endregion

    //endregion
    
}
