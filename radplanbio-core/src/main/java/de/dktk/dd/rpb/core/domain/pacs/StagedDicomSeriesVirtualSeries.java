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

import de.dktk.dd.rpb.core.util.DicomUidReGeneratorUtil;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;

/***
 * DICOM series can consist of different DICOM images. Every image can reference another DICOM series by UID.
 * If a StagedDicomSeries consist of images that refer to different UIDs, then it will be split into different
 * StagedDicomSeriesVirtualSeries that allow in a tree view to chose just the part of the series that consists
 * of the DICOM images that refer to the same other DICOM series.
 */
public class StagedDicomSeriesVirtualSeries extends StagedDicomSeries {

    // true if series is not result of a split
    private boolean isOriginal = true;

    /**
     * Creates a StagedDicomSeriesVirtualSeries, based on the StagedDicomSeries with the Images that refer
     * to the specified DICOM series only.
     * <p>
     * DICOM series can consist of different DICOM images. Every image can reference another DICOM series by UID.
     * If a StagedDicomSeries consist of images that refer to different UIDs, then it will be split into different
     * StagedDicomSeriesVirtualSeries that allow in a tree view to chose just the part of the series that consists
     * of the DICOM images that refer to the same other DICOM series.
     *
     * @param stagedDicomSeries        StagedDicomSeries instance
     * @param referencedDicomSeriesUid String UID of the referenced DICOM series
     */
    public StagedDicomSeriesVirtualSeries(StagedDicomSeries stagedDicomSeries, String referencedDicomSeriesUid) {
        super(stagedDicomSeries.getUidPrefix(), stagedDicomSeries.getPartnerSideCode(), stagedDicomSeries.getEdcCode());

        this.copyPropertiesFromStagedDicomSeries(stagedDicomSeries);

        this.addImagesBasedOnReferences(stagedDicomSeries, referencedDicomSeriesUid);
        this.checkIfSeriesIsOriginal(stagedDicomSeries);

        if (this.getSeriesModality().equals(DICOM_RTSTRUCT)) {
            createUpdatedRegionOfInterestList();
        } else {
            this.setRegionOfInterestList(stagedDicomSeries.getRegionsOfInterestList());
        }

        this.setOriginalDetailsList(this.getSeriesMetaParameterList());

    }

    private void addImagesBasedOnReferences(StagedDicomSeries stagedDicomSeries, String referencedDicomSeriesUid) {
        String referencedStageTwoDicomSeriesUid;
        String referencedStageOneDicomSeriesUid;
        String referencedClinicalDicomSeriesUid;
        if (stagedDicomSeries.getClinicalSeriesUid().isEmpty()) {
            referencedClinicalDicomSeriesUid = "";
            referencedStageOneDicomSeriesUid = referencedDicomSeriesUid;
            if (referencedDicomSeriesUid.equals("null")) {
                referencedStageTwoDicomSeriesUid = "null";
            } else {
                referencedStageTwoDicomSeriesUid = DicomUidReGeneratorUtil.generateStageTwoUid(
                        stagedDicomSeries.getUidPrefix(), stagedDicomSeries.getPartnerSideCode(), stagedDicomSeries.getEdcCode(), referencedDicomSeriesUid
                );
            }
        } else {
            referencedClinicalDicomSeriesUid = referencedDicomSeriesUid;
            if (referencedDicomSeriesUid.equals("null")) {
                referencedStageOneDicomSeriesUid = "null";
                referencedStageTwoDicomSeriesUid = "null";
            } else {
                referencedStageOneDicomSeriesUid = DicomUidReGeneratorUtil.generateStageOneUid(stagedDicomSeries.getUidPrefix(), referencedDicomSeriesUid);
                referencedStageTwoDicomSeriesUid = DicomUidReGeneratorUtil.generateStageTwoUid(
                        stagedDicomSeries.getUidPrefix(), stagedDicomSeries.getPartnerSideCode(), stagedDicomSeries.getEdcCode(), referencedStageOneDicomSeriesUid
                );
            }
        }

        if (!referencedClinicalDicomSeriesUid.isEmpty()) {
            List<DicomImage> clinicalDicomImages = stagedDicomSeries.getClinicalDicomImagesByReference().get(referencedClinicalDicomSeriesUid);
            if (clinicalDicomImages != null) {
                this.setClinicalDicomImages(clinicalDicomImages);
                this.clinicalDicomImagesByReference = this.createDicomImagesByReferenceMap(clinicalDicomImages);
            }
        }

        if (!referencedStageOneDicomSeriesUid.isEmpty()) {
            List<DicomImage> stageOneDicomImages = stagedDicomSeries.getStageOneDicomImagesByReference().get(referencedStageOneDicomSeriesUid);
            if (stageOneDicomImages != null) {
                this.setStageOneDicomImages(stageOneDicomImages);
                this.stageOneDicomImagesByReference = this.createDicomImagesByReferenceMap(stageOneDicomImages);
            }
        }

        if (!referencedStageTwoDicomSeriesUid.isEmpty()) {
            List<DicomImage> stageTwoDicomImages = stagedDicomSeries.getStageTwoDicomImagesByReference().get(referencedStageTwoDicomSeriesUid);
            if (stageTwoDicomImages != null) {
                this.setStageTwoDicomImages(stageTwoDicomImages);
                this.stageTwoDicomImagesByReference = this.createDicomImagesByReferenceMap(stageTwoDicomImages);
            }
        }
    }

