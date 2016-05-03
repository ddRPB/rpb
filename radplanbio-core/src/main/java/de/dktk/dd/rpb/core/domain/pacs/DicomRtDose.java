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

package de.dktk.dd.rpb.core.domain.pacs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/2/15.
 */
public class DicomRtDose {

    //region Members

    private String sopInstanceUid;
    private String doseUnit;
    private String doseType;
    private String doseSummationType;
    private double doseGridScaling;
    private double doseMax;

    private DicomRtPlan rtPlan;
    private List<DicomRtDvh> rtDvhs;

    //endregion

    //region Properties

    public String getSopInstanceUid() {
        return sopInstanceUid;
    }

    public void setSopInstanceUid(String sopInstanceUid) {
        this.sopInstanceUid = sopInstanceUid;
    }

    public String getDoseUnit() {
        return this.doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public String getDoseType() {
        return doseType;
    }

    public void setDoseType(String doseType) {
        this.doseType = doseType;
    }

    public String getDoseSummationType() {
        return doseSummationType;
    }

    public void setDoseSummationType(String doseSummationType) {
        this.doseSummationType = doseSummationType;
    }

    public double getDoseGridScaling() {
        return doseGridScaling;
    }

    public void setDoseGridScaling(double doseGridScaling) {
        this.doseGridScaling = doseGridScaling;
    }

    public double getDoseMax() {
        return doseMax;
    }

    public void setDoseMax(double doseMax) {
        this.doseMax = doseMax;
    }

    public DicomRtPlan getRtPlan() {
        return rtPlan;
    }

    public void setRtPlan(DicomRtPlan rtPlan) {
        this.rtPlan = rtPlan;
    }

    public List<DicomRtDvh> getRtDvhs() {
        return rtDvhs;
    }

    public void setRtDvhs(List<DicomRtDvh> rtDvhs) {
        this.rtDvhs = rtDvhs;
    }

    public int getRtDvhsCount() {
        if (this.rtDvhs != null) {
            return this.rtDvhs.size();
        }

        return 0;
    }

    //endregion

}
