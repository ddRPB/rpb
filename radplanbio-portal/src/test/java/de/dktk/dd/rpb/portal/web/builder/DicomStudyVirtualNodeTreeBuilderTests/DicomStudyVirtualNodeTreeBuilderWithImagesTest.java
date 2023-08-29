package de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilderTests;

import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtDose;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtPlan;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtStruct;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeriesVirtualSeries;
import de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilder;
import org.junit.Test;
import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_CT;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTDOSE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTIMAGE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTPLAN;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests with loaded DICOM images for the DicomStudyVirtualNodeTreeBuilder
 * <p>
 * The DICOM series do not include the DICOM images in the first phase to speed up loading the data.
 * DICOM images are loaded for RT related DICOM series if the user is activating the tree view in the UI.
 * This test class covers the scenarios that occur during that phase.
 */
public class DicomStudyVirtualNodeTreeBuilderWithImagesTest {


    // region hasDicomSeriesDetailData method

    @Test
    public void has_dicom_series_details_returns_false() {
        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder();

        assertFalse(builder.hasDicomSeriesDetailData());
    }

    @Test
    public void has_dicom_series_details_returns_true() {
        // add a DICOM series with images
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        StagedDicomSeries seriesWithImages = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");
        DicomImage image = new DicomImage();
        List<DicomImage> imageList = new ArrayList<>();
        imageList.add(image);
        seriesWithImages.setStageOneDicomImages(imageList);
        seriesList.add(seriesWithImages);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);

