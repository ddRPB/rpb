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

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
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

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

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

    //region Name

    @NotEmpty
    @Size(max = 255)
    @Column(name = "ROLENAME", nullable = false, unique = true)
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

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Role instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("roleName", this.roleName)
                .add("description", this.description)
                .toString();
    }

    //endregion

}