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

package de.dktk.dd.rpb.core.builder.pacs;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomStudy;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import de.dktk.dd.rpb.core.util.DicomUidReGeneratorUtil;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;

/**
 * Builder that allows to create a list of StagedDicomStudies out of the information of DicomStudy representations
 * of different lists. Thereby, a clinical study represents the original data of the clinical system;
 * a Pseudonymised study (also called study zero or stage one) is a pseudonymised copy of the clinical study information.
 * The Project stage represents the data within a specific study of a research project.
 */
public class StagedDicomStudyBuilder {
    private final List<DicomStudy> allStudiesList;
    private final List<DicomStudy> stageTwoStudiesList;
    private final String uidPrefix;
    private final String edcCode;
    private List<DicomStudy> clinicalStudiesList;
    private List<DicomStudy> stageOneStudiesList;
    private List<StagedDicomStudy> stagedStudies;

    private StagedDicomStudyBuilder(List<DicomStudy> allStudiesList, String uidPrefix, String edcCode) {
        this.allStudiesList = allStudiesList;
        this.uidPrefix = uidPrefix;
        this.edcCode = edcCode;
        this.stageOneStudiesList = new ArrayList<>();
        this.stageTwoStudiesList = new ArrayList<>();
    }

    /**
     * Creates an instance of the StagedDicomStudyBuilder
     *
     * @param studies   List of DicomStudies - can be a mix of "Pseudonymised Stage" and "Project Stage" studies
     * @param uidPrefix String - Prefix of the DICOM uid to calculate the Project Stage Uid
     * @param edcCode   String - RPB specific EDC code of the study
     * @return StagedDicomStudyBuilder
     */
    public static StagedDicomStudyBuilder getInstance(List<DicomStudy> studies, String uidPrefix, String edcCode) {
        return new StagedDicomStudyBuilder(studies, uidPrefix, edcCode);
    }

    /**
     * Based on previous steps like:
     * - filterFirstStageStudies
     * - filterSecondStageStudiesByEdcCode
     * - setClinicalStudyList
     *
     * This method provides a result set of StagedDicomStudy instances, with appropriate information out of the list of
     * studies, provided in the constructor and the setClinicalStudyList.
     *
     * @return List of StagedDicomStudy
     */
    public List<StagedDicomStudy> getStagedStudies() {
        this.stagedStudies = new ArrayList<>();
        for (DicomStudy study : this.stageOneStudiesList) {
            StagedDicomStudy researchStudy = getStagedDicomStudy(study);
            String secondStageUid = DicomUidReGeneratorUtil.generateStageTwoUid(this.uidPrefix, "DD", this.edcCode, study.getStudyInstanceUID());
            assignStageTwoRepresentationPropertiesIfExisting(researchStudy, secondStageUid);
            this.stagedStudies.add(researchStudy);
        }
        if (this.clinicalStudiesList != null && this.clinicalStudiesList.size() > 0) {
            this.addClinicalStudyInformation();
        }
        return stagedStudies;
    }

    private StagedDicomStudy getStagedDicomStudy(DicomStudy study) {
        StagedDicomStudy researchStudy = new StagedDicomStudy();
        researchStudy.setStudyDescription(DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(study.getStudyDescription()));
        researchStudy.setStudyInstanceUID(study.getStudyInstanceUID());
        researchStudy.setStageOneStudyInstanceUid(study.getStudyInstanceUID());
        researchStudy.setStudyDate(study.getStudyDate());
        researchStudy.setHasStageOneRepresentation(true);
        return researchStudy;
    }

    private void assignStageTwoRepresentationPropertiesIfExisting(StagedDicomStudy researchStudy, String secondStageUid) {
        if (this.getSecondStageStudyByUid(secondStageUid) != null) {
            researchStudy.setHasStageTwoRepresentation(true);
            researchStudy.setStageTwoStudyInstanceUid(secondStageUid);
        }
    }

    private DicomStudy getSecondStageStudyByUid(String uid) {
        for (DicomStudy study : this.stageTwoStudiesList) {
            String studyUid = study.getStudyInstanceUID();
            if (studyUid.equals(uid)) {
                return study;
            }
        }
        return null;
    }

