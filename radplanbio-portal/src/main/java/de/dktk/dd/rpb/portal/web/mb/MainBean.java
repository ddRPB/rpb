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

package de.dktk.dd.rpb.portal.web.mb;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.*;

import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import org.springframework.context.annotation.Scope;

import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

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
    private static final String ocRoot = "root"; // root user have to exist in RPB database (it is default in OC)

    //endregion

    //region Injects

    //region DefaultAccount Repository

    @Inject
    DefaultAccountRepository defaultAccountRepository;

    @SuppressWarnings("unused")
    public void setDefaultAccountRepository(DefaultAccountRepository value) {
        this.defaultAccountRepository = value;
    }

    //endregion

    //region OpenClinicaData Repository

    @Inject
    IOpenClinicaDataRepository openClinicaDataRepository;

    public IOpenClinicaDataRepository getOpenClinicaDataRepository() {
        return this.openClinicaDataRepository;
    }

    @SuppressWarnings("unused")
    public void setOpenClinicaDataRepository(IOpenClinicaDataRepository openClinicaDataRepository) {
        this.openClinicaDataRepository = openClinicaDataRepository;
    }

    //endregion

    //region OpenClinica SOAP service

    // Do not inject this service, otherwise it will be shared across session
    private IOpenClinicaService openClinicaService;

    @SuppressWarnings("unused")
    public IOpenClinicaService getOpenClinicaService() {
        return this.openClinicaService;
    }

    // Do not inject this service, otherwise it will be share across session
    // this is a root service that have access to all root user studies
    private IOpenClinicaService rootOpenClinicaService;

    @SuppressWarnings("unused")
    public IOpenClinicaService getRootOpenClinicaService() { return this.rootOpenClinicaService; }

    //endregion

    //region Conquest PACS service

    private IConquestService pacsService;

    @SuppressWarnings("unused")
    public IConquestService getPacsService() {
        return this.pacsService;
    }

    //endregion

    //region Mainzelliste PID service

    // Do not inject this service, otherwise it will be shared across session
    private IMainzellisteService svcPid;

    public IMainzellisteService getSvcPid() {
        return this.svcPid;
    }

    //endregion

    //region RadPlanBio Web-API service (python server)

    // Do not inject this service, otherwise it will be shared across session
    private IRadPlanBioWebApiService svcRpb;

    public IRadPlanBioWebApiService getSvcRpb() {
        return this.svcRpb;
    }

    //endregion

    //region RadPlanBio portal Web-API service (new rpb services)

    // Do not inject this service, otherwise it will be shared across session
    private IPortalWebApiService svcWebApi;

    public IPortalWebApiService getSvcWebApi() {
        return this.svcWebApi;
    }

    //endregion

    //region Messages

    @Inject
    @SuppressWarnings("unused")
    private MessageUtil messageUtil;

    //endregion

    //endregion

    //region Members

    private DefaultAccount myAccount;
    private Study activeStudy;
    private String clientIpAddress;

    //endregion

    //region Properties

    //region RadPlanBio user account

    public DefaultAccount getMyAccount() {
        return this.myAccount;
    }

    //endregion

    //region OpenClinica user active study

    public Study getActiveStudy() {
        return this.activeStudy;
    }

    //endregion

    //region Client IP address

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    //endregion

    //region Session

    @SuppressWarnings("unused")
    public void isSessionValid() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        final HttpSession session = (HttpSession) context.getExternalContext().getSession(Boolean.FALSE);
        session.invalidate();
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
            // Initialise
            this.initAccount();

            // Connection to WebAPI need the be initialised first
            this.initRpbWebApiConnection(); // python server
            this.initWebApiConnection(); // new api

            this.initEdcConnection();
            this.initRootEdcConnection();
            this.initPacsConnection();
            this.initPidConnection();

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
        try {
            if (this.myAccount != null &&
                this.svcRpb != null) {

                if (this.myAccount.isLdapUser()) {
                    this.activeStudy = this.svcWebApi.loadDefaultAccount(
                            this.myAccount.getUsername()
                    ).getActiveStudy();
                }
                else {
                    this.activeStudy = this.svcRpb.loadUserActiveStudy(
                            this.myAccount.getOcUsername()
                    );
                }

                // Refresh the page to apply the new active study/site
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void changeUserActiveStudy(Study newActiveStudy) {
        if (this.myAccount != null &&
            this.svcRpb != null) {

            if (this.myAccount.isLdapUser()) {
                this. svcWebApi.changeActiveStudy(
                        this.myAccount.getUsername(),
                        newActiveStudy
                );
            }
            else {
                this.svcRpb.changeUserActiveStudy(
                        this.myAccount.getOcUsername(),
                        newActiveStudy.getId()
                );
            }
        }
    }

    //endregion

    //region Help

    public void navigateToHelp(String reference) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("../help/user/usermanual.html#" + reference);
    }

    /**
     * Navigate to user manual
     *
     * @throws java.io.IOException
     */
    public void navigateToHelpFromRoot(String reference) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("help/user/usermanual.html#" + reference);
    }

    //endregion

    //endregion

    //region Private methods

    /**
     * Initialise RadPlanBio user account that is currently logged in
     */
    private void initAccount() {
        // RadPlanBio user account
        this.myAccount = null;
        if (!UserContext.getUsername().equals(UserContext.ANONYMOUS_USER)) {
            this.myAccount = this.defaultAccountRepository.getByUsername(UserContext.getUsername());
        }
        // TODO: should be possible to initialise account from REST need to load account API key somehow
    }

    /**
     * Initialise EDC SOAP connection for RadPlanBio user
     */
    private void initEdcConnection() {
        // I need to get OC user hash to be able to use SOAP (the RPB and OC password can be different)
        String ocHash = "";
        if (this.myAccount != null &&
            this.myAccount.hasOpenClinicaAccount() &&
            this.svcRpb != null) {

            // For non LDAP user load password hash from DB (python server(
            if (!myAccount.isLdapUser()) {
                ocHash = this.svcRpb.loadAccountPasswordHash(
                        myAccount.getOcUsername()
                );
            }
            // For LDAP user, use new web API services (portal)
            else if (this.svcWebApi != null) {
                ocHash = this.svcWebApi.loadDefaultAccount(
                                myAccount.getUsername()
                            )
                            .getOcPasswordHash();
            }
        }

        this.openClinicaService = this.initEdcConnection(
                this.myAccount, ocHash
        );
    }

    /**
     * Initialise EDC SOAP connection for root user
     */
    private void initRootEdcConnection() {
        DefaultAccount rootAccount = this.defaultAccountRepository.getByUsername(ocRoot);

        // I need to get OC user hash to be able to use SOAP (the RPB and OC password can be different)
        String ocHash = "";
        if (rootAccount != null &&
            rootAccount.hasOpenClinicaAccount() &&
            this.svcRpb != null) {

            ocHash = this.svcRpb.loadAccountPasswordHash(rootAccount);
        }

        this.rootOpenClinicaService = this.initEdcConnection(
                rootAccount, ocHash
        );
    }

    /**
     * Initialise EDC SOAP connection for specified user account
     * @param userAccount specified DefaultAccount
     * @return initialised IOpenClinicaService for specified user
     */
    private IOpenClinicaService initEdcConnection(DefaultAccount userAccount, String ocHash) {
        IOpenClinicaService result = null;

        if (userAccount != null &&
                userAccount.hasOpenClinicaAccount() &&
                userAccount.getPartnerSite().getPortal() != null &&
                userAccount.getPartnerSite().getEdc() != null) {

            // Try to connect to the location of OC SOAP web service instance
            // TODO: REST last argument should be this.myAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            // I use internal URL of portal because JERSEY has troubles with reverse proxy redirection
            result = new OpenClinicaService();
            result.connectWithHash(
                    userAccount.getOcUsername(),
                    ocHash,
                    userAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    userAccount.getPartnerSite().getPortal().getPortalBaseUrl() + "OpenClinica/"
            );
        }

        return result;
    }

    /**
     * Initialise PACS connection for RadPlanBio user
     */
    private void initPacsConnection() {
        // Setup service to communicate with PACS server
        if (this.myAccount != null &&
            this.myAccount.getPartnerSite().getPacs() != null) {

            this.pacsService = new ConquestService();
            this.pacsService.setupConnection(
                    this.myAccount.getPartnerSite().getPacs().getPacsBaseUrl()
            );
        }
    }

    /**
     * Initialise PID generator connection
     */
    private void initPidConnection() {
        if (this.myAccount != null &&
            this.myAccount.getPartnerSite().getPid() != null &&
            this.myAccount.getPartnerSite().getPortal() != null) {

            this.svcPid = new MainzellisteService();
            String adminUsername = myAccount.getPartnerSite().getPid().getAdminUsername();
            String adminPassword = myAccount.getPartnerSite().getPid().getAdminPassword();

            String apiKey = myAccount.getPartnerSite().getPid().getApiKey();
            String genratorBaseUrl = myAccount.getPartnerSite().getPid().getGeneratorBaseUrl();
            String callback = myAccount.getPartnerSite().getPortal().getPortalBaseUrl();

            //TODO: setup for use with apiVersion parameter 2.1 from DB
            String apiVersion = "2.1";

            this.svcPid.setupConnectionInfo(
                    genratorBaseUrl,
                    apiKey,
                    apiVersion,
                    callback
            );
            this.svcPid.setupAdminConnectionInfo(
                    adminUsername,
                    adminPassword
            );
        }
    }

    /**
     * Initialise RPB Web-API connection
     */
    private void initRpbWebApiConnection() {
        if (this.myAccount != null &&
            this.myAccount.getPartnerSite().getServer() != null) {

            this.svcRpb = new RadPlanBioWebApiService();
            this.svcRpb.setupConnection(
                    this.myAccount.getPartnerSite().getServer().getPublicUrl()
            );
        }
    }

    /**
     * Initialise RPB Web-API connection
     */
    private void initWebApiConnection() {
        if (this.myAccount != null &&
            this.myAccount.getPartnerSite().getPortal() != null) {

            this.svcWebApi = new PortalWebApiService();
            this.svcWebApi.setupConnection(
                    this.myAccount.getPartnerSite().getPortal().getPublicUrl(),
                    this.myAccount.getApiKey()
            );
        }
    }

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
