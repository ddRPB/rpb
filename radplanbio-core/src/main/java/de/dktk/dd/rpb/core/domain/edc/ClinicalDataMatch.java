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

package de.dktk.dd.rpb.core.domain.edc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ClinicalDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class ClinicalDataMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ClinicalDataMatch.class);

    //endregion

    //region Members

    private String studyOid;
    private Boolean match;
    private List<SubjectDataMatch> subjectDataMatchList;

    //endregion

    //region Constructors

    public ClinicalDataMatch() {
        // NOOP
    }

    public ClinicalDataMatch(ClinicalData sourceClinicalData, ClinicalData targetClinicalData) {
        this();

        // Ensure that there is the same study
        if (sourceClinicalData.getStudyOid().equals(targetClinicalData.getStudyOid())) {
            this.studyOid = sourceClinicalData.getStudyOid();
            this.subjectDataMatchList = new ArrayList<>();
            this.match = Boolean.TRUE;

            // Lookup for patient with the same SubjectKey in source and target
            if (sourceClinicalData.getStudySubjects() != null && targetClinicalData.getStudySubjects() != null) {
                for (StudySubject sss : sourceClinicalData.getStudySubjects()) {
                    for (StudySubject tss : targetClinicalData.getStudySubjects()) {
                        if (sss.getSubjectKey().equals(tss.getSubjectKey())) {

                            // Create a match object for study subject - propagate the creation of rest down
                            this.getSubjectDataMatchList().add(
                                    new SubjectDataMatch(sss, tss)
                            );

                            // Check if the last added event data matches
                            this.match = this.match && this.getSubjectDataMatchList().get(this.getSubjectDataMatchList().size() - 1).getMatch();

                            break;
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region StudyOID

    public String getStudyOid() {
        return this.studyOid;
    }

    public void setStudyOid(String value) {
        this.studyOid = value;
    }

    //endregion

    //region Match

    public Boolean getMatch() {
        return match;
    }

    public void setMatch(Boolean value) {
        this.match = value;
    }

    //endregion

    //region SubjectDataMatchList

    public List<SubjectDataMatch> getSubjectDataMatchList() {
        return subjectDataMatchList;
    }

    public void setSubjectDataMatchList(List<SubjectDataMatch> subjectDataMatchList) {
        this.subjectDataMatchList = subjectDataMatchList;
    }

    //endregion

}
