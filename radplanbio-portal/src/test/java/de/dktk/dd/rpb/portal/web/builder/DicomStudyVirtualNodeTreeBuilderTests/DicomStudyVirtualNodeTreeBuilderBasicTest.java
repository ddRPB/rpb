package de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilderTests;

import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilder;
import org.junit.Test;
import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_CT;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTIMAGE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTPLAN;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Basic tests for the DicomStudyVirtualNodeTreeBuilder
 *
 * The DICOM series do not include the DICOM images in the first phase to speed up loading the data.
 * This test class covers the scenarios that occur during that phase.
 */
public class DicomStudyVirtualNodeTreeBuilderBasicTest {

    @Test
    public void returns_empty_tree() {
        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder();

        TreeNode node = builder.build();

        assertNotNull(node);
        assertEquals(0, node.getChildren().size());
    }

    // region build without images loaded - result is always a tree with all nodes directly connected to the root

    @Test
    public void build_without_images_returns_tree_with_ct_node_and_rt_struct() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one"));
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);

        TreeNode node = builder.build();

        assertEquals(2, node.getChildren().size());

        StagedDicomSeries series_zero = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_CT, series_zero.getSeriesModality());

        StagedDicomSeries series_two = (StagedDicomSeries) node.getChildren().get(1).getData();
        assertEquals(DICOM_RTSTRUCT, series_two.getSeriesModality());
    }

    @Test
    public void build_without_images_returns_tree_with_ct_node_and_rt_plan_node_and_rt_plan_node() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one"));
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one"));
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);

        TreeNode node = builder.build();

        assertEquals(3, node.getChildren().size());

        StagedDicomSeries series_one = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_CT, series_one.getSeriesModality());

        StagedDicomSeries series_two = (StagedDicomSeries) node.getChildren().get(1).getData();
        assertEquals(DICOM_RTPLAN, series_two.getSeriesModality());

        StagedDicomSeries series_three = (StagedDicomSeries) node.getChildren().get(2).getData();
        assertEquals(DICOM_RTIMAGE, series_three.getSeriesModality());
    }

    @Test
    public void build_without_images_returns_tree_with_rt_image_node() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);

        TreeNode node = builder.build();

        assertEquals(1, node.getChildren().size());

        StagedDicomSeries series = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_RTIMAGE, series.getSeriesModality());
    }

    // endregion

}