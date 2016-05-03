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

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * CurriculumVitaeItem domain entity
 * This is item that can be shown in person electronic curriculum vitae,
 * it holds the information about the person professional development
 *
 * @author tomas@skripcak.net
 * @since 25 Jun 2015
 */
@Entity
@Table(name = "CURRICULUMVITAEITEM")
public class CurriculumVitaeItem implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(CurriculumVitaeItem.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;
    private Date startDate;
    private Date endDate;
    private String comment;

    // Many-to-One
    private CurriculumVitaeItemType type;
    private Organisation employer;
    private Organisation workingPlace;
    private Person person;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public CurriculumVitaeItem() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "curriculumvitaeitem_id_seq")
    @SequenceGenerator(name = "curriculumvitaeitem_id_seq", sequenceName = "curriculumvitaeitem_id_seq")
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
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region StartDate

    @Column(name = "STARTDATE")
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    //endregion

    //region EndDate

    @Column(name = "ENDDATE")
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    //endregion

    //region Comment

    @Column(name = "COMMENT")
    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    //endregion

    //region Many-to-One

    //region Type

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TYPEID")
    public CurriculumVitaeItemType getType() {
        return this.type;
    }

    public void setType(CurriculumVitaeItemType type) {
        this.type = type;
    }

    //endregion

    //region Employer

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="EMPLOYERID")
    public Organisation getEmployer() {
        return this.employer;
    }

    public void setEmployer(Organisation employer) {
        this.employer = employer;
    }

    //endregion

    //region WorkingPlace

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="WORKINGPLACEID")
    public Organisation getWorkingPlace() {
        return this.workingPlace;
    }

    public void setWorkingPlace(Organisation workingPlace) {
        this.workingPlace = workingPlace;
    }

    //endregion

    //region Person

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PERSONID")
    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
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
        return this == other || (other instanceof CurriculumVitaeItem && hashCode() == other.hashCode());
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
