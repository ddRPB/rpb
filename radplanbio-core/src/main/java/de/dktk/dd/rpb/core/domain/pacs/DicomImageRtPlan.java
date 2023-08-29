package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_APPROVAL_STATUS;
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

/**
 * {@inheritDoc}
 */
public class DicomImageRtPlan extends DicomImage {
    private final String rtPlanLabel;
    private final String rtPlanManufacturerModelName;
    private final String rtPlanName;
    private final String rtPlanDate;
    private final String manufacturer;
    private final String rtPlanDescription;
    private final String rtPlanGeometry;
    private final String seriesNumber;
    private final String referencedRtStructUid;
    private final String approvalStatus;

    public DicomImageRtPlan(JSONObject jsonRepresentation) {
        super();
        this.setSopInstanceUID(jsonRepresentation.optString(DICOM_IMAGE_SOP_INSTANCE_UID, ""));
        this.rtPlanLabel = jsonRepresentation.optString(RTPLAN_LABEL, "");
        this.rtPlanManufacturerModelName = jsonRepresentation.optString(RTPLAN_MANUFACTURER_MODEL_NAME, "");
        this.rtPlanName = jsonRepresentation.optString(RTPLAN_NAME, "");
        this.rtPlanDate = jsonRepresentation.optString(RTPLAN_DATE, "");
        this.manufacturer = jsonRepresentation.optString(RTPLAN_MANUFACTURER, "");
        this.rtPlanDescription = jsonRepresentation.optString(RTPLAN_DESCRIPTION, "");
        this.rtPlanGeometry = jsonRepresentation.optString(RTPLAN_GEOMETRY, "");
        this.seriesNumber = jsonRepresentation.optString(SERIES_NUMBER, "");
        this.referencedRtStructUid = jsonRepresentation.optString(REFERENCED_RTSTRUCT_UID, "");
        this.approvalStatus = jsonRepresentation.optString(DICOM_IMAGE_APPROVAL_STATUS, "");
    }

    public DicomImageRtPlan(
            String rtPlanLabel,
            String rtPlanName,
            String rtPlanDate,
            String rtPlanGeometry,
            String referencedRtStructUid
    ) {
        super();
        this.rtPlanLabel = rtPlanLabel;
        this.rtPlanManufacturerModelName = "";
        this.rtPlanName = rtPlanName;
        this.rtPlanDate = rtPlanDate;
        this.manufacturer = "";
        this.rtPlanDescription = "";
        this.rtPlanGeometry = rtPlanGeometry;
        this.seriesNumber = "";
        this.referencedRtStructUid = referencedRtStructUid;
        approvalStatus = "";
    }

    // region Getter and Setter

    public String getRtPlanLabel() {
        return rtPlanLabel;
    }

    public String getRtPlanManufacturerModelName() {
        return rtPlanManufacturerModelName;
    }

    public String getRtPlanName() {
        return rtPlanName;
    }

    public String getRtPlanDate() {
        return rtPlanDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getRtPlanDescription() {
        return rtPlanDescription;
    }

    public String getRtPlanGeometry() {
        return rtPlanGeometry;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public String getReferencedRtStructUid() {
        return referencedRtStructUid;
    }

    // endregion

    // region override

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencedDicomSeriesUID() {
        return this.getReferencedRtStructUid();
    }

    public String getDescription() {

        if (this.rtPlanLabel != null && !"".equals(this.rtPlanLabel)) {
            return this.rtPlanLabel;
        }
        if (this.rtPlanName != null && !"".equals(this.rtPlanName)) {
            return this.rtPlanName;
        }
        if (this.rtPlanDescription != null && !"".equals(this.rtPlanDescription)) {
            return this.rtPlanDescription;
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMetaParameterList() {
        List<String> metaParameterList = new ArrayList<>();

        if (!rtPlanLabel.isEmpty()) {
            metaParameterList.add(RTPLAN_LABEL + ": " + rtPlanLabel);
        }

        if (!rtPlanManufacturerModelName.isEmpty()) {
            metaParameterList.add(RTPLAN_MANUFACTURER_MODEL_NAME + ": " + rtPlanManufacturerModelName);
        }

        if (!manufacturer.isEmpty()) {
            metaParameterList.add(RTPLAN_MANUFACTURER + ": " + manufacturer);
        }

        if (!rtPlanName.isEmpty()) {
            metaParameterList.add(RTPLAN_NAME + ": " + rtPlanName);
        }

        if (!rtPlanDate.isEmpty()) {
            metaParameterList.add(RTPLAN_DATE + ": " + rtPlanDate);
        }

        if (!rtPlanDescription.isEmpty()) {
            metaParameterList.add(RTPLAN_DESCRIPTION + ": " + rtPlanDescription);
        }

        if (!rtPlanGeometry.isEmpty()) {
            metaParameterList.add(RTPLAN_GEOMETRY + ": " + rtPlanGeometry);
        }

        if (!approvalStatus.isEmpty()) {
            metaParameterList.add(DICOM_IMAGE_APPROVAL_STATUS + ": " + approvalStatus);
        }

        return metaParameterList;
    }

    // endregion
}
