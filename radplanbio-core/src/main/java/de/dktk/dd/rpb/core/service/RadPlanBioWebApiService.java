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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.json.JSONException;
import org.json.JSONObject;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.Study;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;

/**
 * RPB Web-API service implementation (old working with python server)
 *
 * @author tomas@skripcak.net
 * @since 21 September 2015
 */
@Named
public class RadPlanBioWebApiService implements IRadPlanBioWebApiService {

    //region Members

    private String url;

    //endregion

    //region Init

    public void setupConnection(String url) {
        this.url = url;
    }

    //endregion

    //region Methods

    //region EDC UserAccount

    /**
     * Get OC user password hash for communication via SOAP when login via portal
     * @param ocUsername OpenClinica username
     * @return OC user password hash
     */
    public String loadAccountPasswordHash(String ocUsername) {
        String result = "";

        String method = "api/v1/getOCAccoutPasswordHash/";
        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + ocUsername
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", UserContext.getUsername())
                .header("Password", UserContext.getPassword())
                .header("Clearpass", UserContext.getClearPassword())
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);

        try {
            JSONObject finalResult = new JSONObject(output);
             result = finalResult.getString("ocPasswordHash");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get OC user password hash for communication via SOAP when using RPB REST services
     * @param account DefaultAccount
     * @return OC user password hash
     */
    public String loadAccountPasswordHash(DefaultAccount account) {
        String result = "";

        String method = "api/v1/getOCAccoutPasswordHash/";
        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + account.getOcUsername()
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", account.getUsername())
                .header("Password", account.getPassword())
                .header("Clearpass", "") // This parameter is not necessary
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);

        try {
            JSONObject finalResult = new JSONObject(output);
            result = finalResult.getString("ocPasswordHash");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    //endregion

    //region EDC Data

    public String loadCrfItemValue(String studySiteOid, String subjectPid, String studyEventOid, Integer studyEventRepeatKey, String formOid, String itemOid) {
        String result = "";

        String method = "api/v2/getCrfItemValue/";
        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + studySiteOid + "/" + subjectPid + "/" + studyEventOid + "/" + studyEventRepeatKey.toString() + "/" + formOid + "/" + itemOid
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", UserContext.getUsername())
                .header("Password", UserContext.getPassword())
                .header("Clearpass", UserContext.getClearPassword())
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        try {
            JSONObject finalResult = new JSONObject(output);
            result = finalResult.getString("itemValue");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    //endregion

    //region EDC Study

    public Study loadOCStudyByIdentifier(String ocStudyIdentifier) {

        Study study = null;

        String method = "api/v1/getOCStudyByIdentifier/";

        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + ocStudyIdentifier
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", UserContext.getUsername())
                .header("Password", UserContext.getPassword())
                .header("Clearpass", UserContext.getClearPassword())
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        try {
            JSONObject finalResult = new JSONObject(output);

            study = new Study();

            study.setId(finalResult.getInt("id"));
            study.setUniqueIdentifier(finalResult.getString("uniqueIdentifier"));
            study.setSecondaryIdentifier(finalResult.getString("secondaryIdentifier"));
            study.setName(finalResult.getString("name"));
            study.setOcoid(finalResult.getString("ocoid"));

            if (finalResult.has("parentStudy")) {
                Study parentStudy = new Study();

                JSONObject parentResult = finalResult.getJSONObject("parentStudy");

                parentStudy.setId(parentResult.getInt("id"));
                parentStudy.setUniqueIdentifier(parentResult.getString("uniqueIdentifier"));
                parentStudy.setSecondaryIdentifier(parentResult.getString("secondaryIdentifier"));
                parentStudy.setName(parentResult.getString("name"));
                parentStudy.setOcoid(parentResult.getString("ocoid"));

                study.setParentStudy(parentStudy);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return study;
    }

    public Study loadUserActiveStudy(String ocUsername) {

        Study study = null;

        String method = "api/v1/getUserActiveStudy/";
        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + ocUsername
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", UserContext.getUsername())
                .header("Password", UserContext.getPassword())
                .header("Clearpass", UserContext.getClearPassword())
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        try {
            JSONObject finalResult = new JSONObject(output);

            study = new Study();

            study.setId(finalResult.getInt("id"));
            study.setUniqueIdentifier(finalResult.getString("uniqueIdentifier"));
            study.setSecondaryIdentifier(finalResult.getString("secondaryIdentifier"));
            study.setName(finalResult.getString("name"));
            study.setOcoid(finalResult.getString("ocoid"));

            if (finalResult.has("parentStudy")) {
                Study parentStudy = new Study();

                JSONObject parentResult = finalResult.getJSONObject("parentStudy");

                parentStudy.setId(parentResult.getInt("id"));
                parentStudy.setUniqueIdentifier(parentResult.getString("uniqueIdentifier"));
                parentStudy.setSecondaryIdentifier(parentResult.getString("secondaryIdentifier"));
                parentStudy.setName(parentResult.getString("name"));
                parentStudy.setOcoid(parentResult.getString("ocoid"));

                study.setParentStudy(parentStudy);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return study;
    }

    public void changeUserActiveStudy(String ocUsername, Integer ocStudyId) {

       String method = "api/v1/changeUserActiveStudy/";

        Client client = Client.create();
        WebResource webResource = client.resource(
                this.url + method + ocUsername + "/" + ocStudyId.toString()
        );

        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("Username", UserContext.getUsername())
                .header("Password", UserContext.getPassword())
                .header("Clearpass", UserContext.getClearPassword())
                .get(ClientResponse.class);

        // Success
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        try {
            JSONObject finalResult = new JSONObject(output);
            finalResult.getString("result");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //endregion

}