    private void addClinicalStudyInformation() {
        for (DicomStudy clinicalStudy : this.clinicalStudiesList) {
            String clinicalStudyInstanceUid = clinicalStudy.getStudyInstanceUID();
            String studyZeroStudyInstanceUid = DicomUidReGeneratorUtil.generateStageOneUid(this.uidPrefix, clinicalStudyInstanceUid);
            StagedDicomStudy stagedStudy = getStagedDicomStudyByStudyZeroInstanceUid(studyZeroStudyInstanceUid);
            if (stagedStudy != null) {
                stagedStudy.setClinicalStudyInstanceUid(clinicalStudyInstanceUid);
            } else {
                stagedStudy = new StagedDicomStudy();
                stagedStudy.setStudyInstanceUID(clinicalStudyInstanceUid);
                stagedStudy.setStageOneStudyInstanceUid(studyZeroStudyInstanceUid);
                stagedStudy.setClinicalStudyInstanceUid(clinicalStudyInstanceUid);
                stagedStudy.setStudyDescription(clinicalStudy.getStudyDescription());
                stagedStudy.setStudyDate(clinicalStudy.getStudyDate());

                this.stagedStudies.add(stagedStudy);
            }
        }
    }

    private StagedDicomStudy getStagedDicomStudyByStudyZeroInstanceUid(String studyZeroStudyInstanceUid) {
        for (StagedDicomStudy stagedStudy : this.stagedStudies) {
            if (stagedStudy.getStudyInstanceUID().equals(studyZeroStudyInstanceUid)) {
                return stagedStudy;
            }
        }
        return null;
    }

    /**
     * All studies that are classified as "Pseudonymized" (stage one, study zero).
     * Needs to be filtered by filterFirstStageStudies() before.
     *
     * @return List of all Pseudonymized studies
     */
    public List<DicomStudy> getStageOneStudiesList() {
        return stageOneStudiesList;
    }

    /**
     * All studies that are classified as "Project Stage" (stage two). Needs the step:
     * - filterSecondStageStudiesByEdcCode()
     *
     * @return List of all Project Stage studies with the specific EDC code of the data set
     */
    public List<DicomStudy> getStageTwoStudiesList() {
        return stageTwoStudiesList;
    }

    /**
     * Copies all "Project Stage" studies from the study list (set by the constructor) to an internal list,
     * which will be used for later processing.
     *
     * @return StagedDicomStudyBuilder
     */
    public StagedDicomStudyBuilder filterFirstStageStudies() {
        stageOneStudiesList = new ArrayList<>();
        String studyZeroPattern = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern(study0EdcCode);
        copyItemsWithSpecificEdcCode(studyZeroPattern, this.allStudiesList, this.stageOneStudiesList);

        return this;
    }

    private void copyItemsWithSpecificEdcCode(String studyZeroPattern, List<DicomStudy> sourceList, List<DicomStudy> destinationList) {
        if (sourceList != null) {
            for (DicomStudy study : sourceList) {
                if (study.getStudyDescription() != null) {
                    if (study.getStudyDescription().startsWith(studyZeroPattern)) {
                        destinationList.add(study);
                    }
                }
            }
        }
    }

    /**
     * Based on the EDC code, provided within the constructor, this method filters Project stage studies and copies
     * it in an internal list
     * @return StagedDicomStudyBuilder
     */

    public StagedDicomStudyBuilder filterSecondStageStudiesByEdcCode() {
        String edcCodeZeroPattern = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern(this.edcCode);
        copyItemsWithSpecificEdcCode(edcCodeZeroPattern, this.allStudiesList, this.stageTwoStudiesList);
        return this;
    }

    /**
     * Set clinical data sets of studies, since they are not part of the constructor.
     *
     * @param clinicalDicomStudies studies from the clinical system
     * @return StagedDicomStudyBuilder
     */
    public StagedDicomStudyBuilder setClinicalStudyList(List<DicomStudy> clinicalDicomStudies) {
        if (clinicalDicomStudies != null && clinicalDicomStudies.size() > 0) {
            this.clinicalStudiesList = clinicalDicomStudies;
        }
        return this;
    }
}