/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.domain.pacs;

import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DicomSeries stage representation in PACS
 * <p>
 * clinical - clinical identifiable DICOM data
 * stage one - de-identified project agnostic DICOM data
 * stage two - de-identified project specific DICOM data
 * <p>
 * It is used especially for DICOM lookup work with representations of the DICOM data in different stages.
 */
public class StagedDicomSeries extends DicomSeries {

    //region Members

    private boolean stageOneRepresentation = false;
    private boolean stageTwoRepresentation = false;

    private String uidPrefix = "";
    private String partnerSideCode = "";
    private String edcCode = "";

    protected String clinicalSeriesUid = "";
    private String stageOneSeriesUid = "";
    private String stageTwoSeriesUid = "";

    protected String toolTipValue = "";

    // List of detail information of the series, depending on the modality
    private List<String> originalDetailsList = new ArrayList<>();

    protected List<DicomImage> clinicalDicomImages = new ArrayList<>();
    protected List<DicomImage> stageOneDicomImages = new ArrayList<>();
    protected List<DicomImage> stageTwoDicomImages = new ArrayList<>();

    protected Map<String, List<DicomImage>> clinicalDicomImagesByReference = new HashMap<>();
    protected Map<String, List<DicomImage>> stageOneDicomImagesByReference = new HashMap<>();
    protected Map<String, List<DicomImage>> stageTwoDicomImagesByReference = new HashMap<>();

    private List<String> regionOfInterestList = new ArrayList<>();

    //endregion

    //region Constructors

    public StagedDicomSeries() {
        super();
    }

    public StagedDicomSeries(String uidPrefix, String partnerSideCode, String edcCode) {
        super();
        this.uidPrefix = uidPrefix;
        this.partnerSideCode = partnerSideCode;
        this.edcCode = edcCode;
    }

    //endregion

    //region Properties

    public String getClinicalSeriesUid() {
        return clinicalSeriesUid;
    }

    public void setClinicalSeriesUid(String clinicalSeriesUid) {
        this.clinicalSeriesUid = clinicalSeriesUid;
    }

    public String getUidPrefix() {
        return uidPrefix;
    }

    public String getPartnerSideCode() {
        return partnerSideCode;
    }

    public String getEdcCode() {
        return edcCode;
    }

    public String getStageTwoSeriesUid() {
        return stageTwoSeriesUid;
    }

    public void setStageTwoSeriesUid(String stageTwoSeriesUid) {
        this.stageTwoSeriesUid = stageTwoSeriesUid;
    }

    public String getStageOneSeriesUid() {
        return stageOneSeriesUid;
    }

    public void setStageOneSeriesUid(String stageOneSeriesUid) {
        this.stageOneSeriesUid = stageOneSeriesUid;
    }

    public boolean isStageOneRepresentation() {
        return stageOneRepresentation;
    }

    public void setStageOneRepresentation(boolean stageOneRepresentation) {
        this.stageOneRepresentation = stageOneRepresentation;
    }

    public boolean isStageTwoRepresentation() {
        return stageTwoRepresentation;
    }

    public void setStageTwoRepresentation(boolean stageTwoRepresentation) {
        this.stageTwoRepresentation = stageTwoRepresentation;
    }

    public List<String> getRegionsOfInterestList() {
        return this.regionOfInterestList;
    }

    public void setRegionOfInterestList(List<String> regionOfInterestList) {
        this.regionOfInterestList = regionOfInterestList;
    }

    public void setToolTipValue(String toolTipValue) {
        this.toolTipValue = toolTipValue;
    }

    public void setOriginalDetailsList(List<String> originalDetailsList) {
        this.originalDetailsList = originalDetailsList;
    }

    public List<DicomImage> getClinicalDicomImages() {
        return clinicalDicomImages;
    }

    public void setClinicalDicomImages(List<DicomImage> clinicalDicomImages) {
        this.clinicalDicomImages = clinicalDicomImages;
        this.clinicalDicomImagesByReference = this.createDicomImagesByReferenceMap(clinicalDicomImages);
    }

