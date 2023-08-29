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

package de.dktk.dd.rpb.api.v2;

import de.dktk.dd.rpb.api.support.BaseService;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service handling Study ItemData
 */
@Component
@Path("/v2/getCrfItemValue")
public class ItemDataService extends BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(ItemDataService.class);

    //endregion

    //region GET

    @GET
    @Path("/{studyOid}/{subjectPid}/{studyEventOid}/{studyEventRepeatKey}/{formOid}/{itemOid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCrfFieldsAnnotationForStudy(@Context HttpHeaders headers,
                                                   @PathParam("studyOid") String studyOid,
                                                   @PathParam("subjectPid") String subjectPid,
                                                   @PathParam("studyEventOid") String studyEventOid,
                                                   @PathParam("studyEventRepeatKey") String studyEventRepeatKey,
                                                   @PathParam("formOid") String formOid,
                                                   @PathParam("itemOid") String itemOid) {

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

        //Create JSON ClientDefaultAccount
        JSONObject itemDataObject = new JSONObject();

        try {
            ItemData itemData = this.openClinicaDataRepository.findItemData(studyOid, subjectPid, studyEventOid, studyEventRepeatKey, formOid, itemOid);

            if (itemData != null) {
                itemDataObject.put("itemOid", itemData.getItemOid());
                itemDataObject.put("itemValue", itemData.getValue());
            }
        }
        catch (Exception err) {
            log.error(err.getMessage(), err);
            return javax.ws.rs.core.Response.status(500).build();
        }

        return javax.ws.rs.core.Response.status(200).entity(itemDataObject.toString()).build();
    }

    //endregion
}
