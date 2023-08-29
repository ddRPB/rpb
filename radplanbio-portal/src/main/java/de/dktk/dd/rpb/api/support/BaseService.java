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

package de.dktk.dd.rpb.api.support;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.repository.rpb.IRadPlanBioDataRepository;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.ConquestService;
import de.dktk.dd.rpb.core.service.EmailService;
import de.dktk.dd.rpb.core.service.EngineService;
import de.dktk.dd.rpb.core.service.IConquestService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.IPacsConfigService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * Base service
 */
@Component
public class BaseService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(BaseService.class);

    //endregion

    //region Members

    //region Facade

    @Inject
    protected StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Repository

    @Inject
    protected IRadPlanBioDataRepository radPlanBioDataRepository;
    @Inject
    protected IOpenClinicaDataRepository openClinicaDataRepository;
    @Inject
    protected IDefaultAccountRepository userRepository;

    protected IOpenClinicaService openClinicaService;
    protected IOpenClinicaService engineOpenClinicaService;

    //endregion

    //region Services

    @Inject
    protected EngineService engineService;
    @Inject
    protected EmailService emailService;
    @Inject
    protected AuditLogService auditLogService;
    @Inject
    protected IPacsConfigService pacsConfigService;

    //endregion

    //endregion

    //region Methods

    //region Authentication

    /**
     * Find apiKey within the HttpServletRequest
     * @param httpServletRequest HttpServletRequest containing apiKey header
     * @return ApiKey
     */
    protected String extractApiKey(HttpServletRequest httpServletRequest) {
        String apiKey = null;

        // Basic Authentication
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic")) {

            String base64Credentials = authHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.decodeBase64(base64Credentials), StandardCharsets.UTF_8);

            // Username(ApiKey):Password
            final String[] values = credentials.split(":",2);
            apiKey = values[0];
        }

        return apiKey;
    }

    /**
     * Find DefaultAccount that corresponds to the provided apiKey within the HttpServletRequest
     * @param httpServletRequest HttpServletRequest containing apiKey header
     * @return DefaultAccount entity
     */
    public DefaultAccount defaultAccountAuthentication(HttpServletRequest httpServletRequest) {
        String apiKey = this.extractApiKey(httpServletRequest);
        return  this.getAuthenticatedUser(apiKey);
    }

    /**
     * Find DefaultAccount that corresponds to the provided apiKey
     * @param apiKey API-Key associated with DefaultAccount
     * @return DefaultAccount entity
     */
    protected DefaultAccount getAuthenticatedUser(String apiKey) {
        DefaultAccount userAccount = null;

        if (apiKey != null && !apiKey.isEmpty()) {
            DefaultAccount example = this.userRepository.getNew();
            example.setIsEnabled(true);
            example.setApiKeyEnabled(true);
            example.setNonLocked(true);
            example.setApiKey(apiKey);

            userAccount = this.userRepository.findUniqueOrNone(example);
        }
        
        return userAccount;
    }

    protected String getOcHash(String username) {
        return this.openClinicaDataRepository.getUserAccountHash(username);
    }

    //endregion

    //region Sleep

    public void sleepSecond() {
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    //endregion

    //region EDC

    /**
     * Setup for the EDC web service connection, based on the user account of the user
     *
     * @param userAccount DefaultAccount user account of the authenticated user
     */
    protected void initializeOpenClinicaService(DefaultAccount userAccount){
        this.openClinicaService = this.createEdcConnection(userAccount);
        this.studyIntegrationFacade.init(this.openClinicaService);
    }

    protected IOpenClinicaService createEdcConnection(DefaultAccount defaultAccount) {
        IOpenClinicaService svcEdc = null;

        // OpenClinica user account
        if (defaultAccount != null &&
                defaultAccount.hasOpenClinicaAccount() &&
                defaultAccount.getPartnerSite().hasEnabledEdc()) {

            String ocHash = this.getOcHash(defaultAccount.getOcUsername());

            svcEdc = new OpenClinicaService();
            svcEdc.connectWithHash(
                    defaultAccount.getOcUsername(),
                    ocHash,
                    defaultAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    defaultAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );
        }

        return svcEdc;
    }

    protected IOpenClinicaService createEdcConnection(DefaultAccount defaultAccount, String ocPassword) {
        IOpenClinicaService svcEdc = null;

        // OpenClinica user account
        if (defaultAccount != null &&
                defaultAccount.hasOpenClinicaAccount() &&
                defaultAccount.getPartnerSite().hasEnabledEdc()) {
            svcEdc = new OpenClinicaService();
            svcEdc.connect(
                    defaultAccount.getOcUsername(),
                    ocPassword,
                    defaultAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    defaultAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );
        }
        return svcEdc;
    }

    protected void initEngineEdcConnection() {
        DefaultAccount engineAccount = this.userRepository.getByUsername(this.engineService.getUsername());

        if (engineAccount != null &&
                engineAccount.hasOpenClinicaAccount() &&
                engineAccount.getPartnerSite().hasEnabledEdc()) {

            this.engineOpenClinicaService = new OpenClinicaService();
            this.engineOpenClinicaService.connect(
                    this.engineService.getUsername(),
                    this.engineService.getPassword(),
                    engineAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    engineAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );

            // Enable caching to speed up study-0
            this.engineOpenClinicaService.setCacheIsEnabled(Boolean.TRUE);
        }
    }

    //endregion

    //region PACS

    protected IConquestService createPacsConnection(DefaultAccount defaultAccount) {
        IConquestService pacsService = null;

        if (defaultAccount != null &&
            defaultAccount.getPartnerSite().hasEnabledPacs()) {

            pacsService = this.createPacsConnectionFromUrl(defaultAccount.getPartnerSite().getPacs().getPacsBaseUrl());
        }

        return pacsService;
    }

    protected IConquestService createPacsConnection(String apiKey) {
        String url = this.radPlanBioDataRepository.getPacsUrlByAccountApiKey(apiKey);
        return this.createPacsConnectionFromUrl(url);
    }

    private IConquestService createPacsConnectionFromUrl(String url) {
        IConquestService pacsService = null;

        // Setup service to communicate with PACS server
        if (url != null && !url.isEmpty()) {

            pacsService = new ConquestService();

            if (this.pacsConfigService.isAuth()) {
                pacsService.setupConnection(
                        url,
                        this.pacsConfigService.getThreadPoolSize(),
                        this.pacsConfigService.getPacsUser(),
                        this.pacsConfigService.getPacsPassword()
                );
            } else {
                pacsService.setupConnection(
                        url,
                        this.pacsConfigService.getThreadPoolSize()
                );
            }
        }

        return pacsService;
    }

    //endregion

    //endregion

}
