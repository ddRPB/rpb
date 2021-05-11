/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;

import java.util.ArrayList;
import java.util.List;

public class DicomSeriesToStagedDicomSeriesConverter {


    public static List<StagedDicomSeries> getStagedDicomSeries(List<DicomSeries> stageOneSeries, List<DicomSeries> stageTwoSeries, String uidPrefix, String partnerSideCode, String edcCode) {
        List<StagedDicomSeries> stagedDicomSeries = new ArrayList<>();
        for (DicomSeries series : stageOneSeries) {
            StagedDicomSeries stagedSeries = new StagedDicomSeries();
            movePropertiesFromDicomSerieToStagedDicomSerie(series, stagedSeries);
            stagedDicomSeries.add(stagedSeries);


            if (stageTwoSeries != null) {
                String stageOneUid = series.getSeriesInstanceUID();
                String stageTwoUid = DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, stageOneUid);
                DicomSeries stageTwoRepresentation = getSeriesByUid(stageTwoSeries, stageTwoUid);
                if (stageTwoRepresentation != null) {
                    stagedSeries.setStageTwoRepresentation(true);
                }
            }
        }
        return stagedDicomSeries;
    }

    private static void movePropertiesFromDicomSerieToStagedDicomSerie(DicomSeries series, StagedDicomSeries stagedSeries) {
        if (series.getSeriesInstanceUID() != null) {
            stagedSeries.setSeriesInstanceUID(series.getSeriesInstanceUID());
        }
        if (series.getSeriesModality() != null) {
            stagedSeries.setSeriesModality(series.getSeriesModality());
        }
        if (series.getSeriesTime() != null) {
            stagedSeries.setSeriesTime(series.getSeriesTime());
        }
        if (series.getSeriesDescription() != null) {
            stagedSeries.setSeriesDescription(series.getUserViewSeriesDescription());
        }
    }

    private static DicomSeries getSeriesByUid(List<DicomSeries> seriesList, String uid) {
        for (DicomSeries series : seriesList) {
            if (series.getSeriesInstanceUID().equals(uid)) {
                return series;
            }
        }
        return null;
    }
}