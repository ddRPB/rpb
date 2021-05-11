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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@SuppressWarnings({"unchecked"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConquestService.class, Logger.class, Client.class})
public class ConquestServiceTest {
    private ConquestService conquestService;
    private Logger logger;

    private Client clientMock;
    private ClientResponse responseMock;

    private String baseUrl;

    public ConquestServiceTest() {
    }

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);

        mockStatic(Client.class);
        clientMock = mock(Client.class);
        WebResource webResourceMock = mock(WebResource.class);
        responseMock = mock(ClientResponse.class);


        when(Client.create()).thenReturn(clientMock);
        when(clientMock.resource(anyString())).thenReturn(webResourceMock);
        when(webResourceMock.get(any(Class.class))).thenReturn(responseMock);
        when(responseMock.getStatus()).thenReturn(200);
        when(responseMock.hasEntity()).thenReturn(true);

        conquestService = new ConquestService();
        baseUrl = "dummyBaseUrl";
        conquestService.setupConnection(baseUrl);

    }

    // region loadPatient

    @Test
    public void handles_empty_results_from_pacs() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        List<Subject> subjects = conquestService.loadPatient("1");
        assertEquals(subjects.size(), 0);
    }

    @Test(expected = Exception.class)
    public void throws_if_response_status_is_not_200() throws Exception {
        when(responseMock.getStatus()).thenReturn(400);
        conquestService.loadPatient("1");
    }

    @Test(expected = Exception.class)
    public void throws_if_response_has_no_entity() throws Exception {
        when(responseMock.hasEntity()).thenReturn(false);
        conquestService.loadPatient("1");
    }

    @Test
    public void handles_patient_with_study_from_pacs() throws Exception {
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

        String patientJsonString = "{\"Patients\":" + fakePatients.toString() + "}";

        when(responseMock.getEntity(String.class)).thenReturn(patientJsonString);
        List<Subject> subjects = conquestService.loadPatient("1");

        assertEquals(subjects.size(), 1);
        assertEquals(subjects.get(0).getUniqueIdentifier(), patientIDPatient0);
    }

    @Test
    public void loadPatient_calls_client_resource_with_appropriate_url() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        conquestService.loadPatient("1");

        verify(clientMock).resource(baseUrl + "?mode=rpbjsondicompatients&PatientID=1");
    }

    // endregion

    // region loadPatients

    @Test
    public void loadPatients_handles_empty_results_from_pacs() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");

        List<StudySubject> studySubjectList = new ArrayList<>();
        StudySubject subjectOne = new StudySubject();
        subjectOne.setPid("1");
        studySubjectList.add(subjectOne);

        List<Subject> subjects = conquestService.loadPatients(studySubjectList);
        assertEquals(subjects.size(), 0);
    }

    @Test
    public void loadPatients_calls_client_resource_with_appropriate_url() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");

        List<StudySubject> studySubjectList = new ArrayList<>();

        StudySubject subjectOne = new StudySubject();
        subjectOne.setPid("1");
        studySubjectList.add(subjectOne);

        StudySubject subjectTwo = new StudySubject();
        subjectTwo.setPid("2");
        studySubjectList.add(subjectTwo);

        conquestService.loadPatients(studySubjectList);

        verify(clientMock).resource(baseUrl + "?mode=rpbjsondicompatients&PatientID=1%2C2");
    }


    @Test(expected = Exception.class)
    public void loadPatients_throws_if_response_status_is_not_200() throws Exception {
        when(responseMock.getStatus()).thenReturn(400);

        List<StudySubject> studySubjectList = new ArrayList<>();
        StudySubject subjectOne = new StudySubject();
        subjectOne.setPid("1");
        studySubjectList.add(subjectOne);

        conquestService.loadPatients(studySubjectList);
    }


    // endregion

    // region loadPatientStudy

    @Test
    public void loadPatientStudy_returns_study() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> series = study.getStudySeries();

        assertEquals("StudyInstanceUID is equal to DicomStudyUid", dicomStudyUid, study.getStudyInstanceUID());
        assertEquals("StudyDescription: ", "dummyStudyDescription", study.getStudyDescription());
        assertEquals("StudyDate", "20131011", study.getStudyDate());
        assertNotNull("Should include series ", series);
        assertEquals("Series count - based on file content", 5, series.size());
    }

    @Test
    public void loadPatientStudy_handles_standard_series() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(0);
        assertEquals("CT Series SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.2",
                series.getSeriesInstanceUID());
        assertEquals("CT SeriesDescription", "CT SeriesDescription", series.getSeriesDescription());
        assertEquals("CT Series SeriesTime", "10:11:53", series.getTimeSeriesString());
        assertEquals("CT Series Modality", "CT", series.getSeriesModality());
    }

    @Test
    public void loadPatientStudy_handles_rtImage_series() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(1);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the RtImageDicomSeries class", "RtImageDicomSeries",
                className);
        assertEquals("RTIMAGE SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.3",
                series.getSeriesInstanceUID());
        assertEquals("RTIMAGE SeriesDescription", "RTIMAGE SeriesDescription", series.getSeriesDescription());
        assertEquals("RTIMAGE Modality", "RTIMAGE", series.getSeriesModality());
        assertEquals("RTImageLabel", "RTIMAGE RTImageLabel", ((RtImageDicomSeries) series).getRtImageLabel());
        assertEquals("RtImageName", "RTIMAGE RTImageName", ((RtImageDicomSeries) series).getRtImageName());
        assertEquals("RtImageDescription", "RTIMAGE RTImageDescription", ((RtImageDicomSeries) series).getRtImageDescription());
        assertEquals("RTIMAGE InstanceCreationDate", "20140811", ((RtImageDicomSeries) series).getInstanceCreationDate());
    }

    @Test
    public void loadPatientStudy_handles_rtPlan_series() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(2);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the RtPlanDicomSeries class", "RtPlanDicomSeries",
                className);
        assertEquals("RTPLAN SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.4",
                series.getSeriesInstanceUID());
        assertEquals("RTPLAN SeriesDescription", "RTPLAN SeriesDescription", series.getSeriesDescription());
        assertEquals("RTPLAN Modality", "RTPLAN", series.getSeriesModality());
        assertEquals("RTPlanLabel", "RTPLAN RTPlanLabel", ((RtPlanDicomSeries) series).getRtPlanLabel());
        assertEquals("RtPlanName", "RTPLAN RTPlanName", ((RtPlanDicomSeries) series).getRtPlanName());
        assertEquals("RtPlanDate", "20140812", ((RtPlanDicomSeries) series).getRtPlanDate());
        assertEquals("RtPlanDescription", "RTPLAN RTPlanDescription", ((RtPlanDicomSeries) series).getRtPlanDescription());
    }

    @Test
    public void loadPatientStudy_handles_rtDose_series() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(3);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the RtDoseDicomSeries class", "RtDoseDicomSeries",
                className);
        assertEquals("RTDOSE SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.5", series.getSeriesInstanceUID());
        assertEquals("RTDOSE SeriesDescription", "RTDOSE SeriesDescription", series.getSeriesDescription());
        assertEquals("RTDOSE Modality", "RTDOSE", series.getSeriesModality());
        assertEquals("RTDOSE DoseUnits", "RTDOSE DoseUnits", ((RtDoseDicomSeries) series).getDoseUnits());
        assertEquals("RTDOSE DoseType", "RTDOSE DoseType", ((RtDoseDicomSeries) series).getDoseType());
        assertEquals("RTDOSE DoseComment", "RTDOSE DoseComment", ((RtDoseDicomSeries) series).getDoseComment());
        assertEquals("RTDOSE DoseSummationType", "RTDOSE DoseSummationType", ((RtDoseDicomSeries) series).getDoseSummationType());
        assertEquals("RTDOSE InstanceCreationDate", "20140811", ((RtDoseDicomSeries) series).getInstanceCreationDate());
    }

    @Test
    public void loadPatientStudy_handles_rtStruct_series() {
        String fileName = "./src/test/java/de/dktk/dd/rpb/core/service/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(4);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the RtStructDicomSeries class", "RtStructDicomSeries",
                className);
        assertEquals("RTSTRUCT SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.6",
                series.getSeriesInstanceUID());
        assertEquals("RTSTRUCT SeriesDescription", "RTSTRUCT SeriesDescription", series.getSeriesDescription());
        assertEquals("RTSTRUCT Modality", "RTSTRUCT", series.getSeriesModality());
        assertEquals("RTSTRUCT StructureSetLabel", "RTSTRUCT StructureSetLabel", ((RtStructDicomSeries) series).getStructureSetLabel());
        assertEquals("RTSTRUCT StructureSetName", "RTSTRUCT StructureSetName", ((RtStructDicomSeries) series).getStructureSetName());
        assertEquals("RTSTRUCT StructureSetDescription", "RTSTRUCT StructureSetDescription", ((RtStructDicomSeries) series).getStructureSetDescription());
        assertEquals("RTSTRUCT StructureSetDate", "20140821", ((RtStructDicomSeries) series).getStructureSetDate());
    }

