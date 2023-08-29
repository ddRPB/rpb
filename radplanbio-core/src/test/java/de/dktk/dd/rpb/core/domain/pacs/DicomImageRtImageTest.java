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
import static de.dktk.dd.rpb.core.util.Constants.IMAGE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.INSTANCE_CREATION_DATE;
import static de.dktk.dd.rpb.core.util.Constants.REFERENCED_RTPLAN_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_DESCRIPTION;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_LABEL;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DicomImageRtImage.class, Logger.class, LoggerFactory.class})
public class DicomImageRtImageTest {

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
        DicomImageRtImage rtImage = new DicomImageRtImage(getRtImageJsonDataObject(dummyDate));

        assertEquals(DICOM_IMAGE_SOP_INSTANCE_UID, rtImage.getSopInstanceUID());
        assertEquals(IMAGE_TYPE, rtImage.getImageType());
        assertEquals(RTIMAGE_LABEL, rtImage.getRtImageLabel());
        assertEquals(RTIMAGE_NAME, rtImage.getRtImageName());
        assertEquals(RTIMAGE_DESCRIPTION, rtImage.getRtImageDescription());
        assertEquals(dummyDate, rtImage.getInstanceCreationDate());
        assertEquals(REFERENCED_RTPLAN_UID, rtImage.getReferencedRTPlanUID());
        assertEquals(REFERENCED_RTPLAN_UID, rtImage.getReferencedDicomSeriesUID());
    }

    @Test
    public void getDescription() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtImageJsonDataObject(dummyDate);

        DicomImageRtImage rtImage = new DicomImageRtImage(jsonData);
        assertEquals(RTIMAGE_NAME, rtImage.getDescription());

        jsonData.remove(RTIMAGE_NAME);

        rtImage = new DicomImageRtImage(jsonData);
        assertEquals(RTIMAGE_LABEL, rtImage.getDescription());

        jsonData.remove(RTIMAGE_LABEL);

        rtImage = new DicomImageRtImage(jsonData);
        assertEquals(RTIMAGE_DESCRIPTION, rtImage.getDescription());

        jsonData.remove(RTIMAGE_DESCRIPTION);

        rtImage = new DicomImageRtImage(jsonData);
        assertEquals("", rtImage.getDescription());
    }

    @Test
    public void getMetaParameterList() throws JSONException {
        String dummyDate = "31.12.1900";
        JSONObject jsonData = getRtImageJsonDataObject(dummyDate);

        DicomImageRtImage rtImage = new DicomImageRtImage(jsonData);
        assertEquals(3, rtImage.getMetaParameterList().size());

    }

    private JSONObject getRtImageJsonDataObject(String dummyDate) throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put(DICOM_IMAGE_SOP_INSTANCE_UID, DICOM_IMAGE_SOP_INSTANCE_UID);
        jsonData.put(IMAGE_TYPE, IMAGE_TYPE);
        jsonData.put(RTIMAGE_LABEL, RTIMAGE_LABEL);
        jsonData.put(RTIMAGE_NAME, RTIMAGE_NAME);
        jsonData.put(RTIMAGE_DESCRIPTION, RTIMAGE_DESCRIPTION);
        jsonData.put(INSTANCE_CREATION_DATE, dummyDate);
        jsonData.put(REFERENCED_RTPLAN_UID, REFERENCED_RTPLAN_UID);

        return jsonData;
    }
}