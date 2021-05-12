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

package de.dktk.dd.rpb.core.domain.pacs;

/**
 * DicomSeries that could be represented by different stages in PACS for research.
 * It is used especially for views that show representations of the study data in different stages.
 */
public class StagedDicomSeries extends DicomSeries {
    private boolean stageOneRepresentation = false;
    private boolean stageTwoRepresentation = false;
    private String clinicalSeriesUid = "";
    private String stageOneSeriesUid = "";
    private String stageTwoSeriesUid = "";

    public StagedDicomSeries() {
        super();
    }

    public String getClinicalSeriesUid() {
        return clinicalSeriesUid;
    }

    public void setClinicalSeriesUid(String clinicalSeriesUid) {
        this.clinicalSeriesUid = clinicalSeriesUid;
    }

    public String getStageTwoSeriesUid() {
        return stageTwoSeriesUid;
    }

    public void setStageTwoSeriesUid(String stageTwoSeriesUid) {
        this.stageTwoSeriesUid = stageTwoSeriesUid;
    }

    public String getStageOneSeriesUid() {
        return stageOneSeriesUid;
    }

    public void setStageOneSeriesUid(String stageOneSeriesUid) {
        this.stageOneSeriesUid = stageOneSeriesUid;
    }

    public boolean isStageOneRepresentation() {
        return stageOneRepresentation;
    }

    public void setStageOneRepresentation(boolean stageOneRepresentation) {
        this.stageOneRepresentation = stageOneRepresentation;
    }

    public boolean isStageTwoRepresentation() {
        return stageTwoRepresentation;
    }

    public void setStageTwoRepresentation(boolean stageTwoRepresentation) {
        this.stageTwoRepresentation = stageTwoRepresentation;
    }

    /**
     * Is true if a Dicom series has a clinical and stage one representation or
     * a stage one and a stage two representation.
     *
     * @return boolean
     */
    public boolean isStaged() {
        boolean isStaged = false;

        if (!this.clinicalSeriesUid.isEmpty()) {
            isStaged = isStageOneRepresentation();
        } else {
            isStaged = isStageTwoRepresentation();
        }

        return isStaged;
    }
}
