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

package de.dktk.dd.rpb.api.v1.dicomweb.retrieve;

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
 * Service handling Retrieval of DICOM Studies binaries as aggregate root resources
 *
 * This access lvl does not consider the site ownership of patient, which means
 * that such query can be only done withing of scope of DefaultAccount associated
 * PACS system (user PartnerSite PACS)
 */
@Component
@Path("/v1/dicomweb/studies")
public class DicomStudiesService extends BaseService {

    //region Finals

    private static final Logger log = Logger.getLogger(DicomStudiesService.class);

    //endregion

    //region DicomInstance

    //region GET

//    @GET
//    @Path("/{stUidParam}/series/{seUidParam}/instances/{inUidParam}")
//    @Produces({"application/dicom"})
//    public Response getInstance(@Context HttpHeaders headers,
//                                @PathParam("stUidParam") String studyIntanceUid,
//                                @PathParam("seUidParam") String seriesInstanceUid,
//                                @PathParam("inUidParam") String sopIntanceUid) {
//
//        // ApiKey for authentication
//        String apiKey = headers.getRequestHeader("X-Api-Key").get(0);
//        if (apiKey == null || apiKey.equals("")) {
//            log.info("Missing X-Api-Key, unauthorised");
//            return javax.ws.rs.core.Response.status(401).build();
//        }
//
//        // Find user that corresponds to that specific apiKey
//        DefaultAccount userAccount = this.getAuthenticatedUser(apiKey);
//        if (userAccount == null) {
//            log.info("No apiKey corresponding user, unauthorised");
//            return javax.ws.rs.core.Response.status(401).build();
//        }
//
//
//
//        return javax.ws.rs.core.Response.status(200).entity(userAccount).build();
//    }

    //endregion

    //endregion

}
