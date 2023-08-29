package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SubjectDataBuilder.class, Logger.class, LoggerFactory.class})
public class SubjectDataBuilderTest {
    private SubjectDataBuilder subjectDataBuilder;
    private final String dummmySubjectkey = "dummmySubjectkey";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        this.subjectDataBuilder = SubjectDataBuilder.getInstance();
        this.subjectDataBuilder.setSubjectKey(this.dummmySubjectkey);
    }

    @Test
    public void get_instance() {
        assertNotNull(this.subjectDataBuilder);
    }

    @Test(expected = MissingPropertyException.class)
    public void build_returns_a_valid_clinicalData_instance() throws MissingPropertyException {
        subjectDataBuilder.setSubjectKey(null);
        this.subjectDataBuilder.build();
    }

    @Test
    public void addStudyEventData_adds_an_event_to_subject() throws MissingPropertyException {
        EventData eventData = new EventData();
        eventData.setStudyEventOid("dummyStudyEventOid");
        StudySubject studySubject = this.subjectDataBuilder.addStudyEventData(eventData).build();
        assertEquals(eventData, studySubject.getStudyEventDataList().get(0));
    }

    @Test
    public void addStudyEventData_adds_an_event_to_subject_once() throws MissingPropertyException {
        EventData eventData = new EventData();
        eventData.setStudyEventOid("dummyStudyEventOid");
        StudySubject studySubject = this.subjectDataBuilder.
                addStudyEventData(eventData).
                build();
        assertEquals(1, studySubject.getStudyEventDataList().size());
    }
}
