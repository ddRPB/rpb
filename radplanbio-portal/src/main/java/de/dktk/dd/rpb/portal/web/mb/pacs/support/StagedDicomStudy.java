package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;

/**
 * Represents a DicomStudy that has been staged in PACS for research
 */
public class StagedDicomStudy extends DicomStudy {
    private boolean hasStageOneRepresentation = false;
    private boolean isHasStageTwoRepresentation = false;
    private String stageTwoStudyInstanceUID = "";

    public StagedDicomStudy() {
        super();
    }

    public String getStageTwoStudyInstanceUID() {
        return stageTwoStudyInstanceUID;
    }

    public void setStageTwoStudyInstanceUID(String stageTwoStudyInstanceUID) {
        this.stageTwoStudyInstanceUID = stageTwoStudyInstanceUID;
    }

    public boolean isHasStageOneRepresentation() {
        return hasStageOneRepresentation;
    }

    public void setHasStageOneRepresentation(boolean hasStageOneRepresentation) {
        this.hasStageOneRepresentation = hasStageOneRepresentation;
    }

    public boolean isHasStageTwoRepresentation() {
        return isHasStageTwoRepresentation;
    }

    public void setHasStageTwoRepresentation(boolean hasStageTwoRepresentation) {
        isHasStageTwoRepresentation = hasStageTwoRepresentation;
    }
}
