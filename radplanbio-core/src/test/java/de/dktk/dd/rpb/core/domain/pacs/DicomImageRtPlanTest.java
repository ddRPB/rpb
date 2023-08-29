package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_SOP_INSTANCE_UID;
import static de.dktk.dd.rpb.core.util.Constants.REFERENCED_RTSTRUCT_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_DATE;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_DESCRIPTION;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_GEOMETRY;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_LABEL;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_MANUFACTURER;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_MANUFACTURER_MODEL_NAME;
import static de.dktk.dd.rpb.core.util.Constants.RTPLAN_NAME;
import static de.dktk.dd.rpb.core.util.Constants.SERIES_NUMBER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DicomImageRtPlan.class, Logger.class, LoggerFactory.class})
public class DicomImageRtPlanTest {

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
        DicomImageRtPlan rtPlan = new DicomImageRtPlan(getRtPlanJsonDataObject(dummyDate));

        assertEquals(DICOM_IMAGE_SOP_INSTANCE_UID, rtPlan.getSopInstanceUID());
        assertEquals(RTPLAN_LABEL, rtPlan.getRtPlanLabel());
        assertEquals(RTPLAN_MANUFACTURER_MODEL_NAME, rtPlan.getRtPlanManufacturerModelName());
        assertEquals(RTPLAN_NAME, rtPlan.getRtPlanName());
        assertEquals(dummyDate, rtPlan.getRtPlanDate());
        assertEquals(RTPLAN_MANUFACTURER, rtPlan.getManufacturer());
        assertEquals(RTPLAN_DESCRIPTION, rtPlan.getRtPlanDescription());
        assertEquals(RTPLAN_DESCRIPTION, rtPlan.getRtPlanDescription());
        assertEquals("1", rtPlan.getSeriesNumber());
        assertEquals(REFERENCED_RTSTRUCT_UID, rtPlan.getReferencedRtStructUid());
        assertEquals(REFERENCED_RTSTRUCT_UID, rtPlan.getReferencedDicomSeriesUID());
    }

    @Test
    public void getDescription() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtPlanJsonDataObject(dummyDate);

        DicomImageRtPlan rtPlan = new DicomImageRtPlan(jsonData);
        assertEquals(RTPLAN_LABEL, rtPlan.getDescription());

        jsonData.remove(RTPLAN_LABEL);
        rtPlan = new DicomImageRtPlan(jsonData);
        assertEquals(RTPLAN_NAME, rtPlan.getDescription());

        jsonData.remove(RTPLAN_NAME);
        rtPlan = new DicomImageRtPlan(jsonData);
        assertEquals(RTPLAN_DESCRIPTION, rtPlan.getDescription());

        jsonData.remove(RTPLAN_DESCRIPTION);
        rtPlan = new DicomImageRtPlan(jsonData);
        assertEquals("", rtPlan.getDescription());

    }

    @Test
    public void getMetaParameterList() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtPlanJsonDataObject(dummyDate);

        DicomImageRtPlan rtPlan = new DicomImageRtPlan(jsonData);
        assertEquals(7, rtPlan.getMetaParameterList().size());

    }

    private JSONObject getRtPlanJsonDataObject(String dummyDate) throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put(DICOM_IMAGE_SOP_INSTANCE_UID, DICOM_IMAGE_SOP_INSTANCE_UID);
        jsonData.put(RTPLAN_LABEL, RTPLAN_LABEL);
        jsonData.put(RTPLAN_MANUFACTURER_MODEL_NAME, RTPLAN_MANUFACTURER_MODEL_NAME);
        jsonData.put(RTPLAN_NAME, RTPLAN_NAME);
        jsonData.put(RTPLAN_DATE, dummyDate);
        jsonData.put(RTPLAN_MANUFACTURER, RTPLAN_MANUFACTURER);
        jsonData.put(RTPLAN_DESCRIPTION, RTPLAN_DESCRIPTION);
        jsonData.put(RTPLAN_GEOMETRY, RTPLAN_GEOMETRY);
        jsonData.put(RTPLAN_GEOMETRY, RTPLAN_GEOMETRY);
        jsonData.put(SERIES_NUMBER, "1");
        jsonData.put(REFERENCED_RTSTRUCT_UID, REFERENCED_RTSTRUCT_UID);

        return jsonData;
    }

}