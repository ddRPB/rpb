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

package de.dktk.dd.rpb.core.domain.edc;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import org.hibernate.validator.constraints.NotEmpty;
import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

/**
 * Electronic Data Capture system (OpenClinica) domain entity
 *
 * @author tomas@skripcak.net
 * @since 12 August 2013
 */
@Entity
@Table(name = "EDC")
public class Edc implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Edc.class);

    //endregion

    //region Members

    private Integer edcId; // pk
    private String edcBaseUrl; // not null
    private String soapBaseUrl; // not null
    private String edcPublicUrl;
    private String soapPublicUrl;
    private String dataPath;
    private Boolean isEnabled;
    private Boolean isValidated;
    private String version;

    //endregion

    //region Constructors

    public Edc() {
        // NOOP
    }

    public Edc(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "EDCID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edc_edcid_seq")
    @SequenceGenerator(name = "edc_edcid_seq", sequenceName = "edc_edcid_seq")
    public Integer getId() {
        return this.edcId;
    }

    public void setId(Integer value) {
        this.edcId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.edcId != null;
    }

    //endregion

    //region EdcBaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "EDCBASEURL", nullable = false)
    public String getEdcBaseUrl()  {
        return this.edcBaseUrl;
    }

    public void setEdcBaseUrl(String value) {
        this.edcBaseUrl = value;
    }

    //endregion

    //region SoapBaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "SOAPBASEURL", nullable = false)
    public String getSoapBaseUrl()  {
        return this.soapBaseUrl;
    }

    public void setSoapBaseUrl(String value) {
        this.soapBaseUrl = value;
    }

    //endregion

    //region EdcPublicUrl

    @Size(max = 255)
    @Column(name = "EDCPUBLICURL")
    public String getEdcPublicUrl() {
        return this.edcPublicUrl;
    }

    public void setEdcPublicUrl(String value) {
        this.edcPublicUrl = value;
    }

    //endregion

    //region SoapPublicUrl

    @Size(max = 255)
    @Column(name = "SOAPPUBLICURL")
    public String getSoapPublicUrl() {
        return this.soapPublicUrl;
    }

    public void setSoapPublicUrl(String value) {
        this.soapPublicUrl = value;
    }

    //endregion

    //region DataPath

    @Size(max = 255)
    @Column(name = "DATAPATH")
    public String getDataPath() {
        return this.dataPath;
    }

    public void setDataPath(String value) {
        this.dataPath = value;
    }

    //endregion

    //region IsEnabled

    @Column(name = "ISENABLED")
    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    //endregion

    //region IsValidated

    @Column(name = "ISVALIDATED")
    public Boolean getIsValidated() {
        return this.isValidated;
    }

    public void setIsValidated(Boolean value) {
        this.isValidated = value;
    }

    //endregion

    //region Version

    @Size(max = 15)
    @Column(name = "VERSION", length = 15)
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Edc && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Edc instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.edcId)
                .add("edcbaseurl", this.edcBaseUrl)
                .add("soapbaseurl", this.soapBaseUrl)
                .add("edcpublicurl", this.edcPublicUrl)
                .add("soappublicurl", this.soapPublicUrl)
                .add("isenabled", this.isEnabled)
                .add("isvalidated", this.isValidated)
                .toString();
    }

    //endregion

}