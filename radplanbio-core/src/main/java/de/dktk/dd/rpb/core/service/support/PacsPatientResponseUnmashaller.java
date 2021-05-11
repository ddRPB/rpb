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

package de.dktk.dd.rpb.core.service.support;

import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshal the Patient response from the PACS system to internal object representation
 *
 * @author kursawero
 * @since 2019
 */
public class PacsPatientResponseUnmashaller {
    private static final Logger log = Logger.getLogger(PacsPatientResponseUnmashaller.class);

    /**
     * Unmarshal JSONArray with Patient JSON objects to List of Subjects
     *
     * @param jsonPatients JSONArray of Patient objects
     * @return List of Subjects with PACS data
     */
    public static List<Subject> unmarshalPacsPatientsResponse(JSONArray jsonPatients) throws JSONException {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);
        ArrayList<Subject> subjectsList = new ArrayList<>();

        if (jsonPatients == null) {
            log.warn("jsonPatients object is null");
        } else {
            for (int i = 0; i < jsonPatients.length(); i++) {
                JSONObject patient = jsonPatients.getJSONObject(i);
                try {
                    Subject subject = unmarshalPacsPatientToSubject(formatter, patient);
                    subjectsList.add(subject);
                } catch (JSONException e) {
                    log.error("There was a problem with the JSON: " + patient.toString());
                    log.debug("StackTrace: " + ExceptionUtils.getFullStackTrace(e));
                }
            }
        }
        return subjectsList;
    }

    private static Subject unmarshalPacsPatientToSubject(SimpleDateFormat formatter, JSONObject patient) throws JSONException {
        Subject subject = new Subject();

        subject.setUniqueIdentifier(patient.getString("PatientID"));

        try {
            subject.setDateOfBirth(formatter.parse(patient.getString("PatientBirthDate")));
        } catch (ParseException e) {
            log.error("There was a problem parsing the PatientBirthDate: " + patient.toString());
            log.debug("StackTrace: " + ExceptionUtils.getFullStackTrace(e));
        }

        JSONArray patientStudies = patient.getJSONArray("Studies");

        for (int j = 0; j < patientStudies.length(); j++) {
            JSONObject patientStudy = patientStudies.getJSONObject(j);
            try {
                DicomStudy study = unmarshalPacsStudyToDicomStudy(patientStudy);
                subject.getDicomStudyList().add(study);
            } catch (JSONException e) {
                log.error("There was a problem with the JSON: " + patientStudy.toString());
                log.debug("StackTrace: " + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return subject;
    }

    private static DicomStudy unmarshalPacsStudyToDicomStudy(JSONObject patientStudy) throws JSONException {
        DicomStudy study = new DicomStudy();
        study.setStudyInstanceUID(patientStudy.getString("StudyInstanceUID"));
        study.setStudyDate(patientStudy.getString("StudyDate"));
        study.setStudyDescription(patientStudy.optString("StudyDescription"));
        return study;
    }
}
