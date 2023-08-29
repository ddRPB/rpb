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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import org.codehaus.jettison.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for communication with Biobank
 *
 * @author tomas@skripcak.net
 * @since 21 Dec 2016
 */
@Named
public class CentraxxService implements IBioBankService  {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(CentraxxService.class);

    //endregion

    //region Members

    private String url;
    private String user;
    private String password;

    //endregion

    //region Constructors

    public CentraxxService() {
        // NOOP
    }

    //endregion

    //region Methods

    //region Setup

    public void setupConnection(String url, String user, String password) {

        this.url = url;
        this.user = user;
        this.password = password;
    }

    //endregion

    //region Hello

    public boolean hello() {
        boolean result = false;

        String method = "";

        ClientResponse response = this.createCxxWebResource(method)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        int status = response.getStatus();
        if (status == 200) {
            result = true;
            String stringResponse = response.getEntity(String.class);
            log.info(stringResponse);
        }

        return result;
    }

    //endregion

    //region Patient

    public Person loadPatient(String mpi) {

//        String method = "";
//
//        return this.transformCxxDataExchangeToPatient("", cxxDataExchange);

        return null;
    }

    public Person loadPatient(String idType, String patientId) {
//        String method = "";
//
//        // All patient samples are reported
//        return this.transformCxxDataExchangeToPatient(cxxDataExchange);
        return null;
    }

    public Person loadPatient(String studyProtocolId, String idType, String patientId) {
//        String method = "";
//
//        // Take into account that only specified study samples are reported
//        return this.transformCxxDataExchangeToPatient(studyProtocolId, cxxDataExchange);
        return null;
    }

    public boolean createPatient(String ssidType, StudySubject studySubject, PartnerSite site, boolean isPrincipalSiteSubject, boolean useMPI, boolean useIDAT) {
        boolean result = false;

//        String pattern = "yyyy" + Constants.RPB_IDENTIFIERSEP + "MM" + Constants.RPB_IDENTIFIERSEP + "dd" + Constants.RPB_IDENTIFIERSEP + "HHmmssSSS";
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        String dateTimeSuffix = format.format(new java.util.Date());
//        String filename = Constants.CXX_RPB + Constants.RPB_IDENTIFIERSEP +  studySubject.getPid() + Constants.RPB_IDENTIFIERSEP + dateTimeSuffix + ".xml";
//
//        try {
//            // Queue for import
//            if (this.addToImportQueue(filename, data)) {
//                // Import
//                result = this.startImport(filename);
//
//                // Cleanup after import
//                if (result) {
//
//                    // File still exists in import queue
//                    if (this.getFromImportQueue().contains(filename)) {
//
//                        // Delete from import queue
//                        if (!this.deleteFromImportQueue(filename)) {
//                            throw new Exception("Failed to delete from import queue");
//                        }
//                    }
//                }
//            }
//            else {
//                throw new Exception("Failed to add to import queue.");
//            }
//        }
//        catch (Exception err) {
//            log.error(err.getMessage(),err);
//        }
//
        return result;
    }

    //endregion

    //region Episode

    public void loadEpisode(String episodeId) {
//        String method = "";
//
//        cxxDataExchange = this.processCxxResponse(response);
    }

    //endregion

    //region Sample

    public void loadSample(String sampleId) {
//        String method = "";
    }

    //endregion

    //endregion

    //region Private Methods

    //region Communication

    private void normaliseUrl() {
        if (!this.url.endsWith("/")) {
            this.url += "/";
        }
    }

    private WebResource createCxxWebResource(String method) {

        this.normaliseUrl();

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(this.user, this.password));

        return client.resource(this.url + method);
    }

    //endregion

    //region Serialisation


    //endregion

    //region Transformation

    //endregion

    //region Patient

    //endregion

    //region Specimen

    //endregion

    //endregion

    //region Import Queue

    private List<String> getFromImportQueue() {
        List<String> fileNames = new ArrayList<>();

        String method = "";
        
        return fileNames;
    }

    private List<String> deleteFromImportQueue() {
        List<String> fileNames = new ArrayList<>();

        String method = "";

        return fileNames;
    }

    private boolean deleteFromImportQueue(String filename) {
        String method = "" + filename;

        // Deleted
        return true;
    }

    private boolean  startImport(String filename) {
        String method = "";

        // Imported
        return true;
    }

    //endregion

    //region Successful Queue

    private List<String> getFromSuccessful() {
        List<String> fileNames = new ArrayList<>();

        String method = "";

        return fileNames;
    }

    private List<String> deleteFromSuccessful() {
        List<String> fileNames = new ArrayList<>();

        String method = "";

        return fileNames;
    }

    private boolean deleteFromSuccessful(String filename) {
        String method = "" + filename;

        // Deleted
        return true;
    }

    //endregion

    //region Error Queue

    private List<String> getFromError() {
        List<String> fileNames = new ArrayList<>();

        String method = "";

        return fileNames;
    }

    private List<String> deleteFromError() {
        List<String> fileNames = new ArrayList<>();

        String method = "";

        return fileNames;
    }

    private boolean deleteFromError(String filename) {
        String method = "" + filename;

        // Deleted
        return true;
    }

    //endregion

    //region Helpers

    private List<String> transformJsonArrayStringToList(String jsonArrayString) {
        List<String> list = new ArrayList<>();

        try {
            JSONArray jsonResponse = new JSONArray(jsonArrayString);

            for (int i = 0; i < jsonResponse.length(); i++) {
                list.add(jsonResponse.getString(i));
            }
        }
        catch (Exception err) {
            log.error(err.getMessage(),err);
        }

        return list;
    }

    //endregion

    //endregion

}
