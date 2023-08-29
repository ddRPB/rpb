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
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeriesRtDose;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeriesRtImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeriesRtPlan;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeriesRtStruct;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings({"unchecked"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConquestService.class, Logger.class, LoggerFactory.class, Client.class})
public class ConquestServiceTest {
    private ConquestService conquestService;
    private Logger logger;

    private Client clientMock;
    private ClientResponse responseMock;

    private String baseUrl;
    private String user;
    private String password;

    public ConquestServiceTest() {
    }

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        mockStatic(Client.class);
        clientMock = mock(Client.class);
        WebResource webResourceMock = mock(WebResource.class);
        responseMock = mock(ClientResponse.class);


        when(Client.create()).thenReturn(clientMock);
        when(clientMock.resource(anyString())).thenReturn(webResourceMock);
        when(webResourceMock.get(any(Class.class))).thenReturn(responseMock);
        when(responseMock.getStatus()).thenReturn(200);
        when(responseMock.hasEntity()).thenReturn(true);

        conquestService = getConquestService();

    }

    private ConquestService getConquestService() {
        conquestService = new ConquestService();
        baseUrl = "dummyBaseUrl";
        conquestService.setupConnection(baseUrl, 1);
        return conquestService;
    }

    private ConquestService getConquestServiceWithBasicAuth() {
        conquestService = new ConquestService();
        baseUrl = "dummyBaseUrl";
        user = "dummyUser";
        password = "dummyPassword";
        conquestService.setupConnection(baseUrl, 1, user, password);
        return conquestService;
    }

    // region setup connection with authentication

    @Test
    public void client_has_authentication_filter() throws Exception {
        conquestService = getConquestServiceWithBasicAuth();
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        conquestService.loadPatient("1");

        verify(clientMock).addFilter(any(HTTPBasicAuthFilter.class));
    }

    // region loadPatient

    @Test
    public void loadPatient_handles_empty_results_from_pacs() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");
        List<Subject> subjects = conquestService.loadPatient("1");
        assertEquals(subjects.size(), 0);
    }

    @Test(expected = Exception.class)
    public void loadPatient_throws_if_response_status_is_not_200() throws Exception {
        when(responseMock.getStatus()).thenReturn(400);
        conquestService.loadPatient("1");
    }

    @Test(expected = Exception.class)
    public void loadPatient_throws_if_response_has_no_entity() throws Exception {
        when(responseMock.hasEntity()).thenReturn(false);
        conquestService.loadPatient("1");
    }

    @Test
    public void loadPatient_handles_patient_with_study_from_pacs() throws Exception {
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
        studySubjectList.add(getDummyStudySubject("1"));

        List<Subject> subjects = conquestService.loadPatients(studySubjectList);
        assertEquals(subjects.size(), 0);
    }

    @Test
    public void loadPatients_calls_client_resource_with_appropriate_url() throws Exception {
        when(responseMock.getEntity(String.class)).thenReturn("{}");

        List<StudySubject> studySubjectList = new ArrayList<>();

        studySubjectList.add(getDummyStudySubject("1"));
        studySubjectList.add(getDummyStudySubject("2"));

        conquestService.loadPatients(studySubjectList);

        verify(clientMock).resource(baseUrl + "?mode=rpbjsondicompatients&PatientID=1%2C2");
    }

    @Test
    public void loadPatients_calls_client_resource_with_several_requests_if_request_has_more_than_25_subjects() throws Exception {
        String fileName = "./src/test/resources/test-data/PacsPatientResponse.json";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());
