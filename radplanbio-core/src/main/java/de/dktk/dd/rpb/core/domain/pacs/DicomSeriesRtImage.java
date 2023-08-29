/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.DicomDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * {@inheritDoc}
 */
public class DicomSeriesRtImage extends DicomSeries {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomSeriesRtImage.class);

    //endregion

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
        return this.instanceCreationDate;
    }

    @Transient
    public void setInstanceCreationDate(String instanceCreationDate) {
        this.instanceCreationDate = instanceCreationDate;
    }

    //endregion

    @Override
    public String getSeriesDate() {
        return this.instanceCreationDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDateSeries() {

        Date result = null;

        try {
            result = DicomDateUtil.convertStringDateToDate(this.instanceCreationDate);
        }
        catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateSeriesString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getDateSeries();
        return date != null ? format.format(date) : null;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSeriesMetaParameterString() {
        String description = super.getSeriesMetaParameterString();

        if (!rtImageName.isEmpty()) {
            description += RTIMAGE_NAME + ": " + rtImageName + "; ";
        }

        if (!rtImageLabel.isEmpty()) {
            description += RTIMAGE_LABEL + ": " + rtImageLabel + "; ";
        }

        if (!rtImageDescription.isEmpty()) {
            description += RTIMAGE_DESCRIPTION + ": " + rtImageDescription + "; ";
        }

        return description.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSeriesMetaParameterList() {
        List<String> metaParameterList = super.getSeriesMetaParameterList();

        if (!rtImageName.isEmpty()) {
            metaParameterList.add(RTIMAGE_NAME + ": " + rtImageName + "; ");
        }

        if (!rtImageLabel.isEmpty()) {
            metaParameterList.add(RTIMAGE_LABEL + ": " + rtImageLabel + "; ");
        }

        if (!rtImageDescription.isEmpty()) {
            metaParameterList.add(RTIMAGE_DESCRIPTION + ": " + rtImageDescription + "; ");
        }

        return metaParameterList;
    }
}
