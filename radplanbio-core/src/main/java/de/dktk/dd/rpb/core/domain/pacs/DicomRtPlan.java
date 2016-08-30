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
public class DicomRtPlan {

    //region Members

    private String label;
    private String name;
    // date
    // time
    // geometry
    private float rxDose;

    //endregion

    //region Constructors

    public DicomRtPlan() {
        this.rxDose = 0;
    }

    //endregion

    //region Properties

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRxDose() {
        return this.rxDose;
    }

    public void setRxDose(float rxDose) {
        this.rxDose = rxDose;
    }

    //endregion

}