// endregion

// region moveDicomStudy

    @Test
    public void moveDicomStudy_returns_true_if_response_is_200() {
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        boolean success = conquestService.moveDicomStudy("1", "2", "3");
        assertTrue(success);
    }

    @Test
    public void moveDicomStudy_returns_false_if_response_is_not_200() {
        when(responseMock.getStatus()).thenReturn(400);
        boolean success = conquestService.moveDicomStudy("1", "2", "3");
        assertFalse(success);
    }

    @Test
    public void moveDicomStudy_creates_appropriate_query_string() {
        conquestService.moveDicomStudy("1", "2", "3");
        verify(clientMock).resource(baseUrl + "?mode=rpbmovedicomstudies&PatientID=1&StudyUID=2&AET=3");
    }

//  endregion

// region moveDicomSeries

    @Test
    public void moveDicomSeries_returns_true_if_response_is_200() {
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        boolean success = conquestService.moveDicomSeries("1", "2", "3", "4");
        assertTrue(success);
    }

    @Test
    public void moveDicomSeries_returns_false_if_response_is_not_200() {
        when(responseMock.getStatus()).thenReturn(400);
        boolean success = conquestService.moveDicomSeries("1", "2", "3", "4");
        assertFalse(success);
    }

    @Test
    public void moveDicomSeries_creates_appropriate_query_string() {
        conquestService.moveDicomSeries("1", "2", "3", "4");
        verify(clientMock).resource(baseUrl + "?mode=rpbmovedicomseries&PatientID=1&StudyUID=2&SeriesUID=3&AET=4");
    }

// endregion

    private JSONObject getDummyPatient(String patientID, String patientBirthDate, JSONArray fakeStudies) {
        JSONObject fakePatient = new JSONObject();
        fakePatient.put("PatientID", patientID);
        fakePatient.put("PatientBirthDate", patientBirthDate);
        fakePatient.put("Studies", fakeStudies);
        return fakePatient;
    }

    private JSONObject getDummyStudy(String studyInstanceUID, String studyDate, String studyDescription) {
        JSONObject fakeStudy = new JSONObject();

        fakeStudy.put("StudyInstanceUID", studyInstanceUID);
        fakeStudy.put("StudyDate", studyDate);
        fakeStudy.put("StudyDescription", studyDescription);
        return fakeStudy;
    }

    private JSONObject getJsonFromFile(String fileName) {
        JSONObject jsonObject = null;
        JSONParser jsonParser = new JSONParser();
        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            logger.error(e.getStackTrace());
        }
        {
            try {
                jsonObject = (JSONObject) jsonParser.parse(reader);
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
        }
        return jsonObject;
    }
}