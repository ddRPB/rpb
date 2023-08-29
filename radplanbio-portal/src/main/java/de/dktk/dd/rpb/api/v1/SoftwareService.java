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
import de.dktk.dd.rpb.core.domain.rpb.Software;
import de.dktk.dd.rpb.core.repository.rpb.ISoftwareRepository;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service handling Software as aggregate root resources
 */
@Component
@Path("/v1/getLatestSoftware")
public class SoftwareService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(SoftwareService.class);

    //endregion

    //region Members

    private ISoftwareRepository softwareRepository;

    //endregion

    //region Constructor

    @Inject
    public SoftwareService(ISoftwareRepository softwareRepository) {
        this.softwareRepository = softwareRepository;
    }

    //endregion

    //region GET

    @GET
    @Path("/{softwareName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestSoftware(@Context HttpHeaders headers,
                                      @PathParam("softwareName") String softwareName) {

        //Create JSON LatestSoftware
        JSONObject latestSoftwareObject = new JSONObject();

        //Create JSON Portal
        JSONObject portalObject = new JSONObject();

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

            //Provide example software
            Software example = new Software();
            example.setName(softwareName);
            example.setLatest(true);

            Software software = this.softwareRepository.findUniqueOrNone(example);

            if (software != null) {

                if (software.getPortal() != null) {
                    //Named to allow backward compatible to currently existing client
                    portalObject.put("portalbaseurl", software.getPortal().getPortalBaseUrl());
                    portalObject.put("portalid", software.getPortal().getId());
                    portalObject.put("publicurl", software.getPortal().getPublicUrl());

                    //Named following the property of the Portal class
                    portalObject.put("isEnabled", software.getPortal().getIsEnabled());
                    portalObject.put("dataPath", software.getPortal().getDataPath());
                    portalObject.put("version", software.getPortal().getVersion());
                }

                latestSoftwareObject.put("portal", portalObject);
                latestSoftwareObject.put("id", software.getId());
                latestSoftwareObject.put("description", software.getDescription());
                latestSoftwareObject.put("version", software.getVersion());
                latestSoftwareObject.put("latest", software.getLatest());
                latestSoftwareObject.put("name", software.getName());
                latestSoftwareObject.put("filename", software.getFilename());
            }

        } catch (Exception err) {

            log.error(err.getMessage(), err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(latestSoftwareObject.toString()).build();
    }

    //endregion

}
