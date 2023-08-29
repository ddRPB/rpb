package de.dktk.dd.rpb.core.domain.lab;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;
import static java.lang.String.valueOf;

/**
 * Contains attributes of the event representation in Labkey
 * including the mapping annotations for the tsv writer.
 */
public class EventAttributes {

    private Double sequenceNum;

    private String studySubjectId;

    private String studyEventOid;

    private String eventName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String systemStatus;

    private String studyEventRepeatKey;

    private String type;

    public EventAttributes() {
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

    public String getStudyEventOid() {
        return studyEventOid;
    }

    public void setStudyEventOid(String studyEventOid) {
        this.studyEventOid = studyEventOid;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getStudyEventRepeatKey() {
        return studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String studyEventRepeatKey) {
        this.studyEventRepeatKey = studyEventRepeatKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static CellProcessor[] getCellProcessors(){
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
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
        headerList.add(LABKEY_EVENT_NAME);
        headerList.add(LABKEY_START_DATE);
        headerList.add(LABKEY_END_DATE);
        headerList.add(LABKEY_STATUS);
        headerList.add(LABKEY_SYSTEM_STATUS);
        headerList.add(LABKEY_STUDY_EVENT_REPEAT_KEY);
        headerList.add(LABKEY_TYPE);

        String[] headerArray = new String[headerList.size()];
        headerList.toArray(headerArray);

        return headerArray;
    }

    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNum));
        valueList.add(this.studySubjectId);
        valueList.add(this.studyEventOid);
        valueList.add(this.eventName);
        valueList.add(this.startDate);
        valueList.add(this.endDate);
        valueList.add(this.status);
        valueList.add(this.systemStatus);
        valueList.add(this.studyEventRepeatKey);
        valueList.add(this.type);

        return valueList;
    }

    @Override
    public String toString() {
        return "EventAttributes{" +
                "sequenceNum=" + sequenceNum +
                ", studySubjectId='" + studySubjectId + '\'' +
                ", studyEventOid='" + studyEventOid + '\'' +
                ", eventName='" + eventName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                ", studyEventRepeatKey='" + studyEventRepeatKey + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
