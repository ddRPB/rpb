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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Service handling EDC Studies as aggregate root resources (EDC study centric)
 *
 * It is basically facade for accessing EDC database data (for things that are not available over EDC web services directly)
 * Note: Later it should be possible to migrate this into StudyService
 */
@Component
@Path("/v1/edcstudies")
public class EdcStudyService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(EdcStudyService.class);

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

        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.openClinicaDataRepository.getStudyByIdentifier(edcStudyIdentifier);

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
