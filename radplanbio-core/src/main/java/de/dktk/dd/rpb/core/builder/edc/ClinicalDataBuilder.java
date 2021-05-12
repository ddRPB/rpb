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

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for the ClinicalData object,
 * which can be used to create the ClinicalData part of an ODM-XML object.
 */
public class ClinicalDataBuilder {
    //region Finals

    private static final Logger log = Logger.getLogger(ClinicalDataBuilder.class);
    private final ClinicalData clinicalData = new ClinicalData();
    private final List<StudySubject> studySubject = new ArrayList<>();

    // endregion

    // region constructor

    private ClinicalDataBuilder() {
    }

    public static ClinicalDataBuilder getInstance() {
        return new ClinicalDataBuilder();
    }

    // endregion

    /**
     * Building the ClinicalData instance out of the current configuration properties.
     *
     * @return ClinicalData
     */
    public ClinicalData build() throws MissingPropertyException {
        if (this.clinicalData.getStudyOid() == null || this.clinicalData.getStudyOid().isEmpty()) {
            throw new MissingPropertyException("StudyOid is missing");
        }

        if (studySubject.size() > 0) {
            this.clinicalData.setStudySubjects(studySubject);
        }
        return this.clinicalData;
    }

    /**
     * Set the studyOid property for the ClinicalData object.
     *
     * @param studyOid String
     * @return ClinicalDataBuilder
     */
    public ClinicalDataBuilder setStudyOid(String studyOid) {
        this.clinicalData.setStudyOid(studyOid);
        return this;
    }

    /**
     * Adding an additional StudySubject to the ClinicalData
     *
     * @param studySubject StudySubject
     * @return ClinicalDataBuilder
     */
    public ClinicalDataBuilder addSubjectData(StudySubject studySubject) {
        if (this.studySubject.indexOf(studySubject) < 0) {
            this.studySubject.add(studySubject);
        }
        return this;
    }
}
