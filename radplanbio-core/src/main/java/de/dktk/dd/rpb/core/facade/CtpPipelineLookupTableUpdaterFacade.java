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

package de.dktk.dd.rpb.core.facade;


import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.EnumStudySubjectStatus;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.CtpService;
import de.dktk.dd.rpb.core.service.ICtpService;
import de.dktk.dd.rpb.core.service.IEngineService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.util.Constants;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade that helps to update the lookup tables on the CTP system.
 */
@Named
public class CtpPipelineLookupTableUpdaterFacade {
    private static final Logger log = LoggerFactory.getLogger(CtpPipelineLookupTableUpdaterFacade.class);
    private final IOpenClinicaDataRepository openClinicaDataRepository;
    private final IOpenClinicaService engineOpenClinicaService;
    private final ICtpService svcCtp;
    private IEngineService engineService;
    private IDefaultAccountRepository defaultAccountRepository;

    public CtpPipelineLookupTableUpdaterFacade(IOpenClinicaDataRepository openClinicaDataRepository,
                                               IOpenClinicaService engineOpenClinicaService,
                                               CtpService svcCtp,
                                               IEngineService engineService,
                                               IDefaultAccountRepository defaultAccountRepository) {
        this.openClinicaDataRepository = openClinicaDataRepository;
        this.engineOpenClinicaService = engineOpenClinicaService;
        this.svcCtp = svcCtp;
        this.engineService = engineService;
        this.defaultAccountRepository = defaultAccountRepository;

    }

    @PostConstruct
    public void init() {
        this.load();
    }

    private void load() {
        this.initEngineEdcConnection();
    }

    private void initEngineEdcConnection() {
        connectOpenClinicaServiceWithEngineServiceAccount();
    }

    private void connectOpenClinicaServiceWithEngineServiceAccount() {
        DefaultAccount engineAccount = this.defaultAccountRepository.getByUsername(this.engineService.getUsername());

        engineOpenClinicaService.connect(
                this.engineService.getUsername(),
                this.engineService.getPassword(),
                engineAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                engineAccount.getPartnerSite().getEdc().getEdcBaseUrl()
        );

        if (!engineOpenClinicaService.isConnected()) {
            log.error("engineOpenClinicaService is not connected.");
        }
    }

    /**
     * Updates the lookup tables that maps subject identifiers within the anonymize step for a specific study.
     * In case of a multi centric study, it uses the partnerSideIdentifier to find the children studies of the
     * corresponding partner side only.
     *
     * @param study                 Study object that refers to the subjects that are part of it
     * @param partnerSideIdentifier Identifier of the location
     * @return List of boolean that reflect the success of the update per subject
     */
    public List<Boolean> updateSubjectLookupForStudy(Study study, String partnerSideIdentifier) {
        String edcCode = study.getTagValue("EDC-code");
        String studyId = study.getProtocolId();
        List<Boolean> successList = new ArrayList<>();

        if (studyId == null ||
                studyId.isEmpty() ||
                edcCode == null ||
                edcCode.isEmpty()) {
            log.error("There is a problem with the study object. Skip update." + " Study: " + study.toString());
            return successList;
        }

        if (partnerSideIdentifier == null || partnerSideIdentifier.isEmpty()) {
            log.error("partnerSideIdentifier is null or empty. Skip update.");
            return successList;
        }

        // List of EDC studies
        List<StudyType> studyTypeList = engineOpenClinicaService.listAllStudies();
        // Need to query children if main study is multi centric
        List<String> queryProtocolId = getPartnerSideStudiesFromStudyList(partnerSideIdentifier, studyId, studyTypeList);


        List<StudySubject> subjects = new ArrayList<>();
        for (String studyProtocolId : queryProtocolId) {
            log.debug("Adding subjects from study: " + studyProtocolId + " to update list.");
            subjects.addAll(this.openClinicaDataRepository.findStudySubjectsByStudy(studyProtocolId));
        }

        for (StudySubject subject : subjects) {
            if (subject.getStatus() != null) {
                updateSubjectEntryOnCtp(edcCode, successList, subject);
            } else {
                log.debug("Cannot update subject, because status is null." + " subject: " + subject.toString());
            }
        }
        return successList;
    }

    private void updateSubjectEntryOnCtp(String edcCode, List<Boolean> successList, StudySubject subject) {
        if (subject.getStatus().equalsIgnoreCase(EnumStudySubjectStatus.AVAILABLE.toString()) ||
                subject.getStatus().equalsIgnoreCase(EnumStudySubjectStatus.SIGNED.toString())) {

            log.debug("Updating subject: " + subject.toString());
            boolean success = this.svcCtp.updateSubjectLookupEntry(edcCode, subject);
            successList.add(success);

        } else {
            log.debug("Ignore subject, because of status." + "subject: " + subject.toString());
        }
    }

    private List<String> getPartnerSideStudiesFromStudyList(String partnerSideIdentifier, String studyId, List<StudyType> studyTypeList) {
        List<String> queryProtocolId = new ArrayList<>();
        for (StudyType type : studyTypeList) {
            if (type.getIdentifier() != null && type.getIdentifier().equals(studyId)) {

                // Multi-centre EDC study
                if (type.getSites() != null && type.getSites().getSite() != null && type.getSites().getSite().size() > 0) {
                    for (SiteType site : type.getSites().getSite()) {
                        if (site.getIdentifier().startsWith(partnerSideIdentifier + Constants.RPB_IDENTIFIERSEP)) {
                            queryProtocolId.add(site.getIdentifier());
                        }
                    }
                }
                // Mono-centre EDC study
                else {
                    queryProtocolId.add(type.getIdentifier());
                }
            }
        }
        return queryProtocolId;
    }

}
