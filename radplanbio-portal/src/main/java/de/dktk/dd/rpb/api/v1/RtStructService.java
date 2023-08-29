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
import de.dktk.dd.rpb.core.domain.admin.RtStruct;
import de.dktk.dd.rpb.core.repository.admin.RtStructRepository;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service handling RtStructs as aggregate root resources
 */
@Component
@Path("/v1/getAllRTStructs")
public class RtStructService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(RtStructService.class);

    //endregion

    //region Members

    private RtStructRepository rtStructRepository;

    //endregion

    //region Constructor

    @Inject
    public RtStructService(RtStructRepository rtStructRepository) {
        this.rtStructRepository = rtStructRepository;
    }

    //endregion

    //region GET

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRtStructs(@Context HttpHeaders headers) {

        JSONArray results = new JSONArray();

        // Extract header parameters that client is sending with the request
        String username = headers.getRequestHeader("Username").get(0);
        String passwordHash = headers.getRequestHeader("Password").get(0);
        String clearpass = headers.getRequestHeader("Clearpass").get(0);

        // Check if all mandatory parameters are set
        if (username == null || username.isEmpty()) {
            log.info("Missing Username, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        if (passwordHash == null || passwordHash.isEmpty()) {
            log.info("Missing Password Hash, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        if (clearpass == null || clearpass.isEmpty()) {
            log.info("Missing Password, unauthorised");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Get the RPB user account to instantiate appropriate connection to EDC system
        DefaultAccount defaultAccount = this.userRepository.getByUsername(username);
        if (defaultAccount == null) {
            log.info("This account does not exist");
            return javax.ws.rs.core.Response.status(401).build();
        }

        IOpenClinicaService svcEdc;
        if (defaultAccount.hasOpenClinicaAccount() && defaultAccount.getPartnerSite().getEdc().getIsEnabled()) {
            svcEdc = this.createEdcConnection(defaultAccount);
        }
        else {
            log.info("This account does not have EDC module activated");
            return javax.ws.rs.core.Response.status(401).build();
        }

        // Use the EDC service to login the user (covers both local DB as well as LDAP if setup in EDC)
        boolean loginSuccess = false;
        if (svcEdc != null) {
            loginSuccess = svcEdc.loginUserAccount(username, clearpass) != null;
        }

        if (!loginSuccess) {
            log.info("Login Failed");
            return javax.ws.rs.core.Response.status(401).build();
        }

        try {
            // Load all RtStruct objects from database
            List<RtStruct> rtStructList = this.rtStructRepository.find();

            if (rtStructList != null) {

                for (RtStruct rtStruct : rtStructList) {

                    // Create JSON RtStructType
                    JSONObject rtStructTypeObject = new JSONObject();
                    if (rtStruct.getType() != null) {
                        rtStructTypeObject.put("description", rtStruct.getType().getDescription());
                        rtStructTypeObject.put("name", rtStruct.getType().getName());
                        rtStructTypeObject.put("id", rtStruct.getType().getId());
                    }

                    // Create JSON RtStruct
                    JSONObject rtStructObject = new JSONObject();
                    rtStructObject.put("structType", rtStructTypeObject);
                    rtStructObject.put("id", rtStruct.getId());
                    rtStructObject.put("identifier", rtStruct.getIdentifier());
                    rtStructObject.put("name", rtStruct.getName());
                    rtStructObject.put("description", rtStruct.getDescription());

                    // Add to resulting JSON array
                    results.put(rtStructObject);
                }
            }
        } catch (Exception err) {

            log.error(err.getMessage(), err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(results.toString()).build();
    }

    //endregion

}
