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

//TODO: Consider creating new class DicomRtImage

/**
 * {@inheritDoc}
 */
public class RtImageDicomSeries extends DicomSeries {

    //region Members

    private String rtImageLabel;
    private String rtImageName;
    private String rtImageDescription;
    private String instanceCreationDate;

    //endregion

    //region Properties

    @XmlTransient
    public String getRtImageLabel() {
        return rtImageLabel;
    }

    @Transient
    public void setRtImageLabel(String rtImageLabel) {
        this.rtImageLabel = rtImageLabel;
    }

    @XmlTransient
    public String getRtImageName() {
        return rtImageName;
    }

    @Transient
    public void setRtImageName(String rtImageName) {
        this.rtImageName = rtImageName;
    }

    @XmlTransient
    public String getRtImageDescription() {
        return rtImageDescription;
    }

    @Transient
    public void setRtImageDescription(String rtImageDescription) {
        this.rtImageDescription = rtImageDescription;
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
            if (this.rtImageName != null && !"".equals(this.rtImageName)) {
                return this.rtImageName;
            }
            if (this.rtImageLabel != null && !"".equals(this.rtImageLabel)) {
                return this.rtImageLabel;
            }
            if (this.rtImageDescription != null && !"".equals(this.rtImageDescription)) {
                return this.rtImageDescription;
            }
        }
        
        return originalDescription;
    }
}
