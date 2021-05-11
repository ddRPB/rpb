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

package de.dktk.dd.rpb.core.domain.pacs;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * {@inheritDoc}
 */
public class RtStructDicomSeries extends DicomSeries {
    private String structureSetLabel;
    private String structureSetDate;
    private String structureSetDescription;
    private String structureSetName;

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
            if (structureSetLabel != null && !"".equals(structureSetLabel)) {
                return structureSetLabel;
            }
            if (structureSetName != null && !"".equals(structureSetName)) {
                return structureSetName;
            }
            if (structureSetDescription != null && !"".equals(structureSetDescription)) {
                return structureSetDescription;
            }
        }
        return originalDescription;
    }

}
