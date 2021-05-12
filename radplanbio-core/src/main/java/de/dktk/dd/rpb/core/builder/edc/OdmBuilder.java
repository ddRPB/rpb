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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class OdmBuilder implements IOdmBuilder {

    //region Finals

    private static final Logger log = Logger.getLogger(OdmBuilder.class);
    private final Odm odm = new Odm();
    private final List<ClinicalData> clinicalDataList = new ArrayList<>();

    // endregion
    // region constructor

    private OdmBuilder() {
    }

    public static OdmBuilder getInstance() {
        return new OdmBuilder();
    }

    // endregion

    /**
     * Building the Odm instance out of the current configuration properties.
     *
     * @return Odm
     */
    public Odm build() {
        if (clinicalDataList.size() > 0) {
            odm.setClinicalDataList(this.clinicalDataList);
        }
        return odm;
    }

    /**
     * Adding an additional ClinicalData to the Odm
     *
     * @param clinicalData ClinicalData
     * @return OdmBuilder
     */
    public OdmBuilder addClinicalData(ClinicalData clinicalData) {
        if (this.clinicalDataList.indexOf(clinicalData) < 0) {
            this.clinicalDataList.add(clinicalData);
        } else {
            log.warn("Clinical data object (" + clinicalData.toString() + ") already exists on the clinicalDataList. Skip adding it again");
        }
        return this;
    }
}
