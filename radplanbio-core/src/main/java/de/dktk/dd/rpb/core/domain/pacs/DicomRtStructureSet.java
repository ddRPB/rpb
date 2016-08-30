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

import java.util.List;

/**
 * Created by root on 12/1/15.
 */
public class DicomRtStructureSet {

    //region Members

    private String label;
    //date
    //time
    private List<DicomRtStructure> structures;

    //endregion

    //region Properties

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<DicomRtStructure> getStructures() {
        return this.structures;
    }

    public void setStructures(List<DicomRtStructure> structures) {
        this.structures = structures;
    }

    public int getStructuresCount() {
        if (this.structures != null) {
            return this.structures.size();
        }

        return 0;
    }

    public String getStructuresString() {
        String result = "";
        if (this.structures != null) {
            for (DicomRtStructure struct : this.structures) {
                if (result.equals("")) {
                    result = struct.getRoiName();
                }
                else {
                    result = result + "\n" + struct.getRoiName();
                }
            }
        }

        return result;
    }

    //endregion

    //region Methods

    public DicomRtStructure getRtStructureByRoiNumber(int roiNumber) {
        if (this.structures != null) {
            for (DicomRtStructure rts : this.structures) {
                if (rts.getRoiNumber() == roiNumber) {
                    return rts;
                }
            }
        }

        return null;
    }

    //endregion

}