    /**
     * Sets an internal boolean in order to facilitate that a series can be moved complete if it is possible to improve
     * the performance compared with moving image by image
     *
     * @param stagedDicomSeries StagedDicomSeries original series where the virtual series comes from
     */
    private void checkIfSeriesIsOriginal(StagedDicomSeries stagedDicomSeries) {
        if (stagedDicomSeries instanceof StagedDicomSeriesVirtualSeries) {
            // check if original was already split
            this.isOriginal = ((StagedDicomSeriesVirtualSeries) stagedDicomSeries).isOriginal();
        }

        if (this.isOriginal) {
            // compare image count
            this.isOriginal = stagedDicomSeries.getAvailableDicomImages().size() == this.getAvailableDicomImages().size() &&
                    stagedDicomSeries.getStagedDicomImages().size() == this.getStagedDicomImages().size();

        }
    }

    /**
     * Creates a StagedDicomSeriesVirtualSeries, based on the StagedDicomSeries with a list of images that will
     * be copied from the StagedDicomSeries.
     * <p>
     * DICOM series can consist of different DICOM images. Every image can reference another DICOM series by UID.
     * If a StagedDicomSeries consist of images that will be referenced by different other series,
     * we split for tree view so that the user can select every branch individually. The remaining unreferenced
     * Dicom images will be bundled into a StagedDicomSeriesVirtualSeries.
     *
     * @param stagedDicomSeries StagedDicomSeries instance
     * @param imageList         List<DicomImage> that we be used to find an copy all
     *                          Dicom images in all stages that belong to the virtual series
     */
    public StagedDicomSeriesVirtualSeries(StagedDicomSeries stagedDicomSeries, List<DicomImage> imageList) {
        super(stagedDicomSeries.getUidPrefix(), stagedDicomSeries.getPartnerSideCode(), stagedDicomSeries.getEdcCode());

        this.copyPropertiesFromStagedDicomSeries(stagedDicomSeries);
        this.addImagesBasedOnImageList(stagedDicomSeries, imageList);
        this.checkIfSeriesIsOriginal(stagedDicomSeries);

        // re-create properties that are based on information from the images
        if (this.getSeriesModality().equals(DICOM_RTSTRUCT)) {
            createUpdatedRegionOfInterestList();
        } else {
            this.setRegionOfInterestList(stagedDicomSeries.getRegionsOfInterestList());
        }

        this.setOriginalDetailsList(this.getSeriesMetaParameterList());

    }

    public boolean isOriginal() {
        return isOriginal;
    }

