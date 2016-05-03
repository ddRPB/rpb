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

package de.dktk.dd.rpb.core.domain.edc;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * AnnotationType domain entity
 * RadPlanBio specific annotation type for CRF items in OpenClinica
 *
 * @author tomas@skripcak.net
 * @since 12 Sep 2013
 */
@Entity
@Table(name = "ANNOTATIONTYPE")
public class AnnotationType implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AnnotationType.class);

    //endregion

    //region Members

    private Integer id; // pk
    private String name; // not null, unique
    private String description;

    //endregion

    //region Constructors

    public AnnotationType() {
        // NOOP
    }

    public AnnotationType(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annotationtype_id_seq")
    @SequenceGenerator(name = "annotationtype_id_seq", sequenceName = "annotationtype_id_seq")
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
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName()  {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Size(max = 255)
    @Column(name = "DESCRIPTION", length = 255)
    public String getDescription()  {
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
        return this == other || (other instanceof AnnotationType && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this AnnotationType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("name", this.getName())
                .add("description", this.getDescription())
                .toString();
    }

    //endregion

}
