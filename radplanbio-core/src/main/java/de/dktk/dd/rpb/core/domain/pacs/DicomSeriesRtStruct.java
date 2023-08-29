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
public class DicomSeriesRtStruct extends DicomSeries {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomSeriesRtStruct.class);

    //endregion

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
        return this.structureSetDate;
    }

    @Transient
    public void setStructureSetDate(String structureSetDate) {
        this.structureSetDate = structureSetDate;
    }

    //endregion

    @Override
    public String getSeriesDate() {
        return this.structureSetDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDateSeries() {

        Date result = null;

        try {
            result = DicomDateUtil.convertStringDateToDate(this.structureSetDate);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSeriesMetaParameterString() {
        String description = super.getSeriesMetaParameterString();

        if (!structureSetLabel.isEmpty()) {
            description += RTSTRUCT_STRUCTURE_SET_LABEL + ": " + structureSetLabel + "; ";
        }

        if (!structureSetName.isEmpty()) {
            description += RTSTRUCT_STRUCTURE_SET_NAME + ": " + structureSetName + "; ";
        }

        if (!structureSetDescription.isEmpty()) {
            description += RTSTRUCT_STRUCTURE_SET_DESCRIPTION + ": " + structureSetDescription + "; ";
        }

        if (!structureSetDate.isEmpty()) {
            description += RTSTRUCT_STRUCTURE_SET_DATE + ": " + structureSetDate + "; ";
        }

        return description.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSeriesMetaParameterList() {
        List<String> metaParameterList = super.getSeriesMetaParameterList();

        if (!structureSetLabel.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_LABEL + ": " + structureSetLabel + "; ");
        }

        if (!structureSetName.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_NAME + ": " + structureSetName + "; ");
        }

        if (!structureSetDescription.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_DESCRIPTION + ": " + structureSetDescription + "; ");
        }

        if (!structureSetDate.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_DATE + ": " + structureSetDate + "; ");
        }

        return metaParameterList;
    }



}
