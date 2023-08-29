package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.lab.FormAttributes;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FormDataConverter.class, OdmEventMetaDataLookup.class, Logger.class,LoggerFactory.class})
public class FormDataConverterTest {
    private String sequenceNumber = "dummy sequence number";
    private String studySubjectId = "DummyStudySubjectId";
    private String studyEventOid = "DummyStudyEventOid";
    private String studyEventName = "DummyStudyEventName";
    private Integer studyEventOrdinal = 1;
    private String studyEventRepeatKey = "7";
    private String formOid = "DummyFormOid";
    private String formVersion = "DummyFormVersion";
    private String interviewDate="2017-03-09";
    private String interviewerName = "Karl Maria Mueller";
    private String status = "DummyStatus";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT);

    private FormDataConverter converter;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        converter = new FormDataConverter(studySubjectId, getDummyEventData(), this.getDummyOdmEventMetaDataLookup(false));
    }

    // region exceptions handling

    @Test(expected = MissingPropertyException.class)
    public void throws_if_studySubjectId_is_null() throws MissingPropertyException {
        new FormDataConverter(null, getDummyEventData(), this.getDummyOdmEventMetaDataLookup(false));
    }

    @Test(expected = MissingPropertyException.class)
    public void throws_if_studySubjectId_is_empty() throws MissingPropertyException {
        new FormDataConverter("", getDummyEventData(), this.getDummyOdmEventMetaDataLookup(false));
    }

    @Test(expected = MissingPropertyException.class)
    public void throws_if_event_data_is_null() throws MissingPropertyException {
        new FormDataConverter(studySubjectId, null, this.getDummyOdmEventMetaDataLookup(false));
    }

    @Test(expected = MissingPropertyException.class)
    public void throws_if_odm_lookup_is_null() throws MissingPropertyException {
        new FormDataConverter(studySubjectId, getDummyEventData(), null);
    }

    // end region

    // region convertToFormAttributes

    @Test
    public void convertToFormAttributes_creates_a_valid_object_for_not_repeating_events() throws MissingPropertyException, ParseException {
        converter = new FormDataConverter(studySubjectId, getDummyEventData(), this.getDummyOdmEventMetaDataLookup(false));
        FormAttributes formAttributes =converter.convertToFormAttributes(getDummyFormData());

        assertNotNull(formAttributes);
        assertEquals(new Double(1.0),formAttributes.getSequenceNum() );
        assertEquals(this.studySubjectId,formAttributes.getStudySubjectId());
        assertEquals(this.studyEventOid, formAttributes.getStudyEventOid());
        assertEquals(this.studyEventRepeatKey, formAttributes.getStudyEventRepeatKey());
        assertEquals(this.formOid, formAttributes.getFormOid());
        assertEquals(this.formVersion, formAttributes.getFormVersion());
        assertEquals(this.interviewerName, formAttributes.getInterviewerName());
        assertEquals(LocalDate.parse(this.interviewDate,formatter), formAttributes.getInterviewDate());
        assertEquals(this.status, formAttributes.getStatus());

    }

    @Test
    public void convertToFormAttributes_creates_a_valid_object_for_repeating_events() throws MissingPropertyException, ParseException {
        converter = new FormDataConverter(studySubjectId, getDummyEventData(), this.getDummyOdmEventMetaDataLookup(true));
        FormAttributes formAttributes =converter.convertToFormAttributes(getDummyFormData());

        assertNotNull(formAttributes);
        assertEquals(new Double(1.0007),formAttributes.getSequenceNum() );
        assertEquals(this.studySubjectId,formAttributes.getStudySubjectId());
        assertEquals(this.studyEventOid, formAttributes.getStudyEventOid());
        assertEquals(this.studyEventRepeatKey, formAttributes.getStudyEventRepeatKey());
        assertEquals(this.formOid, formAttributes.getFormOid());
        assertEquals(this.formVersion, formAttributes.getFormVersion());
        assertEquals(this.interviewerName, formAttributes.getInterviewerName());
        assertEquals(LocalDate.parse(this.interviewDate, formatter), formAttributes.getInterviewDate());
        assertEquals(this.status, formAttributes.getStatus());

    }


    // end region

    private FormData getDummyFormData(){
        FormData formData = new FormData();
        formData.setFormOid(formOid);
        formData.setVersion(formVersion);
        formData.setInterviewerName(interviewerName);
        formData.setInterviewDate(interviewDate);
        formData.setStatus(status);

        return formData;
    }

    private EventData getDummyEventData(){
        EventData eventData = new EventData();
        eventData.setStudyEventOid(studyEventOid);
        eventData.setStudyEventRepeatKey(studyEventRepeatKey);
        return eventData;
    }

    private OdmEventMetaDataLookup getDummyOdmEventMetaDataLookup(boolean isRepeating){
        mockStatic(OdmEventMetaDataLookup.class);
        OdmEventMetaDataLookup lookup = mock(OdmEventMetaDataLookup.class);
        when(lookup.getStudyEventIsRepeating(any(String.class))).thenReturn(isRepeating);
        when(lookup.getStudyEventOrdinal((any(String.class)))).thenReturn(studyEventOrdinal);
        when(lookup.getStudyEventName(any(String.class))).thenReturn(studyEventName);

        return lookup;
    }

}
