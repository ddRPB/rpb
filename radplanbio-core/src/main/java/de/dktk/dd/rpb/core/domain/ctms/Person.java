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
import de.dktk.dd.rpb.core.domain.Personed;
import de.dktk.dd.rpb.core.domain.bio.AbstractSpecimen;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.util.Constants;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;
import org.openclinica.ws.beans.SubjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

//TODO: refactor .... see the Subject class, many things should be moved there

/**
 * Person domain entity
 * <p>
 * Person in RPB can be patient (from EDC, PACS, BIO point of view) or study personnel (from CTMS point of view)
 * <p>
 * Study personnel is persistent entity stored in RPB database
 * Patient is transient entity and has identifiable data (persisted via first level pseudonym generator)
 *
 * @author tomas@skripcak.net
 * @since 22 August 2013
 */
@Entity
@Table(name = "PERSON")
public class Person implements Identifiable<Integer>, Personed, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Person.class);

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
    private boolean isSure;

    // to determine whether patient is registered in BIO bank
    private Boolean isBio;
    // to determine whether patient is registered in PACS
    private Boolean isPacs;

    // Many-to-One
    private PersonStatus status;

    // One-to-Many
    private List<StudyPerson> participatingInStudies; // person within a study with a specific role

    private List<StudySubject> studySubjects; // transient relation to patient StudySubject entities
    private List<DicomStudy> dicomStudies; // transient relation to patient DicomStudy entities
    private List<AbstractSpecimen> bioSpecimens; // transient relation to patient Specimen entities

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    // DEPRECATED
    //private List<CurriculumVitaeItem> curriculumVitaeItems; // personnel normally has some profession history

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

        this.studySubjects = new ArrayList<>();
        this.dicomStudies = new ArrayList<>();
        this.bioSpecimens = new ArrayList<>();
    }

    public Person(Integer primaryKey) {
        setId(primaryKey);
    }

    public Person(SubjectType st) {
        this.pid = st.getUniqueIdentifier();
    }

    /**
     * The Person object is created from the database query results
     *
     * @param row
     */
    public Person(Map<String, Object> row) {
        this.id = (Integer) row.get("id");
        this.titlesBefore = (String) row.get("titlesbefore");
        this.firstname = (String) row.get("firstname");
        this.surname = (String) row.get("surname");
        this.titlesAfter = (String) row.get("titlesafter");
        this.birthname = (String) row.get("birthname");
        this.comment = (String) row.get("comment");

        this.isSure = false;

        this.studySubjects = new ArrayList<>();
        this.dicomStudies = new ArrayList<>();
        this.bioSpecimens = new ArrayList<>();
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
    @Column(name = "TITLESBEFORE")
    public String getTitlesBefore() {
        return this.titlesBefore;
    }

    public void setTitlesBefore(String value) {
        this.titlesBefore = value;
    }

    //endregion

    //region Firstname

    @Size(max = 255)
    @NotEmpty
    @Column(name = "FIRSTNAME", nullable = false)
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
    @Column(name = "SURNAME", nullable = false)
    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String value) {
        this.surname = value;
    }

    //endregion

    //region Titles after the surname

    @Size(max = 255)
    @Column(name = "TITLESAFTER")
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
    @Past
    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date value) {
        this.birthdate = value;
    }

    @Transient
    public String getBirthdateString() {
        if (this.birthdate != null) {
            DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
            return format.format(
                    this.getBirthdate()
            );
        }

        return null;
    }

    //endregion

    //region Birth name

    @Size(max = 255)
    @Column(name = "BIRTHNAME")
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

    //region IsBio

    @Transient
    public Boolean getIsBio() {
        return this.isBio;
    }

    public void setIsBio(Boolean value) {
        this.isBio = value;
    }

    //endregion

    //region IsPacs

    @Transient
    public Boolean getIsPacs() {
        return this.isPacs;
    }

    public void setIsPacs(Boolean value) {
        this.isPacs = value;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUSID")
    public PersonStatus getStatus() {
        return this.status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
    }

    //endregion

    //endregion

    //region One-To-Many

    //region DEPRECATED: CurriculumVitaeItems

//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true)
//    public List<CurriculumVitaeItem> getCurriculumVitaeItems() {
//        return this.curriculumVitaeItems;
//    }
//
//    public void setCurriculumVitaeItems(List<CurriculumVitaeItem> curriculumVitaeItems) {
//        this.curriculumVitaeItems = curriculumVitaeItems;
//    }
//
//    public Boolean addCurriculumVitaeItem(CurriculumVitaeItem cvItem) {
//        if (!this.curriculumVitaeItems.contains(cvItem)) {
//            cvItem.setPerson(this);
//            return this.curriculumVitaeItems.add(cvItem);
//        }
//
//        return Boolean.FALSE;
//    }
//
//    public Boolean removeCurriculumVitaeItem(CurriculumVitaeItem cvItem) {
//        return this.curriculumVitaeItems.contains(cvItem) && this.curriculumVitaeItems.remove(cvItem);
//    }

    //endregion

    //region ParticipatingInStudies

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true)
    public List<StudyPerson> getStudyPersonnel() {
        return this.participatingInStudies;
    }

    public void setStudyPersonnel(List<StudyPerson> studyPersonnel) {
        this.participatingInStudies = studyPersonnel;
    }

    public Boolean modifyStudyPersonnel(List<StudyPerson> studyPersonnelToModify, StudyPerson modifiedStudyPerson) {
        // Preconditions
        if (this.participatingInStudies != null && studyPersonnelToModify != null && modifiedStudyPerson != null) {

            for (StudyPerson spToModify : studyPersonnelToModify) {
                for (StudyPerson sp : this.participatingInStudies) {

                    // Id match
                    if (sp.getId().equals(spToModify.getId())) {

                        // Apply new start date if specified
                        if (modifiedStudyPerson.getStartDate() != null) {
                            sp.setStartDate(modifiedStudyPerson.getStartDate());
                        }
                        // Apply new end date if specified
                        if (modifiedStudyPerson.getEndDate() != null) {
                            sp.setEndDate((modifiedStudyPerson.getEndDate()));
                        }

                        // Modified so move to next one in a list for modification
                        break;
                    }
                }
            }

            // True mean that what was possible to match was modified
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
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
        return !this.dicomStudies.contains(ds) && this.dicomStudies.add(ds);

    }

    public boolean removeDicomStudy(DicomStudy ds) {
        return this.dicomStudies.contains(ds) && this.dicomStudies.remove(ds);
    }

    //endregion

    //region BioSpecimens

    @Transient
    public List<AbstractSpecimen> getBioSpecimens() {
        return this.bioSpecimens;
    }

    public void setBioSpecimens(List<AbstractSpecimen> bioSpecimens) {
        this.bioSpecimens = bioSpecimens;
    }

    public boolean addBioSpecimen(AbstractSpecimen bs) {
        return !this.bioSpecimens.contains(bs) && this.bioSpecimens.add(bs);
    }

    public boolean removeBioSpecimen(AbstractSpecimen bs) {
        return this.bioSpecimens.contains(bs) && this.bioSpecimens.remove(bs);
    }

    //endregion

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values
     */
    public void initDefaultValues() {
        this.clearIdentity();
    }

    //endregion

    //region Patient Methods

    //region IDAT

    public void clearIdentity() {
        this.firstname = "";
        this.surname = "";
        this.birthname = "";
        this.city = "";
        this.zipcode = "";
        this.birthdate = null;
    }

    //endregion

    //region DICOM

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

    //region EDC

    public String findStudySubjectId(String studyIdentifier) {
        if (studyIdentifier != null && this.studySubjects != null) {
            for (StudySubject studySubject : this.studySubjects) {
                if (studyIdentifier.equals(studySubject.getStudy().getOcStudyUniqueIdentifier())) {
                    return studySubject.getStudySubjectId();
                }
            }
        }

        return null;
    }

    //endregion

    //endregion

    //region Equals

    /**
     * Return true when patient IDAT matches
     *
     * @param otherPatient patient to compare to
     * @return true if patient IDAT matches
     */
    public boolean patientIdatEquals(Person otherPatient) {
        // Check the full name without cases
        if (this.surname.equalsIgnoreCase(otherPatient.getSurname()) &&
                this.firstname.equalsIgnoreCase(otherPatient.getFirstname())) {

            // If both have birth name set than check also birth name (when one does not have consider as a match, because this data is optional)
            boolean birthNamePresent = !"".equalsIgnoreCase(this.birthname) && !"".equalsIgnoreCase(otherPatient.getBirthname());
            boolean birthNameMatch = !birthNamePresent;
            if (birthNamePresent) {
                birthNameMatch = this.birthname.equalsIgnoreCase(otherPatient.getBirthname());
            }

            // If date of birth information is missing IDAT could not be compared and patient cannot be the same
            boolean birthDateMatch = false;
            if (this.birthdate != null && otherPatient.birthdate != null) {
                Calendar thisCal = Calendar.getInstance();
                thisCal.setTime(this.birthdate);

                Calendar otherCal = Calendar.getInstance();
                otherCal.setTime(otherPatient.getBirthdate());

                // Check if birth date is the same
                birthDateMatch = (
                        thisCal.get(Calendar.YEAR) == otherCal.get(Calendar.YEAR) &&
                                thisCal.get(Calendar.MONTH) == otherCal.get(Calendar.MONTH) &&
                                thisCal.get(Calendar.DAY_OF_MONTH) == otherCal.get(Calendar.DAY_OF_MONTH)
                );
            }

            // If both have city of residence set check also city (when one does not have consider as a match, because this data is optional)
            boolean cityPresent = !"".equalsIgnoreCase(this.city) && !"".equalsIgnoreCase(otherPatient.getCity());
            boolean cityMatch = !cityPresent;
            if (cityPresent) {
                cityMatch = this.city.equalsIgnoreCase(otherPatient.getCity());
            }

            //If both have zipcode set check also zipcode (when one does not have consider as a match, because this data is optional)
            boolean zipcodePresent = !"".equalsIgnoreCase(this.zipcode) && !"".equalsIgnoreCase(otherPatient.getZipcode());
            boolean zipcodeMatch = !zipcodePresent;
            if (zipcodePresent) {
                zipcodeMatch = this.zipcode.equalsIgnoreCase(otherPatient.getZipcode());
            }

            return birthNameMatch && birthDateMatch && cityMatch && zipcodeMatch;
        } else {
            return false;
        }
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
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("pid", this.pid)
                .add("name", this.firstname)
                .add("surname", this.surname)
                .add("birthDate", birthdate != null ? this.birthdate.toString() : null)
                .add("city", this.city)
                .add("zipcode", this.zipcode)
                .toString();
    }

    //endregion

}
