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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.util.Constants;

import org.apache.log4j.Logger;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Random;

/**
 * ResponseClientCacheService
 *
 * This class help to build client cache for web services requests across RPB platform on a scheduled bases
 * This way the expensive calls building and refreshing cache are handled without user interaction and GUI
 * is free to use fast access to client cache
 *
 * @author tomas@skripcak.net
 * @since 26 Jul 2016
 */
@Named
@Singleton
@Lazy(false)
public class ResponseClientCacheService {

    //region Finals

    private static final Logger log = Logger.getLogger(ResponseClientCacheService.class);

    //endregion

    //region Members

    private IOpenClinicaDataRepository openClinicaDataRepository;
    private IOpenClinicaService rootOpenClinicaService;

    private DefaultAccount rootAccount;

    //endregion

    //region Constructors

    //TODO: It looks like this Singleton exist twice - it may be that there are two spring IoC contexts
    // It is because of DispatcherServlet for special purpose controllers (I should get rid of them)
    @Inject
    public ResponseClientCacheService(IDefaultAccountRepository IDefaultAccountRepository, IOpenClinicaDataRepository openClinicaDataRepository) {

        // Will need to fetch a hash of root user password for accessing EDC web services
        this.openClinicaDataRepository = openClinicaDataRepository;

        // User root as a user for accessing the service resources
        this.rootAccount = IDefaultAccountRepository.getByUsername(Constants.OC_ROOT);
    }

    //endregion

    //region Methods

    /**
     * Reload Client Cache (in ms) initial delay [15 minutes] 900000 than [every hour] 3600000
     */
    @Scheduled(initialDelay = 900000, fixedDelay = 3600000)
    public void reloadCache() {
        this.reloadStudySubjectsCache();
    }

    private void reloadStudySubjectsCache() {

        try {
            // Initialize EDC web service client
            if (this.rootOpenClinicaService == null || !this.rootOpenClinicaService.isConnected()) {
                // Init web service clients
                this.initEdcConnection(rootAccount);
            }

            // Only try when client is connected to web services (they need to by deployed somewhere)
            // and the response client cache is enabled
            if (this.rootOpenClinicaService != null &&
                this.rootOpenClinicaService.isConnected() &&
                this.rootOpenClinicaService.getCacheIsEnabled()) {

                log.info("Reload of StudySubjects ResponseClient cache started.");

                List<StudyType> edcStudies = this.rootOpenClinicaService.listAllStudies();
                if (edcStudies != null) {
                    for (StudyType studyType : edcStudies) {

                        Study study = new Study(studyType);

                        // TODO: only for study-0 for now but this should be configurable for RPB study
                        if (study.getStudyIdentifier().equals(Constants.study0Identifier)) {

                            // Ugly
                            // Sometimes there is SOAP exception - did not figured out the reason why
                            // It looks like this Singleton exist twice - it may be that there are two spring IoC contexts
                            // It is because of DispatcherServlet for special purpose controllers (I should get rid of them)
                            List<StudySubjectWithEventsType> result = null;
                            // 120 seconds - hardcoded for now
                            int retryTimeout = 120000;
                            long endTime = System.currentTimeMillis() + retryTimeout;
                            while (result == null && System.currentTimeMillis() < endTime) {
                                try {
                                    result = this.rootOpenClinicaService.listAllStudySubjectsByStudy(study);
                                }
                                catch (Exception err) {
                                    log.warn("ClientResponseCache: SOAP retry to list subjects.");

                                    // When fail slow down randomly one instance so that the other can finish
                                    Random rand = new Random();
                                    int max = 30;
                                    int min = 10;
                                    int randomNum = rand.nextInt((max - min) + 1) + min;
                                    Thread.sleep(randomNum * 1000);
                                }
                            }

                            if (result != null) {
                                log.info("Reload of StudySubjects ResponseClient cache finished.");
                                //TODO: remove this log
                                log.error("Reload of StudySubjects ResponseClient cache finished.");
                            }
                            else {
                                log.error("Failed to retrieve StudySubjects for ResponseClient cache.");
                            }
                        }
                    }
                }
            }
        }
        catch (Exception err) {
            log.error("Error while reloading StudySubjects ResponseClient cache", err);
        }
    }

    //endregion

    //region Private methods

    /**
     * Initialise EDC SOAP connection for root user
     */
    private void initEdcConnection(DefaultAccount defaultAccount) {

        // I need to get OC user hash to be able to use SOAP (the RPB and OC password can be different)
        String ocHash = "";
        if (defaultAccount != null &&
            defaultAccount.hasOpenClinicaAccount() &&
            this.openClinicaDataRepository != null) {

            ocHash = this.openClinicaDataRepository.getUserAccountHash(defaultAccount.getOcUsername());
        }

        this.rootOpenClinicaService = this.initEdcConnection(
                defaultAccount, ocHash
        );

        // Enable caching to speed up study-0
        this.rootOpenClinicaService.setCacheIsEnabled(Boolean.TRUE);
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

    //endregion

}