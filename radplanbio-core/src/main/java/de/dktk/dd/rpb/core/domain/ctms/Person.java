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

import com.google.common.primitives.Booleans;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;
import org.openclinica.ws.beans.SubjectType;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Person domain entity
 *
 * Person in RPB can be patient or study personnel (from CTMS point of view)
 *
 * Study personnel person is persistent entity stored in RPB database
 * Patient person is transient entity and has identifiable data (persisted outside of RPB)
 * that need to be protected with first level pseudonym generator (PID - pseudonym)
 *
 * @author tomas@skripcak.net
 * @since 22 August 2013
 */
@Entity
@Table(name = "PERSON")
public class Person implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Person.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String pid; // pseudonym

    private String titlesBefore; // e.g. Dr. Ing.
    private String firstname;
    private String surname;
    private Date birthdate;
    private String titlesAfter; // e.g. PhD

    private String birthname;
    private String city; // city of residence
    private String zipcode;
    private String comment;

    // is sure attribute declares the sureness of new patient registration
    // it forces PID generator to generate a new PID in case of possible match
    boolean isSure;

    // Many-to-One
    private PersonStatus status;

    // One-to-Many
    private List<CurriculumVitaeItem> curriculumVitaeItems; // personnel normally has some profession history
    private List<StudyPerson> participatingInStudies; // person within a study with a specific role

    private List<StudySubject> studySubjects; // transient relation to patient studySubject entities
    private List<DicomStudy> dicomStudies; // transient relation to patient dicomStudy entities

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Person() {
        // Init
        this.firstname = "";
        this.surname = "";
        this.city = "";
        this.birthname = "";
        this.zipcode = "";

        this.isSure = false;

        this.studySubjects = new ArrayList<StudySubject>();
        this.dicomStudies = new ArrayList<DicomStudy>();
    }

    public Person(Integer primaryKey) {
        setId(primaryKey);
    }

    public Person(SubjectType st) {
        this.pid = st.getUniqueIdentifier();
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_id_seq")
    @SequenceGenerator(name = "person_id_seq", sequenceName = "person_id_seq")
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

    //region PID - pseudonym

    @Transient
    public String getPid() {
        return this.pid;
    }

    public void setPid(String value) {
        this.pid = value;
    }

    //endregion

    //region Titles before the first name

    @Size(max = 255)
    @Column(name = "TITLESBEFORE", length = 255)
    public String getTitlesBefore() {
        return this.titlesBefore;
    }

    public void setTitlesBefore(String value) {
        this.titlesBefore = value;
    }

    //endregion

    //region First name

    @Size(max = 255)
    @NotEmpty
    @Column(name = "FIRSTNAME", nullable = false, length = 255)
    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String value) {
        this.firstname = value;
    }

    //endregion

    //region Surname

    @Size(max = 255)
    @NotEmpty
    @Column(name = "SURNAME", nullable = false, length = 255)
    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String value) {
        this.surname = value;
    }

    //endregion

    //region Titles after the surname

    @Size(max = 255)
    @Column(name = "TITLESAFTER", length = 255)
    public String getTitlesAfter() {
        return this.titlesAfter;
    }

    public void setTitlesAfter(String value) {
        this.titlesAfter = value;
    }

    //endregion

    //region FullName

    @Transient
    public String getFullName() {
        return this.firstname + " " + this.surname;
    }

    //endregion

    //region FullTitlesName

    @Transient
    @SuppressWarnings("unused")
    public String getFullTitlesName() {
        return this.titlesBefore + " " + this.firstname + " " + this.surname + " " + this.titlesBefore;
    }

    //endregion

    //region Birth date

    @Transient
    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date value) {
        this.birthdate = value;
    }

    //endregion

    //region Birth name

    @Size(max = 255)
    @Column(name = "BIRTHNAME", length = 255)
    public String getBirthname() {
        return this.birthname;
    }

    public void setBirthname(String value) {
        this.birthname = value;
    }

    //endregion

    //region City

    @Transient
    public String getCity() {
        return this.city;
    }

    public void setCity(String value) {
        if (value != null && !value.equals(this.city)) {
            this.city = value;
        }
    }

    //endregion

    //region ZIP code

    @Transient
    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String value) {
        if (value != null && !value.equals(this.zipcode)) {
            this.zipcode = value;
        }
    }

    //endregion

    //region IsSure

    @Transient
    public boolean getIsSure() {
        return this.isSure;
    }

    public void setIsSure(boolean value) {
        if (this.isSure != value) {
            this.isSure = value;
        }
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

    //region Status

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="STATUSID")
    public PersonStatus getStatus() {
        return this.status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
    }

    //endregion

    //endregion

    //region One-To-Many

    //region CurriculumVitaeItems

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true)
    public List<CurriculumVitaeItem> getCurriculumVitaeItems() {
        return this.curriculumVitaeItems;
    }

    public void setCurriculumVitaeItems(List<CurriculumVitaeItem> curriculumVitaeItems) {
        this.curriculumVitaeItems = curriculumVitaeItems;
    }

    public Boolean addCurriculumVitaeItem(CurriculumVitaeItem cvItem) {
        if (!this.curriculumVitaeItems.contains(cvItem)) {
            cvItem.setPerson(this);
            return this.curriculumVitaeItems.add(cvItem);
        }

        return false;
    }

    public Boolean removeCurriculumVitaeItem(CurriculumVitaeItem cvItem) {
        return this.curriculumVitaeItems.contains(cvItem) && this.curriculumVitaeItems.remove(cvItem);
    }

    //endregion

    //region ParticipatingInStudies

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    public List<StudyPerson> getStudyPersonnel() {
        return this.participatingInStudies;
    }

    public void setStudyPersonnel(List<StudyPerson> studyPersonnel) {
        this.participatingInStudies = studyPersonnel;
    }

    //endregion

    //region StudySubjects

    @Transient
    public List<StudySubject> getStudySubjects() {
        return this.studySubjects;
    }

    public void setStudySubjects(List<StudySubject> studySubjects) {
        this.studySubjects = studySubjects;
    }

    //endregion

    //region DicomStudies

    @Transient
    public List<DicomStudy> getDicomStudies() {
        return this.dicomStudies;
    }

    public void setDicomStudies(List<DicomStudy> dicomStudies) {
        this.dicomStudies = dicomStudies;
    }

    public boolean addDicomStudy(DicomStudy ds) {
        if (!this.dicomStudies.contains(ds)) {
            return this.dicomStudies.add(ds);
        }

        return false;
    }

    public boolean removeDicomStudy(DicomStudy ds) {
        return this.dicomStudies.contains(ds) && this.dicomStudies.remove(ds);
    }

    //endregion

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values
     */
    public void initDefaultValues() {
        this.firstname = "";
        this.surname = "";
        this.birthname = "";
        this.city = "";
        this.zipcode = "";
        this.birthdate = null;
    }

    public boolean hasDicomStudyWithUid(String studyInstanceUid) {
        if (this.dicomStudies != null) {
            for (DicomStudy ds : this.dicomStudies) {
                if (!"".equals(ds.getStudyInstanceUID()) && ds.getStudyInstanceUID().equals(studyInstanceUid)) {
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

    public DicomStudy getDicomStudyWithUid(String studyInstanceUid) {
        if (this.dicomStudies != null) {
            for (DicomStudy ds : this.dicomStudies) {
                if (!"".equals(ds.getStudyInstanceUID()) && ds.getStudyInstanceUID().equals(studyInstanceUid)) {
                    return ds;
                }
            }
        }

        return null;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Person && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Study instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("pid", this.getPid()) //
                .add("name", this.getFirstname())
                .add("surname", this.getSurname()) //
                .toString();
    }

    //endregion

}
