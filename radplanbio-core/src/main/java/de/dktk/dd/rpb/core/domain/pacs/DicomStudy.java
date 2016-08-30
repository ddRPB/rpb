/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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
    private List<DicomSerie> studySeries;

    //TODO: it is maybe a good idea to reference the ItemDefinition from ODM that actually holds the DICOM study in RPB
    private ItemDefinition crfItemDefinition;

    private String eCrfLabel;
    private String eCrfDescription;

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
        if (this.studyDate != null && !this.studyDate.equals("")) {
            DateFormat format = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);
            try {
                return format.parse(this.studyDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getDateStudyString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getDateStudy();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region StudySeries

    public List<DicomSerie> getStudySeries() {
        return this.studySeries;
    }

    public void setStudySeries(List<DicomSerie> values) {
        this.studySeries = values;
    }

    //endregion

    public ItemDefinition getCrfItemDefinition() {
        return this.crfItemDefinition;
    }

    public void setCrfItemDefinition(ItemDefinition crfItemDefinition) {
        this.crfItemDefinition = crfItemDefinition;
    }

    public String geteCrfLabel() {
        return this.eCrfLabel;
    }

    public void seteCrfLabel(String value) {
        if (this.eCrfLabel == null || !this.eCrfLabel.equals(value)) {
            this.eCrfLabel = value;
        }
    }

    public String geteCrfDescription() {
        return this.eCrfDescription;
    }

    public void seteCrfDescription(String value) {
        if (this.eCrfDescription == null || !this.eCrfDescription.equals(value)) {
            this.eCrfDescription = value;
        }
    }

    public RtTreatmentCase getRtTreatmentCase() {
        return this.rtTreatmentCase;
    }

    public void setRtTreatmentCase(RtTreatmentCase treatmentCase) {
        this.rtTreatmentCase = treatmentCase;
    }

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

        if (this.hasModality("RTSTRUCT") &&
                this.hasModality("RTPLAN") &&
                this.hasModality("RTDOSE")) {
            result = "TP";
        }
        else if (this.hasModality("PT")) {
            if (this.hasModality("CT")) {
                result = "PET-CT";
            }
            else if (this.hasModality("MR")) {
                result = "PET-MRI";
            }
            else {
                result = "PET";
            }
        }
        else if (this.hasModality("MR")) {
            result = "MRI";
        }
        else if (this.hasModality("CT")) {
            result = "CT";
        }
        else if (this.hasModality("US")) {
            result = "US";
        }
        else if (this.hasModality("ST")) {
            result = "SPECT";
        }
        else {
            result = "OTH";
        }

        return result;
    }

    public List<DicomSerie> getSeriesByModality(String modality) {
        List<DicomSerie> results = new ArrayList<>();

        if (this.studySeries != null) {
            for (DicomSerie ds : this.studySeries) {
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
            for (DicomSerie ds : this.studySeries) {
                if (ds.getSeriesModality() != null && ds.getSeriesModality().trim().equals(modality)) {
                    return true;
                }
            }
        }

        return false;
    }

    //endregion

}