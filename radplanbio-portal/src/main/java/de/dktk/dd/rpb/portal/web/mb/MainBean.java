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

package de.dktk.dd.rpb.portal.web.mb;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.*;

import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.ResourcesUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
 * <p>
 * Contains session scoped objects like user accounts and service directly related to user accounts
 * Other view scoped beans can just inject main been to get access to preinitialised RPB services (for logged in user)
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

    //region NoInjects - UserContext/PartnerSite dependent services that needs initialisation

    //region OpenClinica SOAP service

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IOpenClinicaService openClinicaService;

    public IOpenClinicaService getOpenClinicaService() {
        return this.openClinicaService;
    }

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    // this is a root service that have access to all root user studies (only SOAP)
    private IOpenClinicaService rootOpenClinicaService;

    public IOpenClinicaService getRootOpenClinicaService() {
        return this.rootOpenClinicaService;
    }

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    // this is engine service that have low privilege access to all iengine user studies (SOAP + REST)
    private IOpenClinicaService engineOpenClinicaService;

    public IOpenClinicaService getEngineOpenClinicaService() {
        return this.engineOpenClinicaService;
    }

    //endregion

    //region Conquest PACS service

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IConquestService pacsService;

    public IConquestService getPacsService() {
        return this.pacsService;
    }

    //endregion

    //region Mainzelliste PID service

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IMainzellisteService svcPid;

    public IMainzellisteService getSvcPid() {
        return this.svcPid;
    }

    //endregion

    //region RadPlanBio Web-API service (python server)

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IRadPlanBioWebApiService svcRpb;

    public IRadPlanBioWebApiService getSvcRpb() {
        return this.svcRpb;
    }

    //endregion

    //region RadPlanBio portal Web-API service (new rpb services)

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IPortalWebApiService svcWebApi;

    public IPortalWebApiService getSvcWebApi() {
        return this.svcWebApi;
    }

    //endregion

    //region BIO bank service

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private IBioBankService svcBio;

    public IBioBankService getSvcBio() {
        return this.svcBio;
    }

    //endregion

    //region Study integration facade

    // Do not inject this service, otherwise it will be created just one and used across all sessions
    private StudyIntegrationFacade studyIntegrationFacade;

    public StudyIntegrationFacade getStudyIntegrationFacade() {
        return this.studyIntegrationFacade;
    }

    //endregion

    //endregion

    //region Members

    private DefaultAccount myAccount;
    private Study activeStudy; // EDC study
    private String clientIpAddress;

    private IDefaultAccountRepository defaultAccountRepository;
    private IPartnerSiteRepository partnerSiteRepository;
    private IStudyRepository studyRepository;
    private IOpenClinicaDataRepository openClinicaDataRepository;

    private EngineService svcEngine;

    private ResourcesUtil resourcesUtil;
    private MessageUtil messageUtil;

    //endregion

    //region Constructors

    @Inject
    public MainBean(IDefaultAccountRepository defaultAccountRepository,
                    IPartnerSiteRepository partnerSiteRepository,
                    IStudyRepository studyRepository,
                    IOpenClinicaDataRepository openClinicaDataRepository,
                    EngineService engineService,
                    ResourcesUtil resourcesUtil,
                    MessageUtil messageUtil) {

        this.defaultAccountRepository = defaultAccountRepository;
        this.partnerSiteRepository = partnerSiteRepository;
        this.studyRepository = studyRepository;
        this.openClinicaDataRepository = openClinicaDataRepository;

        this.svcEngine = engineService;

        this.resourcesUtil = resourcesUtil;
        this.messageUtil = messageUtil;
    }

    //endregion

    //region Properties

    //region DefaultAccount

    public DefaultAccount getMyAccount() {
        return this.myAccount;
    }

    //endregion

    //region ActiveStudy - EDC

    public Study getActiveStudy() {
        return this.activeStudy;
    }

    //endregion

    //region ClientIpAddress

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    //endregion

    //region StudyRepository

    public IStudyRepository getStudyRepository() {
        return this.studyRepository;
    }

    //endregion

    //region OpenClinicaDataRepository

    public IOpenClinicaDataRepository getOpenClinicaDataRepository() {
        return this.openClinicaDataRepository;
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

    /**
     * Initialise UserContext/PartnerSite dependent services
     */
    public void load() {
        try {
            // First init user defaultAccount (that is aggregate root)
            this.initAccount();

            // Than connection to WebAPI need the be initialised
            this.initRpbWebApiConnection(); // python server
            this.initWebApiConnection(); // new api

            // Than rest of RPB services
            this.initEdcConnection();
            this.initRootEdcConnection();
            this.initEngineEdcConnection();
            this.initPacsConnection();
            this.initPidConnection();
            this.initBioConnection();

            // Than refresh data active study user data
            this.refreshActiveStudy();
            this.refreshClientIpAddress();

            // At the end init facades (that can use all the above mentioned)
            this.studyIntegrationFacade = new StudyIntegrationFacade();
            this.studyIntegrationFacade.init(this);
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Refresh

    public void refreshActiveStudy() {
        try {
            if (this.myAccount != null && this.svcWebApi != null) {

                // Load via portal web API
                this.activeStudy = this.svcWebApi.loadDefaultAccount(
                        this.myAccount.getUsername(),
                        this.myAccount.getApiKey()
                )
                        .getActiveStudy();

                // Refresh the page to apply the new active study/site
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void changeUserActiveStudy(Study newActiveStudy) {
        if (this.myAccount != null && this.svcWebApi != null) {

            // Change study via portal web API
            this.svcWebApi.changeActiveStudy(
                    this.myAccount.getUsername(),
                    this.myAccount.getApiKey(),
                    newActiveStudy
            );
        }
    }

    //endregion

    //region RPB subject pseudonym

    /**
     * Determine whether subject belongs to principal site depending on the pseudonym prefix (RPB partner site identifier)
     *
     * @param pid RPB subject pseudonym
     * @return true if subject belongs to RPB principal site
     */
    public boolean isPrincipalSiteSubject(String pid) {
        if (pid != null && !pid.isEmpty()) {
            String prefix = this.resourcesUtil.getProperty("partner_site_identifier") + Constants.RPB_IDENTIFIERSEP;
            return pid.startsWith(prefix);
        }

        return false;
    }

    /**
     * Determine whether subject belongs to logged user site depending on the pseudonym prefix (RPB partner site identifier)
     *
     * @param pid RPB subject pseudonym
     * @return true if subject belongs to logged user site
     */
    public boolean isMySiteSubject(String pid) {
        if (pid != null && !pid.isEmpty()) {
            String prefix = this.getMyAccount().getPartnerSite().getIdentifier() + Constants.RPB_IDENTIFIERSEP;
            return pid.startsWith(prefix);
        }

        return false;
    }

    /**
     * Extract pseudonym without prefix (RPB partner site identifier) to search local PID
     *
     * @param pid RPB subject pseudonym
     * @return pseudonym without prefix (RPB partner site identifier) to search local PID
     */
    public String extractMySubjectPurePid(String pid) {
        String result = "";

        if (pid != null && !pid.isEmpty()) {
            String prefix = this.getMyAccount().getPartnerSite().getIdentifier() + Constants.RPB_IDENTIFIERSEP;
            result = pid.startsWith(prefix) ? pid.replace(prefix, "") : pid;
        }

        return result;
    }

    /**
     * Construct full pseudonym with prefix (RPB partner site identifier)
     *
     * @param purePid RPB subject pseudonym without prefix
     * @return pseudonym with prefix (RPB partner site identifier)
     */
    public String constructMySubjectFullPid(String purePid) {
        String result = "";

        if (purePid != null && !purePid.isEmpty()) {
            String prefix = this.getMyAccount().getPartnerSite().getIdentifier() + Constants.RPB_IDENTIFIERSEP;

            // Make sure that purePid is really pure (without the RPB partner site identifier)
            if (!purePid.startsWith(prefix)) {
                result = prefix + purePid;
            }
            // Otherwise it looks like that purePid is already full pseudonym
            else {
                result = purePid;
            }
        }

        return result;
    }

    /**
     * Extract site (RPB partner site) identifier from any study subject PID (pseudonym)
     *
     * @param pid RPB subject pseudonym
     * @return partner site identifier
     */
    public String extractSubjectSiteIdentifier(String pid) {
        String result = "";

        if (pid != null && !pid.isEmpty()) {
            int index = pid.indexOf(Constants.RPB_IDENTIFIERSEP);
            result = pid.substring(0, index);
        }

        return result;
    }

    /**
     * Find the RPB partner site to whome specified RPB subject pseudonym belong
     *
     * @param pid RPB subject pseudonym
     * @return subject partner site
     */
    public PartnerSite findSubjectSite(String pid) {
        PartnerSite siteExample = new PartnerSite();
        String siteIdentifier = this.extractSubjectSiteIdentifier(pid);
        siteExample.setIdentifier(siteIdentifier);

        return partnerSiteRepository.findUniqueOrNone(siteExample);
    }

    //endregion

    //region Redirect

    /**
     * Redirect to selected patient centric view
     *
     * @param studySubject studySubject to load
     */
    public void redirectToPid(de.dktk.dd.rpb.core.domain.edc.StudySubject studySubject) {
        try {
            // Is allowed to access that patient (does it belong to user PartnerSite)
            if (this.isMySiteSubject(studySubject.getPid())) {
                // PID module URL
                String url = "/pid/pidCRUD.faces?pid=" + studySubject.getPid();

                // Redirect to PID form
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(url);
            } else {
                this.messageUtil.infoText("Not allowed to navigate to the RPB patient centric view, because selected study subject does not belong to your site.");
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Redirect to selected patient centric view
     *
     * @param studySubject studySubject to load
     */
    public void redirectToPid(StudySubject studySubject) {
        try {
            // Is allowed to access that patient (does it belong to user PartnerSite)
            if (this.isMySiteSubject(studySubject.getPersonID())) {
                // PID module URL
                String url = "/pid/pidCRUD.faces?pid=" + studySubject.getPersonID();

                // Redirect to PID form
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(url);
            } else {
                this.messageUtil.infoText("Not allowed to navigate to the RPB patient centric view, because selected study subject does not belong to your site.");
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Redirect to selected patient centric view
     *
     * @param studySubject to load
     */
    public void redirectToPid(Subject studySubject) {
        try {
            // Is allowed to access that patient (does it belong to user PartnerSite)
            if (this.isMySiteSubject(studySubject.getUniqueIdentifier())) {
                // PID module URL
                String url = "/pid/pidCRUD.faces?pid=" + studySubject.getUniqueIdentifier();

                // Redirect to PID form
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(url);
            } else {
                this.messageUtil.infoText("Not allowed to navigate to the RPB patient centric view, because selected study subject does not belong to your site.");
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Force session expiration
     */
    public void sessionExpired() {
        try {
            // Get session to cleanup
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

            // Log out from portal
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, null, null);
            }
            SecurityContextHolder.getContext().setAuthentication(null);

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            // Invalidate Session
            externalContext.invalidateSession();
            // Redirect to login
            externalContext.redirect("/login.faces?session_expired=1&faces-redirect=true");
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Help

    /**
     * Navigate to the referenced section in user manual
     *
     * @param reference referencedSection identifier
     * @throws IOException IOException
     */
    public void navigateToHelp(String reference) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("../help/user/usermanual.html#" + reference);
    }

    /**
     * Navigate to the referenced section in user manual
     *
     * @param reference referencedSection identifier
     * @throws IOException IOException
     */
    public void navigateToHelpFromRoot(String reference) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("help/user/usermanual.html#" + reference);
    }

    //endregion

    //endregion

    //region Private

    /**
     * Initialise RadPlanBio user account that is currently logged in
     */
    private void initAccount() {

        // RPB user account
        this.myAccount = null;

        if (!UserContext.getUsername().equals(UserContext.ANONYMOUS_USER)) {
            this.myAccount = this.defaultAccountRepository.getByUsername(UserContext.getUsername());
        }
    }

    /**
     * Initialise EDC SOAP connection for RadPlanBio user
     */
    private void initEdcConnection() {
        // I need to get OC user password hash to be allow to use SOAP (the RPB and OC password can be different)
        String ocHash = "";
        if (this.myAccount != null &&
                this.myAccount.hasOpenClinicaAccount() &&
                this.svcWebApi != null) {

            // Load via portal web API
            ocHash = this.svcWebApi.loadDefaultAccount(
                    myAccount.getUsername(),
                    myAccount.getApiKey()
            )
                    .getOcPasswordHash();
        }

        // EDC connection for logged user
        this.openClinicaService = this.initEdcConnection(this.myAccount, ocHash);

        // Enable caching to speed up study-0
        this.openClinicaService.setCacheIsEnabled(Boolean.TRUE);
    }

    /**
     * Initialise EDC SOAP connection for root user
     */
    private void initRootEdcConnection() {
        DefaultAccount rootAccount = this.defaultAccountRepository.getByUsername(Constants.OC_ROOT);

        // I need to get OC user password hash to be allow to use SOAP (the RPB and OC password can be different)
        String ocHash = "";
        if (rootAccount != null &&
                rootAccount.hasOpenClinicaAccount() &&
                this.svcWebApi != null) {

            // Load via portal web API
            ocHash = this.svcWebApi.loadDefaultAccount(
                    rootAccount.getUsername(),
                    rootAccount.getApiKey()
            )
                    .getOcPasswordHash();
        }

        this.rootOpenClinicaService = this.initEdcConnection(rootAccount, ocHash);

        // Enable caching to speed up study-0
        this.rootOpenClinicaService.setCacheIsEnabled(Boolean.TRUE);
    }

    /**
     * Initialise EDC SOAP + REST connection for engine user
     */
    private void initEngineEdcConnection() {
        DefaultAccount engineAccount = this.defaultAccountRepository.getByUsername(this.svcEngine.getUsername());

        if (engineAccount != null &&
                engineAccount.hasOpenClinicaAccount() &&
                engineAccount.getPartnerSite().hasEnabledEdc()) {

            this.engineOpenClinicaService = new OpenClinicaService();
            this.engineOpenClinicaService.connect(
                    this.svcEngine.getUsername(),
                    this.svcEngine.getPassword(),
                    engineAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    engineAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );

            // Enable caching to speed up study-0
            this.engineOpenClinicaService.setCacheIsEnabled(Boolean.TRUE);
        }
    }

    /**
     * Initialise EDC SOAP connection for specified user account
     *
     * @param userAccount specified DefaultAccount
     * @return initialised IOpenClinicaService for specified user
     */
    private IOpenClinicaService initEdcConnection(DefaultAccount userAccount, String ocHash) {
        IOpenClinicaService result = null;

        if (userAccount != null &&
                userAccount.hasOpenClinicaAccount() &&
                userAccount.getPartnerSite().hasEnabledEdc()) {

            result = new OpenClinicaService();
            result.connectWithHash(
                    userAccount.getOcUsername(),
                    ocHash,
                    userAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    userAccount.getPartnerSite().getEdc().getEdcBaseUrl()
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
                this.myAccount.getPartnerSite().hasEnabledPacs()) {

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

            String adminUsername = this.myAccount.getPartnerSite().getPid().getAdminUsername();
            String adminPassword = this.myAccount.getPartnerSite().getPid().getAdminPassword();

            String apiKey = this.myAccount.getPartnerSite().getPid().getApiKey();
            String generatorBaseUrl = this.myAccount.getPartnerSite().getPid().getGeneratorBaseUrl();
            String callback = this.myAccount.getPartnerSite().getPortal().getPortalBaseUrl();
            String apiVersion = this.myAccount.getPartnerSite().getPid().getApiVersion();

            this.svcPid.setupConnectionInfo(
                    generatorBaseUrl,
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
            this.svcRpb.setupConnection(this.myAccount.getPartnerSite().getServer().getPublicUrl());
        }
    }

    /**
     * Initialise RPB Web-API connection
     */
    private void initWebApiConnection() {
        if (this.myAccount != null &&
                this.myAccount.getPartnerSite().getPortal() != null) {

            this.svcWebApi = new PortalWebApiService();
            this.svcWebApi.setupConnection(this.myAccount.getPartnerSite().getPortal().getPublicUrl());
        }
    }

    /**
     * Initialise RPB BIO-bank connection
     */
    private void initBioConnection() {
        // Setup service to communicate with BIO bank server
        if (this.myAccount != null &&
                this.myAccount.getPartnerSite().hasEnabledBio()) {

            this.svcBio = null;
//            this.svcBio = new CentraxxService();
//            this.svcBio.setupConnection(
//                    this.myAccount.getPartnerSite().getBio().getBaseUrl(),
//                    this.myAccount.getPartnerSite().getBio().getUsername(),
//                    this.myAccount.getPartnerSite().getBio().getPassword()
//            );
        }
    }

    /**
     * Refresh real client IP address
     */
    private void refreshClientIpAddress() {
        // Is the client behind something? (proxy, etc.)
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

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
