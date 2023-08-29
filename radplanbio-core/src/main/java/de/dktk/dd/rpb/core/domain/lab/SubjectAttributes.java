package de.dktk.dd.rpb.core.domain.lab;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Contains attributes of the subject representation in Labkey
 * including the mapping annotations for the tsv writer.
 */
public class SubjectAttributes {
    private Double sequenceNum;

    // Study Subject ID
    private String studySubjectId;

    // Person ID (PID) - pseudo identificator
    private String uniqueIdentifier;

    // Secondary ID
    private String secondaryId;

    // can be 'm' of 'f'
    // TODO: change to enumeration
    private String gender;


    private LocalDate dateOfBirth;

    private int yearOfBirth;

    // Date of enrollment into RadPlanBio study;
    private LocalDate enrollmentDate;

    private String status;

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
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

    public CellProcessor[] getCellProcessors() {
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < this.getValues().size(); i++) {
            cellProcessorList.add(new Optional());
        }
        CellProcessor[] array = new CellProcessor[cellProcessorList.size()];

        return cellProcessorList.toArray(array);
    }

    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNum));
        valueList.add(this.studySubjectId);
        valueList.add(this.uniqueIdentifier);
        valueList.add(this.secondaryId);
        if (this.gender != null) {
            valueList.add(this.gender);
        }
        if (this.dateOfBirth != null) {
            valueList.add(this.dateOfBirth);
        }
        if (this.yearOfBirth > 0) {
            valueList.add(this.yearOfBirth);
        }
        valueList.add(this.enrollmentDate);
        valueList.add(this.status);

        return valueList;
    }

}
