/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

package de.dktk.dd.rpb.participate.web.mb;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.service.*;

import de.dktk.dd.rpb.participate.web.util.MessageUtil;

import org.springframework.context.annotation.Scope;

import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Managed bean for main master view (layout template)
 * Contains session scoped objects like user accounts and service directly related to user accounts (reused later)
 *
 * @author tomas@skripcak.net
 * @since 01 Oct 2013
 */
@Named("mbMain")
@Scope("session")
public class MainBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region DefaultAccount Repository

//    @Inject
//    private IDefaultAccountRepository defaultAccountRepository;
//
//    @SuppressWarnings("unused")
//    public void setIDefaultAccountRepository(IDefaultAccountRepository value) {
//        this.defaultAccountRepository = value;
//    }

    //endregion

    //region OpenClinica web services

    // Do not inject this service, otherwise it will be shared across session
    private IOpenClinicaService openClinicaService;

    @SuppressWarnings("unused")
    public IOpenClinicaService getOpenClinicaService() {
        return this.openClinicaService;
    }

    //endregion

    //region Conquest PACS service

//    private IConquestService pacsService;
//
//    @SuppressWarnings("unused")
//    public IConquestService getPacsService() {
//        return this.pacsService;
//    }

    //endregion

    //region RadPlanBio Web-API service (python server)

    // Do not inject this service, otherwise it will be shared across session
//    private IRadPlanBioWebApiService svcRpb;
//
//    @SuppressWarnings("unused")
//    public IRadPlanBioWebApiService getSvcRpb() {
//        return this.svcRpb;
//    }

    //endregion

    //region RadPlanBio portal Web-API service (new rpb services)

    // Do not inject this service, otherwise it will be shared across session
//    private IPortalWebApiService svcWebApi;
//
//    public IPortalWebApiService getSvcWebApi() {
//        return this.svcWebApi;
//    }

    //endregion

    //region Messages

    @Inject
    @SuppressWarnings("unused")
    private MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    private Study activeStudy; // EDC study
    private String clientIpAddress;

    //endregion

    //region Properties

    //region OpenClinica user active study

    public Study getActiveStudy() {
        return this.activeStudy;
    }

    //endregion

    //region Client IP address

    @SuppressWarnings("unused")
    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.load();
    }

    //endregion

    //region Commands

    //region Load

    public void load() {
        try {
            // Connection to WebAPI need the be initialised first
            this.initEdcConnection();

            // Refresh data
            this.refreshActiveStudy();
            this.refreshClientIpAddress();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Refresh

    public void refreshActiveStudy() {
//        try {
//            if (this.myAccount != null &&
//                    this.svcRpb != null) {
//
//                if (this.myAccount.isLdapUser()) {
//                    this.activeStudy = this.svcWebApi.loadDefaultAccount(
//                            this.myAccount.getUsername()
//                    ).getActiveStudy();
//                }
//                else {
//                    this.activeStudy = this.svcRpb.loadUserActiveStudy(
//                            this.myAccount.getOcUsername()
//                    );
//                }
//
//                // Refresh the page to apply the new active study/site
//                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
//            }
//        }
//        catch (Exception err) {
//            this.messageUtil.error(err);
//        }
    }

    //endregion

    //region Study

    public void changeUserActiveStudy(Study newActiveStudy) {
//        if (this.myAccount != null &&
//                this.svcRpb != null) {
//
//            if (this.myAccount.isLdapUser()) {
//                this. svcWebApi.changeActiveStudy(
//                        this.myAccount.getUsername(),
//                        newActiveStudy
//                );
//            }
//            else {
//                this.svcRpb.changeUserActiveStudy(
//                        this.myAccount.getOcUsername(),
//                        newActiveStudy.getId()
//                );
//            }
//        }
    }

    //endregion

    //endregion

    //region Private methods

    /**
     * Initialise EDC SOAP connection for RadPlanBio user
     */
    private void initEdcConnection() {

        this.openClinicaService = this.initEdcConnection(
                UserContext.getUsername(), UserContext.getPassword()
        );

        // Catching ws responses speeds up Study-0 loading
        this.openClinicaService.setCacheIsEnabled(Boolean.TRUE);
    }

    /**
     * Initialise EDC SOAP connection for specified user account
     * @param ocUsername specified ocUsername
     * @param ocHash specified user password hash
     * @return initialised IOpenClinicaService for specified user
     */
    private IOpenClinicaService initEdcConnection(String ocUsername, String ocHash) {
        IOpenClinicaService result = null;

        // EDC resources
        String resourceName = "edc.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        // EDC properties
        String edcBaseUrl = "";
        String edcWsBaseUrl = "";
        String edcVersion = "";
        boolean edcIsEnabled = false;

        try {
            InputStream resourceStream = loader.getResourceAsStream(resourceName);
            props.load(resourceStream);

            edcBaseUrl= props.getProperty("edc.base.url");
            edcWsBaseUrl = props.getProperty("edc.ws.base.url");
            edcVersion = props.getProperty("edc.version");
            edcIsEnabled = Boolean.valueOf(props.getProperty("edc.enabled"));

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        if (edcIsEnabled) {

            result = new OpenClinicaService();
            result.connectWithHash(
                    ocUsername,
                    ocHash,
                    edcWsBaseUrl,
                    edcBaseUrl
            );
        }

        return result;
    }

//    /**
//     * Initialise RPB Web-Api connection
//     */
//    private void initRpbWebApiConnection() {
//        if (this.myAccount != null &&
//            this.myAccount.getPartnerSite().getServer() != null) {
//
//            this.svcRpb = new RadPlanBioWebApiService();
//            this.svcRpb.setupConnection(
//                    this.myAccount.getPartnerSite().getServer().getPublicUrl()
//            );
//        }
//    }

//    /**
//     * Initialise RPB Web-API connection
//     */
//    private void initWebApiConnection() {
//        if (this.myAccount != null &&
//                this.myAccount.getPartnerSite().getPortal() != null) {
//
//            this.svcWebApi = new PortalWebApiService();
//            this.svcWebApi.setupConnection(
//                    this.myAccount.getPartnerSite().getPortal().getPublicUrl(),
//                    this.myAccount.getApiKey()
//            );
//        }
//    }

    /**
     * Refresh real client IP address
     */
    private void refreshClientIpAddress() {
        // Is the client behind something? (proxy, etc.)
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String ipAddress = "";
        if (request != null) {
            ipAddress = request.getHeader("X-FORWARDED-FOR");

            // If not get the IP from standard location
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        }

        this.clientIpAddress = ipAddress;
    }

    //endregion

}