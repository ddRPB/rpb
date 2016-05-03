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

import org.json.JSONObject;

import de.dktk.dd.rpb.core.domain.ctms.Person;

import java.util.List;

/**
 * Mainzelliste PID Service Interface
 *
 * @author tomas@skripcak.net
 * @since 21 August 2013
 */
public interface IMainzellisteService {

    //region Connection Setup

    /**
     * Prepare service for communication with mainzelliste backend
     *
     * @param baseUrl manizelliste location
     * @param apiKey secret key to access mainzelliste
     * @param callback where the callback is sent (Portal or MDAT server)
     */
    void setupConnectionInfo(String baseUrl, String apiKey, String callback);

    /**
     * Prepare service for communication with mainzelliste backend
     *
     * @param baseUrl manizelliste location
     * @param apiKey secret key to access mainzelliste
     * @param apiVersion version of mainzelliste
     * @param callback where the callback is sent (Portal or MDAT server)
     */
    @SuppressWarnings("unused")
    void setupConnectionInfo(String baseUrl, String apiKey, String apiVersion, String callback);

    /**
     * Prepare service to execute administrator communication with mainzelliste
     *
     * @param adminUsername admin user name
     * @param adminPassword admin password
     */
    void setupAdminConnectionInfo(String adminUsername, String adminPassword);

    //endregion

    //region Session

    /**
     * Get user session in order to communicate with mainzelliste
     *
     * @return Session JSON object
     */
    JSONObject newSession() throws Exception;

    /**
     * Delete user session and return the status code of result
     *
     * @param sessionId sessionID
     * @return resultStatusCode
     */
    int deleteSession(String sessionId) throws Exception;

    //endregion

    //region Patient Tokens

    /**
     * Create a token for new patient creation to patient identity database
     * @param sessionId sessionId string
     * @return JSON formatted new patient token
     */
    JSONObject newPatientToken(String sessionId);

    /**
     * Create a token for reading existing patient from patient identity database
     * @param sessionId sessionId string
     * @param pid pseudonym to read
     * @return JSON formatted read patient token
     */
    JSONObject readPatientToken(String sessionId, String pid) throws Exception;

    /**
     * Create a token for reading multiple existing patients from patient identity database
     * @param sessionId sessionId string
     * @param pids list of pseudonyms to read
     * @return JSON formatted read multiple patients token
     * @throws Exception Exception
     */
    JSONObject readPatientsToken(String sessionId, List<String> pids) throws Exception;

    /**
     * Create a token for edition existing patient in patient identity database
     * @param sessionId sessionId string
     * @param pid existing patient pseudonym
     * @return JSON formatted edit patient token
     * @throws Exception Exception
     */
    JSONObject editPatientToken(String sessionId, String pid) throws Exception;

    //endregion

    //region Create Patient PID

    /**
     * Create a patient in patient identity database
     * @param tokenId patient creation token id
     * @param person person (patient) entity to be created
     * @return JSON formatted result of patient creation
     */
    JSONObject createPatientJson(String tokenId, Person person) throws Exception;

    /**
     * Force to create a new patient in patient identity database (even if there is a possible match)
     * @param tokenId patient creation token id
     * @param person person (patient) entity to be created
     * @return JSON formatted result of patient creationg
     */
    JSONObject createSurePatienJson(String tokenId, Person person);

    //endregion

    // region Edit Patient Data

    /**
     * Edit a patient in patient identity database
     *
     * @param tokenId patient edit token id
     * @param person person (patient) entity to be updated
     * @return true if edited IDAT was accepted
     */
    Boolean editPatientJson(String tokenId, Person person) throws Exception;

    //endregion

    //region Get Patients

    /**
     * Get patient IDAT data
     *
     * @param tid read token id
     * @return person IDAT object
     */
    Person getPatient(String tid) throws Exception;

    /**
     *
     * @param tid read token id
     * @return list of patient idat objects
     * @throws Exception
     */
    List<Person> getPatientList(String tid) throws Exception;

    /**
     * Get all patients from patient identity database
     * @return JSON formated list of all patients in patient identity database
     */
    List<Person> getAllPatients();

    //endregion
}
