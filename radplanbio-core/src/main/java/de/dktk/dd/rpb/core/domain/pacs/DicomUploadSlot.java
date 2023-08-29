package de.dktk.dd.rpb.core.domain.pacs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DicomUploadSlot {
    private String patientId;
    private String dicomStudyIdentifier;
    private String dicomSeriesIdentifier;
    private String dicomStudyInstanceItemOid;
    private String dicomStudyInstanceItemValue;
    private String dicomPatientIdItemOid;
    private String dicomPatientIdItemValue;
    private String itemGroupOid;
    private String formOid;
    private String studyEventOid;
    private String studyEventRepeatKey;
    private String studyIdentifier;
    private String siteIdentifier;
    private String subjectKey;
    private String subjectId;
    private String studyOid;

    // region constructor
    public DicomUploadSlot() {
    }

    public DicomUploadSlot(
            String patientId,
            String dicomStudyIdentifier,
            String dicomSeriesIdentifier,
            String dicomStudyInstanceItemOid,
            String dicomStudyInstanceItemValue,
            String dicomPatientIdItemOid,
            String dicomPatientIdItemValue,
            String itemGroupOid,
            String formOid,
            String studyEventOid,
            String studyEventRepeatKey,
            String studyIdentifier,
            String siteIdentifier,
            String subjectKey,
            String subjectId,
            String studyOid
    ) {
        this.patientId = patientId;
        this.dicomStudyIdentifier = dicomStudyIdentifier;
        this.dicomSeriesIdentifier = dicomSeriesIdentifier;
        this.dicomStudyInstanceItemOid = dicomStudyInstanceItemOid;
        this.dicomStudyInstanceItemValue = dicomStudyInstanceItemValue;
        this.dicomPatientIdItemOid = dicomPatientIdItemOid;
        this.dicomPatientIdItemValue = dicomPatientIdItemValue;
        this.itemGroupOid = itemGroupOid;
        this.formOid = formOid;
        this.studyEventOid = studyEventOid;
        this.studyEventRepeatKey = studyEventRepeatKey;
        this.subjectKey = subjectKey;
        this.subjectId = subjectId;
        this.studyIdentifier = studyIdentifier;
        this.siteIdentifier = siteIdentifier;
        this.studyOid = studyOid;
    }

    // endregion

    // region Getter and Setter


    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDicomStudyIdentifier() {
        return dicomStudyIdentifier;
    }

    public void setDicomStudyIdentifier(String dicomStudyIdentifier) {
        this.dicomStudyIdentifier = dicomStudyIdentifier;
    }

    public String getDicomSeriesIdentifier() {
        return dicomSeriesIdentifier;
    }

    public void setDicomSeriesIdentifier(String dicomSeriesIdentifier) {
        this.dicomSeriesIdentifier = dicomSeriesIdentifier;
    }

    public String getDicomStudyInstanceItemOid() {
        return dicomStudyInstanceItemOid;
    }

    public void setDicomStudyInstanceItemOid(String dicomStudyInstanceItemOid) {
        this.dicomStudyInstanceItemOid = dicomStudyInstanceItemOid;
    }

    public String getDicomStudyInstanceItemValue() {
        return dicomStudyInstanceItemValue;
    }

    public void setDicomStudyInstanceItemValue(String dicomStudyInstanceItemValue) {
        this.dicomStudyInstanceItemValue = dicomStudyInstanceItemValue;
    }

    public String getDicomPatientIdItemOid() {
        return dicomPatientIdItemOid;
    }

    public void setDicomPatientIdItemOid(String dicomPatientIdItemOid) {
        this.dicomPatientIdItemOid = dicomPatientIdItemOid;
    }

    public String getDicomPatientIdItemValue() {
        return dicomPatientIdItemValue;
    }

    public void setDicomPatientIdItemValue(String dicomPatientIdItemValue) {
        this.dicomPatientIdItemValue = dicomPatientIdItemValue;
    }

    public String getItemGroupOid() {
        return itemGroupOid;
    }

    public void setItemGroupOid(String itemGroupOid) {
        this.itemGroupOid = itemGroupOid;
    }

    public String getFormOid() {
        return formOid;
    }

    public void setFormOid(String formOid) {
        this.formOid = formOid;
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

    public String getSubjectKey() {
        return subjectKey;
    }

    public void setSubjectKey(String subjectKey) {
        this.subjectKey = subjectKey;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStudyOid() {
        return studyOid;
    }

    public void setStudyOid(String studyOid) {
        this.studyOid = studyOid;
    }

    public String getStudyIdentifier() {
        return studyIdentifier;
    }

    public void setStudyIdentifier(String studyIdentifier) {
        this.studyIdentifier = studyIdentifier;
    }

    public String getSiteIdentifier() {
        return siteIdentifier;
    }

    public void setSiteIdentifier(String siteIdentifier) {
        this.siteIdentifier = siteIdentifier;
    }

    // endregion
}
