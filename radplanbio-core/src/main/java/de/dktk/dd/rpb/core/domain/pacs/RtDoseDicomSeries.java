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

//TODO: Consider to merge this class with DicomRtDose

/**
 * {@inheritDoc}
 */
public class RtDoseDicomSeries extends DicomSeries {

    //region Members

    private String doseUnits;
    private String doseType;
    private String doseComment;
    private String doseSummationType;
    private String instanceCreationDate;

    //endregion

    //region Properties

    @XmlTransient
    public String getDoseUnits() {
        return doseUnits;
    }

    @Transient
    public void setDoseUnits(String doseUnits) {
        this.doseUnits = doseUnits;
    }

    @XmlTransient
    public String getDoseType() {
        return doseType;
    }

    @Transient
    public void setDoseType(String doseType) {
        this.doseType = doseType;
    }

    @XmlTransient
    public String getDoseComment() {
        return doseComment;
    }

    @Transient
    public void setDoseComment(String doseComment) {
        this.doseComment = doseComment;
    }

    @XmlTransient
    public String getDoseSummationType() {
        return doseSummationType;
    }

    @Transient
    public void setDoseSummationType(String doseSummationType) {
        this.doseSummationType = doseSummationType;
    }

    @XmlTransient
    public String getInstanceCreationDate() {
        return instanceCreationDate;
    }

    @Transient
    public void setInstanceCreationDate(String instanceCreationDate) {
        this.instanceCreationDate = instanceCreationDate;
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
            if (this.doseComment != null && !"".equals(this.doseComment)) {
                return this.doseComment;
            }
        }
        
        return originalDescription;
    }
}