    private void copyPropertiesFromStagedDicomSeries(StagedDicomSeries stagedDicomSeries) {
        this.setStageOneRepresentation(stagedDicomSeries.isStageOneRepresentation());
        this.setStageTwoRepresentation(stagedDicomSeries.isStageTwoRepresentation());

        this.setClinicalSeriesUid(stagedDicomSeries.getClinicalSeriesUid());
        this.setStageOneSeriesUid(stagedDicomSeries.getStageOneSeriesUid());
        this.setStageTwoSeriesUid(stagedDicomSeries.getStageTwoSeriesUid());

        this.setId(stagedDicomSeries.getId());
        this.setSeriesInstanceUID(stagedDicomSeries.getSeriesInstanceUID());
        this.setFrameOfReferenceUid(stagedDicomSeries.getFrameOfReferenceUid());
        this.setSeriesDescription(stagedDicomSeries.getSeriesDescription());
        this.setSeriesNumber(stagedDicomSeries.getSeriesNumber());
        this.setSeriesModality(stagedDicomSeries.getSeriesModality());
        this.setSeriesTime(stagedDicomSeries.getSeriesTime());
        this.setSeriesDate(stagedDicomSeries.getSeriesDate());
        this.setSeriesNumber(stagedDicomSeries.getSeriesNumber());
        this.setToolTipValue(stagedDicomSeries.getToolTipValue());
    }

    private void addImagesBasedOnImageList(StagedDicomSeries stagedDicomSeries, List<DicomImage> imageList) {
        for (DicomImage image : imageList) {
            String clinicalUid;
            String stageOneUid;
            String stageTwoUid;

            if (isClinicalSystemDataContext(stagedDicomSeries)) {
                clinicalUid = image.getSopInstanceUID();
                stageOneUid = DicomUidReGeneratorUtil.generateStageOneUid(
                        stagedDicomSeries.getUidPrefix(),
                        image.getSopInstanceUID()
                );
                stageTwoUid = DicomUidReGeneratorUtil.generateStageTwoUid(
                        stagedDicomSeries.getUidPrefix(),
                        stagedDicomSeries.getPartnerSideCode(),
                        stagedDicomSeries.getEdcCode(),
                        image.getSopInstanceUID()
                );
            } else {
                clinicalUid = "";
                stageOneUid = image.getSopInstanceUID();
                stageTwoUid = DicomUidReGeneratorUtil.generateStageTwoUid(
                        stagedDicomSeries.getUidPrefix(),
                        stagedDicomSeries.getPartnerSideCode(),
                        stagedDicomSeries.getEdcCode(),
                        image.getSopInstanceUID()
                );
            }

            if (!clinicalUid.isEmpty()) {
                for (DicomImage clinicalImage : stagedDicomSeries.getClinicalDicomImages()) {
                    if (clinicalUid.equals(clinicalImage.getSopInstanceUID())) {
                        this.clinicalDicomImages.add(clinicalImage);
                        this.clinicalDicomImagesByReference = this.createDicomImagesByReferenceMap(clinicalDicomImages);
                    }
                }
            }

            if (!stageOneUid.isEmpty()) {
                for (DicomImage stageOneImage : stagedDicomSeries.getStageOneDicomImages()) {
                    if (stageOneUid.equals(stageOneImage.getSopInstanceUID())) {
                        this.stageOneDicomImages.add(stageOneImage);
                        this.stageOneDicomImagesByReference = this.createDicomImagesByReferenceMap(stageOneDicomImages);
                    }
                }
            }

            if (!stageTwoUid.isEmpty()) {
                for (DicomImage stageTwoImage : stagedDicomSeries.getStageTwoDicomImages()) {
                    if (stageTwoUid.equals(stageTwoImage.getSopInstanceUID())) {
                        this.stageTwoDicomImages.add(stageTwoImage);
                        this.stageTwoDicomImagesByReference = this.createDicomImagesByReferenceMap(stageTwoDicomImages);
                    }
                }
            }


        }
    }

    private void createUpdatedRegionOfInterestList() {
        List<String> roiNameList = new ArrayList<>();
        if (this.getAvailableDicomImages().size() > 0) {
            for (DicomImage image : this.getAvailableDicomImages()) {
                DicomImageRtStruct dicomImageRtStruct = (DicomImageRtStruct) image;
                roiNameList.addAll(dicomImageRtStruct.getRoiNameList());
            }
        }
        this.setRegionOfInterestList(roiNameList);
    }

}
