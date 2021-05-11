/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;

import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Organisation domain object
 *
 * Organisation is used in CTMS module to manage personnel CV employers list as well as institutes involved in studies
 *
 * @author tomas@skripcak.net
 * @since 29 Jun 2013
 */
@Entity
@Table(name = "ORGANISATION")
public class Organisation implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Organisation.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;

    // One-to-Many
    //TODO: private List<Address> addresses;
    private List<Organisation> children;
    private List<StudyOrganisation> studyOrganisations;

    // Many-to-one
    private Organisation parent;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Organisation() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation_id_seq")
    @SequenceGenerator(name = "organisation_id_seq", sequenceName = "organisation_id_seq")
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

    //region Name

    @Size(max = 255)
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region One-to-Many

    //TODO: lazy load
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Organisation> getChildren() {
        return this.children;
    }

    public void setChildren(List<Organisation> children) {
        this.children = children;
    }

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisation")
    public List<StudyOrganisation> getStudyOrganisations() {
        return this.studyOrganisations;
    }

    public void setStudyOrganisations(List<StudyOrganisation> organisations) {
        this.studyOrganisations = organisations;
    }

    //endregion

    //region Many-to-One

    //TODO: lazy load
    @ManyToOne()
    @JoinColumn(name="PARENTID")
    public Organisation getParent() {
        return  this.parent;
    }

    public void setParent(Organisation parent) {
        this.parent = parent;
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
        return this == other || (other instanceof Organisation && hashCode() == other.hashCode());
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
                .add("name", this.name)
                .toString();
    }

    //endregion

}