package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, LoggerFactory.class})
public class StudyEventConverterTest {

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

    }

    @Test
    public void creates_SequenceNumber_for_repeating_events() throws MissingPropertyException {

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
        eventData.setStartDate("");
        eventData.setEndDate("");

        EventAttributes eventAttributes = StudyEventConverter.convertToLabkey(eventData, "dummyStudyEventId");

        Double sequenceNumber = eventAttributes.getSequenceNum();
        assertEquals(new Double("4.0002"), sequenceNumber);
    }

    @Test
    public void creates_SequenceNumber_for_non_repeating_events() throws MissingPropertyException {

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
        eventData.setStartDate("");
        eventData.setEndDate("");

        EventAttributes eventAttributes = StudyEventConverter.convertToLabkey(eventData, "dummyStudyEventId");

        Double sequenceNumber = eventAttributes.getSequenceNum();
        assertEquals(new Double("4.0"), sequenceNumber);
    }
}
