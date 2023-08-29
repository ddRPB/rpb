package de.dktk.dd.rpb.core;

import de.dktk.dd.rpb.core.domain.pacs.*;
import org.json.JSONException;
import org.json.JSONObject;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Factory to create an appropriate DicomImage instance out of the JSON representation, based on modality.
 */
public class DicomImageFactory {

    public static DicomImage getDicomImage(String modality, JSONObject jsonRepresentation) throws JSONException {
        DicomImage image;
        switch (modality) {
            case DICOM_RTPLAN:
                image = new DicomImageRtPlan(jsonRepresentation);
                break;
            case DICOM_RTDOSE:
                image = new DicomImageRtDose(jsonRepresentation);
                break;
            case DICOM_RTSTRUCT:
                image = new DicomImageRtStruct(jsonRepresentation);
                break;
            case DICOM_RTIMAGE:
                image = new DicomImageRtImage(jsonRepresentation);
                break;
            default:
                image = new DicomImage();
        }

        image.setSopInstanceUID(jsonRepresentation.optString(DICOM_IMAGE_SOP_INSTANCE_UID,""));
        image.setSize(jsonRepresentation.optInt(DICOM_IMAGE_SIZE, 1024));

        return image;
    }
}
