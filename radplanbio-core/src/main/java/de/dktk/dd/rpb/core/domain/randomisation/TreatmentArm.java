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

package de.dktk.dd.rpb.core.domain.randomisation;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;
import de.dktk.dd.rpb.core.domain.ctms.Study;

import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Study TreatmentArm domain entity
 *
 * @author tomas@skripcak.net
 * @since 23 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Table(name = "TREATMENTARM")
public class TreatmentArm implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TreatmentArm.class);

    //endregion

    //region Members

    private Integer id; // pk
    private String name; // Name of treatment arm
    private String description; // Detailed description of treatment arm
    private Integer plannedSubjectsCount; // How many patients are planned for this treatment arm

    // One to Many
    private List<TrialSubject> subjects = new ArrayList<>(); // Patient assigned to this treatment arm

    // Many to One
    private Study study; // RadPlanBio study

    //endregion

    //region Constructors

    public TreatmentArm() {
        // NOOP
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "treatmentarm_id_seq")
    @SequenceGenerator(name = "treatmentarm_id_seq", sequenceName = "treatmentarm_id_seq")
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
    @Column(name = "NAME")
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

    //region Planned Subjects Count

    @Column(name = "SUBJECTCOUNT")
    public Integer getPlannedSubjectsCount() {
        return this.plannedSubjectsCount;
    }

    public void setPlannedSubjectsCount(Integer value) {
        this.plannedSubjectsCount = value;
    }

    //endregion

    //region One to Many

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="treatmentArm", fetch=FetchType.LAZY)
    public List<TrialSubject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(List<TrialSubject> list) {
        this.subjects = list;
    }

    public boolean addSubject(TrialSubject subject) {
        return this.subjects.add(subject);
    }

    public boolean removeSubject(TrialSubject subject) {
        return this.subjects.remove(subject);
    }

    public boolean containsSubject(TrialSubject subject) {
        return this.subjects != null && this.subjects.contains(subject);
    }

    //endregion

    //region Many to One

    @ManyToOne
    @JoinColumn(name="STUDYID")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
       this.study = study;
    }

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
     * Equals implementation using an object hash code
     *
     * @param other object to compare with
     * @return true if objects are the same
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TreatmentArm && this.hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this object instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("plannedSubjectsCount", this.plannedSubjectsCount)
                .toString();
    }

    //endregion

}