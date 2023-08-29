/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

import de.dktk.dd.rpb.api.support.BaseService;
import org.dcm4che3.util.UIDUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/v1/pacs/generateuids/")
public class PacsDicomUidGeneratorService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(PacsDicomUidGeneratorService.class);

    //endregion

    /**
     * Generates DICOM UIDs, based on the DICOM standard
     *
     * @param headers       HTTP header
     * @param orgRootPrefix String organisation specific UID prefix (default: '2.25.')
     * @param count         Number of UIDs to be generated (limit 10000)
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateDicomUids(@Context HttpHeaders headers,
                                      @QueryParam("orgroot") String orgRootPrefix,
                                      @QueryParam("count") Integer count) {


        if (orgRootPrefix == null) {
            orgRootPrefix = "2.25";
        }

        if (count == null) {
            count = 10;
        } else if (count > 10000) {
            // limit
            count = 10000;

        }

        List<String> uidList = new ArrayList<>();

        try {
            for (int i = 0; i < count; i++) {
                uidList.add(UIDUtils.createUID(orgRootPrefix));
            }
        } catch (Exception err) {
            log.error(err.getMessage(), err);
            return Response.status(500).build();
        }

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uidList", uidList);
            jsonObj.put("count", count);
        } catch (JSONException err) {
            log.error(err.getMessage(), err);
            return Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(jsonObj.toString()).build();

    }
}
