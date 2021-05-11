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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.io.InputStream;

/**
 * CTP (ClinicaTrialProcessor) service
 *
 * @author tomas@skripcak.net
 * @since 28 Nov 2017
 */
@Named
public class CtpService implements ICtpService {

    //region Finals

    private static final Logger log = Logger.getLogger(CtpService.class);

    //endregion

    //region Members

    @Value("${ctp.baseUrl:}")
    private String url;
    @Value("${ctp.httpImportUrl:}")
    private String importUrl;
    @Value("${ctp.username:}")
    private String user;
    @Value("${ctp.password:}")
    private String password;
    @Value("${ctp.httpImportPacsVerificationEnabled:}")
    private boolean httpImportPacsVerificationEnabled;
    @Value("${ctp.httpImportPacsVerificationTimeout}")
    private int httpImportPacsVerificationTimeout;
    @Value("${ctp.rootUid}")
    private String dicomUidPrefix;
    @Value("${ctp.baseAetName}")
    private String baseAetName;

    //endregion

    //region Constructors

    public CtpService() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getBaseAetName() {
        return this.baseAetName;
    }

    public String getDicomUidPrefix() {
        return this.dicomUidPrefix;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean getIsHttpImportPacsVerificationEnabled() {
        return this.httpImportPacsVerificationEnabled;
    }

    public int getHttpImportPacsVerificationTimeout() {
        return this.httpImportPacsVerificationTimeout;
    }

    //endregion

    //region Methods

    public void setupConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    //endregion

    //region StudySubject

    public boolean updateStudySubjectPseudonym(String edcCode, String subjectIdentifier, String pid) {
        boolean result = false;

        String method = "lookup";

        ClientResponse response = this.createCtpWebResource(this.url, method)
                .queryParam("id", edcCode)
                .queryParam("key", "PID/" + subjectIdentifier)
                .queryParam("value", pid)
                .put(ClientResponse.class);

        //304: Not modified - the request could not be serviced with the supplied parameters
        int status = response.getStatus();
        if (status == 200) {
            result = true;
        } else {
            log.error("CTP put status: " + status);
        }

        return result;
    }

    public boolean updateStudySubjectId(String edcCode, String subjectIdentifier, String studySubjectId) {
        boolean result = false;

        String method = "lookup";

        ClientResponse response = this.createCtpWebResource(this.url, method)
                .queryParam("id", edcCode)
                .queryParam("key", "SSID/" + subjectIdentifier)
                .queryParam("value", studySubjectId)
                .put(ClientResponse.class);

        //304: Not modified - the request could not be serviced with the supplied parameters
        int status = response.getStatus();
        if (status == 200) {
            result = true;
        } else {
            log.error("CTP put status: " + status);
        }

        return result;
    }

    //endregion

    //region DICOM

    public boolean httpImportDicom(InputStream is) {
        boolean result = false;

        // No method string, direct to CTP import Url
        String method = "";
        // CTP http import accepts only this specific content type
        String cptContentType = "application/x-mirc";

        ClientResponse response = this.createCtpWebResource(this.importUrl, method)
                .accept(cptContentType)
                .type(cptContentType)
                .post(ClientResponse.class, is);

        int status = response.getStatus();
        if (status == 200 || status == 201) {
            result = true;
        } else {
            log.error("CTP post status: " + status);
        }

        return result;
    }

    //endregion

    //region Private Methods

    //region Communication

    private WebResource createCtpWebResource(String url, String method) {

        this.normaliseUrl();

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(this.user, this.password));

        return client.resource(url + method);
    }

    private void normaliseUrl() {
        if (!this.url.endsWith("/")) {
            this.url += "/";
        }
    }

    //endregion

    //endregion

}
