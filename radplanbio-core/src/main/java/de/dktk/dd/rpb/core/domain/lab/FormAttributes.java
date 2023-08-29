package de.dktk.dd.rpb.core.domain.lab;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;
import static java.lang.String.valueOf;

/**
 * Represents the information of a row in the LabKey FormAttributes table
 */
public class FormAttributes {

    private Double sequenceNum;
    private String studySubjectId;
    private String studyEventOid;
    private String studyEventRepeatKey;
    private String formOid;
    private String formVersion;
    private String interviewerName;
    private LocalDate interviewDate;
    private String status;


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

    public String getStudyEventOid() {
        return studyEventOid;
    }

    public void setStudyEventOid(String studyEventOid) {
        this.studyEventOid = studyEventOid;
    }

    public String getStudyEventRepeatKey() {
        return studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String studyEventRepeatKey) {
        this.studyEventRepeatKey = studyEventRepeatKey;
    }

    public String getFormOid() {
        return formOid;
    }

    public void setFormOid(String formOid) {
        this.formOid = formOid;
    }

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVersion) {
        this.formVersion = formVersion;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * CellProcessors for the tsv writer
     *
     * @return CellProcessor[]
     */
    public static CellProcessor[] getCellProcessors() {
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            cellProcessorList.add(new Optional());
        }
        CellProcessor[] array = new CellProcessor[cellProcessorList.size()];

        return cellProcessorList.toArray(array);
    }

    public static String[] getHeaders(String subjectColumnName){
        List<String> headerList = new ArrayList<>();
        headerList.add(LABKEY_SEQUENCE_NUMBER);
        headerList.add(subjectColumnName);
        headerList.add(LABKEY_STUDY_EVENT_OID);
        headerList.add(LABKEY_STUDY_EVENT_REPEAT_KEY);
        headerList.add(LABKEY_FORM_OID);
        headerList.add(LABKEY_FORM_VERSION);
        headerList.add(LABKEY_INTERVIEWER_NAME);
        headerList.add(LABKEY_INTERVIEW_DATE);
        headerList.add(LABKEY_STATUS);

        String[] headerArray = new String[headerList.size()];
        headerList.toArray(headerArray);

        return headerArray;
    }

    /**
     * List of value objects for the export to a tsv writer
     *
     * @return List<Object>
     */
    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNum));
        valueList.add(this.studySubjectId);
        valueList.add(this.studyEventOid);
        valueList.add(this.studyEventRepeatKey);
        valueList.add(this.formOid);
        valueList.add(this.formVersion);
        valueList.add(this.interviewerName);
        valueList.add(this.interviewDate);
        valueList.add(this.status);

        return valueList;
    }

    @Override
    public String toString() {
        return "FormAttributes{" +
                "sequenceNum=" + sequenceNum +
                ", studySubjectId='" + studySubjectId + '\'' +
                ", studyEventOid='" + studyEventOid + '\'' +
                ", studyEventRepeatKey='" + studyEventRepeatKey + '\'' +
                ", formOid='" + formOid + '\'' +
                ", formVersion='" + formVersion + '\'' +
                ", interviewerName='" + interviewerName + '\'' +
                ", interviewDate=" + interviewDate +
                ", status='" + status + '\'' +
                '}';
    }
}
