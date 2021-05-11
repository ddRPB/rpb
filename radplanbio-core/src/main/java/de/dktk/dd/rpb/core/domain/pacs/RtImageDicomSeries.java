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
public class RtImageDicomSeries extends DicomSeries {
    private String rtImageLabel;
    private String rtImageName;
    private String rtImageDescription;
    private String instanceCreationDate;


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
            if (rtImageName != null && !"".equals(rtImageName)) {
                return rtImageName;
            }
            if (rtImageLabel != null && !"".equals(rtImageLabel)) {
                return rtImageLabel;
            }
            if (rtImageDescription != null && !"".equals(rtImageDescription)) {
                return rtImageDescription;
            }
        }
        return originalDescription;
    }
}
