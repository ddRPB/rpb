/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

//TODO: consider merging or integrating with DicomRtPlan

/**
 * {@inheritDoc}
 */
public class RtPlanDicomSeries extends DicomSeries {

    //region Members

    private String rtPlanLabel;
    private String rtPlanName;
    private String rtPlanDate;
    private String rtPlanDescription;

    //endregion

    //region Properties

    @XmlTransient
    public String getRtPlanDescription() {
        return rtPlanDescription;
    }

    @Transient
    public void setRtPlanDescription(String rtPlanDescription) {
        this.rtPlanDescription = rtPlanDescription;
    }

    @XmlTransient
    public String getRtPlanLabel() {
        return rtPlanLabel;
    }

    @Transient
    public void setRtPlanLabel(String rtPlanLabel) {
        this.rtPlanLabel = rtPlanLabel;
    }

    @XmlTransient
    public String getRtPlanName() {
        return rtPlanName;
    }

    @Transient
    public void setRtPlanName(String rtPlanName) {
        this.rtPlanName = rtPlanName;
    }

    @XmlTransient
    public String getRtPlanDate() {
        return rtPlanDate;
    }

    @Transient
    public void setRtPlanDate(String rtPlanDate) {
        this.rtPlanDate = rtPlanDate;
    }

    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserViewSeriesDescription() {
        String description = super.getUserViewSeriesDescription();
        description = replaceEmptyDescriptionIfNecessary(description);
        return description;
    }

    private String replaceEmptyDescriptionIfNecessary(String originalDescription) {
        if ("".equals(originalDescription)) {
            if (this.rtPlanLabel != null && !"".equals(this.rtPlanLabel)) {
                return this.rtPlanLabel;
            }
            if (this.rtPlanName != null && !"".equals(this.rtPlanName)) {
                return this.rtPlanName;
            }
            if (this.rtPlanDescription != null && !"".equals(this.rtPlanDescription)) {
                return this.rtPlanDescription;
            }
        }
        
        return originalDescription;
    }

}
