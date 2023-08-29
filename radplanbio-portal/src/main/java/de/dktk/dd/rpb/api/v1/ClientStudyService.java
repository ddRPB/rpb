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
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
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
 * Service handling Study by OC identifier as aggregate root resources
 */
@Component
@Path("v1/getStudyByOcIdentifier")
public class ClientStudyService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(ClientStudyService.class);

    //endregion

    //region Members

    private IStudyRepository studyRepository;

    //endregion

    //region Constructor

    @Inject
    public ClientStudyService(IStudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    //endregion

    //region GET

    @GET
    @Path("/{ocStudyIdentifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudyByOcIdentifier(@Context HttpHeaders headers,
                                           @PathParam("ocStudyIdentifier") String ocStudyIdentifier) {

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

        //Create JSON ClientStudy
        JSONObject clientStudy = new JSONObject();

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

            //Provide example study
            Study example = new Study();
            example.setOcStudyIdentifier(ocStudyIdentifier);

            Study study = this.studyRepository.findUniqueOrNone(example);

            if (study != null) {

                if (study.getPartnerSite() != null) {

                    //Put elements to JSON Partnersite
                    partnersite.put("identifier", study.getPartnerSite().getIdentifier());
                    partnersite.put("sitename", study.getPartnerSite().getName());
                    partnersite.put("siteid", study.getPartnerSite().getId());

                    //Put elements to JSON EDC
                    if (study.getPartnerSite().hasEnabledEdc()) {
                        edc.put("isenabled", study.getPartnerSite().getEdc().getIsEnabled());
                        edc.put("edcid", study.getPartnerSite().getEdc().getId());
                        edc.put("soappublicurl", study.getPartnerSite().getEdc().getEdcPublicUrl());
                        edc.put("version", study.getPartnerSite().getEdc().getVersion());
                        edc.put("edcpublicurl", study.getPartnerSite().getEdc().getSoapBaseUrl());
                        edc.put("soapbaseurl", study.getPartnerSite().getEdc().getSoapBaseUrl());
                        edc.put("edcbaseurl", study.getPartnerSite().getEdc().getEdcBaseUrl());
                        edc.put("datapath", study.getPartnerSite().getEdc().getDataPath());
                        edc.put("isvalidated", study.getPartnerSite().getEdc().getIsValidated());
                    }

                    //Put elements to JSON PACS
                    if (study.getPartnerSite().hasEnabledPacs()) {
                        pacs.put("pacsid", study.getPartnerSite().getPacs().getId());
                        pacs.put("isenabled", study.getPartnerSite().getPacs().getIsEnabled());
                        pacs.put("pacsbaseurl", study.getPartnerSite().getPacs().getPacsBaseUrl());
                        pacs.put("version", study.getPartnerSite().getPacs().getVersion());
                    }

                    //Put elements to JSON Portal
                    if (study.getPartnerSite().getPortal() != null) {
                        portal.put("datapath", study.getPartnerSite().getPortal().getDataPath());
                        portal.put("portalid", study.getPartnerSite().getPortal().getId());
                        portal.put("isenabled", study.getPartnerSite().getPortal().getIsEnabled());
                        portal.put("portalbaseurl", study.getPartnerSite().getPortal().getPortalBaseUrl());
                        portal.put("publicurl", study.getPartnerSite().getPortal().getPublicUrl());
                        portal.put("software", study.getPartnerSite().getPortal().getSoftware());
                        portal.put("version", study.getPartnerSite().getPortal().getVersion());
                    }

                    //Put elements to JSON Server
                    if (study.getPartnerSite().getServer() != null) {
                        serverie.put("serverid", study.getPartnerSite().getServer().getId());
                        serverie.put("ipaddress", study.getPartnerSite().getServer().getIpAddress());
                        serverie.put("isenabled", study.getPartnerSite().getServer().getIsEnabled());
                        serverie.put("port", study.getPartnerSite().getServer().getPort());
                        serverie.put("publicurl", study.getPartnerSite().getServer().getPublicUrl());
                        serverie.put("version", study.getPartnerSite().getServer().getVersion());
                    }

                    //Put elements to JSON Partnersite
                    partnersite.put("edc", edc);
                    partnersite.put("pacs", pacs);
                    partnersite.put("portal", portal);
                    partnersite.put("serverie", serverie);

                    //Put elements to JSON ClientStudy
                    clientStudy.put("partnersite", partnersite); //put JSON object
                }

                clientStudy.put("id", study.getId());
                clientStudy.put("ocstudyidentifier", study.getOcStudyIdentifier());

            }

        } catch (Exception err) {

            log.error(err.getMessage(),err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(clientStudy.toString()).build();

    }

    //endregion
}
