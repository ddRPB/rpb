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

package de.dktk.dd.rpb.core.service;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONObject;
import org.json.JSONArray;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.dktk.dd.rpb.core.domain.ctms.Person;

import java.util.*;

/**
 * Mainzelliste Pseudonym Generation Service Interface Implementation
 *
 * @author tomas@skripcak.net
 * @since 21 August 2013
 */
@Transactional(readOnly = true)
@Named("pidService")
public class MainzellisteService implements IMainzellisteService {

    //region Finals

    private static final Logger log = Logger.getLogger(MainzellisteService.class);

    //endregion

    //region Members

    private String baseUrl;
    private String adminUsername;
    private String adminPassword;
    private String apiKey;
    private String apiVersion;

    private String callback;

    //endregion

    //region Constructors

    /**
     * Default constructor
     */
    public MainzellisteService() {
        // NOOP
    }

    //endregion

    //region Properties

    /**
     * Check if all data members for standard user communication are setup
     * @return true if the service is ready for standard user communication
     */
    public boolean getHasConnectionInfo() {
        return (this.baseUrl != null && !this.baseUrl.isEmpty() &&
                this.apiKey != null && !this.apiKey.isEmpty() &&
                this.callback != null && !this.callback.isEmpty());
    }

    /**
     * Check if all data members for admin user communication are setup
     * @return true if the service is ready for admin user communication
     */
    public boolean getHasAdminConnectionInfo() {
        return (this.getHasConnectionInfo() &&
                this.adminUsername != null && !this.adminUsername.isEmpty() &&
                this.adminPassword != null && !this.adminPassword.isEmpty());
    }

    /**
     * Check whether API version will be used in http request header while communication with mainzelliste
     * @return true if API version is used
     */
    public boolean getUseApiVersion() {
        return !"".equals(this.apiVersion);
    }

    //endregion

    //region Methods

    //region Setup

