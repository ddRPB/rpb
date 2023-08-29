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
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service handling Client Default Account as aggregate root resources
 */
@Component
@Path("/v1/getMyDefaultAccount")
public class ClientDefaultAccountService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(ClientDefaultAccountService.class);

    //endregion

    //region GET

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyDefaultAccount(@Context HttpHeaders headers) {

        //region Authentication

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

        //endregion

        //Create JSON ClientDefaultAccount
        JSONObject clientDefaultAccount = new JSONObject();

        //Create JSON Partnersite
        JSONObject partnersite = new JSONObject();

        //Create JSON EDC
        JSONObject edc = new JSONObject();

        //Create JSON PACS
        JSONObject pacs = new JSONObject();

        //Create JSON Portal
        JSONObject portal = new JSONObject();

        //Create JSON Server
        JSONObject serverie = new JSONObject();

        try {

            if (defaultAccount.getPartnerSite() != null) {

                //Put elements to JSON Partnersite
                partnersite.put("siteid", defaultAccount.getPartnerSite().getId());
                partnersite.put("sitename", defaultAccount.getPartnerSite().getName());
                partnersite.put("identifier", defaultAccount.getPartnerSite().getIdentifier());
                partnersite.put("description", defaultAccount.getPartnerSite().getDescription());
                partnersite.put("iprange", defaultAccount.getPartnerSite().getIpRange());
                partnersite.put("isenabled", defaultAccount.getPartnerSite().getIsEnabled());
                partnersite.put("latitude", defaultAccount.getPartnerSite().getLatitude());
                partnersite.put("longitude", defaultAccount.getPartnerSite().getLongitude());

                //Put elements to JSON EDC
                if (defaultAccount.getPartnerSite().hasEnabledEdc()) {
                    edc.put("isenabled", defaultAccount.getPartnerSite().getEdc().getIsEnabled());
                    edc.put("edcid", defaultAccount.getPartnerSite().getEdc().getId());
                    edc.put("soappublicurl", defaultAccount.getPartnerSite().getEdc().getSoapPublicUrl());
                    edc.put("version", defaultAccount.getPartnerSite().getEdc().getVersion());
                    edc.put("edcpublicurl", defaultAccount.getPartnerSite().getEdc().getEdcPublicUrl());
                    edc.put("soapbaseurl", defaultAccount.getPartnerSite().getEdc().getSoapBaseUrl());
                    edc.put("edcbaseurl", defaultAccount.getPartnerSite().getEdc().getEdcBaseUrl());
                    edc.put("datapath", defaultAccount.getPartnerSite().getEdc().getDataPath());
                    edc.put("isvalidated", defaultAccount.getPartnerSite().getEdc().getIsValidated());
                }
                //Put elements to JSON PACS
                if (defaultAccount.getPartnerSite().hasEnabledPacs()) {
                    pacs.put("pacsid", defaultAccount.getPartnerSite().getPacs().getId());
                    pacs.put("isenabled", defaultAccount.getPartnerSite().getPacs().getIsEnabled());
                    pacs.put("pacsbaseurl", defaultAccount.getPartnerSite().getPacs().getPacsBaseUrl());
                    pacs.put("version", defaultAccount.getPartnerSite().getPacs().getVersion());
                }

                //Put elements to JSON Portal
                if (defaultAccount.getPartnerSite().getPortal() != null) {
                    portal.put("portalbaseurl", defaultAccount.getPartnerSite().getPortal().getPortalBaseUrl());
                    portal.put("portalid", defaultAccount.getPartnerSite().getPortal().getId());
                    portal.put("publicurl", defaultAccount.getPartnerSite().getPortal().getPublicUrl());
                    portal.put("datapath", defaultAccount.getPartnerSite().getPortal().getDataPath());
                    portal.put("isenabled", defaultAccount.getPartnerSite().getPortal().getIsEnabled());
                    portal.put("version", defaultAccount.getPartnerSite().getPortal().getVersion());
                }

                //Put elements to JSON Server
                if (defaultAccount.getPartnerSite().getServer() != null) {
                    serverie.put("serverid", defaultAccount.getPartnerSite().getServer().getId());
                    serverie.put("ipaddress", defaultAccount.getPartnerSite().getServer().getIpAddress());
                    serverie.put("port", defaultAccount.getPartnerSite().getServer().getPort());
                    serverie.put("publicurl", defaultAccount.getPartnerSite().getServer().getPublicUrl());
                    serverie.put("isenabled", defaultAccount.getPartnerSite().getServer().getIsEnabled());
                    serverie.put("version", defaultAccount.getPartnerSite().getServer().getVersion());
                }

                //Put elements to JSON Partnersite
                partnersite.put("edc", edc); //put JSON Object
                partnersite.put("pacs", pacs); //put JSON Object
                partnersite.put("portal", portal); //put JSON Object
                partnersite.put("serverie", serverie); //put JSON Object

                //Put elements to JSON ClientDefaultAccount
                clientDefaultAccount.put("partnersite", partnersite); //put JSON Object
            }

            clientDefaultAccount.put("username", defaultAccount.getUsername());
            clientDefaultAccount.put("ocusername", defaultAccount.getOcUsername());
            clientDefaultAccount.put("isenabled", defaultAccount.getIsEnabled());
            clientDefaultAccount.put("id", defaultAccount.getId());
            clientDefaultAccount.put("apikey", defaultAccount.getApiKey());
            clientDefaultAccount.put("isapikeyenabled", defaultAccount.getApiKeyEnabled());
            clientDefaultAccount.put("email", defaultAccount.getEmail());
            clientDefaultAccount.put("lastvisit", defaultAccount.getLastVisit());
            clientDefaultAccount.put("nonlocked", defaultAccount.getNonLocked());
            clientDefaultAccount.put("ocpasswordhash", this.getOcHash(defaultAccount.getOcUsername()));
            clientDefaultAccount.put("password", defaultAccount.getPassword());

        } catch (Exception err) {

            log.error(err.getMessage(),err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(clientDefaultAccount.toString()).build();

    }

    //endregion
}
