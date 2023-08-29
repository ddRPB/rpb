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

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * CurriculumVitaeItemType domain entity
 *
 * @author tomas@skripcak.net
 * @since 25 Jun 2015
 */
@Entity
@Table(name = "CURRICULUMVITAEITEMTYPE")
public class CurriculumVitaeItemType implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CurriculumVitaeItemType.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;
    private String description;

    // Many-to-One
    private CurriculumVitaeItemType parent;

    // One-to-Many
    private List<CurriculumVitaeItemType> children;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public CurriculumVitaeItemType() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "curriculumvitaeitemtype_id_seq")
    @SequenceGenerator(name = "curriculumvitaeitemtype_id_seq", sequenceName = "curriculumvitaeitemtype_id_seq")
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
    @NotEmpty
    @Column(name = "NAME", nullable = false, unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
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

    //region Many-to-One

    //region Parent

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PARENTID")
    public CurriculumVitaeItemType getParent() {
        return this.parent;
    }

    public void setParent(CurriculumVitaeItemType parent) {
        this.parent = parent;
    }

    //endregion

    //endregion

    //region One-to-Many

    //region Children

    @OneToMany(mappedBy = "parent")
    public List<CurriculumVitaeItemType> getChildren() {
        return this.children;
    }

    public void setChildren(List<CurriculumVitaeItemType> children) {
        this.children = children;
    }

    //endregion

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
        return this == other || (other instanceof CurriculumVitaeItemType && hashCode() == other.hashCode());
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
                .add("description", this.description)
                .toString();
    }

    //endregion

}
