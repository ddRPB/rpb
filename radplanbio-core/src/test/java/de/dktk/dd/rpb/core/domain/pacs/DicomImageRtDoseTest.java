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
import static de.dktk.dd.rpb.core.util.Constants.DOSE_COMMENT;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_SUMMATION_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_UNIT;
import static de.dktk.dd.rpb.core.util.Constants.IMAGE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.INSTANCE_CREATION_DATE;
import static de.dktk.dd.rpb.core.util.Constants.REFERENCED_RTPLAN_UID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DicomImageRtDose.class, Logger.class, LoggerFactory.class})
public class DicomImageRtDoseTest {

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
        DicomImageRtDose rtDose = new DicomImageRtDose(getRtDoseJsonDataObject(dummyDate));

        assertEquals(DICOM_IMAGE_SOP_INSTANCE_UID, rtDose.getSopInstanceUID());
        assertEquals(IMAGE_TYPE, rtDose.getImageType());
        assertEquals(DOSE_UNIT, rtDose.getDoseUnits());
        assertEquals(DOSE_TYPE, rtDose.getDoseType());
        assertEquals(DOSE_COMMENT, rtDose.getDoseComment());
        assertEquals(DOSE_SUMMATION_TYPE, rtDose.getDoseSummationType());
        assertEquals(dummyDate, rtDose.getInstanceCreationDate());
        assertEquals(REFERENCED_RTPLAN_UID, rtDose.getReferencedRTPlanUID());
        assertEquals(REFERENCED_RTPLAN_UID, rtDose.getReferencedDicomSeriesUID());

    }

    @Test
    public void getDescription() throws JSONException {

        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtDoseJsonDataObject(dummyDate);

        DicomImageRtDose rtDose = new DicomImageRtDose(jsonData);
        assertEquals(DOSE_COMMENT, rtDose.getDescription());

        jsonData.remove(DOSE_COMMENT);
        rtDose = new DicomImageRtDose(jsonData);
        assertEquals("", rtDose.getDescription());

    }

    @Test
    public void getMetaParameterList() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtDoseJsonDataObject(dummyDate);

        DicomImageRtDose rtDose = new DicomImageRtDose(jsonData);
        assertEquals(4, rtDose.getMetaParameterList().size());

    }

    private JSONObject getRtDoseJsonDataObject(String dummyDate) throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put(DICOM_IMAGE_SOP_INSTANCE_UID, DICOM_IMAGE_SOP_INSTANCE_UID);
        jsonData.put(IMAGE_TYPE, IMAGE_TYPE);
        jsonData.put(DOSE_UNIT, DOSE_UNIT);
        jsonData.put(DOSE_TYPE, DOSE_TYPE);
        jsonData.put(DOSE_COMMENT, DOSE_COMMENT);
        jsonData.put(DOSE_SUMMATION_TYPE, DOSE_SUMMATION_TYPE);
        jsonData.put(INSTANCE_CREATION_DATE, dummyDate);
        jsonData.put(REFERENCED_RTPLAN_UID, REFERENCED_RTPLAN_UID);

        return jsonData;
    }
}