        assertTrue(builder.hasDicomSeriesDetailData());
    }

    // endregion

    // region build images with images

    @Test
    public void build_returns_empty_tree() {
        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder();

        TreeNode node = builder.build();

        assertNotNull(node);
        assertEquals(0, node.getChildren().size());
    }

    @Test
    public void build_returns_tree_with_ct_node() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        StagedDicomSeries series = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_CT, series.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_rt_plan_node() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        StagedDicomSeries series = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_RTPLAN, series.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_rt_images_node() {
        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one"));

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        StagedDicomSeries rtImagesSeries = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_RTIMAGE, rtImagesSeries.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_ct_node_and_rt_struct_node_as_child() {
        // RTStruct series with an DICOM image that references a CT Series
        StagedDicomSeries rtStructStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");
        String rtStructSopInstanceUid = "RtStructSopInstanceUid";
        DicomImageRtStruct dicomImageRtStruct = new DicomImageRtStruct(
                "dummyLabel",
                "dummyFrameOfReferenceId",
                DICOM_CT + "-one-1-id");
        dicomImageRtStruct.setSopInstanceUID(rtStructSopInstanceUid);
        List<DicomImage> rtStructDicomImageList = new ArrayList();
        rtStructDicomImageList.add(dicomImageRtStruct);
        rtStructStagedDicomSeries.setStageOneDicomImages(rtStructDicomImageList);

        // CT series that is referenced
        StagedDicomSeries ctStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one");

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtStructStagedDicomSeries);
        seriesList.add(ctStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        // first child node of root is the CT
        StagedDicomSeries rtSeries = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_CT, rtSeries.getSeriesModality());
        assertEquals(1, node.getChildren().get(0).getChildren().size());

        // first child node of the CT is the RTStruct
        StagedDicomSeries ctSeries = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(0).getData();
        assertEquals(DICOM_RTSTRUCT, ctSeries.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_ct_node_and_rt_struct_node_if_the_reference_does_not_match() {
        // RTStruct series with an DICOM image that references a CT Series
        StagedDicomSeries rtStructStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");
        String rtStructSopInstanceUid = "RtStructSopInstanceUid";
        DicomImageRtStruct dicomImageRtStruct = new DicomImageRtStruct(
                "dummyLabel",
                "dummyFrameOfReferenceId",
                DICOM_CT + "-one-2-id");
        dicomImageRtStruct.setSopInstanceUID(rtStructSopInstanceUid);
        List<DicomImage> rtStructDicomImageList = new ArrayList();
        rtStructDicomImageList.add(dicomImageRtStruct);
        rtStructStagedDicomSeries.setStageOneDicomImages(rtStructDicomImageList);

        // CT series that is referenced
        StagedDicomSeries ctStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one");

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtStructStagedDicomSeries);
        seriesList.add(ctStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        // root has two children
        assertEquals(2, node.getChildren().size());

        // first child node is the RTStruct
        StagedDicomSeries ctSeries = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_RTSTRUCT, ctSeries.getSeriesModality());

        // the other child node of root is the CT
        StagedDicomSeries rtSeries = (StagedDicomSeries) node.getChildren().get(1).getData();
        assertEquals(DICOM_CT, rtSeries.getSeriesModality());

    }

    @Test
    public void build_returns_tree_with_ct_node_and_two_rt_struct_nodes_as_children() {
        // RTStruct series with image that refers to the CT series
        StagedDicomSeries rtStructStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");
        String rtStructSopInstanceUid = "RtStructSopInstanceUid";
        DicomImageRtStruct dicomImageRtStruct = new DicomImageRtStruct(
                "dummyLabel",
                "dummyFrameOfReferenceId",
                DICOM_CT + "-one-1-id"
        );
        dicomImageRtStruct.setSopInstanceUID(rtStructSopInstanceUid);
        List<DicomImage> rtStructDicomImageList = new ArrayList();
        rtStructDicomImageList.add(dicomImageRtStruct);
        rtStructStagedDicomSeries.setStageOneDicomImages(rtStructDicomImageList);

        // Another RTStruct series with image that refers to the same CT series
        StagedDicomSeries rtStructStagedDicomSeriesTwo = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("two");
        String rtStructSopInstanceUidTwo = "RtStructSopInstanceUidTwo";
        DicomImageRtStruct dicomImageRtStructTwo = new DicomImageRtStruct(
                "dummyLabelTwo",
                "dummyFrameOfReferenceId",
                DICOM_CT + "-one-1-id"
        );
        dicomImageRtStructTwo.setSopInstanceUID(rtStructSopInstanceUidTwo);
        List<DicomImage> rtStructDicomImageListTwo = new ArrayList();
        rtStructDicomImageListTwo.add(dicomImageRtStructTwo);
        rtStructStagedDicomSeriesTwo.setStageOneDicomImages(rtStructDicomImageListTwo);

        // A CT series that is referenced from the images that belong to the RTStructs
        StagedDicomSeries ctStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getCtStagedDicomSeries("one");

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtStructStagedDicomSeries);
        seriesList.add(rtStructStagedDicomSeriesTwo);
        seriesList.add(ctStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        // first child node of the root is the CT node and has two children
        StagedDicomSeries rtSeries = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_CT, rtSeries.getSeriesModality());
        assertEquals(2, node.getChildren().get(0).getChildren().size());

        // first child node after the CT node is a RTStruct
        StagedDicomSeries ctSeries = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(0).getData();
        assertEquals(DICOM_RTSTRUCT, ctSeries.getSeriesModality());

        // second child node after the CT node is a RTStruct
        StagedDicomSeries ctSeriesTwo = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(1).getData();
        assertEquals(DICOM_RTSTRUCT, ctSeriesTwo.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_rt_struct_node_and_plan_as_child_node() {
        // RTStruct series
        StagedDicomSeries rtStructStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");

        // RTPlan series with an image that references the RTStruct
        StagedDicomSeries rtPlanStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one");
        DicomImageRtPlan dicomImageRtPlan = new DicomImageRtPlan(
                "",
                "",
                "",
                "",
                DICOM_RTSTRUCT + "-one-1-id");
        dicomImageRtPlan.setSopInstanceUID("rtPlanDicomImageUid");
        List<DicomImage> rtPlanDicomImageList = new ArrayList();
        rtPlanDicomImageList.add(dicomImageRtPlan);
        rtPlanStagedDicomSeries.setStageOneDicomImages(rtPlanDicomImageList);

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtStructStagedDicomSeries);
        seriesList.add(rtPlanStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        // first child node of the root os the RTStruct
        StagedDicomSeries rtSeries = (StagedDicomSeries) node.getChildren().get(0).getData();
        assertEquals(DICOM_RTSTRUCT, rtSeries.getSeriesModality());

        // child node of the RTStruct is the RTPlan
        assertEquals(1, node.getChildren().get(0).getChildren().size());
        StagedDicomSeries ctSeries = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(0).getData();
        assertEquals(DICOM_RTPLAN, ctSeries.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_an_rt_struct_that_has_rt_plan_node_and_rt_image_dose_as_child_nodes() {
        // RTPlan with an image that references an RTStruct
        StagedDicomSeries rtPlanStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one");

        // RTImage with an image that references the RTPlan
        StagedDicomSeries rtImagesStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one");
        DicomImageRtImage dicomImageRtImage = new DicomImageRtImage(
                "RtImage-one",
                "",
                "",
                "",
                "",
                "",
                DICOM_RTPLAN + "-one-1-id"
        );
        List<DicomImage> rtImageDicomList = new ArrayList();
        rtImageDicomList.add(dicomImageRtImage);
        rtImagesStagedDicomSeries.setStageOneDicomImages(rtImageDicomList);

        //  RTDose that references the RTPlan
        StagedDicomSeries rtDoseStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtDoseStagedDicomSeries("one");
        DicomImageRtDose dicomImageRtDose = new DicomImageRtDose(
                "",
                "",
                "",
                "",
                "",
                "",
                DICOM_RTPLAN + "-one-1-id"
        );
        List<DicomImage> rtDoseDicomList = new ArrayList();
        rtDoseDicomList.add(dicomImageRtDose);
        rtDoseStagedDicomSeries.setStageOneDicomImages(rtDoseDicomList);

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtPlanStagedDicomSeries);
        seriesList.add(rtImagesStagedDicomSeries);
        seriesList.add(rtDoseStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);

        TreeNode node = builder.build();
        StagedDicomSeries rtSeries = (StagedDicomSeries) node.getChildren().get(0).getData();

        assertEquals(DICOM_RTPLAN, rtSeries.getSeriesModality());

        assertEquals(2, node.getChildren().get(0).getChildren().size());

        StagedDicomSeries ctSeries = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(0).getData();
        assertEquals(DICOM_RTIMAGE, ctSeries.getSeriesModality());

        StagedDicomSeries doseSeries = (StagedDicomSeries) node.getChildren().get(0).getChildren().get(1).getData();
        assertEquals(DICOM_RTDOSE, doseSeries.getSeriesModality());
    }

    @Test
    public void build_returns_tree_with_rt_struct_node_and_plan_nodes_if_no_reference_exists() {
        // RTStruct series
        StagedDicomSeries rtStructStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtStructStagedDicomSeries("one");

        // RTPlan series with DICOM image that refers to not existing series
        StagedDicomSeries rtPlanStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one");
        DicomImageRtPlan dicomImageRtPlan = new DicomImageRtPlan(
                "",
                "",
                "",
                "",
                DICOM_RTSTRUCT + "does-not-exist"
        );
        dicomImageRtPlan.setSopInstanceUID("rtPlanDicomImageUid");
        List<DicomImage> rtPlanDicomImageList = new ArrayList();
        rtPlanDicomImageList.add(dicomImageRtPlan);
        rtPlanStagedDicomSeries.setStageOneDicomImages(rtPlanDicomImageList);

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtStructStagedDicomSeries);
        seriesList.add(rtPlanStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode node = builder.build();

        // root has two direct children
        assertEquals(2, node.getChildren().size());


        // check by modalities that both series are part of the array
        List<String> modalities = new ArrayList<>();
        for (TreeNode seriesNode : node.getChildren()) {
            modalities.add(((StagedDicomSeries) seriesNode.getData()).getSeriesModality());
        }

        assertTrue(modalities.contains(DICOM_RTSTRUCT));
        assertTrue(modalities.contains(DICOM_RTPLAN));
    }

    // endregion

    // region build with images that refer to different series

    @Test
    public void build_returns_tree_with_two_virtual_rt_plan_node_and_image_dose_as_child_nodes() {
        // First referenced RTPlan
        StagedDicomSeries rtPlanStagedDicomSeriesOne = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one");
        // Second referenced RTPlan
        StagedDicomSeries rtPlanStagedDicomSeriesTwo = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("two");

        // RTImage that consists of DICOM images where some reference the first RTPlan and others the second RTPlan
        StagedDicomSeries rtImagesStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one");
        DicomImageRtImage dicomImageRtImageOne = new DicomImageRtImage("RtImage-one","",
                "",
                "",
                "",
                "",
                DICOM_RTPLAN + "-one-1-id"
        );
        DicomImageRtImage dicomImageRtImageTwo = new DicomImageRtImage("RtImage-two","",
                "",
                "",
                "",
                "",
                DICOM_RTPLAN + "-two-1-id"
        );
        List<DicomImage> rtImageDicomList = new ArrayList();
        rtImageDicomList.add(dicomImageRtImageOne);
        rtImageDicomList.add(dicomImageRtImageTwo);
        rtImagesStagedDicomSeries.setStageOneDicomImages(rtImageDicomList);

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtPlanStagedDicomSeriesOne);
        seriesList.add(rtPlanStagedDicomSeriesTwo);
        seriesList.add(rtImagesStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode rootNode = builder.build();

        // Both RTPlan Series are children of root
        StagedDicomSeries rtSeriesOne = (StagedDicomSeries) rootNode.getChildren().get(0).getData();
        StagedDicomSeries rtSeriesTwo = (StagedDicomSeries) rootNode.getChildren().get(1).getData();

        assertEquals(DICOM_RTPLAN, rtSeriesOne.getSeriesModality());
        assertEquals(DICOM_RTPLAN, rtSeriesTwo.getSeriesModality());

        // Both RTPlan series have one DICOM image as child
        assertEquals(1, rootNode.getChildren().get(0).getChildren().size());
        assertEquals(1, rootNode.getChildren().get(1).getChildren().size());

        StagedDicomSeriesVirtualSeries virtualImageSeriesOne = (StagedDicomSeriesVirtualSeries) rootNode.getChildren().get(1).getChildren().get(0).getData();
        assertEquals(DICOM_RTIMAGE, virtualImageSeriesOne.getSeriesModality());

        StagedDicomSeriesVirtualSeries virtualImageSeriesTwo = (StagedDicomSeriesVirtualSeries) rootNode.getChildren().get(1).getChildren().get(0).getData();
        assertEquals(DICOM_RTIMAGE, virtualImageSeriesTwo.getSeriesModality());
    }

    @Test
    public void build_returns_tree_virtual_node_that_has_unreferencing_image_and_rtplan_with_virtual_node_and_referenced_image() {
        // RTPlan series
        StagedDicomSeries rtPlanStagedDicomSeriesOne = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtPlanStagedDicomSeries("one");

        // RTImage with DICOM images where some reference the RTPlan and others do not have a reference
        StagedDicomSeries rtImagesStagedDicomSeries = DicomStudyVirtualNodeTreeBuilderTestHelper.getRtImagesStagedDicomSeries("one");
        DicomImageRtImage dicomImageRtImageOne = new DicomImageRtImage(
                "RtImage-one",
                "",
                "",
                "",
                "",
                "",
                DICOM_RTPLAN + "-one-1-id"
        );
        dicomImageRtImageOne.setSopInstanceUID("DICOMIMAGESOP01");
        DicomImageRtImage dicomImageRtImageTwo = new DicomImageRtImage(
                "RtImage-two",
                "",
                "",
                "",
                "",
                "",
                null
        );
        dicomImageRtImageTwo.setSopInstanceUID("DICOMIMAGESOP02");
        List<DicomImage> rtImageDicomList = new ArrayList();
        rtImageDicomList.add(dicomImageRtImageOne);
        rtImageDicomList.add(dicomImageRtImageTwo);
        rtImagesStagedDicomSeries.setStageOneDicomImages(rtImageDicomList);
        rtImagesStagedDicomSeries.setStageOneDicomImages(rtImageDicomList);

        List<StagedDicomSeries> seriesList = new ArrayList<>();
        seriesList.add(rtPlanStagedDicomSeriesOne);
        seriesList.add(rtImagesStagedDicomSeries);

        DicomStudyVirtualNodeTreeBuilder builder = new DicomStudyVirtualNodeTreeBuilder(seriesList);
        TreeNode rootNode = builder.build();

        // The root node has two children - one is the RTPlan and the other the "virtual" DICOM RTImage with the DICOM images that do not have a reference
        assertEquals(2, rootNode.getChildren().size());
        StagedDicomSeries rootSeriesOne = (StagedDicomSeries) rootNode.getChildren().get(0).getData();
        StagedDicomSeries rootSeriesTwo = (StagedDicomSeries) rootNode.getChildren().get(1).getData();

        assertTrue(rootSeriesOne instanceof StagedDicomSeriesVirtualSeries || rootSeriesTwo instanceof StagedDicomSeriesVirtualSeries);
        assertTrue(rootSeriesOne.getSeriesModality().equals(DICOM_RTPLAN) || rootSeriesTwo.getSeriesModality().equals(DICOM_RTPLAN));

        // The RTPlan has another "virtual" DICOM RTImage series
        if (rootSeriesOne.getSeriesModality().equals(DICOM_RTPLAN)) {
            assertEquals(1, rootNode.getChildren().get(0).getChildren().size());
            assertEquals(
                    DICOM_RTPLAN + "-one-1-id",
                    ((StagedDicomSeriesVirtualSeries) rootNode.getChildren().get(0).getChildren().get(0).getData()).getAvailableDicomImages().get(0).getReferencedDicomSeriesUID()
            );

        } else {
            assertEquals(1, rootNode.getChildren().get(1).getChildren().size());
            assertEquals(
                    DICOM_RTPLAN + "-one-1-id",
                    ((StagedDicomSeriesVirtualSeries) rootNode.getChildren().get(1).getChildren().get(0).getData()).getAvailableDicomImages().get(0).getReferencedDicomSeriesUID()
            );
        }

        // The virtual RTImage has no children
        if (rootSeriesOne instanceof StagedDicomSeriesVirtualSeries) {
            assertEquals(0, rootNode.getChildren().get(0).getChildren().size());
        } else {
            assertEquals(0, rootNode.getChildren().get(1).getChildren().size());
        }

    }

    // endregion

}