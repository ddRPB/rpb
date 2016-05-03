/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.Named;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import com.google.common.base.Objects;

/**
 * RadPlanBio Role domain entity
 *
 * @author tomas@skripcak.net
 * @since 08 August 2013
 */
@Entity
@Table(name = "ROLE")
public class Role implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Role.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id; // pk
    private String roleName; // unique (not null)
    private String description;

    //endregion

    //region Constructors

    public Role() {
        // NOOP
    }

    public Role(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Persistent Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    // region RoleName

    @Size(max = 255)
    @NotEmpty
    @Column(name = "ROLENAME", nullable = false, unique = true, length = 255)
    public String getName() {
        return this.roleName;
    }

    public void setName(String roleName) {
        this.roleName = roleName;
    }

    //endregion

    //region Description

    @Size(max = 4000)
    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
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
        return this == other || (other instanceof Role && hashCode() == other.hashCode());
    }

    private volatile int previousHashCode = 0;

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(this.roleName);
        if (previousHashCode != 0 && previousHashCode != hashCode) {
            log.warn("DEVELOPER: hashCode has changed!." //
                    + "If you encounter this message you should take the time to carefuly review equals/hashCode for: " //
                    + getClass().getCanonicalName());
        }

        previousHashCode = hashCode;
        return hashCode;
    }

    /**
     * Construct a readable string representation for this Role instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("roleName", this.roleName)
                .add("description", this.description)
                .toString();
    }

    //endregion

}