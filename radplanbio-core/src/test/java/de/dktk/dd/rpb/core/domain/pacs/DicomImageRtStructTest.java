package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.dktk.dd.rpb.core.util.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DicomImageRtStruct.class, Logger.class, LoggerFactory.class})
public class DicomImageRtStructTest {

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void parsesJsonObjectInConstructor() throws JSONException {
        String dummyDate = "31.12.1900";
        DicomImageRtStruct rtStruct = getDicomImageRtStruct(dummyDate);

        assertEquals(DICOM_IMAGE_SOP_INSTANCE_UID, rtStruct.getSopInstanceUID());
        assertEquals(RTSTRUCT_STRUCTURE_SET_LABEL, rtStruct.getStructureSetLabel());
        assertEquals(RTSTRUCT_STRUCTURE_SET_NAME, rtStruct.getStructureSetName());
        assertEquals(RTSTRUCT_STRUCTURE_SET_DESCRIPTION, rtStruct.getStructureSetDescription());
        assertEquals(dummyDate, rtStruct.getStructureSetDate());
        assertEquals(RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID, rtStruct.getReferencedFrameOfReferenceUid());
        assertEquals(RTSTRUCT_REFERENCED_RTSERIES_UID, rtStruct.getReferencedRtSeriesUid());
        assertEquals(RTSTRUCT_REFERENCED_RTSERIES_UID, rtStruct.getReferencedDicomSeriesUID());
    }

    @Test
    public void getDescription() throws JSONException {
        String dummyDate = "31.12.1900";
        DicomImageRtStruct rtStruct = getDicomImageRtStruct(dummyDate);

        assertEquals(RTSTRUCT_STRUCTURE_SET_LABEL, rtStruct.getDescription());

        JSONObject jsonData = getRtStructJsonDataObject(dummyDate);
        jsonData.remove(RTSTRUCT_STRUCTURE_SET_LABEL);
        rtStruct = new DicomImageRtStruct(jsonData);

        assertEquals(RTSTRUCT_STRUCTURE_SET_NAME, rtStruct.getDescription());

        jsonData.remove(RTSTRUCT_STRUCTURE_SET_NAME);
        rtStruct = new DicomImageRtStruct(jsonData);

        assertEquals(RTSTRUCT_STRUCTURE_SET_DESCRIPTION, rtStruct.getDescription());


        jsonData.remove(RTSTRUCT_STRUCTURE_SET_DESCRIPTION);
        rtStruct = new DicomImageRtStruct(jsonData);

        assertEquals("", rtStruct.getDescription());
    }

    @Test
    public void getMetaParameterList() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtStructJsonDataObject(dummyDate);

        DicomImageRtStruct rtStruct = new DicomImageRtStruct(jsonData);
        assertEquals(4, rtStruct.getMetaParameterList().size());

    }

    @Test
    public void createRoiNameList() throws JSONException {
        String dummyDate = "31.12.1900";
        DicomImageRtStruct rtStruct = getDicomImageRtStruct(dummyDate);

        assertEquals("1 - ROIName_1 (RTROIInterpretedType_1)", rtStruct.getRoiNameList().get(0));

    }

    private DicomImageRtStruct getDicomImageRtStruct(String dummyDate) throws JSONException {
        JSONObject jsonData = getRtStructJsonDataObject(dummyDate);
        DicomImageRtStruct rtStruct = new DicomImageRtStruct(jsonData);
        return rtStruct;
    }

    private JSONObject getRtStructJsonDataObject(String dummyDate) throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put(DICOM_IMAGE_SOP_INSTANCE_UID, DICOM_IMAGE_SOP_INSTANCE_UID);
        jsonData.put(RTSTRUCT_STRUCTURE_SET_LABEL, RTSTRUCT_STRUCTURE_SET_LABEL);
        jsonData.put(RTSTRUCT_STRUCTURE_SET_NAME, RTSTRUCT_STRUCTURE_SET_NAME);
        jsonData.put(RTSTRUCT_STRUCTURE_SET_DESCRIPTION, RTSTRUCT_STRUCTURE_SET_DESCRIPTION);
        jsonData.put(RTSTRUCT_STRUCTURE_SET_DATE, dummyDate);
        jsonData.put(RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID, RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID);
        jsonData.put(RTSTRUCT_REFERENCED_RTSERIES_UID, RTSTRUCT_REFERENCED_RTSERIES_UID);


        JSONObject roiOne = new JSONObject();
        roiOne.put(DICOM_IMAGE_ROI_NAME, DICOM_IMAGE_ROI_NAME + "_1");
        roiOne.put(DICOM_IMAGE_REFERENCED_FRAME_OF_REFERENCE_UID, DICOM_IMAGE_REFERENCED_FRAME_OF_REFERENCE_UID + "_1");
        roiOne.put(DICOM_IMAGE_ROI_GENERATION_ALGORITHM, DICOM_IMAGE_ROI_GENERATION_ALGORITHM + "_1");
        roiOne.put(DICOM_IMAGE_ROI_NUMBER, "1");

        JSONArray roiArray = new JSONArray();
        roiArray.put(roiOne);
        jsonData.put(DICOM_IMAGE_ROIs, roiArray);

        JSONObject roiObservationOne = new JSONObject();
        roiObservationOne.put(DICOM_IMAGE_RT_ROI_INTERPRETED_TYPE, DICOM_IMAGE_RT_ROI_INTERPRETED_TYPE + "_1");
        roiObservationOne.put(DICOM_IMAGE_REFERENCED_ROI_NUMBER, "1");
        roiObservationOne.put(DICOM_IMAGE_OBSERVATION_NUMBER, DICOM_IMAGE_OBSERVATION_NUMBER + "_1");

        JSONArray roiObservations = new JSONArray();
        roiObservations.put(roiObservationOne);
        jsonData.put(DICOM_IMAGE_RT_ROI_OBSERVATIONS, roiObservations);

        return jsonData;
    }
}