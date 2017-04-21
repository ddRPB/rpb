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
 * Created by root on 9/8/16.
 */
public class DicomRtStructurePlane {

    //region Members

    private String referencedSOPInstanceUID;
    private String geometricType;
    private int numberOfContourPoints;

    //endregion

    //region Properties

    public String getReferencedSOPInstanceUID() {
        return referencedSOPInstanceUID;
    }

    public void setReferencedSOPInstanceUID(String referencedSOPInstanceUID) {
        this.referencedSOPInstanceUID = referencedSOPInstanceUID;
    }

    public String getGeometricType() {
        return geometricType;
    }

    public void setGeometricType(String geometricType) {
        this.geometricType = geometricType;
    }

    public int getNumberOfContourPoints() {
        return numberOfContourPoints;
    }

    public void setNumberOfContourPoints(int numberOfContourPoints) {
        this.numberOfContourPoints = numberOfContourPoints;
    }

    //endregion

}
