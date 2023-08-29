package de.dktk.dd.rpb.core.domain.pacs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_APPROVAL_STATUS;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_IMAGE_SOP_INSTANCE_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_REFERENCED_RTSERIES_UID;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_STRUCTURE_SET_DATE;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_STRUCTURE_SET_DESCRIPTION;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_STRUCTURE_SET_LABEL;
import static de.dktk.dd.rpb.core.util.Constants.RTSTRUCT_STRUCTURE_SET_NAME;

/**
 * {@inheritDoc}
 */
public class DicomImageRtStruct extends DicomImage {
    private final String structureSetLabel;
    private String structureSetDate;
    private String structureSetDescription;
    private String structureSetName;
    private final String referencedFrameOfReferenceUid;
    private final String referencedRtSeriesUid;
    private final List<String> roiNameList = new ArrayList<>();
    private final String approvalStatus;

    public DicomImageRtStruct(JSONObject jsonRepresentation) {

        super();
        this.setSopInstanceUID(jsonRepresentation.optString(DICOM_IMAGE_SOP_INSTANCE_UID, ""));
        this.structureSetLabel = jsonRepresentation.optString(RTSTRUCT_STRUCTURE_SET_LABEL, "");
        this.structureSetDate = jsonRepresentation.optString(RTSTRUCT_STRUCTURE_SET_DATE, "");
        this.structureSetDescription = jsonRepresentation.optString(RTSTRUCT_STRUCTURE_SET_DESCRIPTION, "");
        this.structureSetName = jsonRepresentation.optString(RTSTRUCT_STRUCTURE_SET_NAME, "");
        this.referencedFrameOfReferenceUid = jsonRepresentation.optString(RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID, "");
        this.referencedRtSeriesUid = jsonRepresentation.optString(RTSTRUCT_REFERENCED_RTSERIES_UID, "");
        this.approvalStatus = jsonRepresentation.optString(DICOM_IMAGE_APPROVAL_STATUS, "");

        // Creates a list of ROI descriptions for the UI, based on the "ROIs" and "RTROIObservations" lists
        this.createRoiNameList(jsonRepresentation);
    }

    private void createRoiNameList(JSONObject jsonRepresentation) {
        HashMap<String, String> roiObservationsLookup = new HashMap<>();

        createRoiObservationsLookup(jsonRepresentation, roiObservationsLookup);
        addRoiNameAndNumberToRoiNameList(jsonRepresentation, roiObservationsLookup);
    }

    private void createRoiObservationsLookup(JSONObject jsonRepresentation, HashMap<String, String> roiObservationsLookup) {
        JSONArray roiObservationArray = new JSONArray();

        if (jsonRepresentation.optJSONArray("RTROIObservations") != null) {
            roiObservationArray = jsonRepresentation.optJSONArray("RTROIObservations");
        }

        for (int i = 0; i < roiObservationArray.length(); i++) {

            JSONObject roiObjects = roiObservationArray.optJSONObject(i);

            String referencedROINumber = roiObjects.optString("ReferencedROINumber");
            String roiInterpretedType = roiObjects.optString("RTROIInterpretedType");
            roiObservationsLookup.put(referencedROINumber, roiInterpretedType);
        }
    }

    private void addRoiNameAndNumberToRoiNameList(JSONObject jsonRepresentation, HashMap<String, String> roiObservationsLookup) {
        JSONArray roiArray = new JSONArray();

        if (jsonRepresentation.optJSONArray("ROIs") != null) {
            roiArray = jsonRepresentation.optJSONArray("ROIs");
        }

        for (int i = 0; i < roiArray.length(); i++) {

            JSONObject roiObjects = roiArray.optJSONObject(i);
            String roiName = roiObjects.optString("ROIName");
            String roiNumber = roiObjects.optString("ROINumber");

            if (roiObservationsLookup.containsKey(roiNumber)) {
                String roiInterpretedType = roiObservationsLookup.get(roiNumber);
                this.roiNameList.add(roiNumber + " - " + roiName + " (" + roiInterpretedType + ")");
            } else {
                this.roiNameList.add(roiNumber + " - " + roiName);
            }

        }
    }

    public DicomImageRtStruct(String structureSetLabel, String referencedFrameOfReferenceUid, String referencedRtSeriesUid) {

        super();

        this.structureSetLabel = structureSetLabel;
        this.referencedFrameOfReferenceUid = referencedFrameOfReferenceUid;
        this.referencedRtSeriesUid = referencedRtSeriesUid;
        this.approvalStatus = "";
    }

    public String getStructureSetLabel() {
        return structureSetLabel;
    }

    public String getStructureSetDate() {
        return structureSetDate;
    }

    public String getStructureSetDescription() {
        return structureSetDescription;
    }

    public String getStructureSetName() {
        return structureSetName;
    }

    public String getReferencedFrameOfReferenceUid() {
        return referencedFrameOfReferenceUid;
    }

    public String getReferencedRtSeriesUid() {
        return referencedRtSeriesUid;
    }

    public List<String> getRoiNameList() {
        return roiNameList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencedDicomSeriesUID() {
        return this.getReferencedRtSeriesUid();
    }

    public String getDescription() {
        if (this.structureSetLabel != null && !"".equals(this.structureSetLabel)) {
            return this.structureSetLabel;
        }
        if (this.structureSetName != null && !"".equals(this.structureSetName)) {
            return this.structureSetName;
        }
        if (this.structureSetDescription != null && !"".equals(this.structureSetDescription)) {
            return this.structureSetDescription;
        }
        return "";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMetaParameterList() {
        List<String> metaParameterList = new ArrayList<>();

        if (!structureSetLabel.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_LABEL + ": " + structureSetLabel);
        }

        if (!structureSetName.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_NAME + ": " + structureSetName);
        }

        if (!structureSetDescription.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_DESCRIPTION + ": " + structureSetDescription);
        }

        if (!structureSetDate.isEmpty()) {
            metaParameterList.add(RTSTRUCT_STRUCTURE_SET_DATE + ": " + structureSetDate);
        }

        if (!approvalStatus.isEmpty()) {
            metaParameterList.add(DICOM_IMAGE_APPROVAL_STATUS + ": " + approvalStatus);
        }

        return metaParameterList;
    }
}
