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

package de.dktk.dd.rpb.core.domain.rpb;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import org.apache.log4j.Logger;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Software domain entity
 *
 * This is a software representation within RadPlanBio
 *
 * @author tomas@skripcak.net
 * @since 29 October 2014
 */
@Entity
@Table(name = "SOFTWARE")
public class Software implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Software.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id; // pk, auto-increment, serial
    private String name; // not null
    private String description;
    private String version;
    private String filename;
    private String platform;
    private Boolean latest;

    // Many to one
    private Portal portal; // association to Portal

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Software() {
        // NOOP
    }

    public Software(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "software_id_seq")
    @SequenceGenerator(name = "software_id_seq", sequenceName = "software_id_seq")
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

    //region Name

    @Size(max = 255)
    @NotEmpty
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Size(max = 255)
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
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

    //region Filename

    @Size(max = 255)
    @Column(name = "FILENAME")
    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String value) {
        this.filename = value;
    }

    //endregion

    //region Platform

    @Size(max = 255)
    @Column(name = "PLATFORM")
    public String getPlatform() { return this.platform; }

    public void setPlatform(String value) {
        this.platform = value;
    }

    //endregion

    //region Latest

    @Column(name = "LATEST")
    public Boolean getLatest() {
        return this.latest;
    }

    public void setLatest(Boolean value) {
        this.latest = value;
    }

    //endregion

    //region Portal

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PORTALID", nullable  = false)
    public Portal getPortal() {
        return this.portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    //endregion

    //endregion

    //region Init

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
        return this == other || (other instanceof Software && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Software instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("version", this.version)
                .add("filename", this.filename)
                .toString();
    }

    //endregion

}