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

package de.dktk.dd.rpb.core.domain.bio;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.AnnotationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.List;

/**
 * AbstractSpecimen transient domain entity
 *
 * @author tomas@skripcak.net
 * @since 04 Jan 2017
 */
public class AbstractSpecimen implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AbstractSpecimen.class);

    //endregion

    //region Members

    protected Integer id;
    protected String sampleId;
    protected String secondaryId;

    protected String primaryContainer;
    protected SpecimenTypeEnum type;
    protected String kind;
    protected String location;
    protected String longTermStorage;

    protected Double amountInitial;
    protected String amountInitialUnit;
    protected Double amountRest;
    protected String amountRestUnit;

    protected XMLGregorianCalendar samplingDate;
    protected XMLGregorianCalendar repositionDate;

    protected Integer positionX;
    protected Integer positionY;

    protected boolean useSPREC;

    protected String comment;

    // Many-To-One
    protected Study study;
    protected PartnerSite partnerSite;
    protected Person patient;

    // One-To-Many
    protected List<AbstractSpecimen> children;

    //endregion

    //region Properties

    //region ID

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    //region Sample ID

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    //endregion

    //region Secondary ID

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    //endregion

    //region Primary Container

    public String getPrimaryContainer() {
        return primaryContainer;
    }

    public void setPrimaryContainer(String primaryContainer) {
        this.primaryContainer = primaryContainer;
    }

    //endregion

    //region Type

    public SpecimenTypeEnum getType() {
        return this.type;
    }

    public void setType(SpecimenTypeEnum type) {
        this.type = type;
    }

    //endregion

    //region Kind

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    //endregion

    //region Location

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //endregion

    //region LongTermStorage

    public String getLongTermStorage() {
        return longTermStorage;
    }

    public void setLongTermStorage(String longTermStorage) {
        this.longTermStorage = longTermStorage;
    }

    //endregion

    //region AmountInitial

    public Double getAmountInitial() {
        return amountInitial;
    }

    public void setAmountInitial(Double amountInitial) {
        this.amountInitial = amountInitial;
    }

    //endregion

    //region AmountInitialUnit

    public String getAmountInitialUnit() {
        return amountInitialUnit;
    }

    public void setAmountInitialUnit(String amountInitialUnit) {
        this.amountInitialUnit = amountInitialUnit;
    }

    //endregion

    //region AmountRest

    public Double getAmountRest() {
        return amountRest;
    }

    public void setAmountRest(Double amountRest) {
        this.amountRest = amountRest;
    }

    //endregion

    //region AmountRestUnit

    public String getAmountRestUnit() {
        return amountRestUnit;
    }

    public void setAmountRestUnit(String amountRestUnit) {
        this.amountRestUnit = amountRestUnit;
    }

    //endregion

    //region SamplingDate

    public XMLGregorianCalendar getSamplingDate() {
        return samplingDate;
    }

    public void setSamplingDate(XMLGregorianCalendar samplingDate) {
        this.samplingDate = samplingDate;
    }

    //endregion

    //region RepositionDate

    public XMLGregorianCalendar getRepositionDate() {
        return repositionDate;
    }

    public void setRepositionDate(XMLGregorianCalendar repositionDate) {
        this.repositionDate = repositionDate;
    }

    //endregion

    //region Position X

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    //endregion

    //region Position Y

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    //endregion

    //region Use SPREC

    public Boolean getUseSPREC() {
        return this.useSPREC;
    }

    public void setUseSPREC(Boolean value) {
        this.useSPREC = value;
    }

    //endregion

    //region Comment

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    //endregion

    //region Many-To-One

    //region Study

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    //region PartnerSite

    public PartnerSite getPartnerSite() {
        return partnerSite;
    }

    public void setPartnerSite(PartnerSite partnerSite) {
        this.partnerSite = partnerSite;
    }

    //endregion

    //region Patient

    public Person getPatient() {
        return patient;
    }

    public void setPatient(Person patient) {
        this.patient = patient;
    }

    //endregion

    //endregion

    //region One-To-Many

    //region Children

    public List<AbstractSpecimen> getChildren() {
        return children;
    }

    public void setChildren(List<AbstractSpecimen> children) {
        this.children = children;
    }

    public boolean addChild(AbstractSpecimen bs) {
        return !this.children.contains(bs) && this.children.add(bs);

    }

    public boolean removeChild(AbstractSpecimen bs) {
        return this.children.contains(bs) && this.children.remove(bs);
    }

    //endregion

    //endregion

    //endregion

    //region Methods

    public void initByAnnotations(List<AnnotationType> annotations) {
        // Sample use SPREC
        this.useSPREC = this.annotationExists("BIO_SPECIMEN_SPREC", annotations);

        // Liquid Sample Type
        if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID", annotations)) {
            this.type = SpecimenTypeEnum.LIQUID;

            // Sample Liquid Kind
            if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_SER", annotations)) {
                this.kind = "SER";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_RBC", annotations)) {
                this.kind = "RBC";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_PL1", annotations)) {
                this.kind = "PL1";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_PL2", annotations)) {
                this.kind = "PL2";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_PAXGENE-DNA", annotations)) {
                this.kind = "BLD_PAXGENE-DNA";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_PAXGENE-RNA", annotations)) {
                this.kind = "BLD_PAXGENE-RNA";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_LIHE", annotations)) {
                this.kind = "BLD_LIHE";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_EDTA", annotations)) {
                this.kind = "BLD_EDTA";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_CITRAT", annotations)) {
                this.kind = "BLD_CITRAT";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_CELLFREE_DNA", annotations)) {
                this.kind = "BLD_CELLFREE_DNA";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD_CELLFREE_RNA", annotations)) {
                this.kind = "BLD_CELLFREE_RNA";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BLD", annotations)) {
                this.kind = "BLD";
            }
            else if (this.annotationExists("BIO_SPECIMEN_TYPE_LIQUID_KIND_BFF", annotations)) {
                this.kind = "BFF";
            }
        }
        // Tissue Sample Type
        else if (this.annotationExists("BIO_SPECIMEN_TYPE_TISSUE", annotations)) {
            this.type = SpecimenTypeEnum.TISSUE;

            // Sample Tissue Kind
            if (this.annotationExists("BIO_SPECIMEN_TYPE_TISSUE_KIND_TIS_TUMOR", annotations)) {
                this.kind = "TIS_TUMOR";
            }
        }

        // Primary Container
        if (this.annotationExists("BIO_SPECIMEN_PCT_CAT_SARSTEDT", annotations)) {
            this.primaryContainer = "CAT_SARSTEDT";
        }
        else if (this.annotationExists("BIO_SPECIMEN_PCT_PED_SARSTEDT", annotations)) {
            this.primaryContainer = "PED_SARSTEDT";
        }

        // Sample Container (long term storage LTS)
        if (this.annotationExists("BIO_SPECIMEN_LTS_A_MATRIX_THERMO_0.5ML_M80", annotations)) {
            this.longTermStorage = "A_MATRIX_THERMO_0.5ML_M80";
        }
        else if (this.annotationExists("BIO_SPECIMEN_LTS_A_MATRIX_THERMO_1.0ML_M80", annotations)) {
            this.longTermStorage = "A_MATRIX_THERMO_1.0ML_M80";
        }
        else if (this.annotationExists("BIO_SPECIMEN_LTS_A_MATRIX_THERMO_2.0ML_M80", annotations)) {
            this.longTermStorage = "A_MATRIX_THERMO_2.0ML_M80";
        }
        else if (this.annotationExists("BIO_SPECIMEN_LTS_Y_ORIGINALCONTAINER", annotations)) {
            this.longTermStorage = "Y_ORIGINALCONTAINER";
        }
    }

    //endregion

    //region Private Methods

    private boolean annotationExists(String annotationTypeName, List<AnnotationType> annotations) {
        if (annotationTypeName != null && annotations != null) {
            for (AnnotationType annotationType : annotations) {
                if (annotationType.getName().equals(annotationTypeName)) {
                    return true;
                }
            }
        }

        return false;
    }

    //endregion

}
