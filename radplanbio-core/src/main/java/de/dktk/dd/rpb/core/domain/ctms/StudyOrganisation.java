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

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Organisation associated to specific study with a specific role
 *
 * @author tomas@skripcak.net
 * @since 30 Jun 2015
 */
@Entity
@Table(name = "STUDYORGANISATION")
public class StudyOrganisation implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(StudyOrganisation.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment

    // Many-to-One
    private Study study;
    private Organisation organisation;
    private StudyOrganisationRole role;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public StudyOrganisation() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studyorganisation_id_seq")
    @SequenceGenerator(name = "studyorganisation_id_seq", sequenceName = "studyorganisation_id_seq")
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

    //region Many-to-One

    @ManyToOne
    @JoinColumn(name = "studyid", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @ManyToOne
    @JoinColumn(name = "organisationid", nullable = false)
    public Organisation getOrganisation () {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @ManyToOne
    @JoinColumn(name = "roleid", nullable = false)
    public StudyOrganisationRole getRole () {
        return this.role;
    }

    public void setRole(StudyOrganisationRole role) {
        this.role = role;
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
        return this == other || (other instanceof StudyOrganisation && hashCode() == other.hashCode());
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
                .toString();
    }

    //endregion

}