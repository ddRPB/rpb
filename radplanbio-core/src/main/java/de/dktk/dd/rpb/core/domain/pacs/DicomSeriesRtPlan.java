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
public class DicomSeriesRtPlan extends DicomSeries {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomSeriesRtPlan.class);

    //endregion

    //region Members

    private String rtPlanLabel;
    private String rtPlanManufacturerModelName;
    private String rtPlanName;
    private String rtPlanDate;
    private String manufacturer;
    private String rtPlanDescription;
    private String rtPlanGeometry;
    private float rxDose;

    //endregion

    //region Constructors

    public DicomSeriesRtPlan() {
        this.rxDose = 0;
    }

    //endregion

    //region Properties

    @XmlTransient
    public String getRtPlanLabel() {
        return this.rtPlanLabel;
    }

    @Transient
    public void setRtPlanLabel(String rtPlanLabel) {
        this.rtPlanLabel = rtPlanLabel;
    }

    @XmlTransient
    public String getRtPlanManufacturerModelName() {
        return rtPlanManufacturerModelName;
    }

    @Transient
    public void setRtPlanManufacturerModelName(String rtPlanManufacturerModelName) {
        this.rtPlanManufacturerModelName = rtPlanManufacturerModelName;
    }

    @XmlTransient
    public String getRtPlanName() {
        return this.rtPlanName;
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

    @XmlTransient
    public String getManufacturer() {
        return manufacturer;
    }

    @Transient
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @XmlTransient
    public String getRtPlanDescription() {
        return rtPlanDescription;
    }

    @Transient
    public void setRtPlanDescription(String rtPlanDescription) {
        this.rtPlanDescription = rtPlanDescription;
    }

    @XmlTransient
    public String getRtPlanGeometry() {
        return rtPlanGeometry;
    }

    @Transient
    public void setRtPlanGeometry(String rtPlanGeometry) {
        this.rtPlanGeometry = rtPlanGeometry;
    }

    @XmlTransient
    public float getRxDose() {
        return this.rxDose;
    }

    @Transient
    public void setRxDose(float rxDose) {
        this.rxDose = rxDose;
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

    @Override
    public String getSeriesDate() {
        return this.rtPlanDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDateSeries() {

        Date result = null;

        try {
            result = DicomDateUtil.convertStringDateToDate(this.rtPlanDate);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSeriesMetaParameterString() {
        String description = super.getSeriesMetaParameterString();

        if (!rtPlanLabel.isEmpty()) {
            description += RTPLAN_LABEL + ": " + rtPlanLabel + "; ";
        }

        if (!rtPlanManufacturerModelName.isEmpty()) {
            description += RTPLAN_MANUFACTURER_MODEL_NAME + ": " + rtPlanManufacturerModelName + "; ";
        }

        if (!manufacturer.isEmpty()) {
            description += RTPLAN_MANUFACTURER + ": " + manufacturer + "; ";
        }

        if (!rtPlanName.isEmpty()) {
            description += RTPLAN_NAME + ": " + rtPlanName + "; ";
        }

        if (!rtPlanDate.isEmpty()) {
            description += RTPLAN_DATE + ": " + rtPlanDate + "; ";
        }

        if (!rtPlanDescription.isEmpty()) {
            description += RTPLAN_DESCRIPTION + ": " + rtPlanDescription + "; ";
        }

        if (!rtPlanGeometry.isEmpty()) {
            description += RTPLAN_GEOMETRY + ": " + rtPlanGeometry + "; ";
        }

        return description.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSeriesMetaParameterList() {
        List<String> metaParameterList = super.getSeriesMetaParameterList();

        if (!rtPlanLabel.isEmpty()) {
            metaParameterList.add(RTPLAN_LABEL + ": " + rtPlanLabel + "; ");
        }

        if (!rtPlanManufacturerModelName.isEmpty()) {
            metaParameterList.add(RTPLAN_MANUFACTURER_MODEL_NAME + ": " + rtPlanManufacturerModelName + "; ");
        }

        if (!manufacturer.isEmpty()) {
            metaParameterList.add(RTPLAN_MANUFACTURER + ": " + manufacturer + "; ");
        }

        if (!rtPlanName.isEmpty()) {
            metaParameterList.add(RTPLAN_NAME + ": " + rtPlanName + "; ");
        }

        if (!rtPlanDate.isEmpty()) {
            metaParameterList.add(RTPLAN_DATE + ": " + rtPlanDate + "; ");
        }

        if (!rtPlanDescription.isEmpty()) {
            metaParameterList.add(RTPLAN_DESCRIPTION + ": " + rtPlanDescription + "; ");
        }

        if (!rtPlanGeometry.isEmpty()) {
            metaParameterList.add(RTPLAN_GEOMETRY + ": " + rtPlanGeometry + "; ");
        }

        return metaParameterList;
    }
}
