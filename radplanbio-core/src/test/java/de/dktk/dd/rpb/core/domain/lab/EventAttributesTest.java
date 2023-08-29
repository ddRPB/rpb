package de.dktk.dd.rpb.core.domain.lab;

import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.OC_TIMESTAMPFORMAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventAttributesTest {

    private final Double sequenceNum = new Double(1);
    private final String studySubjectId = "studySubjectId";
    private final Integer id = 11;
    private final String studyEventOid = "Study Event Oid";
    private final String eventName = "Event Name";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OC_TIMESTAMPFORMAT);
    private final LocalDate startDate = LocalDate.parse("2020-01-19 01:23:46.0",formatter);
    private final LocalDate endDate = LocalDate.parse("2020-01-25 01:23:46.0",formatter);
    private final String status = "status";
    private final String studyEventRepeatKey = "Study Event Repeat Key";
    private final String systemStatus = "dummy system status";
    private final String type = "dummy type";


    public EventAttributesTest() throws ParseException {
    }

    @Test
    public void getCellProcessors_returns_correct_items() {
        EventAttributes event = getLabkeyDummyEvent();
        CellProcessor[] cellProcessors = event.getCellProcessors();
        assertNotNull(cellProcessors);
        assertEquals(10, cellProcessors.length);

    }

    @Test
    public void getValues_returns_correct_items() {

        EventAttributes event = getLabkeyDummyEvent();
        List<Object> values = event.getValues();

        assertNotNull(values);
        assertEquals(10, values.size());
        values.get(1).equals(event.getSequenceNum());
        values.get(0).equals(event.getStudySubjectId());
        values.get(2).equals(event.getStudyEventOid());
        values.get(3).equals(event.getEventName());
        values.get(4).equals(event.getStartDate());
        values.get(5).equals(event.getEndDate());
        values.get(6).equals(event.getStatus());
        values.get(7).equals(event.getSystemStatus());
        values.get(8).equals(event.getStudyEventRepeatKey());
        values.get(8).equals(event.getType());

    }

    private EventAttributes getLabkeyDummyEvent() {
        EventAttributes event = new EventAttributes();
        event.setSequenceNum(sequenceNum);
        event.setStudySubjectId(studySubjectId);
        event.setStudyEventOid(studyEventOid);
        event.setEventName(eventName);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setStatus(status);
        event.setSystemStatus(systemStatus);
        event.setStudyEventRepeatKey(studyEventRepeatKey);
        event.setType(type);

        return event;
    }
}