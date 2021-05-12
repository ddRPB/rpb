package de.dktk.dd.rpb.core.domain.lab;

import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;

import java.util.Date;

/**
 * Contains attributes of the event representation in Labkey
 * including the mapping annotations for the tsv writer.
 */
public class EventAttributes {

    @Parsed(field = "SequenceNum")
    private Double sequenceNum;

    @Parsed(field = "ParticipantId")
    private String studySubjectId;

    @Parsed(field = "StudyEventOID")
    private String studyEventOid;

    @Parsed(field = "EventName")
    private String eventName;

    @Parsed(field = "StartDate")
    @Format(formats = {"yyyy-MM-dd"})
    private Date startDate;

    @Parsed(field = "EndDate")
    @Format(formats = {"yyyy-MM-dd"})
    private Date endDate;

    @Parsed(field = "Status")
    private String status;

    @Parsed(field = "System Status")
    private String systemStatus;

    @Parsed(field = "StudyEventRepeatKey")
    private String studyEventRepeatKey;

    @Parsed(field = "Type")
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
