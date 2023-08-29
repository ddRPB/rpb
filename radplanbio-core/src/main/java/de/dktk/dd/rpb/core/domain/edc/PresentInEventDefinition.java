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

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.adapter.NoYesBooleanAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * PresentInEventDefinition transient domain entity (CDISC ODM OC extension)
 *
 * Provides extra information whether specific form definition is present in event definition
 *
 * @author tomas@skripcak.net
 * @since 04 Jul 2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PresentInEventDefinition", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class PresentInEventDefinition implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(PresentInEventDefinition.class);

    //endregion

    //region Members

    @XmlAttribute(name="StudyEventOID")
    private String studyEventOid;

    @XmlAttribute(name="SourceDataVerification")
    private String sourceDataVerification;

    @XmlAttribute(name="IsDefaultVersion")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean isDefaultVersion;

    @XmlAttribute(name="PasswordRequired")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean passwordRequired;

    @XmlAttribute(name="DoubleDataEntry")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean doubleDataEntry;

    @XmlAttribute(name="HideCRF")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean hideCrf;

    @XmlAttribute(name="ParticipantForm")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean participantForm;

    //endregion

    //region Constructors

    public PresentInEventDefinition() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getStudyEventOid() {
        return studyEventOid;
    }

    public void setStudyEventOid(String studyEventOid) {
        this.studyEventOid = studyEventOid;
    }

    public String getSourceDataVerification() {
        return sourceDataVerification;
    }

    public void setSourceDataVerification(String sourceDataVerification) {
        this.sourceDataVerification = sourceDataVerification;
    }

    public Boolean getIsDefaultVersion() {
        return this.isDefaultVersion;
    }

    public void setIsDefaultVersion(Boolean isDefaultVersion) {
        this.isDefaultVersion = isDefaultVersion;
    }

    public Boolean getPasswordRequired() {
        return passwordRequired;
    }

    public void setPasswordRequired(Boolean passwordRequired) {
        this.passwordRequired = passwordRequired;
    }

    public Boolean getDoubleDataEntry() {
        return doubleDataEntry;
    }

    public void setDoubleDataEntry(Boolean doubleDataEntry) {
        this.doubleDataEntry = doubleDataEntry;
    }

    public Boolean getHideCrf() {
        return hideCrf;
    }

    public void setHideCrf(Boolean hideCrf) {
        this.hideCrf = hideCrf;
    }

    public Boolean getParticipantForm() {
        return participantForm;
    }

    public void setParticipantForm(Boolean participantForm) {
        this.participantForm = participantForm;
    }

    //endregion

}