    public List<DicomImage> getStageOneDicomImages() {
        return stageOneDicomImages;
    }

    public void setStageOneDicomImages(List<DicomImage> stageOneDicomImages) {
        this.stageOneDicomImages = stageOneDicomImages;
        this.stageOneDicomImagesByReference = this.createDicomImagesByReferenceMap(stageOneDicomImages);
    }

    public List<DicomImage> getStageTwoDicomImages() {
        return stageTwoDicomImages;
    }

    public void setStageTwoDicomImages(List<DicomImage> stageTwoDicomImages) {
        this.stageTwoDicomImages = stageTwoDicomImages;
        this.stageTwoDicomImagesByReference = this.createDicomImagesByReferenceMap(stageTwoDicomImages);
    }

    public Map<String, List<DicomImage>> getClinicalDicomImagesByReference() {
        return clinicalDicomImagesByReference;
    }

    public Map<String, List<DicomImage>> getStageOneDicomImagesByReference() {
        return stageOneDicomImagesByReference;
    }

    public Map<String, List<DicomImage>> getStageTwoDicomImagesByReference() {
        return stageTwoDicomImagesByReference;
    }

    //endregion

    //region Methods

    public List<String> getDetailsList() {
        if(this.getAvailableDicomImages().size() > 0){
            return this.getSeriesMetaParameterList();
        } else {
            return originalDetailsList;
        }
    }

    public List<DicomImage> getAvailableDicomImages() {
        if (isClinicalSystemDataContext()) {
            return this.clinicalDicomImages;
        } else {
            return this.stageOneDicomImages;
        }
    }

    public List<DicomImage> getStagedDicomImages() {
        if (isClinicalSystemDataContext()) {
            return this.stageOneDicomImages;
        } else {
            return this.stageTwoDicomImages;
        }
    }

    /**
     * Is true if a Dicom series has a clinical and stage one representation or
     * a stage one and a stage two representation.
     *
     * @return true if considered staged
     */
    public boolean isStaged() {
        boolean isStaged = false;

        if (!this.clinicalSeriesUid.isEmpty()) {
            isStaged = this.isStageOneRepresentation();
        } else {
            isStaged = this.isStageTwoRepresentation();
        }

        return isStaged;
    }

    /**
     * Compares staged and available images and return the state for the ThreeState checkbox component
     *
     * @return String state: 0 - no image stages, 1 - all images stages, 2 - count differs -> not all images staged
     */
    public String getStagingState() {
        if (this.isRtRelated()) {
            return getStagingStateRtRelatedSeries();
        } else {
            return getStagingStateNotRtRelatedSeries();
        }
    }

    private String getStagingStateRtRelatedSeries() {
        int stagedImagesCount = this.getStagedDicomImages().size();
        int dicomImagesCount = this.getAvailableDicomImages().size();
        // no DICOM image staged
        if (stagedImagesCount == 0) {
            return "0";
        }
        // all available DICOM images are staged
        if (stagedImagesCount == dicomImagesCount) {
            return "1";
        }
        // there is a difference between staged and available images
        return "2";
    }

