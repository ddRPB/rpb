/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;

import java.io.InputStream;

/**
 * The interface for communication with CTP (ClinicalTrialProcessor)
 *
 * @author tomas@skripcak.net
 * @since 28 Nov 2017
 */
public interface ICtpService {

    String getBaseAetName();

    String getDicomUidPrefix();

    String getUrl();

    boolean getIsHttpImportPacsVerificationEnabled();

    int getHttpImportPacsVerificationTimeout();

    void setupConnection(String url, String user, String password);

    /**
     * Updates or creates an entry in the lookup table for the DicomAnonymizer step in the study specific pipeline
     * of the CTP system. The entry represents the mapping of the patient identifier in the hospital system and the
     * pseudonym of the first research stage.
     *
     * @param edcCode           String edcCode of the study - mapped to the pipeline and step name by convention
     * @param subjectIdentifier Hospital identifier of the patient
     * @param pid               Pseudonym identifier
     * @return boolean success of the update or creation
     */
    boolean updateStudySubjectPseudonym(String edcCode, String subjectIdentifier, String pid);

    /**
     * Updates or creates an entry in the lookup table for the DicomAnonymizer step in the study specific pipeline
     * of the CTP system. The entry represents the mapping of the patient pseudonym in the first stage data set and the
     * pseudonym used for the specific study.
     *
     * @param edcCode String edcCode of the study - mapped to the pipeline and step name by convention
     * @param subjectIdentifier Pseudonym of the first stage step
     * @param studySubjectId Identifier within the study context
     * @return boolean success of the update or creation
     */
    boolean updateStudySubjectId(String edcCode, String subjectIdentifier, String studySubjectId);

    /**
     * Updates or creates an entry in the lookup table for the DicomAnonymizer step in the study specific pipeline
     * of the CTP system. Depending on the study (edcCode), it creates a mapping of the hospital identifier to the first
     * stage pseudonym or a mapping from the first stage pseudonym to the study specific identifier.
     *
     * @param edcCode String edcCode of the study - mapped to the pipeline and step name by convention
     * @param studySubject StudySubject for the lookup
     * @return boolean success of the update or creation
     */
    boolean updateSubjectLookupEntry(String edcCode, StudySubject studySubject);

    boolean httpImportDicom(InputStream is);

}
