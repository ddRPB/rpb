package de.dktk.dd.rpb.core.domain.edc.sorter;

import de.dktk.dd.rpb.core.domain.edc.Study;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StudyListSorterTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void sort_by_OcStudyName() {
        String a = "a";
        String b = "b";

        Study studyOne = new Study();
        studyOne.setName(a);
        Study studyTwo = new Study();
        studyTwo.setName(b);

        List<Study> studyList = new ArrayList<>();
        studyList.add(studyTwo);
        studyList.add(studyOne);

        List<Study> resultList = StudyListSorter.sortStudyList(studyList);
        assertEquals(a, resultList.get(0).getOcStudyName());
    }

    @Test
    public void sort_by_OcStudyName_then_by_UniqueIdentifier() {
        String a = "a";
        String b = "b";
        String c = "c";

        Study studyOne = new Study();
        studyOne.setName(a);
        studyOne.setUniqueIdentifier(b);

        Study studyTwo = new Study();
        studyTwo.setName(a);
        studyTwo.setUniqueIdentifier(c);

        Study studyThree = new Study();
        studyThree.setName(b);
        studyThree.setUniqueIdentifier(a);

        List<Study> studyList = new ArrayList<>();
        studyList.add(studyThree);
        studyList.add(studyTwo);
        studyList.add(studyOne);

        List<Study> resultList = StudyListSorter.sortStudyList(studyList);
        assertEquals(b, resultList.get(0).getUniqueIdentifier());
        assertEquals(c, resultList.get(1).getUniqueIdentifier());
    }
}