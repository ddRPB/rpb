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
public class DicomSeriesRtDose extends DicomSeries {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomSeriesRtDose.class);

    //endregion

    //region Members

    private String sopInstanceUid;
    private String doseUnits = "";
    private String doseType = "";
    private String doseComment = "";
    private String doseSummationType = "";
    private String instanceCreationDate = "";

    private double doseGridScaling;
    private double doseMax;

    private DicomSeriesRtPlan rtPlan;
    private List<DicomRtDvh> rtDvhs;

    //endregion

    //region Properties

    @XmlTransient
    public String getSopInstanceUid() {
        return sopInstanceUid;
    }

    @Transient
    public void setSopInstanceUid(String sopInstanceUid) {
        this.sopInstanceUid = sopInstanceUid;
    }

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

    @XmlTransient
    public double getDoseGridScaling() {
        return doseGridScaling;
    }

    @Transient
    public void setDoseGridScaling(double doseGridScaling) {
        this.doseGridScaling = doseGridScaling;
    }

    @XmlTransient
    public double getDoseMax() {
        return doseMax;
    }

    @Transient
    public void setDoseMax(double doseMax) {
        this.doseMax = doseMax;
    }

    @XmlTransient
    public DicomSeriesRtPlan getRtPlan() {
        return rtPlan;
    }

    @Transient
    public void setRtPlan(DicomSeriesRtPlan rtPlan) {
        this.rtPlan = rtPlan;
    }

    @XmlTransient
    public List<DicomRtDvh> getRtDvhs() {
        return rtDvhs;
    }

    @Transient
    public void setRtDvhs(List<DicomRtDvh> rtDvhs) {
        this.rtDvhs = rtDvhs;
    }

    @XmlTransient
    public int getRtDvhsCount() {
        if (this.rtDvhs != null) {
            return this.rtDvhs.size();
        }

        return 0;
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
            if (this.doseComment != null && !"".equals(this.doseComment)) {
                return this.doseComment;
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

        if (!doseComment.isEmpty()) {
            description += DOSE_COMMENT + ": " + doseComment + "; ";
        }

        if (!doseSummationType.isEmpty()) {
            description += DOSE_SUMMATION_TYPE + ": " + doseSummationType + "; ";
        }

        if (!doseUnits.isEmpty()) {
            description += DOSE_UNIT + ": " + doseUnits + "; ";
        }

        if (!doseType.isEmpty()) {
            description += DOSE_TYPE + ": " + doseType + "; ";
        }

        return description.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSeriesMetaParameterList() {
        List<String> metaParameterList = super.getSeriesMetaParameterList();

        if (!doseComment.isEmpty()) {
            metaParameterList.add(DOSE_COMMENT + ": " + doseComment + "; ");
        }

        if (!doseSummationType.isEmpty()) {
            metaParameterList.add(DOSE_SUMMATION_TYPE + ": " + doseSummationType + "; ");
        }

        if (!doseUnits.isEmpty()) {
            metaParameterList.add(DOSE_UNIT + ": " + doseUnits + "; ");
        }

        if (!doseType.isEmpty()) {
            metaParameterList.add(DOSE_TYPE + ": " + doseType + "; ");
        }

        return metaParameterList;
    }

}