    /**
     * {@inheritDoc}
     */
    public void setupConnectionInfo(String baseUrl, String apiKey, String callback) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.callback = callback;
    }

    /**
     * {@inheritDoc}
     */
    public void setupConnectionInfo(String baseUrl, String apiKey, String apiVersion, String callback) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.apiVersion = apiVersion;
        this.callback = callback;
    }

    /**
     * {@inheritDoc}
     */
    public void setupAdminConnectionInfo(String adminUsername, String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    //endregion

    //region Session

    /**
     * {@inheritDoc}
     */
    public JSONObject newSession() throws Exception {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions";

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint);

            ClientResponse response;
            // Determine whether to use API version in http request header
            if (this.getUseApiVersion()) {
                response = webResource
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .header("mainzellisteApiVersion", this.apiVersion)
                        .post(ClientResponse.class);
            }
            else {
                response = webResource
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .post(ClientResponse.class);
            }

            // Created
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String output = response.getEntity(String.class);
            finalResult = new JSONObject(output);
        }

        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public int deleteSession(String sessionId) throws Exception {
        int resultStatusCode = -1;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions/";

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint + sessionId);

            ClientResponse response;
            if (this.getUseApiVersion()) {
                response = webResource
                        .header("mainzellisteApiKey", this.apiKey)
                        .header("mainzellisteApiVersion", this.apiVersion)
                        .delete(ClientResponse.class);
            }
            else {
                response = webResource
                        .header("mainzellisteApiKey", this.apiKey)
                        .delete(ClientResponse.class);
            }

            resultStatusCode = response.getStatus();
        }

        return resultStatusCode;
    }

    //endregion

    //region Patient Tokens

    /**
     * {@inheritDoc}
     */
    public JSONObject newPatientToken(String sessionId) {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions/";
            String method = "addPatient";

            try {
            String data = "{ \"type\": \"" + method + "\", \"data\": { \"callback\": \"" + this.callback + "\" } }";

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint + sessionId + "/tokens");

            ClientResponse response = webResource
                    .type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("mainzellisteApiKey", this.apiKey)
                    .post(ClientResponse.class, data);

                // Created
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }

                String output = response.getEntity(String.class);
                finalResult = new JSONObject(output);
            }
            catch (Exception err){
                err.printStackTrace();
            }
        }

        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject readPatientToken(String sessionId, String pid) throws Exception {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions/";
            String method = "readPatients";

            JSONObject id = new JSONObject();
            id.put("idType", "pid");
            id.put("idString", pid);

            JSONArray ids = new JSONArray();
            ids.put(id);

            JSONArray resultFields = new JSONArray();
            resultFields.put("vorname");
            resultFields.put("nachname");
            resultFields.put("geburtsname");
            resultFields.put("geburtstag");
            resultFields.put("geburtsmonat");
            resultFields.put("geburtsjahr");
            resultFields.put("plz");
            resultFields.put("ort");

            JSONArray resultIds = new JSONArray();

            JSONObject data = new JSONObject();
            data.put("searchIds", ids);
            data.put("resultFields", resultFields);
            data.put("resultIds", resultIds);

            JSONObject mainObj = new JSONObject();
            mainObj.put("type", method);
            mainObj.put("data", data);

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint + sessionId + "/tokens");

            // Determine whether to use API version in http request header
            ClientResponse response;
            if (this.getUseApiVersion()) {
                response = webResource
                        .type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .header("mainzellisteApiVersion", this.apiVersion)
                        .post(ClientResponse.class, mainObj.toString());
            }
            else {
                response = webResource
                        .type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .post(ClientResponse.class, mainObj.toString());
            }

            finalResult = this.getMainzellisteCreatedResponse(response);
        }

        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject readPatientsToken(String sessionId, List<String> pids) throws Exception {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions/";
            String method = "readPatients";

            JSONArray ids = new JSONArray();

            for (String pid : pids) {
                JSONObject id = new JSONObject();
                id.put("idType", "pid");
                id.put("idString", pid);
                ids.put(id);
            }

            JSONArray resultFields = new JSONArray();
            resultFields.put("vorname");
            resultFields.put("nachname");
            resultFields.put("geburtsname");
            resultFields.put("geburtstag");
            resultFields.put("geburtsmonat");
            resultFields.put("geburtsjahr");
            resultFields.put("plz");
            resultFields.put("ort");

            JSONArray resultIds = new JSONArray();
            resultIds.put("pid");

            JSONObject data = new JSONObject();
            data.put("searchIds", ids);
            data.put("resultFields", resultFields);
            data.put("resultIds", resultIds);

            JSONObject mainObj = new JSONObject();
            mainObj.put("type", method);
            mainObj.put("data", data);

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint + sessionId + "/tokens");

            // Determine whether to use API version in http request header
            ClientResponse response;
            if (this.getUseApiVersion()) {
                response = webResource
                        .type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .header("mainzellisteApiVersion", this.apiVersion)
                        .post(ClientResponse.class, mainObj.toString());
            }
            else {
                response = webResource
                        .type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .post(ClientResponse.class, mainObj.toString());
            }

            finalResult = this.getMainzellisteCreatedResponse(response);
        }

        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject editPatientToken(String sessionId, String pid) throws Exception {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "sessions/";
            String method = "editPatient";

            JSONObject id = new JSONObject();
            id.put("idType", "pid"); // pseudonym ID type
            id.put("idString", pid);

            JSONArray fields = new JSONArray();
            fields.put("vorname");
            fields.put("nachname");
            fields.put("geburtsname");
            fields.put("geburtstag");
            fields.put("geburtsmonat");
            fields.put("geburtsjahr");
            fields.put("plz");
            fields.put("ort");

            JSONObject data = new JSONObject();
            data.put("patientId", id);
            data.put("fields", fields);
            //data.put("redirect", url)

            //TODO
            //JSONArray ids = new JSONArray();
            //ids.put("hisid");

            JSONObject mainObj = new JSONObject();
            mainObj.put("type", method);
            mainObj.put("data", data);
            //TODO
            //mainObj.put("ids", ids);

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint + sessionId + "/tokens");

            // Determine whether to use API version in http request header
            ClientResponse response;
            if (this.getUseApiVersion()) {
                response = webResource
                        .type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("mainzellisteApiKey", this.apiKey)
                        .header("mainzellisteApiVersion", this.apiVersion)
                        .post(ClientResponse.class, mainObj.toString());

                // Token Created
                if (response.getStatus() == 201) {
                    if (response.hasEntity()) {
                        String output = response.getEntity(String.class);
                        finalResult = new JSONObject(output);
                    }
                }
                // Wrong data request
                else if (response.getStatus() == 400) {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus() + "; wrong data in request.");
                }
                // Missing tt_EditPatient right
                else if (response.getStatus() == 401) {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus() + "; missing edit patient permision.");
                }
                else {
                    if (response.hasEntity()) {
                        String entity = response.getEntity(String.class);
                        throw new RuntimeException("Failed : HTTP error code: " +
                                response.getStatus() +
                                " message: " +
                                entity);
                    }
                    else {
                        throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
                    }
                }
            }
        }

        return finalResult;
    }

    //endregion

    // region Create Patient PID

    /**
     * {@inheritDoc}
     */
    public JSONObject createPatient(Person newPerson) throws Exception {
        JSONObject response = this.newSession();

        String sessionId = "";
        if (response != null) {
            sessionId = response.getString("sessionId");
        }

        response = this.newPatientToken(sessionId);

        String tokenId = "";
        if (response != null) {
            tokenId = response.getString("tokenId");
        }

        return this.getCreatePatientJsonResponse(tokenId, newPerson);
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject getCreatePatientJsonResponse(String tokenId, Person person) throws Exception {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "patients";

            Calendar cal = Calendar.getInstance();
            cal.setTime(person.getBirthdate());

            String year = String.format("%4d", cal.get(Calendar.YEAR));
            String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

            // Create a HTML form data
            // mandatory vorname, nachname, geburtstag, geburtsmonat, geburtsjahr,
            // optional geburtsname, plz, ort
            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            formData.add("vorname", person.getFirstname());
            formData.add("nachname", person.getSurname());
            formData.add("geburtstag", day);
            formData.add("geburtsmonat", month);
            formData.add("geburtsjahr", year);
            formData.add("geburtsname", person.getBirthname());
            formData.add("plz", person.getZipcode());
            formData.add("ort", person.getCity());

            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.baseUrl + endpoint)
                    .queryParam("tokenId", tokenId);

            ClientResponse response = webResource
                    .type(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, formData);

            // Not accepted nor created = no PID generated
            if (response.getStatus() != 202 && response.getStatus() != 201) {
                if (response.hasEntity()) {
                    String entity = response.getEntity(String.class);
                    throw new RuntimeException("Failed : HTTP error code: " +
                            response.getStatus() +
                            " message: " +
                            entity);
                }
                else {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
                }
            }

            String output = response.getEntity(String.class);
            finalResult = new JSONObject(output);
        }

        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject createSurePatientJson(String tokenId, Person person) {
        JSONObject finalResult = null;

        if (this.getHasConnectionInfo()) {
            try {
                String endpoint = "patients";

                Calendar cal = Calendar.getInstance();
                cal.setTime(person.getBirthdate());

                String year = String.format("%4d", cal.get(Calendar.YEAR));
                String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
                String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

                // Create a HTML form data
                // mandatory vorname, nachname, geburtstag, geburtsmonat, geburtsjahr,
                // optional geburtsname, plz, ort

                MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
                formData.add("vorname", person.getFirstname());
                formData.add("nachname", person.getSurname());
                formData.add("geburtstag", day);
                formData.add("geburtsmonat", month);
                formData.add("geburtsjahr", year);
                formData.add("geburtsname", person.getBirthname());
                formData.add("plz", person.getZipcode());
                formData.add("ort", person.getCity());
                formData.add("sureness", Boolean.toString(true));

                Client client = Client.create();
                WebResource webResource = client
                        .resource(this.baseUrl + endpoint)
                        .queryParam("tokenId", tokenId);

                ClientResponse response = webResource
                        .type(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, formData);

                // Accepted or created
                if (response.getStatus() != 202 && response.getStatus() != 201) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }

                String output = response.getEntity(String.class);
                finalResult = new JSONObject(output);

            }
            catch (Exception err){
                err.printStackTrace();
            }
        }

        return finalResult;
    }

    //endregion

    // region Edit Patient Data

    /**
     * {@inheritDoc}
     */
    public boolean editPatientJson(String tokenId, Person person) throws Exception {
        Boolean editResult = Boolean.FALSE;

        if (this.getHasConnectionInfo()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(person.getBirthdate());

            String year = String.format("%4d", cal.get(Calendar.YEAR));
            String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

            JSONObject data = new JSONObject();
            data.put("vorname", person.getFirstname());
            data.put("nachname", person.getSurname());
            data.put("geburtstag", day);
            data.put("geburtsmonat", month);
            data.put("geburtsjahr", year);
            data.put("geburtsname", person.getBirthname());
            data.put("plz", person.getZipcode());
            data.put("ort", person.getCity());

            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.baseUrl)
                    .path("patients")
                    .path("tokenId")
                    .path(tokenId);

            ClientResponse response = webResource
                    .type(MediaType.APPLICATION_JSON)
                    .put(ClientResponse.class, data.toString());

            // 204 No Content = changes accepted
            if (response.getStatus() != 204) {
                if (response.hasEntity()) {
                    String entity = response.getEntity(String.class);
                    throw new RuntimeException("Failed : HTTP error code: " +
                            response.getStatus() +
                            " message: " +
                            entity);
                }
                else {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
                }
            }
            else {
                editResult = Boolean.TRUE;
            }

        }

        return editResult;
    }

    /**
     * {@inheritDoc}
     */
    public boolean editPatient(Person person) {

        boolean result = Boolean.FALSE;

        try {
            if (person != null && person.getPid() != null) {
                JSONObject finalResult = this.newSession();

                String sessionId = "";
                if (finalResult != null) {
                    sessionId = finalResult.getString("sessionId");
                }

                finalResult = this.editPatientToken(
                        sessionId,
                        person.getPid()
                );

                String tokenId = "";
                if (finalResult != null) {
                    tokenId = finalResult.getString("id");
                }

                result = this.editPatientJson(tokenId, person);
            }
            else {
                log.info("Cannot edit patient: provided person or PID is null.");
            }
        }
        catch (Exception err) {
            log.error("Failed to edit person");
        }

        return result;
    }

    //endregion

    //region Get Patients

    public Person loadPatient(String pid) throws Exception {
        Person result;

        // Session orchestration for identity management
        JSONObject json = this.newSession();

        String sessionId = "";
        if (json != null) {
            sessionId = json.getString("sessionId");
        }

        json = this.readPatientToken(
                sessionId,
                pid
        );

        String tokenId = "";
        if (json != null) {
            tokenId = json.getString("id");
        }

        // Load IDAT
        result = this.getPatient(tokenId);

        // Preserve PID
        if (result != null) {
            result.setPid(pid);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Person getPatient(String tid) throws Exception {
        Person person = null;

        if (this.getHasConnectionInfo()) {
            String endpoint = "patients/tokenId/" + tid;

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint);
            ClientResponse response = webResource
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            // Accepted
            if (response.getStatus() == 200) {
                if (response.hasEntity()) {
                    String output = response.getEntity(String.class);
                    JSONArray returnArray = new JSONArray(output);
                    JSONObject returnObject = returnArray.getJSONObject(0).getJSONObject("fields");

                    person = new Person();

                    person.setFirstname(returnObject.getString("vorname"));
                    person.setSurname(returnObject.getString("nachname"));
                    person.setBirthname(returnObject.getString("geburtsname"));
                    person.setCity(returnObject.getString("ort"));
                    person.setZipcode(returnObject.getString("plz"));

                    int day = returnObject.getInt("geburtstag");
                    int month = returnObject.getInt("geburtsmonat");
                    int year = returnObject.getInt("geburtsjahr");

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month - 1);
                    cal.set(Calendar.DAY_OF_MONTH, day);

                    person.setBirthdate(cal.getTime());
                }
            }
            else {
                if (response.hasEntity()) {
                    String entity = response.getEntity(String.class);
                    throw new RuntimeException("Failed : HTTP error code: " +
                            response.getStatus() +
                            " message: " +
                            entity);
                }
                else {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
                }
            }
        }

        return person;
    }

    /**
     * {@inheritDoc}
     */
    public List<Person> getPatientListByPIDs(List<Person> personPIDList) throws Exception {
        List<Person> result;

        // Extract PIDs
        List<String> pids = new ArrayList<>();
        for (Person p : personPIDList) {
            if (p.getPid() != null) {
                pids.add(p.getPid());
            }
        }

        JSONObject finalResult = this.newSession();
        String sessionId = "";
        if (finalResult != null) {
            sessionId = finalResult.getString("sessionId");
        }
        finalResult = this.readPatientsToken(sessionId, pids);
        String tokenId = "";
        if (finalResult != null) {
            tokenId = finalResult.getString("id");
        }
        result = this.getPatientList(tokenId);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Person> getPatientList(String tid) throws Exception {
        List<Person> personList = new ArrayList<>();

        if (this.getHasConnectionInfo()) {
            String endpoint = "patients/tokenId/" + tid;

            Client client = Client.create();
            WebResource webResource = client.resource(this.baseUrl + endpoint);
            ClientResponse response = webResource
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            // Accepted
            if (response.getStatus() == 200) {
                if (response.hasEntity()) {
                    String output = response.getEntity(String.class);
                    JSONArray returnArray = new JSONArray(output);

                    for (int i = 0; i < returnArray.length(); i++) {
                        JSONObject returnObject = returnArray.getJSONObject(i).getJSONObject("fields");
                        JSONArray returnIds =  returnArray.getJSONObject(i).getJSONArray("ids");
                        JSONObject pid = returnIds.getJSONObject(0);

                        Person person = new Person();
                        person.setPid(pid.getString("idString"));

                        person.setFirstname(returnObject.getString("vorname"));
                        person.setSurname(returnObject.getString("nachname"));
                        person.setBirthname(returnObject.getString("geburtsname"));
                        person.setCity(returnObject.getString("ort"));
                        person.setZipcode(returnObject.getString("plz"));

                        int day = returnObject.getInt("geburtstag");
                        int month = returnObject.getInt("geburtsmonat");
                        int year = returnObject.getInt("geburtsjahr");

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month - 1);
                        cal.set(Calendar.DAY_OF_MONTH, day);

                        person.setBirthdate(cal.getTime());

                        personList.add(person);
                    }
                }
            }
            else {
                if (response.hasEntity()) {
                    String entity = response.getEntity(String.class);
                    throw new RuntimeException("Failed : HTTP error code: " +
                            response.getStatus() +
                            " message: " +
                            entity);
                }
                else {
                    throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
                }
            }
        }

        return personList;
    }

    /**
     * {@inheritDoc}
     */
    public List<Person> getAllPatientPIDs() {
        List<Person> persons = new ArrayList<>();

        if (this.getHasAdminConnectionInfo()) {
            String endpoint = "patients";

            try {
                Client client = Client.create();
                client.addFilter(new HTTPDigestAuthFilter(this.adminUsername, this.adminPassword));
                WebResource webResource = client.resource(this.baseUrl + endpoint);
                ClientResponse response = webResource
                        .type(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .get(ClientResponse.class);

                // If not accepted request
                if (response.getStatus() != 202 && response.getStatus() != 200) {
                    throw new RuntimeException("Failed to communicate with identity management: HTTP error code : "
                            + response.getStatus());
                }

                // Everything is fine so process the result
                String output = response.getEntity(String.class);

                JSONArray finalArray = new JSONArray(output);

                for (int i = 0; i < finalArray.length(); i++) {
                    JSONArray jsonIdArray = finalArray.getJSONArray(i);

                    JSONObject jsonObj = null;
                    for (int j = 0; j < jsonIdArray.length(); j++) {
                        jsonObj = jsonIdArray.getJSONObject(j);
                        String type = jsonObj.getString("type");
                        if ("pid".equals(type)) {
                            break;
                        }
                    }

                    if (jsonObj != null) {
                        Person person = new Person();
                        person.setPid(jsonObj.getString("idString"));
                        persons.add(person);
                    }
                }
            }
            catch(Exception err) {
                throw new RuntimeException("JSON processing exception: " + err.getMessage());
            }
        }

        return persons;
    }

    //endregion

    //endregion

    //region Private Methods

    private JSONObject getMainzellisteCreatedResponse(ClientResponse response) throws Exception {

        JSONObject finalResult = null;

        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            if (response.hasEntity()) {
                String output = response.getEntity(String.class);
                finalResult = new JSONObject(output);
            }
        }
        else {
            if (response.hasEntity()) {
                String entity = response.getEntity(String.class);
                throw new RuntimeException("Failed : HTTP error code: " +
                        response.getStatus() +
                        " message: " +
                        entity);
            }
            else {
                throw new RuntimeException("Failed : HTTP error code: " + response.getStatus());
            }
        }

        return finalResult;
    }

    //endregion

}
