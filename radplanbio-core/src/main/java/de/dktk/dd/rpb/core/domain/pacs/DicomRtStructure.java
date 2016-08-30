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
 * Created by root on 12/1/15.
 */
public class DicomRtStructure {

    //region Members

    private int roiNumber;
    private String roiName;

    //private int observationNumber;
    private String rtRoiInterpretedType;
    //private String roiObservationLabel

    //endregion

    //region Properties

    public int getRoiNumber() {
        return this.roiNumber;
    }

    public void setRoiNumber(int number) {
        this.roiNumber = number;
    }

    public String getRoiName() {
        return this.roiName;
    }

    public void setRoiName(String roiName) {
        this.roiName = roiName;
    }

    public String getRtRoiInterpretedType() {
        return this.rtRoiInterpretedType;
    }

    public void setRtRoiInterpretedType(String roiInterpretedType) {
        this.rtRoiInterpretedType = roiInterpretedType;
    }

    //endregion

}