//        when(responseMock.getEntity(String.class)).thenReturn("{}");

        List<StudySubject> studySubjectList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            studySubjectList.add(getDummyStudySubject(Integer.toString(i)));
        }

        List<Subject> subjectList = conquestService.loadPatients(studySubjectList);

        verify(clientMock, times(2)).resource(anyString());
        verify(clientMock).resource(getDummyRequestUrl(1, 15));
        verify(clientMock).resource(getDummyRequestUrl(16, 20));

        assertEquals("List has 2 Subjects, because one subject will be returned per invocation",
                subjectList.size(), 2);
    }

    private String getDummyRequestUrl(int startId, int endId) {
        String requestString = baseUrl + "?mode=rpbjsondicompatients&PatientID=" + startId;
        for (int i = startId + 1; i <= endId; i++) {
            requestString = requestString + "%2C" + i;
        }
        return requestString;
    }

    private StudySubject getDummyStudySubject(String pid) {
        StudySubject subjectOne = new StudySubject();
        subjectOne.setPid(pid);
        return subjectOne;
    }


    @Test(expected = Exception.class)
    public void loadPatients_throws_if_response_status_is_not_200() throws Exception {
        when(responseMock.getStatus()).thenReturn(400);

        List<StudySubject> studySubjectList = new ArrayList<>();
        studySubjectList.add(getDummyStudySubject("1"));

        conquestService.loadPatients(studySubjectList);
    }


    // endregion

    // region loadPatientStudy

    @Test
    public void loadPatientStudy_returns_study() {
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
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
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
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
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(1);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the DicomSeriesRtImage class", "DicomSeriesRtImage",
                className);
        assertEquals("RTIMAGE SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.3",
                series.getSeriesInstanceUID());
        assertEquals("RTIMAGE SeriesDescription", "RTIMAGE SeriesDescription", series.getSeriesDescription());
        assertEquals("RTIMAGE Modality", "RTIMAGE", series.getSeriesModality());
        assertEquals("RTImageLabel", "RTIMAGE RTImageLabel", ((DicomSeriesRtImage) series).getRtImageLabel());
        assertEquals("RtImageName", "RTIMAGE RTImageName", ((DicomSeriesRtImage) series).getRtImageName());
        assertEquals("RtImageDescription", "RTIMAGE RTImageDescription", ((DicomSeriesRtImage) series).getRtImageDescription());
        assertEquals("RTIMAGE InstanceCreationDate", "20140811", ((DicomSeriesRtImage) series).getInstanceCreationDate());
    }

    @Test
    public void loadPatientStudy_handles_rtPlan_series() {
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(2);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the DicomSeriesRtPlan class", "DicomSeriesRtPlan",
                className);
        assertEquals("RTPLAN SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.4",
                series.getSeriesInstanceUID());
        assertEquals("RTPLAN SeriesDescription", "RTPLAN SeriesDescription", series.getSeriesDescription());
        assertEquals("RTPLAN Modality", "RTPLAN", series.getSeriesModality());
        assertEquals("RTPlanLabel", "RTPLAN RTPlanLabel", ((DicomSeriesRtPlan) series).getRtPlanLabel());
        assertEquals("RtPlanName", "RTPLAN RTPlanName", ((DicomSeriesRtPlan) series).getRtPlanName());
        assertEquals("RtPlanDate", "20140812", ((DicomSeriesRtPlan) series).getRtPlanDate());
        assertEquals("RtPlanDescription", "RTPLAN RTPlanDescription", ((DicomSeriesRtPlan) series).getRtPlanDescription());
    }

    @Test
    public void loadPatientStudy_handles_rtDose_series() {
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(3);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the DicomSeriesRtDose class", "DicomSeriesRtDose",
                className);
        assertEquals("RTDOSE SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.5", series.getSeriesInstanceUID());
        assertEquals("RTDOSE SeriesDescription", "RTDOSE SeriesDescription", series.getSeriesDescription());
        assertEquals("RTDOSE Modality", "RTDOSE", series.getSeriesModality());
        assertEquals("RTDOSE DoseUnits", "RTDOSE DoseUnits", ((DicomSeriesRtDose) series).getDoseUnits());
        assertEquals("RTDOSE DoseType", "RTDOSE DoseType", ((DicomSeriesRtDose) series).getDoseType());
        assertEquals("RTDOSE DoseComment", "RTDOSE DoseComment", ((DicomSeriesRtDose) series).getDoseComment());
        assertEquals("RTDOSE DoseSummationType", "RTDOSE DoseSummationType", ((DicomSeriesRtDose) series).getDoseSummationType());
        assertEquals("RTDOSE InstanceCreationDate", "20140811", ((DicomSeriesRtDose) series).getInstanceCreationDate());
    }

    @Test
    public void loadPatientStudy_handles_rtStruct_series() {
        String fileName = "./src/test/resources/test-data/PacsStudiesResponse.json";
        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";

        JSONObject jsonObject = getJsonFromFile(fileName);
        when(responseMock.getEntity(String.class)).thenReturn(jsonObject.toString());

        DicomStudy study = conquestService.loadPatientStudy("1", dicomStudyUid);
        List<DicomSeries> seriesList = study.getStudySeries();

        DicomSeries series = seriesList.get(4);
        String className = series.getClass().getSimpleName();
        assertEquals("Should be an instance of the DicomSeriesRtStruct class", "DicomSeriesRtStruct",
                className);
        assertEquals("RTSTRUCT SeriesInstanceUID", "1.2.826.0.1.3680043.9.7275.0.6",
                series.getSeriesInstanceUID());
        assertEquals("RTSTRUCT SeriesDescription", "RTSTRUCT SeriesDescription", series.getSeriesDescription());
        assertEquals("RTSTRUCT Modality", "RTSTRUCT", series.getSeriesModality());
        assertEquals("RTSTRUCT StructureSetLabel", "RTSTRUCT StructureSetLabel", ((DicomSeriesRtStruct) series).getStructureSetLabel());
        assertEquals("RTSTRUCT StructureSetName", "RTSTRUCT StructureSetName", ((DicomSeriesRtStruct) series).getStructureSetName());
        assertEquals("RTSTRUCT StructureSetDescription", "RTSTRUCT StructureSetDescription", ((DicomSeriesRtStruct) series).getStructureSetDescription());
        assertEquals("RTSTRUCT StructureSetDate", "20140821", ((DicomSeriesRtStruct) series).getStructureSetDate());
    }

