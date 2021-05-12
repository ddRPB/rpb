package de.dktk.dd.rpb.core.domain.edc.sorter;

import com.google.common.collect.ComparisonChain;
import de.dktk.dd.rpb.core.domain.edc.Study;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudyListSorter {

    public static List<Study> sortStudyList(List<Study> list) {
        Collections.sort(list, new Comparator<Study>() {
            public int compare(Study studyOne, Study studyTwo) {
                return ComparisonChain.start()
                        .compare(studyOne.getOcStudyName(), studyTwo.getOcStudyName())
                        .compare(studyOne.getUniqueIdentifier(), studyTwo.getUniqueIdentifier())
                        .result();
            }
        });
        return list;
    }
}
