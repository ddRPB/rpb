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
public class RtTreatmentCase {

    //region Members

    private List<DicomRtStructureSet> rtStructureSets;
    private List<DicomRtPlan> rtPlans;
    private List<DicomRtDose> rtDoses;

    //endregion

    //region Properties

    public List<DicomRtStructureSet> getRtStructureSets() {
        return this.rtStructureSets;
    }

    public void setRtStructureSets(List<DicomRtStructureSet> rtStructureSets) {
        this.rtStructureSets = rtStructureSets;
    }

    public List<DicomRtPlan> getRtPlans() {
        return this.rtPlans;
    }

    public void setRtPlans(List<DicomRtPlan> rtPlans) {
        this.rtPlans = rtPlans;
    }

    public List<DicomRtDose> getRtDoses() {
        return rtDoses;
    }

    public void setRtDoses(List<DicomRtDose> rtDoses) {
        this.rtDoses = rtDoses;
    }

    //endregion

}