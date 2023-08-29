/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for the SubjectData object,
 * which can be used to create the SubjectData part of an ODM-XML object.
 */
public class SubjectDataBuilder {
    //region Finals

    private static final Logger log = LoggerFactory.getLogger(SubjectDataBuilder.class);
    private final StudySubject studySubject = new StudySubject();
    private final List<EventData> studyEventDataList = new ArrayList<>();

    // endregion
    // region constructor

    private SubjectDataBuilder() {
    }

    public static SubjectDataBuilder getInstance() {
        return new SubjectDataBuilder();
    }

    // endregion

    /**
     * Builds the StudySubject instance out of the current configuration properties.
     * The StudySubject can be used as SubjectData representation within the ODM-XML.
     *
     * @return StudySubject
     */
    public StudySubject build() throws MissingPropertyException {
        if (this.studySubject.getSubjectKey() == null || this.studySubject.getSubjectKey().isEmpty()) {
            throw new MissingPropertyException("SubjectKey is missing");
        }
        if (this.studyEventDataList.size() > 0) {
            this.studySubject.setStudyEventDataList(this.studyEventDataList);
        }
        return this.studySubject;
    }

    /**
     * Set the subjectKey property for the StudySubject object
     *
     * @param subjectKey String
     * @return SubjectDataBuilder
     */
    public SubjectDataBuilder setSubjectKey(String subjectKey) {
        this.studySubject.setSubjectKey(subjectKey);
        return this;
    }

    /**
     * Adding an additional EventData to the SubjectData
     *
     * @param eventData EventData
     * @return SubjectDataBuilder
     */
    public SubjectDataBuilder addStudyEventData(EventData eventData) {
        if (this.studyEventDataList.indexOf(eventData) < 0) {
            this.studyEventDataList.add(eventData);
        }
        return this;
    }
}
