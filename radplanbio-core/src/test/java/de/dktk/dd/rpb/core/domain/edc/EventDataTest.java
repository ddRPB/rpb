package de.dktk.dd.rpb.core.domain.edc;

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
@PrepareForTest({ Logger.class, LoggerFactory.class})
public class EventDataTest {
    private EventData eventData;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        eventData = new EventData();
        eventData.setStudyEventOid("b1");
        eventData.setStudyEventRepeatKey("2");
    }

//    region compareTo

    @Test
    public void is_less_if_repeat_key_of_argument_is_null_and_object_has_the_same_oid_and_a_repeat_key() {
        EventData o = new EventData();
        o.setStudyEventOid("b1");

        assertEquals(1, eventData.compareTo(o));
    }

    @Test
    public void is_the_same_if_repeat_key_of_argument_is_null_and_object_has_the_same_oid_and_repeat_key_is_null_as_well() {
        eventData.setStudyEventRepeatKey(null);
        EventData o = new EventData();
        o.setStudyEventOid("b1");

        assertEquals(0, eventData.compareTo(o));
    }

    @Test
    public void object_oid_is_bigger() {
        EventData o = new EventData();
        o.setStudyEventOid("a1");

        assertEquals(1, eventData.compareTo(o));
    }


    @Test
    public void object_oid_is_less() {
        EventData o = new EventData();
        o.setStudyEventOid("c1");

        assertEquals(-1, eventData.compareTo(o));
    }

    @Test
    public void repeat_key_is_bigger() {
        EventData o = new EventData();
        o.setStudyEventOid("b1");
        o.setStudyEventRepeatKey("1");

        assertEquals(1, eventData.compareTo(o));
    }

    @Test
    public void repeat_key_is_less() {
        EventData o = new EventData();
        o.setStudyEventOid("b1");
        o.setStudyEventRepeatKey("3");

        assertEquals(-1, eventData.compareTo(o));
    }

    @Test
    public void repeat_key_is_the_same() {
        EventData o = new EventData();
        o.setStudyEventOid("b1");
        o.setStudyEventRepeatKey("2");

        assertEquals(0, eventData.compareTo(o));
    }

    //    endregion

}
