package de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilderTests;

import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_CT;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTDOSE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTIMAGE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTPLAN;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;

public class DicomStudyVirtualNodeTreeBuilderTestHelper {

    public static StagedDicomSeries getRtStructStagedDicomSeries(String name) {
        StagedDicomSeries series = new StagedDicomSeries();
        series.setSeriesModality(DICOM_RTSTRUCT);
        series.setSeriesDescription(DICOM_RTSTRUCT + "-" + name);
        series.setStageOneSeriesUid(DICOM_RTSTRUCT + "-" + name + "-1" + "-id");
        series.setStageTwoSeriesUid(DICOM_RTSTRUCT + "-" + name + "-2" + "-id");
        return series;
    }

    public static StagedDicomSeries getCtStagedDicomSeries(String name) {
        StagedDicomSeries series = new StagedDicomSeries();
        series.setSeriesModality(DICOM_CT);
        series.setSeriesDescription(DICOM_CT + "-" + name);
        series.setStageOneSeriesUid(DICOM_CT + "-" + name + "-1" + "-id");
        series.setStageTwoSeriesUid(DICOM_CT + "-" + name + "-2" + "-id");
        return series;
    }

    public static StagedDicomSeries getRtPlanStagedDicomSeries(String name) {
        StagedDicomSeries series = new StagedDicomSeries();
        series.setSeriesModality(DICOM_RTPLAN);
        series.setSeriesDescription(DICOM_RTPLAN + "-" + name);
        series.setStageOneSeriesUid(DICOM_RTPLAN + "-" + name + "-1" + "-id");
        series.setStageTwoSeriesUid(DICOM_RTPLAN + "-" + name + "-2" + "-id");
        return series;
    }

    public static StagedDicomSeries getRtImagesStagedDicomSeries(String name) {
        StagedDicomSeries series = new StagedDicomSeries();
        series.setSeriesModality(DICOM_RTIMAGE);
        series.setSeriesDescription(DICOM_RTIMAGE + "-" + name);
        series.setStageOneSeriesUid(DICOM_RTIMAGE + "-" + name + "-1" + "-id");
        series.setStageTwoSeriesUid(DICOM_RTIMAGE + "-" + name + "-2" + "-id");
        return series;
    }

    public static StagedDicomSeries getRtDoseStagedDicomSeries(String name) {
        StagedDicomSeries series = new StagedDicomSeries();
        series.setSeriesModality(DICOM_RTDOSE);
        series.setSeriesDescription(DICOM_RTDOSE + "-" + name);
        series.setStageOneSeriesUid(DICOM_RTDOSE + "-" + name + "-1" + "-id");
        series.setStageTwoSeriesUid(DICOM_RTDOSE + "-" + name + "-2" + "-id");
        return series;
    }
}