    private String getStagingStateNotRtRelatedSeries() {
        if (this.isStaged()) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * Compares staged and available images and return the a description for the user
     *
     * @return String description about the state
     */
    public String stagedImagesToAvailableImagesCount() {
        if(!this.isRtRelated()){
            return "";
        }

        int stagedImagesCount = this.getStagedDicomImages().size();
        int dicomImagesCount = this.getAvailableDicomImages().size();

        return "(" + stagedImagesCount + "/" + dicomImagesCount + ")";
    }


    protected Map<String, List<DicomImage>> createDicomImagesByReferenceMap(List<DicomImage> imagesList) {
        Map<String, List<DicomImage>> uidToImageListMap = new HashMap<>();

        if (imagesList != null) {
            for (DicomImage image : imagesList) {
                String referencedSeriesUid = image.getReferencedDicomSeriesUID();

                if (referencedSeriesUid == null) {
                    referencedSeriesUid = "null";
                }
                if (!referencedSeriesUid.isEmpty()) {
                    List<DicomImage> seriesList = uidToImageListMap.get(referencedSeriesUid);
                    if (seriesList == null) {
                        seriesList = new ArrayList<>();
                    }
                    seriesList.add(image);
                    uidToImageListMap.put(referencedSeriesUid, seriesList);
                }

            }
        }
        return uidToImageListMap;
    }

    /**
     * DICOM Images can refer other DICOM series. This function provides a list of DICOM images that refer
     * to the specified DICOM series.
     *
     * @param referencedUID String DICOM series UID that is referenced by the DICOM images
     * @return List<DicomImage> List of DICOM images that refer to the specified DICOM series.
     */
    public List<DicomImage> getImagesByReference(String referencedUID) {
        if (isClinicalSystemDataContext()) {
            return this.clinicalDicomImagesByReference.get(referencedUID);
        } else {
            return this.stageOneDicomImagesByReference.get(referencedUID);
        }
    }

    /**
     * DICOM Images can refer other DICOM series.
     * This function returns a set of referenced DICOM Series UIDs.
     *
     * @return Set<String> referenced DICOM series UIDs
     */
    public Set<String> getReferencedUidSet() {
        if (isClinicalSystemDataContext()) {
            return this.clinicalDicomImagesByReference.keySet();
        } else {
            return this.stageOneDicomImagesByReference.keySet();
        }
    }

    /**
     * Creates a String with the parameters of the series for the UI out of the seriesMetaParameter list.
     * If the list is empty it returns the original ToolTip value that was created for the StagedVirtual series.
     *
     * @return String series details that will be presented as tooltip in the UI
     */
    public String getToolTipValue() {
        if (this.getSeriesMetaParameterList().size() > 0) {
            return String.join("; ", getSeriesMetaParameterList());
        }
        return this.toolTipValue;
    }

    //endregion

    // region overwrites

    /**
     * @param images
     */
    @Override
    public void setSeriesImages(List<DicomImage> images) {
        if (images != null) {
            this.seriesImages = images;
        }
    }

    @Override
    public String getSeriesDescription() {
        if (this.getAvailableDicomImages().size() > 0) {
            DicomImage image = this.getAvailableDicomImages().get(0);

            String description = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(image.getDescription());
            if (!description.isEmpty()) {
                return description;
            }
        }
        return super.getSeriesDescription();
    }

    /**
     * Creates an array with series parameters that can be shown in the UI.
     * <p>
     * In the DicomSeries this information is created from PACS system,
     * based on an aggregation of the DICOM images of the series in PACS.
     * Since the StagedDicomVirtualSeries is a split of a DicomSeries, this function re-creates the information,
     * based on the images that belong to the "virtual" Series (all DICOM images reference the same other DICOM series)
     *
     * @return List<String> series information
     */
    @Override
    public List<String> getSeriesMetaParameterList() {
        List<String> metaParameterList = new ArrayList<>();

        if (this.getAvailableDicomImages().size() > 0) {
            DicomImage image = this.getAvailableDicomImages().get(0);

            String description = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(getSeriesDescription());
            if (!image.getDescription().isEmpty()) {
                description = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(image.getDescription());
            }
            if (!description.isEmpty()) {
                metaParameterList.add(Constants.DICOM_SERIES_DESCRIPTION + ": " + description);
            }

            if (image.getMetaParameterList().size() > 0) {
                metaParameterList.addAll(image.getMetaParameterList());
            }
        }

        if (!getSeriesNumber().isEmpty()) {
            metaParameterList.add(Constants.SERIES_NUMBER + ": " + getSeriesNumber());
        }

        return metaParameterList;
    }

    protected boolean isClinicalSystemDataContext(StagedDicomSeries stagedDicomSeries){
        return !stagedDicomSeries.getClinicalSeriesUid().isEmpty();
    }

    protected boolean isClinicalSystemDataContext(){
        return !this.getClinicalSeriesUid().isEmpty();
    }

    // endregion
}
