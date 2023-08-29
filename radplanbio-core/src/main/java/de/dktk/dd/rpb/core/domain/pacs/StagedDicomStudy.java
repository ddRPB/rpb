/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.domain.pacs;

/**
 * DicomStudy that could be represented by different stages in PACS for research
 */
public class StagedDicomStudy extends DicomStudy {
    private boolean hasStageOneRepresentation = false;
    private boolean hasStageTwoRepresentation = false;
    private String clinicalStudyInstanceUid = "";
    private String stageOneStudyInstanceUid = "";
    private String stageTwoStudyInstanceUid = "";

    public StagedDicomStudy() {
        super();
    }

    public String getClinicalStudyInstanceUid() {
        return clinicalStudyInstanceUid;
    }

    public void setClinicalStudyInstanceUid(String clinicalStudyInstanceUid) {
        this.clinicalStudyInstanceUid = clinicalStudyInstanceUid;
    }

    public String getStageOneStudyInstanceUid() {
        return stageOneStudyInstanceUid;
    }

    public void setStageOneStudyInstanceUid(String stageOneStudyInstanceUid) {
        this.stageOneStudyInstanceUid = stageOneStudyInstanceUid;
    }

    public String getStageTwoStudyInstanceUid() {
        return stageTwoStudyInstanceUid;
    }

    public void setStageTwoStudyInstanceUid(String stageTwoStudyInstanceUid) {
        this.stageTwoStudyInstanceUid = stageTwoStudyInstanceUid;
    }

    public boolean hasStageOneRepresentation() {
        return hasStageOneRepresentation;
    }

    public void setHasStageOneRepresentation(boolean hasStageOneRepresentation) {
        this.hasStageOneRepresentation = hasStageOneRepresentation;
    }

    public boolean hasStageTwoRepresentation() {
        return hasStageTwoRepresentation;
    }

    public void setHasStageTwoRepresentation(boolean hasStageTwoRepresentation) {
        this.hasStageTwoRepresentation = hasStageTwoRepresentation;
    }

    /**
     * Indicated if the study has at least two representations
     *
     * @return boolean true if there is a clinical and a stage one representation or if there is a stage one and stage two representation
     */
    public boolean isStaged() {
        if (!this.clinicalStudyInstanceUid.isEmpty()) {
            return hasStageOneRepresentation;
        } else {
            return hasStageTwoRepresentation();
        }
    }
}
