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

package de.dktk.dd.rpb.core.domain.admin;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import org.hibernate.validator.constraints.NotEmpty;
import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

/**
 * RadPlanBio Pacs domain entity
 *
 * @author tomas@skripcak.net
 * @since 08 August 2013
 */
@Entity
@Table(name = "PACS")
public class Pacs implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Pacs.class);

    //endregion

    //region Members

    private Integer pacsId; // pk
    private String pacsBaseUrl; // not null
    private Boolean isEnabled;
    private String version;

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Pacs() {
        // NOOP
    }

    @SuppressWarnings("unused")
    public Pacs(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "PACSID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pacs_pacsid_seq")
    @SequenceGenerator(name = "pacs_pacsid_seq", sequenceName = "pacs_pacsid_seq")
    public Integer getId() {
        return this.pacsId;
    }

    public void setId(Integer value) {
        this.pacsId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.pacsId != null;
    }

    //endregion

    //region PacsBaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "PACSBASEURL", nullable = false, length = 255)
    public String getPacsBaseUrl()  {
        return this.pacsBaseUrl;
    }

    public void setPacsBaseUrl(String value) {
        this.pacsBaseUrl = value;
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

    //endregion

    //region Methods

    /**
     * Set the default values.
     */
    public void initDefaultValues()
    {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Pacs && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Pacs instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("pacsbaseurl", this.getPacsBaseUrl())
                .add("isenabled", this.getIsEnabled())
                .toString();
    }

    //endregion

}
