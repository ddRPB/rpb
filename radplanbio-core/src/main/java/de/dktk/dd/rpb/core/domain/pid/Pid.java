/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.pid;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

/**
 * Pseudonym ID generator domain entity
 * Basically it hold the communication configuration to Mainzelliste pseudonymisation service
 *
 * @author tomas@skripcak.net
 * @since 14 August 2013
 */
@Entity
@Table(name = "PIDG")
public class Pid implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Pid.class);

    //endregion

    //region Members

    private Integer generatorId; // pk
    private String generatorBaseUrl; // not null

    private String apiKey; // not null
    private String adminUsername; // not null
    private String adminPassword; // not null

    private Boolean isEnabled; // default true
    private String version;

    //TODO: setup for use with apiVersion parameter from DB
    private String apiVersion = "2.1";  // "2.1" -> "3.0"

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Pid() {
        // NOOP
    }

    @SuppressWarnings("unused")
    public Pid(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "GENERATORID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pidg_generatorid_seq")
    @SequenceGenerator(name = "pidg_generatorid_seq", sequenceName = "pidg_generatorid_seq")
    public Integer getId() {
        return this.generatorId;
    }

    public void setId(Integer value) {
        this.generatorId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.generatorId != null;
    }

    //endregion

    //region PidBaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "GENERATORBASEURL", nullable = false)
    public String getGeneratorBaseUrl()  {
        return this.generatorBaseUrl;
    }

    public void setGeneratorBaseUrl(String value) {
        this.generatorBaseUrl = value;
    }

    //endregion

    //region ApiKey

    @Size(max = 255)
    @NotEmpty
    @Column(name = "APIKEY", nullable = false)
    public String getApiKey()  {
        return this.apiKey;
    }

    public void setApiKey(String value) {
        this.apiKey = value;
    }

    //endregion

    //region AdminUsername

    @Size(max = 255)
    @NotEmpty
    @Column(name = "ADMINUSERNAME", nullable = false)
    public String getAdminUsername()  {
        return this.adminUsername;
    }

    public void setAdminUsername(String value) {
        this.adminUsername = value;
    }

    //endregion

    //region AdminPassword

    @Size(max = 255)
    @NotEmpty
    @Column(name = "ADMINPASSWORD", nullable = false)
    public String getAdminPassword()  {
        return this.adminPassword;
    }

    public void setAdminPassword(String value) {
        this.adminPassword = value;
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

    //region ApiVersion

    //TODO: setup for use with apiVersion parameter 2.1 from DB
    @Transient
    public String getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(String value) {
        this.apiVersion = value;
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
        return this == other || (other instanceof Pid && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Pid instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("generatorbaseurl", this.getGeneratorBaseUrl())
                .add("apikey", this.getApiKey())
                .add("adminusername", this.getAdminUsername())
                .add("adminpassword", this.getAdminPassword())
                .add("isenabled", this.getIsEnabled())
                .toString();
    }

    //endregion

}