package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, Logger.class})
public class StudySubjectListUtilTest {

    @Before
    public void setUp() throws Exception {
        // prepare logger
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void getPidKeyHashMap_returns_empty_map_if_list_is_empty() {
        List<StudySubject> subjectList = new ArrayList<>();

        Map<String, StudySubject> subjectMap = StudySubjectListUtil.getPidKeyHashMap(subjectList);

        assertTrue(subjectMap.isEmpty());
    }

    @Test
    public void getPidKeyHashMap_returns_empty_map_if_list_is_null() {

        Map<String, StudySubject> subjectMap = StudySubjectListUtil.getPidKeyHashMap(null);

        assertTrue(subjectMap.isEmpty());
    }

    @Test
    public void getPidKeyHashMap_creates_hash_map_with_one_element() {
        List<StudySubject> subjectList = new ArrayList<>();
        StudySubject subjectOne = new StudySubject();
        String pidOne = "PidOne";
        subjectOne.setPid(pidOne);
        subjectList.add(subjectOne);

        Map<String, StudySubject> subjectMap = StudySubjectListUtil.getPidKeyHashMap(subjectList);
        assertTrue(subjectMap.containsKey(pidOne));

    }

    @Test
    public void getPidKeyHashMap_creates_hash_map_with_two_elements() {
        List<StudySubject> subjectList = new ArrayList<>();

        StudySubject subjectOne = new StudySubject();
        String pidOne = "PidOne";
        subjectOne.setPid(pidOne);
        subjectList.add(subjectOne);

        StudySubject subjectTwo = new StudySubject();
        String pidTwo = "PidTwo";
        subjectTwo.setPid(pidTwo);
        subjectList.add(subjectTwo);

        Map<String, StudySubject> subjectMap = StudySubjectListUtil.getPidKeyHashMap(subjectList);

        assertTrue(subjectMap.containsKey(pidOne));
        assertTrue(subjectMap.containsKey(pidTwo));

    }

    @Test
    public void filterByStudySubjectList_returns_empty_list() {
        List<StudySubject> subjectList = new ArrayList<>();
        List<StudySubject> filterList = new ArrayList<>();

        List<StudySubject> resultList = StudySubjectListUtil.filterByStudySubjectList(subjectList, filterList);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void filterByStudySubjectList_returns_empty_list_if_subject_list_is_null() {
        List<StudySubject> subjectList = new ArrayList<>();
        List<StudySubject> filterList = new ArrayList<>();

        List<StudySubject> resultList = StudySubjectListUtil.filterByStudySubjectList(null, filterList);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void filterByStudySubjectList_returns_matching_element() {
        List<StudySubject> subjectList = new ArrayList<>();
        List<StudySubject> filterList = new ArrayList<>();


        StudySubject subjectOne = new StudySubject();
        String pidOne = "PidOne";
        subjectOne.setPid(pidOne);
        subjectList.add(subjectOne);
        filterList.add(subjectOne);

        StudySubject subjectTwo = new StudySubject();
        String pidTwo = "PidTwo";
        subjectTwo.setPid(pidTwo);
        subjectList.add(subjectTwo);

        List<StudySubject> resultList = StudySubjectListUtil.filterByStudySubjectList(subjectList, filterList);
        assertEquals(1, resultList.size());
        assertEquals(subjectOne, resultList.get(0));
    }
}