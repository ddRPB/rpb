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

package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.util.DicomUidReGeneratorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts DicomSeries representations in different stages to StagedDicomSeries instances
 *
 * @see StagedDicomSeries
 */
public class DicomSeriesToStagedDicomSeriesConverter {

    /**
     * Creates a list of StagedDicomSeries out of two lists with DicomSeries where one list represents the original
     * study in "Pseudonymised Stage" (stageOneSeriesList) and the second list consists of modified copies in
     * "Project Stage" (stageTwoSeriesList) for the specific study context.
     *
     * @param stageOneSeriesList List of DicomStudies in "Pseudonymised Stage" (study zero, stage one)
     * @param stageTwoSeriesList List of DicomStudies in "Project Stage" (stage two)
     * @param uidPrefix          Prefix for the Dicom uid of the Project Stage series
     * @param partnerSideCode    RPB specific code of the partner side which is needed to create uid of stage two
     * @param edcCode            RPB specific code of the study in Project Stage
     * @return list of StagedDicomStudies
     */
    public static List<StagedDicomSeries> getStagedDicomSeries(
            List<DicomSeries> stageOneSeriesList,
            List<DicomSeries> stageTwoSeriesList,
            String uidPrefix,
            String partnerSideCode,
            String edcCode
    ) {

        List<StagedDicomSeries> stagedDicomSeriesList = new ArrayList<>();

        for (DicomSeries series : stageOneSeriesList) {

            StagedDicomSeries stagedSeries = new StagedDicomSeries(uidPrefix, partnerSideCode, edcCode);
            stagedSeries.setStageOneRepresentation(true);
            movePropertiesFromDicomSeriesToStagedDicomSeries(series, stagedSeries);
            String stageOneUid = series.getSeriesInstanceUID();
            stagedSeries.setStageOneSeriesUid(stageOneUid);

            String stageTwoUid = DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, stageOneUid);
            setStageTwoInformation(stageTwoSeriesList, stagedSeries, stageTwoUid);

            stagedDicomSeriesList.add(stagedSeries);
        }

        return stagedDicomSeriesList;
    }

    private static void movePropertiesFromDicomSeriesToStagedDicomSeries(
            DicomSeries series,
            StagedDicomSeries stagedSeries
    ) {

        if (series.getSeriesInstanceUID() != null) {
            stagedSeries.setSeriesInstanceUID(series.getSeriesInstanceUID());
        }

        if(series.getSeriesNumber() != null){
            stagedSeries.setSeriesNumber(series.getSeriesNumber());
        }

        if (series.getSeriesModality() != null) {
            stagedSeries.setSeriesModality(series.getSeriesModality());
        }

        if (series.getSeriesTime() != null) {
            stagedSeries.setSeriesTime(series.getSeriesTime());
        }

        if (series.getSeriesDate() != null) {
            stagedSeries.setSeriesDate(series.getSeriesDate());
        }

        if (series.getSeriesDescription() != null) {
            stagedSeries.setSeriesDescription(series.getUserViewSeriesDescription());
        }

        if (series.getSeriesMetaParameterString() != null) {
            stagedSeries.setToolTipValue(series.getSeriesMetaParameterString());
        }

        if (series.getSeriesMetaParameterList() != null) {
            stagedSeries.setOriginalDetailsList(series.getSeriesMetaParameterList());
        }
    }

    private static void setStageTwoInformation(
            List<DicomSeries> stageTwoSeriesList,
            StagedDicomSeries stagedSeries,
            String stageTwoUid
    ) {
        DicomSeries stageTwoRepresentation = getSeriesByUid(stageTwoSeriesList, stageTwoUid);

        if (stageTwoRepresentation != null) {
            stagedSeries.setStageTwoRepresentation(true);
            stagedSeries.setStageTwoSeriesUid(stageTwoUid);
        }
    }

    private static DicomSeries getSeriesByUid(List<DicomSeries> seriesList, String uid) {
        if (seriesList != null && seriesList.size() > 0) {
            for (DicomSeries series : seriesList) {
                if (series.getSeriesInstanceUID().equals(uid)) {
                    return series;
                }
            }
        }
        return null;
    }

    /**
     * Creates a list of StagedDicomSeries out of two lists with DicomSeries where one list represents the original
     * clinical study (clinicalSeriesList) and the second list consists of copies in "Pseudonymised Stage"
     * (stageTwoSeries) for the specific study context.
     *
     * @param clinicalSeriesList List of the original clinical DicomStudies
     * @param stageOneSeriesList List of DicomStudies in "Pseudonymised Stage" (study zero, stage one)
     * @param uidPrefix          Prefix for the Dicom uid of the Project Stage series
     * @return list of StagedDicomStudies
     */
    public static List<StagedDicomSeries> getStagedDicomSeries(
            List<DicomSeries> clinicalSeriesList,
            List<DicomSeries> stageOneSeriesList,
            String uidPrefix
    ) {
        List<StagedDicomSeries> stagedDicomSeries = new ArrayList<>();

        for (DicomSeries clinicalSeries : clinicalSeriesList) {
            StagedDicomSeries stagedSeries = new StagedDicomSeries(uidPrefix, "", "");
            movePropertiesFromDicomSeriesToStagedDicomSeries(clinicalSeries, stagedSeries);
            stagedDicomSeries.add(stagedSeries);

            String clinicalSeriesUid = clinicalSeries.getSeriesInstanceUID();
            stagedSeries.setClinicalSeriesUid(clinicalSeriesUid);

            String stageOneSeriesUid = DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, clinicalSeriesUid);
            setStageOneInformation(stageOneSeriesList, stagedSeries, stageOneSeriesUid);
        }

        return stagedDicomSeries;
    }

    private static void setStageOneInformation(
            List<DicomSeries> stageOneSeriesList,
            StagedDicomSeries stagedSeries,
            String stageOneSeriesUid
    ) {
        DicomSeries stageOneRepresentation = getSeriesByUid(stageOneSeriesList, stageOneSeriesUid);

        if (stageOneRepresentation != null) {
            stagedSeries.setStageOneRepresentation(true);
            stagedSeries.setStageOneSeriesUid(stageOneRepresentation.getSeriesInstanceUID());
        }
    }
}