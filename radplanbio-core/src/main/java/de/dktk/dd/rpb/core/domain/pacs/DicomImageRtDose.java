package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_APPROVAL_STATUS;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_SOP_INSTANCE_UID;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_COMMENT;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_SUMMATION_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.DOSE_UNIT;
import static de.dktk.dd.rpb.core.util.Constants.IMAGE_TYPE;
import static de.dktk.dd.rpb.core.util.Constants.INSTANCE_CREATION_DATE;
import static de.dktk.dd.rpb.core.util.Constants.REFERENCED_RTPLAN_UID;

/**
 * DicomImage with RtDose modality
 */
public class DicomImageRtDose extends DicomImage {
    private final String imageType;
    private final String doseUnits;
    private final String doseType;
    private final String doseComment;
    private final String doseSummationType;
    private final String instanceCreationDate;
    private final String referencedRTPlanUID;
    private final String approvalStatus;

    public DicomImageRtDose(JSONObject jsonRepresentation) throws JSONException {
        super();
        this.setSopInstanceUID(jsonRepresentation.optString(DICOM_IMAGE_SOP_INSTANCE_UID, ""));
        this.imageType = jsonRepresentation.optString(IMAGE_TYPE, "");
        this.doseUnits = jsonRepresentation.optString(DOSE_UNIT, "");
        this.doseType = jsonRepresentation.optString(DOSE_TYPE, "");
        this.doseComment = jsonRepresentation.optString(DOSE_COMMENT, "");
        this.doseSummationType = jsonRepresentation.optString(DOSE_SUMMATION_TYPE, "");
        this.instanceCreationDate = jsonRepresentation.optString(INSTANCE_CREATION_DATE, "");
        this.referencedRTPlanUID = jsonRepresentation.optString(REFERENCED_RTPLAN_UID, "");
        this.approvalStatus = jsonRepresentation.optString(DICOM_IMAGE_APPROVAL_STATUS, "");
    }

    public DicomImageRtDose(
            String imageType,
            String doseUnits,
            String doseType,
            String doseComment,
            String doseSummationType,
            String instanceCreationDate,
            String referencedRTPlanUID
    ) {
        this.imageType = imageType;
        this.doseUnits = doseUnits;
        this.doseType = doseType;
        this.doseComment = doseComment;
        this.doseSummationType = doseSummationType;
        this.instanceCreationDate = instanceCreationDate;
        this.referencedRTPlanUID = referencedRTPlanUID;
        this.approvalStatus = "";
    }

    public String getImageType() {
        return imageType;
    }

    public String getDoseUnits() {
        return doseUnits;
    }

    public String getDoseType() {
        return doseType;
    }

    public String getDoseComment() {
        return doseComment;
    }

    public String getDoseSummationType() {
        return doseSummationType;
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

        if (this.doseComment != null && !"".equals(this.doseComment)) {
            return this.doseComment;
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMetaParameterList() {
        List<String> metaParameterList = new ArrayList<>();

        if (!doseComment.isEmpty()) {
            metaParameterList.add(DOSE_COMMENT + ": " + doseComment);
        }

        if (!doseSummationType.isEmpty()) {
            metaParameterList.add(DOSE_SUMMATION_TYPE + ": " + doseSummationType);
        }

        if (!doseUnits.isEmpty()) {
            metaParameterList.add(DOSE_UNIT + ": " + doseUnits);
        }

        if (!doseType.isEmpty()) {
            metaParameterList.add(DOSE_TYPE + ": " + doseType);
        }

        if (!approvalStatus.isEmpty()) {
            metaParameterList.add(DICOM_IMAGE_APPROVAL_STATUS + ": " + approvalStatus);
        }

        return metaParameterList;
    }

}
