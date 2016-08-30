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

import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StudyPerson association domain entity
 *
 * Person associated to study with specific role
 *
 * @author tomas@skripcak.net
 * @since 25 Jun 2015
 */
@Entity
@Table(name = "STUDYPERSON")
public class StudyPerson implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(StudyPerson.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private Date startDate;
    private Date endDate;
    private Boolean canObtainInformedConsent; // person can obtain informed consent for patient
    private String comment;

    // Many-to-One
    private Study study;
    private Person person;
    private StudyPersonRole role;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public StudyPerson() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studyperson_id_seq")
    @SequenceGenerator(name = "studyperson_id_seq", sequenceName = "studyperson_id_seq")
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

    //region StartDate

    @Column(name= "STARTDATE")
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Transient
    public String getStartDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getStartDate();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region EndDate

    @Column(name= "ENDDATE")
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Transient
    public String getEndDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getEndDate();
        return date != null ? format.format(date) : null;
    }


    //endregion

    //region CanObtainInformedConsent

    @Column(name = "INFORMEDCONSENT")
    public Boolean getCanObtainInformedConsent() {
        return this.canObtainInformedConsent;
    }

    public void setCanObtainInformedConsent(Boolean value) {
        this.canObtainInformedConsent = value;
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

    @ManyToOne
    @JoinColumn(name = "studyid", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @ManyToOne
    @JoinColumn(name = "personid", nullable = false)
    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @ManyToOne
    @JoinColumn(name = "studypersonroleid", nullable = false)
    public StudyPersonRole getRole() {
        return this.role;
    }

    public void setRole(StudyPersonRole role) {
        this.role = role;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        this.canObtainInformedConsent = false;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof StudyPerson && hashCode() == other.hashCode());
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