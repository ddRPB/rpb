package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_APPROVAL_STATUS;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_SOP_INSTANCE_UID;
import static de.dktk.dd.rpb.core.util.Constants.IMAGE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.INSTANCE_CREATION_DATE;
import static de.dktk.dd.rpb.core.util.Constants.REFERENCED_RTPLAN_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_DESCRIPTION;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_LABEL;
import static de.dktk.dd.rpb.core.util.Constants.RTIMAGE_NAME;

/**
 * {@inheritDoc}
 */
public class DicomImageRtImage extends DicomImage {
    private final String imageType;
    private final String rtImageLabel;
    private final String rtImageName;
    private final String rtImageDescription;
    private final String instanceCreationDate;
    private final String referencedRTPlanUID;
    private final String approvalStatus;

    public DicomImageRtImage(JSONObject jsonRepresentation) {
        super();
        this.setSopInstanceUID(jsonRepresentation.optString(DICOM_IMAGE_SOP_INSTANCE_UID, ""));
        this.imageType = jsonRepresentation.optString(IMAGE_TYPE, "");
        this.rtImageLabel = jsonRepresentation.optString(RTIMAGE_LABEL, "");
        this.rtImageName = jsonRepresentation.optString(RTIMAGE_NAME, "");
        this.rtImageDescription = jsonRepresentation.optString(RTIMAGE_DESCRIPTION, "");
        this.instanceCreationDate = jsonRepresentation.optString(INSTANCE_CREATION_DATE, "");
        this.referencedRTPlanUID = jsonRepresentation.optString(REFERENCED_RTPLAN_UID, "");
        this.approvalStatus = jsonRepresentation.optString(DICOM_IMAGE_APPROVAL_STATUS, "");
    }

    public DicomImageRtImage(
            String sopInstanceUID,
            String imageType,
            String rtImageLabel,
            String rtImageName,
            String rtImageDescription,
            String instanceCreationDate,
            String referencedRTPlanUID
    ) {
        super();
        this.setSopInstanceUID(sopInstanceUID);
        this.imageType = imageType;
        this.rtImageLabel = rtImageLabel;
        this.rtImageName = rtImageName;
        this.rtImageDescription = rtImageDescription;
        this.instanceCreationDate = instanceCreationDate;
        this.referencedRTPlanUID = referencedRTPlanUID;
        this.approvalStatus = "";
    }

    public String getImageType() {
        return imageType;
    }

    public String getRtImageLabel() {
        return rtImageLabel;
    }

    public String getRtImageName() {
        return rtImageName;
    }

    public String getRtImageDescription() {
        return rtImageDescription;
    }

    public String getInstanceCreationDate() {
        return instanceCreationDate;
    }

    public String getReferencedRTPlanUID() {
        return referencedRTPlanUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencedDicomSeriesUID() {
        return this.getReferencedRTPlanUID();
    }

    public String getDescription() {

        if (this.rtImageName != null && !"".equals(this.rtImageName)) {
            return this.rtImageName;
        }
        if (this.rtImageLabel != null && !"".equals(this.rtImageLabel)) {
            return this.rtImageLabel;
        }
        if (this.rtImageDescription != null && !"".equals(this.rtImageDescription)) {
            return this.rtImageDescription;
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMetaParameterList() {
        List<String> metaParameterList = new ArrayList<>();

        if (!rtImageName.isEmpty()) {
            metaParameterList.add(RTIMAGE_NAME + ": " + rtImageName);
        }

        if (!rtImageLabel.isEmpty()) {
            metaParameterList.add(RTIMAGE_LABEL + ": " + rtImageLabel);
        }

        if (!rtImageDescription.isEmpty()) {
            metaParameterList.add(RTIMAGE_DESCRIPTION + ": " + rtImageDescription);
        }

        if (!approvalStatus.isEmpty()) {
            metaParameterList.add(DICOM_IMAGE_APPROVAL_STATUS + ": " + approvalStatus);
        }

        return metaParameterList;
    }
}
