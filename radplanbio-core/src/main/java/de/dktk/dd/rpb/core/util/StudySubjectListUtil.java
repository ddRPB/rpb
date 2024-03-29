/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudySubjectListUtil {
    /**
     * Creates a HashMap with the Pid as key out of a StudySubjectList
     *
     * @param studySubjectList List<StudySubject>
     * @return Map<String, StudySubject>
     */
    public static Map<String, StudySubject> getPidKeyHashMap(List<StudySubject> studySubjectList) {
        Map<String, StudySubject> subjectMap = new HashMap<>();

        if (studySubjectList == null) {
            return subjectMap;
        }

        for (StudySubject studySubject : studySubjectList) {
            if (studySubject.getPid() != null && !studySubject.getPid().isEmpty()) {
                subjectMap.put(studySubject.getPid(), studySubject);
            }
        }
        return subjectMap;
    }

    /**
     * Filters the item of the mainList based on the Pid of the filterList items.
     *
     * @param mainList   List<StudySubject>
     * @param filterList List<StudySubject>
     * @return List<StudySubject> items with the same Pid in the mainList and the filterList
     */
    public static List<StudySubject> filterByStudySubjectList(List<StudySubject> mainList, List<StudySubject> filterList) {
        List<StudySubject> resultList = new ArrayList<>();

        if (mainList == null) {
            return resultList;
        }

        Map<String, StudySubject> subjectMap = StudySubjectListUtil.getPidKeyHashMap(filterList);
        for (StudySubject studySubject : mainList) {
            if (subjectMap.containsKey(studySubject.getPid())) {
                resultList.add(studySubject);
            }
        }
        return resultList;
    }
}
