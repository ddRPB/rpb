package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OdmBuilderDirector.class, Logger.class})
public class OdmBuilderDirectorTest {
    private OdmBuilderDirector odmBuilderDirector;
    private Odm odm;
    private String dicomStudyInstanceItemOid = "DummyDicomStudyInstanceItemOid";
    private String dicomStudyInstanceItemValue = "DummyDicomStudyInstanceItemValue";
    private String dicomPatientIdItemOid = "DummyDicomPatienIdItemOid";
    private String dicomPatienIdItemValue = "DummyDicomPatienIdItemValue";
    private String itemGroupOid = "DummyItemGroupOid";
    private String formOid = "DummyFormOid";
    private String studyEventOid = "DummyStudyEventOid";
    private String studyEventRepeatKey = "DummyStudyEventRepeatKey";
    private String subjectKey = "DummySubjectKey";
    private String studyOid = "DummyStudyOi";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
        this.odmBuilderDirector = new OdmBuilderDirector(OdmBuilder.getInstance());
        try {
            this.odm = odmBuilderDirector.buildUpdateCrfAnnotationOdm(
                    this.dicomStudyInstanceItemOid,
                    this.dicomStudyInstanceItemValue,
                    this.dicomPatientIdItemOid,
                    this.dicomPatienIdItemValue,
                    this.itemGroupOid,
                    this.formOid,
                    this.studyEventOid,
                    this.studyEventRepeatKey,
                    this.subjectKey,
                    this.studyOid
            );
        } catch (MissingPropertyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void odm_has_StudyOid() {
        assertEquals(this.studyOid, odm.getClinicalDataList().get(0).getStudyOid());
    }

    @Test
    public void odm_has_SubjectKey() {
        StudySubject subject = this.odm.getClinicalDataList().get(0).getStudySubjects().get(0);
        assertEquals(this.subjectKey, subject.getSubjectKey());
    }

    @Test
    public void odm_has_StudyEventOid() {
        EventData eventData = this.odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0);
        assertEquals(this.studyEventOid, eventData.getStudyEventOid());
    }

    @Test
    public void odm_has_StudyEventRepeatKey() {
        EventData eventData = this.odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0);
        assertEquals(this.studyEventRepeatKey, eventData.getStudyEventRepeatKey());
    }

    @Test
    public void odm_has_FormOid() {
        FormData formData = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0);
        assertEquals(this.formOid, formData.getFormOid());
    }

    @Test
    public void odm_has_ItemGroupOid() {
        ItemGroupData itemGroupData = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0);
        assertEquals(this.itemGroupOid, itemGroupData.getItemGroupOid());
    }

    @Test
    public void odm_has_DicomStudyInstanceItemOid() {
        ItemData firstItemDataObject = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0).getItemDataList().get(0);
        assertEquals(this.dicomStudyInstanceItemOid, firstItemDataObject.getItemOid());
    }

    @Test
    public void odm_has_DicomStudyInstanceItemValue() {
        ItemData firstItemDataObject = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0).getItemDataList().get(0);
        assertEquals(this.dicomStudyInstanceItemValue, firstItemDataObject.getValue());
    }

    @Test
    public void odm_has_DicomPatientIdItemOid() {
        ItemData secondItemDataObject = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0).getItemDataList().get(1);
        assertEquals(this.dicomPatientIdItemOid, secondItemDataObject.getItemOid());
    }

    @Test
    public void odm_has_DicomPatienIdItemValue() {
        ItemData secondItemDataObject = odm.getClinicalDataList().get(0).getStudySubjects().get(0).getStudyEventDataList().get(0).getFormDataList().get(0).getItemGroupDataList().get(0).getItemDataList().get(1);
        assertEquals(this.dicomPatienIdItemValue, secondItemDataObject.getValue());
    }
}