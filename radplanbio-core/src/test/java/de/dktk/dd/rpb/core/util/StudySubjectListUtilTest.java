package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class StudySubjectListUtilTest {

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