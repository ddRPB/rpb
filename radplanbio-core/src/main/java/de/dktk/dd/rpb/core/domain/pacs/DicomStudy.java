/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * RPB DICOM Study domain object to encapsulate data coming from PACS
 *
 * @author tomas@skripcak.net
 * @since 07 August 2013
 */
@XmlRootElement
public class DicomStudy implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(DicomStudy.class);

    //endregion

    //region Members

    private Integer id; // unused for this Transient entity

    private String studyInstanceUID;
    private String studyDescription;
    private String studyDate;
    private List<DicomSeries> studySeries;

    // Details for CRF item that is referencing this DICOM study
    private ItemDefinition crfItemDefinition;

    // Details for treatment plans
    private RtTreatmentCase rtTreatmentCase;

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public DicomStudy() {
       // NOOP
    }

    //endregion

    //region Properties

    //region Id

    @XmlTransient
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region StudyInstanceUID

    public String getStudyInstanceUID() {
        return this.studyInstanceUID;
    }

    public void setStudyInstanceUID(String value) {
        if (this.studyInstanceUID == null || !this.studyInstanceUID.equals(value)) {
            this.studyInstanceUID = value;
        }
    }

    //endregion

    //region StudyDescription

    public String getStudyDescription() {
        return this.studyDescription;
    }

    public void setStudyDescription(String value) {
        if (this.studyDescription == null || !this.studyDescription.equals(value)) {
            this.studyDescription = value;
        }
    }

    //endregion

    //region StudyDate

    public String getStudyDate() {
        return this.studyDate;
    }

    public void setStudyDate(String value) {
        this.studyDate = value;
    }

    public Date getDateStudy() {

        Date result = null;

        // Check whether DICOM study date is set
        if (this.studyDate != null && !this.studyDate.isEmpty() &&
            !this.studyDate.equals("00000000") &&
            !this.studyDate.equals("Unknown")) {
            DateFormat format = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);
            try {
                result = format.parse(this.studyDate);
            }
            catch (ParseException e) {
                log.error(e);
            }
        }
        // Set the RPB default DICOM date 01.01.1900
        else {
            Calendar defaultCalendar = Calendar.getInstance();
            defaultCalendar.set(Calendar.YEAR, 1900);
            defaultCalendar.set(Calendar.MONTH, 0);
            defaultCalendar.set(Calendar.DAY_OF_MONTH, 1);
            result = defaultCalendar.getTime();
        }

        return result;
    }

    public String getDateStudyString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getDateStudy();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region StudySeries

    public List<DicomSeries> getStudySeries() {
        return this.studySeries;
    }

    public void setStudySeries(List<DicomSeries> values) {
        this.studySeries = values;
    }

    //endregion

    //region CRF ItemDefinition

    public ItemDefinition getCrfItemDefinition() {
        return this.crfItemDefinition;
    }

    public void setCrfItemDefinition(ItemDefinition crfItemDefinition) {
        this.crfItemDefinition = crfItemDefinition;
    }

    //endregion

    //region RT TreatmentCase

    public RtTreatmentCase getRtTreatmentCase() {
        return this.rtTreatmentCase;
    }

    public void setRtTreatmentCase(RtTreatmentCase treatmentCase) {
        this.rtTreatmentCase = treatmentCase;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof DicomStudy && this.hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.studyInstanceUID);
    }

    /**
     * Construct a readable string representation for this Study instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("studyInstanceUid", this.studyInstanceUID)
                .add("studyDescription", this.studyDescription)
                .toString();
    }

    //endregion

    //region Methods

    /**
     * Study type depending on modality of study series
     *
     * @return string representation
     *
     */
    public String getStudyType() {
        String result;

        if (this.hasModality(Constants.DICOM_CT) &&
            this.hasModality(Constants.DICOM_RTSTRUCT) &&
            !this.hasModality(Constants.DICOM_RTPLAN) &&
            !this.hasModality(Constants.DICOM_RTDOSE)) {
            result = Constants.RPB_CONTOURING;
        }
        else if (this.hasModality(Constants.DICOM_RTSTRUCT) &&
            this.hasModality(Constants.DICOM_RTPLAN) &&
            this.hasModality(Constants.DICOM_RTDOSE)) {
            result = Constants.RPB_TREATMENTPLAN;
        }
        else if (this.hasModality(Constants.DICOM_PT)) {
            if (this.hasModality(Constants.DICOM_CT)) {
                result = Constants.RPB_PETCT;
            }
            else if (this.hasModality(Constants.DICOM_MR)) {
                result = Constants.RPB_PETMRI;
            }
            else {
                result = Constants.RPB_PET;
            }
        }
        else if (this.hasModality(Constants.DICOM_MR)) {
            result = Constants.RPB_MRI;
        }
        else if (this.hasModality(Constants.DICOM_CT)) {
            result = Constants.RPB_CT;
        }
        else if (this.hasModality(Constants.DICOM_US)) {
            result = Constants.RPB_US;
        }
        else if (this.hasModality(Constants.DICOM_ST)) {
            result = Constants.RPB_SPECT;
        }
        else {
            result = Constants.RPB_OTH;
        }

        return result;
    }

    public List<DicomSeries> getSeriesByModality(String modality) {
        List<DicomSeries> results = new ArrayList<>();

        if (this.studySeries != null) {
            for (DicomSeries ds : this.studySeries) {
                if (ds.getSeriesModality() != null && ds.getSeriesModality().trim().equals(modality)) {
                    results.add(ds);
                }
            }
        }

        return results;
    }

    //endregion

    //region Private Methods

    private boolean hasModality(String modality) {
        if (this.studySeries != null) {
            for (DicomSeries ds : this.studySeries) {
                if (ds.getSeriesModality() != null && ds.getSeriesModality().trim().equals(modality)) {
                    return true;
                }
            }
        }

        return false;
    }

    //endregion

}