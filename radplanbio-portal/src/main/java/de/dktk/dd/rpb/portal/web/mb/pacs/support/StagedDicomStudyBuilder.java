package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;

public class StagedDicomStudyBuilder {
    private final List<DicomStudy> allStudiesList;
    private final List<DicomStudy> stageTwoStudiesList;
    private List<DicomStudy> stageOneStudiesList;
    private List<StagedDicomStudy> stagedStudies;
    private String uidPrefix;
    private String edcCode;

    public StagedDicomStudyBuilder(List<DicomStudy> allStudiesList, String uidPrefix, String edcCode) {
        this.allStudiesList = allStudiesList;
        this.uidPrefix = uidPrefix;
        this.edcCode = edcCode;
        this.stageOneStudiesList = new ArrayList<>();
        this.stageTwoStudiesList = new ArrayList<>();
    }

    public static StagedDicomStudyBuilder getInstance(List<DicomStudy> studies, String uidPrefix, String edcCode) {
        return new StagedDicomStudyBuilder(studies, uidPrefix, edcCode);
    }

    public List<StagedDicomStudy> getStagedStudies() {
        this.stagedStudies = new ArrayList<>();
        for (DicomStudy study : this.stageOneStudiesList) {
            StagedDicomStudy researchStudy = getStagedDicomStudy(study);
            String secondStageUid = DicomUidReGeneratorUtil.generateStageTwoUid(this.uidPrefix, "DD", this.edcCode, study.getStudyInstanceUID());
            assignStageTwoRepresentationPropertiesIfExisting(researchStudy, secondStageUid);
            this.stagedStudies.add(researchStudy);
        }
        return stagedStudies;
    }

    private StagedDicomStudy getStagedDicomStudy(DicomStudy study) {
        StagedDicomStudy researchStudy = new StagedDicomStudy();
        researchStudy.setStudyDescription(DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(study.getStudyDescription()));
        researchStudy.setStudyInstanceUID(study.getStudyInstanceUID());
        researchStudy.setStudyDate(study.getStudyDate());
        researchStudy.setHasStageOneRepresentation(true);
        return researchStudy;
    }

    private void assignStageTwoRepresentationPropertiesIfExisting(StagedDicomStudy researchStudy, String secondStageUid) {
        if (this.getSecondStageStudyByUid(secondStageUid) != null) {
            researchStudy.setHasStageTwoRepresentation(true);
            researchStudy.setStageTwoStudyInstanceUID(secondStageUid);
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

    public List<DicomStudy> getStageOneStudiesList() {
        return stageOneStudiesList;
    }

    public List<DicomStudy> getStageTwoStudiesList() {
        return stageTwoStudiesList;
    }

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

    public StagedDicomStudyBuilder filterSecondStageStudiesByEdcCode() {
        String edcCodeZeroPattern = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern(this.edcCode);
        copyItemsWithSpecificEdcCode(edcCodeZeroPattern, this.allStudiesList, this.stageTwoStudiesList);
        return this;
    }
}