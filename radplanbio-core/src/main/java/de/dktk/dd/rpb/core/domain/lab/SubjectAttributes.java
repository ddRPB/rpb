package de.dktk.dd.rpb.core.domain.lab;

import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;

import java.util.Date;

/**
 * Contains attributes of the subject representation in Labkey
 * including the mapping annotations for the tsv writer.
 */
public class SubjectAttributes {

    @Parsed(field = "SequenceNum")
    private Double sequenceNum;

    // Study Subject ID
    @Parsed(field = "ParticipantId")
    private String studySubjectId;

    // Person ID (PID) - pseudo identificator
    @Parsed(field = "Pid")
    private String uniqueIdentifier;

    // Secondary ID
    @Parsed(field = "SecondaryId")
    private String secondaryId;

    // can be 'm' of 'f'
    // TODO: change to enumeration
    @Parsed(field = "Gender")
    private String gender;


    @Parsed(field = "BirthDate")
    @Format(formats = {"yyyy-MM-dd"})
    private Date dateOfBirth;

    @Parsed(field = "BirthYear")
    private int yearOfBirth;

    // Date of enrollment into RadPlanBio study;
    @Parsed(field = "Enrollment")
    @Format(formats = {"yyyy-MM-dd"})
    private Date enrollmentDate;

    @Parsed(field = "Status")
    private String status;

    //    @Parsed(field = "Is_Enabled")
    private Boolean isEnabled;


    public SubjectAttributes() {
        this.sequenceNum = new Double(0);
    }

    @Override
    public String toString() {
        return "SubjectAttributes{" +
                "sequenceNum=" + sequenceNum +
                ", studySubjectId='" + studySubjectId + '\'' +
                ", uniqueIdentifier='" + uniqueIdentifier + '\'' +
                ", secondaryId='" + secondaryId + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", yearOfBirth=" + yearOfBirth +
                ", enrollmentDate=" + enrollmentDate +
                ", status='" + status + '\'' +
                '}';
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Double sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getStudySubjectId() {
        return studySubjectId;
    }

    public void setStudySubjectId(String studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
