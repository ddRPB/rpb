/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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
 * Created by root on 12/2/15.   Cumulative DVH
 */
public class DicomRtDvh {

    //region Enums

    public enum DVHSOURCE {
        PROVIDED, RPB_CALCUALTED
    }

    //endregion

    //region Members

    private int referencedRoiNumber;
    private String type;
    private String doseUnit;
    private String doseType;
    private double dvhDoseScaling;
    private String dvhVolumeUnit;
    private int dvhNumberOfBins;
    private double dvhMinimumDose;
    private double dvhMaximumDose;
    private double dvhMeanDose;
    private double[] dvhData;   // Careful "filler" values are included in DVH data array (even values are DVH values)

    //endregion

    //region Constructors

    public DicomRtDvh() {

    }

    //endregion

    //region Properties

    public int getReferencedRoiNumber() {
        return referencedRoiNumber;
    }

    public void setReferencedRoiNumber(int referencedRoiNumber) {
        this.referencedRoiNumber = referencedRoiNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoseUnit() {
        return doseUnit;
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

    public double getDvhDoseScaling() {
        return dvhDoseScaling;
    }

    public void setDvhDoseScaling(double dvhDoseScaling) {
        this.dvhDoseScaling = dvhDoseScaling;
    }

    public String getDvhVolumeUnit() {
        return dvhVolumeUnit;
    }

    public void setDvhVolumeUnit(String dvhVolumeUnit) {
        this.dvhVolumeUnit = dvhVolumeUnit;
    }

    public int getDvhNumberOfBins() {
        return dvhNumberOfBins;
    }

    public void setDvhNumberOfBins(int dvhNumberOfBins) {
        this.dvhNumberOfBins = dvhNumberOfBins;
    }

    public double getDvhMinimumDose() {
        return dvhMinimumDose;
    }

    public void setDvhMinimumDose(double dvhMinimumDose) {
        this.dvhMinimumDose = dvhMinimumDose;
    }

    public double getDvhMaximumDose() {
        return dvhMaximumDose;
    }

    public void setDvhMaximumDose(double dvhMaximumDose) {
        this.dvhMaximumDose = dvhMaximumDose;
    }

    public double getDvhMeanDose() {
        return dvhMeanDose;
    }

    public void setDvhMeanDose(double dvhMeanDose) {
        this.dvhMeanDose = dvhMeanDose;
    }

    public double[] getDvhData() {
        return dvhData;
    }

    public void setDvhData(double[] dvhData) {
        this.dvhData = dvhData;
    }

    //endregion

}