/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak, Ronny Kursawe
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
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PacsPatientResponseUnmashallerTest.class, Logger.class})
public class PacsPatientResponseUnmashallerTest {
    Logger logger;

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
    }

    // region unmarshalPacsPatientsResponse tests
    @Test
    public void unmashals_empty_patient_object_to_empty_list() throws JSONException {
        JSONArray fakePatients = new JSONArray();
        ArrayList<Subject> emptyArray = new ArrayList<>();

        List<Subject> subjects = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(fakePatients);

        assertArrayEquals(subjects.toArray(), emptyArray.toArray());
    }

    @Test
    public void unmashals_patient_with_one_study() throws JSONException {
        String studyInstanceUIDFakeStudy0 = "2.25.47335512428127212837968588258421836332343594176870791808528";
        String studyDateStudy0 = "00000000";
        String studyDescriptionStudy0 = "fake studyDescriptionStudy0";

        JSONObject fakeStudy = getDummyStudy(studyInstanceUIDFakeStudy0, studyDateStudy0, studyDescriptionStudy0);

        JSONArray fakeStudies = new JSONArray();
        fakeStudies.put(fakeStudy);

        String patientIDPatient0 = "DD-000CU0WB";
        String patientBirthDatePatient0 = "19000201";
        JSONObject fakePatient = getDummyPatient(patientIDPatient0, patientBirthDatePatient0, fakeStudies);

        JSONArray fakePatients = new JSONArray();
        fakePatients.put(fakePatient);

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);

        List<Subject> subjects = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(fakePatients);
        List<DicomStudy> studies = subjects.get(0).getDicomStudyList();

        assertEquals(patientIDPatient0, subjects.get(0).getUniqueIdentifier());
        assertEquals(patientBirthDatePatient0, formatter.format(subjects.get(0).getDateOfBirth()));
        assertEquals(studyInstanceUIDFakeStudy0, studies.get(0).getStudyInstanceUID());
        assertEquals(studyDateStudy0, studies.get(0).getStudyDate());
        assertEquals(studyDescriptionStudy0, studies.get(0).getStudyDescription());
    }

    @Test
    public void unmashals_list_with_two_patients() throws JSONException {
        String studyInstanceUIDFakeStudy0 = "2.25.47335512428127212837968588258421836332343594176870791808528";
        String studyDateStudy0 = "00000000";
        String studyDescriptionStudy0 = "fake studyDescriptionStudy0";
        JSONObject fakeStudy = getDummyStudy(studyInstanceUIDFakeStudy0, studyDateStudy0, studyDescriptionStudy0);

        JSONArray fakeStudies = new JSONArray();
        fakeStudies.put(fakeStudy);

        String patientIDPatient0 = "DD-000CU0WB";
        String patientBirthDatePatient0 = "19000201";
        String patientIDPatient1 = "DD-11111111";
        String patientBirthDatePatient1 = "20000201";
        JSONObject fakePatient0 = getDummyPatient(patientIDPatient0, patientBirthDatePatient0, new JSONArray());
        JSONObject fakePatient1 = getDummyPatient(patientIDPatient1, patientBirthDatePatient1, fakeStudies);

        JSONArray fakePatients = new JSONArray();
        fakePatients.put(fakePatient0);
        fakePatients.put(fakePatient1);


        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DICOM_DATEFORMAT);

        List<Subject> subjects = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(fakePatients);
        List<DicomStudy> studies = subjects.get(1).getDicomStudyList();

        assertEquals(patientIDPatient1, subjects.get(1).getUniqueIdentifier());
        assertEquals(patientBirthDatePatient1, formatter.format(subjects.get(1).getDateOfBirth()));
        assertEquals(studyInstanceUIDFakeStudy0, studies.get(0).getStudyInstanceUID());
        assertEquals(studyDateStudy0, studies.get(0).getStudyDate());
        assertEquals(studyDescriptionStudy0, studies.get(0).getStudyDescription());
    }

    @Test
    public void empty_studies_property_will_be_ignored() throws JSONException {
        JSONArray fakeStudies = new JSONArray();

        String patientIDPatient0 = "DD-000CU0WB";
        String patientBirthDatePatient0 = "19000201";
        JSONObject fakePatient = getDummyPatient(patientIDPatient0, patientBirthDatePatient0, fakeStudies);

        JSONArray fakePatients = new JSONArray();
        fakePatients.put(fakePatient);

        List<Subject> subjects = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(fakePatients);

        assertEquals(patientIDPatient0, subjects.get(0).getUniqueIdentifier());
    }

    @Test
    public void handleNullObjectFromPacs() throws JSONException {
        List<Subject> subjects = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(null);

        ArrayList<Subject> emptyArray = new ArrayList<>();
        assertArrayEquals(subjects.toArray(), emptyArray.toArray());

    }

    private JSONObject getDummyPatient(String patientID, String patientBirthDate, JSONArray fakeStudies) throws JSONException {
        JSONObject fakePatient = new JSONObject();
        fakePatient.put("PatientID", patientID);
        fakePatient.put("PatientBirthDate", patientBirthDate);
        fakePatient.put("Studies", fakeStudies);
        return fakePatient;
    }

    private JSONObject getDummyStudy(String studyInstanceUID, String studyDate, String studyDescription) throws JSONException {
        JSONObject fakeStudy = new JSONObject();

        fakeStudy.put("StudyInstanceUID", studyInstanceUID);
        fakeStudy.put("StudyDate", studyDate);
        fakeStudy.put("StudyDescription", studyDescription);
        return fakeStudy;
    }

    // endregion

}
