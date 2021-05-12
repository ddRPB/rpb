package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Coordinates building of Odm objects
 */
public class OdmBuilderDirector {
    private static final Logger log = Logger.getLogger(OdmBuilderDirector.class);
    private IOdmBuilder odmBuilder;

    public void setOdmBuilder(IOdmBuilder odmBuilder) {
        this.odmBuilder = odmBuilder;
    }

    public OdmBuilderDirector(IOdmBuilder odmBuilder) {
        this.odmBuilder = odmBuilder;
    }

    /**
     * Builds an Odm object that can be used to update or create an CRF annotation
     *
     * @param dicomStudyInstanceItemOid   String
     * @param dicomStudyInstanceItemValue String
     * @param dicomPatienIdItemOid        String
     * @param dicomPatienIdItemValue      String
     * @param itemGroupOid                String
     * @param formOid                     String
     * @param studyEventOid               String
     * @param studyEventRepeatKey         String
     * @param subjectKey                  String
     * @param studyOid                    String
     * @return Odm
     */
    public Odm buildUpdateCrfAnnotationOdm(
            String dicomStudyInstanceItemOid,
            String dicomStudyInstanceItemValue,
            String dicomPatienIdItemOid,
            String dicomPatienIdItemValue,
            String itemGroupOid,
            String formOid,
            String studyEventOid,
            String studyEventRepeatKey,
            String subjectKey,
            String studyOid
    ) throws MissingPropertyException {

        ClinicalData newClinicalData = getClinicalData(dicomStudyInstanceItemOid, dicomStudyInstanceItemValue, dicomPatienIdItemOid, dicomPatienIdItemValue, itemGroupOid, formOid, studyEventOid, studyEventRepeatKey, subjectKey, studyOid);
        return this.getOdm(newClinicalData);
    }

    private ClinicalData getClinicalData(String dicomStudyInstanceItemOid, String dicomStudyInstanceItemValue, String dicomPatienIdItemOid, String dicomPatienIdItemValue, String itemGroupOid, String formOid, String studyEventOid, String studyEventRepeatKey, String subjectKey, String studyOid) throws MissingPropertyException {
        ItemData dicomStudyInstanceUidData = getItemData(dicomStudyInstanceItemOid, dicomStudyInstanceItemValue);
        ItemData dicomPatientIdData = getItemData(dicomPatienIdItemOid, dicomPatienIdItemValue);

        ItemGroupData itemGroupData = getItemGroupData(itemGroupOid, dicomStudyInstanceUidData, dicomPatientIdData);
        FormData formData = getFormData(formOid, itemGroupData);
        EventData newEventData = getEventData(studyEventOid, studyEventRepeatKey, formData);
        StudySubject studySubject = getStudySubject(subjectKey, newEventData);
        return getClinicalData(studyOid, studySubject);
    }

    private Odm getOdm(ClinicalData newClinicalData) {
        OdmBuilder odmBuilder = OdmBuilder.getInstance();
        return odmBuilder.
                addClinicalData(newClinicalData).
                build();
    }

    private ClinicalData getClinicalData(String studyOid, StudySubject studySubject) throws MissingPropertyException {
        ClinicalDataBuilder clinicalDataBuilder = ClinicalDataBuilder.getInstance();
        return clinicalDataBuilder.
                setStudyOid(studyOid).
                addSubjectData(studySubject).
                build();
    }

    private StudySubject getStudySubject(String subjectKey, EventData newEventData) throws MissingPropertyException {
        SubjectDataBuilder subjectDataBuilder = SubjectDataBuilder.getInstance();
        return subjectDataBuilder.
                setSubjectKey(subjectKey).
                addStudyEventData(newEventData).
                build();
    }

    private EventData getEventData(String studyEventOid, String studyEventRepeatKey, FormData formData) {
        EventData newEventData = new EventData();
        newEventData.setStudyEventOid(studyEventOid);
        newEventData.setStudyEventRepeatKey(studyEventRepeatKey);
        newEventData.setFormDataList(new ArrayList<FormData>());
        newEventData.getFormDataList().add(formData);
        return newEventData;
    }

    private FormData getFormData(String formOid, ItemGroupData itemGroupData) throws MissingPropertyException {
        FormDataBuilder formDataBuilder = FormDataBuilder.getInstance();
        return formDataBuilder.
                setFormOid(formOid).
                addItemGroupData(itemGroupData).
                build();
    }

    private ItemGroupData getItemGroupData(String itemGroupOid, ItemData dicomStudyInstanceUidData, ItemData dicomPatienIdData) throws MissingPropertyException {
        ItemGroupDataBuilder itemGroupDataBuilder = ItemGroupDataBuilder.getInstance();
        return itemGroupDataBuilder.
                setItemGroupOid(itemGroupOid).
                addItemData(dicomStudyInstanceUidData).
                addItemData(dicomPatienIdData).
                build();
    }

    private ItemData getItemData(String dicomStudyInstanceItemOid, String dicomStudyInstanceItemValue) {
        ItemData dicomStudyInstanceUidData = new ItemData();
        dicomStudyInstanceUidData.setItemOid(dicomStudyInstanceItemOid);
        dicomStudyInstanceUidData.setValue(dicomStudyInstanceItemValue);
        return dicomStudyInstanceUidData;
    }
}