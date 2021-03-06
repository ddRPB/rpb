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

package de.dktk.dd.rpb.core.domain.edc;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;

import org.apache.log4j.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * RadPlanBio Subject - domain entity (transient)
 * Data about subject can originated from different systems (OC, PID, randomisation), this transient domain object
 * aggregates the important information about subject together in order to be presented in the consistent form
 *
 * @author tomas@skripcak.net
 * @since 14 Jun 2013
 */
public class Subject implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Subject.class);

    //endregion

    //region Members

    private Integer id;

    // Study Subject ID
    private String studySubjectId;

    // Secondary ID
    private String secondaryId;

    // Person ID (PID) - pseudo identificator
    private String uniqueIdentifier;

    // OID - from open clinica
    private String oid;

    // Should depend on study configuration but right now it is mandatory (OC - bug)
    // can be 'm' of 'f'
    // TODO: change to enumeration
    private String gender;

    // Optional attribute depending on study configuration (ISO date string)
    private Date dateOfBirth;
    private int yearOfBirth;

    // Date of enrollment into RadPlanBio study;
    private XMLGregorianCalendar enrollmentDate;

    // When subject is human it has identity
    private Person person;

    // When subject is included in randomised study it can have treatment arm
    private TreatmentArm arm;

    //endregion

    //region Constructors

    public Subject() {
        // NOOP
    }

    //endregion

    //region Properties

    //region Id - not actually used because it is transient entity

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region Study Subject ID

    public String getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(String value) {
        this.studySubjectId = value;
    }

    //endregion

    //region Secondary ID

    public String getSecondaryId() {
        return this.secondaryId;
    }

    public void setSecondaryId(String value) {
        this.secondaryId = value;
    }

    //endregion

    //region Unique Identifier (PersonID or PID)

    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    public void setUniqueIdentifier(String value) {
        if (value != null && !value.equals(this.uniqueIdentifier)) {
            this.uniqueIdentifier = value;
        }
    }

    //endregion

    //region OID (OpenClinica Object ID)

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region Gender

    public String getGender() {
        return  this.gender;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    //endregion

    //region DateOfBirth

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date value){
        this.dateOfBirth = value;
    }

    //endregion

    //region YearOfBirth

    @SuppressWarnings("unused")
    public int getYearOfBirth() {
        return this.yearOfBirth;
    }

    @SuppressWarnings("unused")
    public void setYearOfBirth(int value) {
        if (this.yearOfBirth != value) {
            this.yearOfBirth = value;
        }
    }

    //endregion

    //region Enrollment date

    public XMLGregorianCalendar getEnrollmentDate() {
        return this.enrollmentDate;
    }

    public void setEnrollmentDate(XMLGregorianCalendar value) {
        this.enrollmentDate = value;
    }

    /**
     * Converts XMLGregorianCalendar to java.util.Date in Java (for GUI)
     */
    public Date getComparableEnrollmentDate() {
        return this.enrollmentDate != null ? this.enrollmentDate.toGregorianCalendar().getTime() : null;
    }

    /**
     * Converts java.util.Date in Java to XMLGregorianCalendar (from GUI)
     * @param value enrollment date as Date
     */
    public void setComparableEnrollmentDate(Date value) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.enrollmentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(df.format(value));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Person - patient identity data

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person value) {
        this.person = value;
    }

    //endregion

    //region TreatmentArm

    public TreatmentArm getArm() {
        return this.arm;
    }

    public void setArm(TreatmentArm arm) {
        this.arm = arm;
    }

    //endregion

    //region Is randomised

    public boolean getIsAlreadyRandomised() {
        return this.arm != null;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values
     */
    public void initDefaultValues() {
        this.studySubjectId = "";
        this.uniqueIdentifier = "";
        this.secondaryId = "";
        this.gender = "";

        this.enrollmentDate = null;

        // Also person data if present
        if (this.person != null) {
            this.person.initDefaultValues();
        }
    }

    //endregion

}