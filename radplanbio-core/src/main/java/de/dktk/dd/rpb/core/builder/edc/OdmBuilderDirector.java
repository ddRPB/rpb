package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.pacs.DicomUploadSlot;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Coordinates building of Odm objects
 */
public class OdmBuilderDirector {
    private static final Logger log = LoggerFactory.getLogger(OdmBuilderDirector.class);
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
     * @param dicomPatientIdItemOid       String
     * @param dicomPatientIdItemValue     String
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
            String dicomPatientIdItemOid,
            String dicomPatientIdItemValue,
            String itemGroupOid,
            String formOid,
            String studyEventOid,
            String studyEventRepeatKey,
            String subjectKey,
            String studyOid
    ) throws MissingPropertyException {

        ClinicalData newClinicalData = getClinicalData(
                dicomStudyInstanceItemOid,
                dicomStudyInstanceItemValue,
                dicomPatientIdItemOid,
                dicomPatientIdItemValue,
                itemGroupOid,
                formOid,
                studyEventOid,
                studyEventRepeatKey,
                subjectKey,
                studyOid
        );
        return this.getOdm(newClinicalData);
    }

    public Odm buildUpdateCrfAnnotationOdm(
            DicomUploadSlot dicomUploadSlot
    ) throws MissingPropertyException {

        ClinicalData newClinicalData = getClinicalData(
                dicomUploadSlot.getDicomStudyInstanceItemOid(),
                dicomUploadSlot.getDicomStudyInstanceItemValue(),
                dicomUploadSlot.getDicomPatientIdItemOid(),
                dicomUploadSlot.getDicomPatientIdItemValue(),
                dicomUploadSlot.getItemGroupOid(),
                dicomUploadSlot.getFormOid(),
                dicomUploadSlot.getStudyEventOid(),
                dicomUploadSlot.getStudyEventRepeatKey(),
                dicomUploadSlot.getSubjectKey(),
                dicomUploadSlot.getStudyOid()
        );

        return this.getOdm(newClinicalData);
    }

    private ClinicalData getClinicalData(
            String dicomStudyInstanceItemOid,
            String dicomStudyInstanceItemValue,
            String dicomPatientIdItemOid,
            String dicomPatientIdItemValue,
            String itemGroupOid,
            String formOid,
            String studyEventOid,
            String studyEventRepeatKey,
            String subjectKey,
            String studyOid
    ) throws MissingPropertyException {

        ItemData dicomStudyInstanceUidData = getItemData(dicomStudyInstanceItemOid, dicomStudyInstanceItemValue);
        ItemData dicomPatientIdData = getItemData(dicomPatientIdItemOid, dicomPatientIdItemValue);

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

    private ItemGroupData getItemGroupData(
            String itemGroupOid,
            ItemData dicomStudyInstanceUidData,
            ItemData dicomPatientIdData
    ) throws MissingPropertyException {
        ItemGroupDataBuilder itemGroupDataBuilder = ItemGroupDataBuilder.getInstance();
        return itemGroupDataBuilder.
                setItemGroupOid(itemGroupOid).
                addItemData(dicomStudyInstanceUidData).
                addItemData(dicomPatientIdData).
                build();
    }

    private ItemData getItemData(String dicomStudyInstanceItemOid, String dicomStudyInstanceItemValue) {
        ItemData dicomStudyInstanceUidData = new ItemData();
        dicomStudyInstanceUidData.setItemOid(dicomStudyInstanceItemOid);
        dicomStudyInstanceUidData.setValue(dicomStudyInstanceItemValue);
        return dicomStudyInstanceUidData;
    }
}
