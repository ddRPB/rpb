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

//TODO: consider merging or integrating with DicomRtStructureSet

/**
 * {@inheritDoc}
 */
public class RtStructDicomSeries extends DicomSeries {

    //region Members

    private String structureSetLabel;
    private String structureSetDate;
    private String structureSetDescription;
    private String structureSetName;

    //endregion

    //region Properties

    public String getStructureSetName() {
        return structureSetName;
    }

    public void setStructureSetName(String structureSetName) {
        this.structureSetName = structureSetName;
    }

    @XmlTransient
    public String getStructureSetDescription() {
        return structureSetDescription;
    }

    @Transient
    public void setStructureSetDescription(String structureSetDescription) {
        this.structureSetDescription = structureSetDescription;
    }

    @XmlTransient
    public String getStructureSetLabel() {
        return structureSetLabel;
    }

    @Transient
    public void setStructureSetLabel(String structureSetLabel) {
        this.structureSetLabel = structureSetLabel;
    }

    @XmlTransient
    public String getStructureSetDate() {
        return structureSetDate;
    }

    @Transient
    public void setStructureSetDate(String structureSetDate) {
        this.structureSetDate = structureSetDate;
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
            if (this.structureSetLabel != null && !"".equals(this.structureSetLabel)) {
                return this.structureSetLabel;
            }
            if (this.structureSetName != null && !"".equals(this.structureSetName)) {
                return this.structureSetName;
            }
            if (this.structureSetDescription != null && !"".equals(this.structureSetDescription)) {
                return this.structureSetDescription;
            }
        }
        
        return originalDescription;
    }

}