// endregion

    // region addStudySeriesImages

//    @Test
//    public void addStudySeriesImages_ignores_non_rt_related_series() throws InterruptedException, ExecutionException, JSONException {
//        List<StagedDicomSeries> stagedDicomSeriesList = new ArrayList<>();
//        StagedDicomSeries stagedRtPlanDicomSeries = new StagedDicomSeries();
//        stagedRtPlanDicomSeries.setSeriesModality("dummy");
//        stagedDicomSeriesList.add(stagedRtPlanDicomSeries);
//
//        List<StagedDicomSeries> updatedSeriesList = conquestService.addStudySeriesImages(stagedDicomSeriesList, "1", "dicomStudyUid");
//
//        assertEquals(1, updatedSeriesList.size());
//        verify(responseMock, times(0)).getEntity(String.class);
//
//    }

//    @Test
//    public void addStudySeriesImages_handles_rt_related_series() throws InterruptedException, ExecutionException, JSONException {
//        String rtPlanSeriesResponseFileName = "./src/test/resources/test-data/PacsRtPlanSeriesResponse.json";
//        String rtImageSeriesResponseFileName = "./src/test/resources/test-data/PacsRtImageSeriesResponse.json";
//        String rtStructSeriesResponseFileName = "./src/test/resources/test-data/PacsRtStructSeriesResponse.json";
//        String rtDoseSeriesResponseFileName = "./src/test/resources/test-data/PacsRtDoseSeriesResponse.json";
//
//
//        String dicomStudyUid = "1.2.826.0.1.3680043.9.7275.0.1";
//
//        JSONObject rtPlanSeriesResponse = getJsonFromFile(rtPlanSeriesResponseFileName);
//        JSONObject rtImageSeriesResponse = getJsonFromFile(rtImageSeriesResponseFileName);
//        JSONObject rtStructSeriesResponse = getJsonFromFile(rtStructSeriesResponseFileName);
//        JSONObject rtDoseSeriesResponse = getJsonFromFile(rtDoseSeriesResponseFileName);
//
//
//        when(responseMock.getEntity(String.class)).thenReturn(
//                rtPlanSeriesResponse.toString(),
//                rtImageSeriesResponse.toString(),
//                rtStructSeriesResponse.toString(),
//                rtDoseSeriesResponse.toString()
//        );
//
//        List<StagedDicomSeries> stagedDicomSeriesList = new ArrayList<>();
//        StagedDicomSeries stagedRtPlanDicomSeries = new StagedDicomSeries();
//        stagedRtPlanDicomSeries.setSeriesModality(DICOM_RTPLAN);
//        stagedDicomSeriesList.add(stagedRtPlanDicomSeries);
//
//        StagedDicomSeries stagedRtImageDicomSeries = new StagedDicomSeries();
//        stagedRtImageDicomSeries.setSeriesModality(DICOM_RTIMAGE);
//        stagedDicomSeriesList.add(stagedRtImageDicomSeries);
//
//        StagedDicomSeries stagedRtStructDicomSeries = new StagedDicomSeries();
//        stagedRtStructDicomSeries.setSeriesModality(DICOM_RTSTRUCT);
//        stagedDicomSeriesList.add(stagedRtStructDicomSeries);
//
//        StagedDicomSeries stagedRtDoseDicomSeries = new StagedDicomSeries();
//        stagedRtDoseDicomSeries.setSeriesModality(DICOM_RTDOSE);
//        stagedDicomSeriesList.add(stagedRtDoseDicomSeries);
//
//        List<StagedDicomSeries> updatedSeriesList = conquestService.addStudySeriesImages(stagedDicomSeriesList, "1", dicomStudyUid);
//
//        assertEquals(4, updatedSeriesList.size());
//
//        for (StagedDicomSeries series :
//                updatedSeriesList) {
//            switch (series.getSeriesModality()) {
//                case DICOM_RTPLAN:
//                    assertNotNull(series.getSeriesImages());
//                    assertEquals(2, series.getSeriesImages().size());
//                    break;
//                case DICOM_RTIMAGE:
//                    assertNotNull(series.getSeriesImages());
//                    assertEquals(2, series.getSeriesImages().size());
//                    break;
//                case DICOM_RTSTRUCT:
//                    assertNotNull(series.getSeriesImages());
//                    assertEquals(1, series.getSeriesImages().size());
//                    break;
//                case DICOM_RTDOSE:
//                    assertNotNull(series.getSeriesImages());
//                    assertEquals(1, series.getSeriesImages().size());
//                    break;
//
//            }
//        }
//


//    }

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
            logger.error(e.getMessage());
        }
        {
            try {
                jsonObject = (JSONObject) jsonParser.parse(reader);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return jsonObject;
    }
}
