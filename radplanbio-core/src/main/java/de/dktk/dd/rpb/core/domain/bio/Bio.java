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

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * RPB BIO bank component domain entity
 *
 * @author tomas@skripcak.net
 * @since 23 Dec 2016
 */
@Entity
@Table(name = "BIO")
public class Bio implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Bio.class);

    //endregion

    //region Members

    private Integer id; // pk
    private String baseUrl; // not null
    private String publicUrl;

    private Boolean isEnabled; // default true
    private String version;

    private String username; // not null
    private String password; // not null

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Bio() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bio_id_seq")
    @SequenceGenerator(name = "bio_id_seq", sequenceName = "bio_id_seq")
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

    //region BaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "BASEURL", nullable = false)
    public String getBaseUrl()  {
        return this.baseUrl;
    }

    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    //endregion

    //region PublicUrl

    @Size(max = 255)
    @Column(name = "PUBLICURL")
    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
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

    //region Username

    @Size(max = 255)
    @NotEmpty
    @Column(name = "USERNAME", nullable = false)
    public String getUsername()  {
        return this.username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    //endregion

    //region Password

    @Size(max = 255)
    @NotEmpty
    @Column(name = "PASSWORD", nullable = false)
    public String getPassword()  {
        return this.password;
    }

    public void setPassword(String value) {
        this.password = value;
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
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Bio && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("baseUrl", this.baseUrl)
                .add("publicUrl", this.publicUrl)
                .add("isEnabled", this.isEnabled)
                .add("version", this.version)
                .toString();
    }

    //endregion

}
