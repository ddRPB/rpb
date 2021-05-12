package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StudyEventConverterTest {
    @Test
    public void creates_SequenceNumber_for_repeating_events() {

        EventDefinition definition = new EventDefinition();
        definition.setIsRepeating(true);
        definition.setOrdinal(4);
        definition.setName("dummyName");
        definition.setDescription("dummyDescription");
        definition.setCategory("DummyCategorie");

        EventData eventData = new EventData();
        eventData.setId(1);
        eventData.setStudyEventRepeatKey("2");
        eventData.setEventDefinition(definition);

        EventAttributes eventAttributes = StudyEventConverter.convertToLabkey(eventData, "dummyStudyEventId");

        Double sequenceNumber = eventAttributes.getSequenceNum();
        assertEquals(new Double("4.0002"), sequenceNumber);
    }

    @Test
    public void creates_SequenceNumber_for_non_repeating_events() {

        EventDefinition definition = new EventDefinition();
        definition.setIsRepeating(false);
        definition.setOrdinal(4);
        definition.setName("dummyName");
        definition.setDescription("dummyDescription");
        definition.setCategory("DummyCategorie");

        EventData eventData = new EventData();
        eventData.setId(1);
        eventData.setStudyEventRepeatKey("2");
        eventData.setEventDefinition(definition);

        EventAttributes eventAttributes = StudyEventConverter.convertToLabkey(eventData, "dummyStudyEventId");

        Double sequenceNumber = eventAttributes.getSequenceNum();
        assertEquals(new Double("4.0"), sequenceNumber);
    }
}