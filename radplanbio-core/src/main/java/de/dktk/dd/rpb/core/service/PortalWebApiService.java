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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.domain.pacs.EnumConquestMode;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.inject.Named;
import java.io.InputStream;

/**
 * RPB Web-API service implementation (new working with portal API)
 *
 * @author tomas@skripcak.net
 * @since 19 Nov 2015
 */
@Named
public class PortalWebApiService implements IPortalWebApiService {

    //region Finals

    private static final Logger log = Logger.getLogger(PortalWebApiService.class);

    //endregion

    //region Members

    private String url;

    //TODO: it would make sense to setup apiKey as member and initialise...

    //endregion

    //region Init

    public void setupConnection(String url) {
        this.url = url;

        if (!this.url.endsWith("/")) {
            this.url += "/";
        }
    }

    //endregion

    //region DefaultAccount

    public DefaultAccount loadDefaultAccount(String username, String apiKey) {
        DefaultAccount result = null;

        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "api/v1/defaultaccounts/" + username);

            ClientResponse response = webResource
                    .accept("application/vnd.defaultaccount.v1+json")
                    .header("X-Api-Key", apiKey)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                // This will not deserialise the activeStudy (examine why and if it is possible to change this behaviour)
                //result = response.getEntity(DefaultAccount.class);

                // Parse JSON manually and create domain objects
                String tempResult = response.getEntity(String.class);

                JSONObject returnObject = new JSONObject(tempResult);

                result = new DefaultAccount();
                result.setId(returnObject.getInt("id"));
                result.setUsername(returnObject.getString("username"));
                result.setIsEnabled(returnObject.optBoolean("isEnabled"));
                result.setPassword(returnObject.getString("password"));
                result.setEmail(returnObject.optString("email"));
                result.setApiKey(returnObject.optString("apiKey"));
                result.setIsEnabled(returnObject.optBoolean("apiKeyEnabled"));
                result.setOcUsername(returnObject.optString("ocUsername"));
                result.setOcPasswordHash(returnObject.optString("ocPasswordHash"));

                JSONObject returnStudy = returnObject.optJSONObject("activeStudy");
                if (returnStudy != null) {
                    Study edcActiveStudy = new Study();
                    edcActiveStudy.setId(returnStudy.getInt("id"));
                    edcActiveStudy.setUniqueIdentifier(returnStudy.getString("uniqueIdentifier"));
                    edcActiveStudy.setName(returnStudy.getString("name"));
                    edcActiveStudy.setOcoid(returnStudy.getString("ocoid"));

                    // Parent study
                    JSONObject returnParentStudy = returnStudy.optJSONObject("parentStudy");
                    if (returnParentStudy != null) {
                        Study parentStudy = new Study();
                        parentStudy.setId(returnParentStudy.getInt("id"));
                        parentStudy.setUniqueIdentifier(returnParentStudy.getString("uniqueIdentifier"));
                        parentStudy.setName(returnParentStudy.getString("name"));
                        parentStudy.setOcoid(returnParentStudy.getString("ocoid"));

                        edcActiveStudy.setParentStudy(parentStudy);
                    }

                    result.setActiveStudy(edcActiveStudy);
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }

        return result;
    }

    //endregion

    //region Study

    public Study loadEdcStudy(String apiKey, String edcStudyIdentifier) {
        Study study = null;

        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "api/v1/edcstudies/" + edcStudyIdentifier);

            ClientResponse response = webResource
                    .accept("application/vnd.edcstudy.v1+json")
                    .header("X-Api-Key", apiKey)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                String output = response.getEntity(String.class);

                JSONObject finalResult = new JSONObject(output);
                study = new Study();

                study.setId(finalResult.getInt("id"));
                study.setUniqueIdentifier(finalResult.getString("uniqueIdentifier"));
                study.setSecondaryIdentifier(finalResult.getString("secondaryIdentifier"));
                study.setName(finalResult.getString("name"));
                study.setOcoid(finalResult.optString("ocoid"));

                if (finalResult.has("parentStudy")) {
                    Study parentStudy = new Study();

                    JSONObject parentResult = finalResult.getJSONObject("parentStudy");

                    parentStudy.setId(parentResult.getInt("id"));
                    parentStudy.setUniqueIdentifier(parentResult.getString("uniqueIdentifier"));
                    parentStudy.setSecondaryIdentifier(parentResult.getString("secondaryIdentifier"));
                    parentStudy.setName(parentResult.getString("name"));
                    parentStudy.setOcoid(parentResult.optString("ocoid"));

                    study.setParentStudy(parentStudy);
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }

        return study;
    }

    public void changeActiveStudy(String username, String apiKey, Study newActiveStudy) {
        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "api/v1/defaultaccounts/" + username + "/activestudy/" + newActiveStudy.getUniqueIdentifier());

            ClientResponse response = webResource
                    .header("X-Api-Key", apiKey)
                    .put(ClientResponse.class);

            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    //endregion

    //region Pacs

    public InputStream pacsCreateStudyArchive(String patientId, String dicomUid, Integer verifyCount, String apiKey) {
        InputStream result = null;

        try {
            String pacsObjectIdentifier = patientId + ":" + dicomUid;

            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "api/v1/pacs/archive/" + pacsObjectIdentifier)
                    .queryParam("mode", EnumConquestMode.ZIP_STUDY.toString())
                    .queryParam("verifyCount", verifyCount.toString());

            ClientResponse response = webResource
                    .header("X-Api-Key", apiKey)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                result = response.getEntityInputStream();
            }

        } catch (Exception err) {
            log.error(err);
        }

        return result;
    }

    public InputStream pacsCreateSeriesArchive(String patientId, String dicomStudyUid, String dicomSeriesUid, String apiKey) {
        InputStream result = null;

        try {
            String pacsObjectIdentifier = patientId + ":" + dicomStudyUid + ":" + dicomSeriesUid;

            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "api/v1/pacs/archive/" + pacsObjectIdentifier)
                    .queryParam("mode", EnumConquestMode.ZIP_SERIES.toString())
                    .queryParam("verifyCount", "-1"); // -1 -> the PacsArchiveService will determine the number of files

            ClientResponse response = webResource
                    .header("X-Api-Key", apiKey)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                result = response.getEntityInputStream();
            }

        } catch (Exception err) {
            log.error(err);
        }

        return result;
    }

    //endregion

}
