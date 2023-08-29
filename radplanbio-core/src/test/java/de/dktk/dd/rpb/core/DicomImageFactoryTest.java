package de.dktk.dd.rpb.core;

import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtDose;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtPlan;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtStruct;
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
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, LoggerFactory.class})
public class DicomImageFactoryTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void passes_with_empty_string_if_json_object_has_no_SOPInstanceUID_property() throws JSONException {
        JSONObject object = new JSONObject("{}");

        DicomImage image = DicomImageFactory.getDicomImage("", object);

        assertTrue(image.getSopInstanceUID().isEmpty());
    }

    @Test
    public void returns_image_if_json_object_has_SOPInstanceUID_property() throws JSONException {
        JSONObject object = new JSONObject("{" +
                "\"SOPInstanceUID\": \"1.2.826.0.1.3680043.9.7275.0.12251386410679453829395555494384314\"," +
                "\"Size\": \"11498\"" +
                "}");

        DicomImage image = DicomImageFactory.getDicomImage("", object);

        assertNotNull(image);
        assertThat(image, instanceOf(DicomImage.class));
        assertTrue(image.getSopInstanceUID().equals(object.get(DICOM_IMAGE_SOP_INSTANCE_UID)));
        assertTrue(image.getSize() == object.getInt(DICOM_IMAGE_SIZE));
    }

    // region RtStruct

    @Test
    public void passes_with_empty_string_for_RtStruct() throws JSONException {
        JSONObject object = new JSONObject("{}");

        DicomImage image = DicomImageFactory.getDicomImage(DICOM_RTSTRUCT, object);

        assertTrue(image.getSopInstanceUID().isEmpty());
    }

    @Test
    public void returns_rtstruct_image() throws JSONException {
        JSONObject object = this.getRtStructObject();

        DicomImage image = DicomImageFactory.getDicomImage(DICOM_RTSTRUCT, object);

        assertNotNull(image);
        assertThat(image, instanceOf(DicomImageRtStruct.class));
        assertTrue(((DicomImageRtStruct) image).getStructureSetLabel().equals(object.get(RTSTRUCT_STRUCTURE_SET_LABEL)));
        assertTrue(((DicomImageRtStruct) image).getReferencedFrameOfReferenceUid().equals(object.get(RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID)));
        assertTrue(((DicomImageRtStruct) image).getReferencedRtSeriesUid().equals(object.get(RTSTRUCT_REFERENCED_RTSERIES_UID)));
        assertTrue((image.getSopInstanceUID().equals(object.get(DICOM_IMAGE_SOP_INSTANCE_UID))));
        assertTrue( image.getSize() == object.getInt(DICOM_IMAGE_SIZE));
    }

    private JSONObject getRtStructObject() throws JSONException {
        String rtPlan = "{ " +
                "\"StructureSetLabel\": \"Gloiblastom\"," +
                "\"ReferencedFrameOfReferenceUID\": \"1.2.826.0.1.3680043.9.7275.0.76144633654213532954969271880615139\"," +
                "\"ReferencedRTSeriesUID\": \"1.2.826.0.1.3680043.9.7275.0.12484414159556553853953222969357983\"," +
                "\"SOPInstanceUID\": \"1.2.826.0.1.3680043.9.7275.0.14017962418464852965840573030698134\"," +
                "\"Size\": \"1173514\"" +
                "}";
        return new JSONObject(rtPlan);
    }

    // endregion

    // region RTPlan

    @Test
    public void returns_rtplan_image() throws JSONException {
        JSONObject object = this.getRtPlanObject();

        DicomImage image = DicomImageFactory.getDicomImage(DICOM_RTPLAN, object);

        assertNotNull(image);
        assertThat(image, instanceOf(DicomImageRtPlan.class));
        assertTrue(((DicomImageRtPlan) image).getRtPlanLabel().equals(object.get(RTPLAN_LABEL)));
        assertTrue(((DicomImageRtPlan) image).getRtPlanName().equals(object.get(RTPLAN_NAME)));
        assertTrue(((DicomImageRtPlan) image).getRtPlanDate().equals(object.get(RTPLAN_DATE)));
        assertTrue(((DicomImageRtPlan) image).getRtPlanGeometry().equals(object.get(RTPLAN_GEOMETRY)));
        assertTrue(((DicomImageRtPlan) image).getReferencedRtStructUid().equals(object.get(REFERENCED_RTSTRUCT_UID)));
        assertTrue(((DicomImageRtPlan) image).getSopInstanceUID().equals(object.get(DICOM_IMAGE_SOP_INSTANCE_UID)));
        assertTrue(((DicomImageRtPlan) image).getSize() == object.getInt(DICOM_IMAGE_SIZE));
    }

    private JSONObject getRtPlanObject() throws JSONException {
        String rtPlan = "{ " +
                "\"RTPlanLabel\": \"O4_HiTu_TS2_LABEL\"," +
                "\"RTPlanName\": \"O4_HiTu_TS2\"," +
                "\"RTPlanDate\": \"20130911\"," +
                "\"RTPlanGeometry\": \"PATIENT\", " +
                "\"ReferencedRTStructUID\": \"1.2.826.0.1.3680043.9.7275.0.84629781582644298416868747535489258\"," +
                "\"SOPInstanceUID\": \"1.2.826.0.1.3680043.9.7275.0.14017962418464852965840573030698134\"," +
                "\"Size\": \"11498\"" +
                "}";
        return new JSONObject(rtPlan);
    }

    // endregion

    // region RTDose

    @Test
    public void returns_rtdose_image() throws JSONException{
        JSONObject object = this.getRtDoseObject();

        DicomImage image = DicomImageFactory.getDicomImage(DICOM_RTDOSE, object);

        assertNotNull(image);
        assertThat(image, instanceOf(DicomImageRtDose.class));
        assertTrue(((DicomImageRtDose) image).getImageType().equals(object.get(IMAGE_TYPE)));
        assertTrue(((DicomImageRtDose) image).getDoseUnits().equals(object.get(DOSE_UNIT)));
        assertTrue(((DicomImageRtDose) image).getDoseType().equals(object.get(DOSE_TYPE)));
        assertTrue(((DicomImageRtDose) image).getDoseComment().equals(object.get(DOSE_COMMENT)));
        assertTrue(((DicomImageRtDose) image).getDoseSummationType().equals(object.get(DOSE_SUMMATION_TYPE)));
        assertTrue(((DicomImageRtDose) image).getInstanceCreationDate().equals(object.get(INSTANCE_CREATION_DATE)));
        assertTrue(((DicomImageRtDose) image).getReferencedRTPlanUID().equals(object.get(REFERENCED_RTPLAN_UID)));
        assertTrue(((DicomImageRtDose) image).getSopInstanceUID().equals(object.get(DICOM_IMAGE_SOP_INSTANCE_UID)));
        assertTrue(((DicomImageRtDose) image).getSize() == object.getInt(DICOM_IMAGE_SIZE));
    }

    private JSONObject getRtDoseObject() throws JSONException {
        String rtPlan = "{ " +
                "\"ImageType\": \"ORIGINAL\\\\PRIMARY\\\\DOSE\"," +
                "\"DoseUnits\": \"GY\"," +
                "\"DoseType\": \"PHYSICAL\"," +
                "\"DoseComment\": \"InsufficientExtensionBeamNotCrossingDefinedOutline\", " +
                "\"DoseSummationType\": \"BEAM\"," +
                "\"InstanceCreationDate\": \"20140724\"," +
                "\"ReferencedRTPlanUID\": \"1.2.826.0.1.3680043.9.7275.0.71680156093653982354901858650707144\"," +
                "\"SOPInstanceUID\": \"1.2.826.0.1.3680043.9.7275.0.14017962418464852965840573030698134\"," +
                "\"Size\": \"11498\"" +
                "}";
        return new JSONObject(rtPlan);
    }

    // endregion

    // region RTImage

    @Test
    public void returns_rtimage() throws JSONException{
        JSONObject object = this.getRtImageObject();

        DicomImage image = DicomImageFactory.getDicomImage(DICOM_RTIMAGE, object);

        assertNotNull(image);
        assertThat(image, instanceOf(DicomImageRtImage.class));
        assertTrue(((DicomImageRtImage) image).getImageType().equals(object.get(IMAGE_TYPE)));
        assertTrue(((DicomImageRtImage) image).getRtImageLabel().equals(object.get(RTIMAGE_LABEL)));
        assertTrue(((DicomImageRtImage) image).getRtImageName().equals(object.get(RTIMAGE_NAME)));
        assertTrue(((DicomImageRtImage) image).getRtImageDescription().equals(object.get(RTIMAGE_DESCRIPTION)));
        assertTrue(((DicomImageRtImage) image).getInstanceCreationDate().equals(object.get(INSTANCE_CREATION_DATE)));
        assertTrue(((DicomImageRtImage) image).getReferencedRTPlanUID().equals(object.get(REFERENCED_RTPLAN_UID)));
        assertTrue(((DicomImageRtImage) image).getSopInstanceUID().equals(object.get(DICOM_IMAGE_SOP_INSTANCE_UID)));
        assertTrue(((DicomImageRtImage) image).getSize() == object.getInt(DICOM_IMAGE_SIZE));
    }

    private JSONObject getRtImageObject() throws JSONException {
        String rtPlan = "{ " +
                "\"ImageType\": \"ORIGINAL\\\\PRIMARY\\\\DOSE\"," +
                "\"RTImageLabel\": \"01_G000_Label\"," +
                "\"RTImageName\": \"O3_HiTu_Ts1:03_G070\"," +
                "\"RTImageDescription\": \"O3_HiTu_Ts1\", " +
                "\"InstanceCreationDate\": \"20140724\"," +
                "\"ReferencedRTPlanUID\": \"1.2.826.0.1.3680043.9.7275.0.71680156093653982354901858650707144\"," +
                "\"SOPInstanceUID\": \"1.2.826.0.1.3680043.9.7275.0.14017962418464852965840573030698134\"," +
                "\"Size\": \"11498\"" +
                "}";
        return new JSONObject(rtPlan);
    }

    // endregion
}
