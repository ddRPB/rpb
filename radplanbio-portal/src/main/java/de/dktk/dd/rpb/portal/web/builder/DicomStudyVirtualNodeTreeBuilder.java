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

package de.dktk.dd.rpb.portal.web.builder;

import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeriesVirtualSeries;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_CT;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTIMAGE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTPLAN;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;

/**
 * Builder that creates tree structure of nodes with StagedDicomSeries, based on references.
 * Series that contain DICOM Images that refer to different other Series will be split into one "virtual" series
 * per reference.
 */
public class DicomStudyVirtualNodeTreeBuilder {
    private final List<StagedDicomSeries> stagedDicomSeriesList = new ArrayList<>();

    private TreeNode rootNode = new DefaultTreeNode();
    private final boolean updated = false;

    public DicomStudyVirtualNodeTreeBuilder() {
    }

    /***
     * Builder that creates tree structure of nodes with StagedDicomSeries, based on references.
     * Series that contain DICOM Images that refer to different other Series will be split into one "virtual" series
     * per reference.
     *
     * @param stagedDicomSeriesList List<StagedDicomSeries> all Series that will be used to create the tree
     */
    public DicomStudyVirtualNodeTreeBuilder(List<StagedDicomSeries> stagedDicomSeriesList) {
        Set<String> referencedSopInstanceUids = new HashSet<>();
        List<StagedDicomSeries> splitCandidatesList = new ArrayList<>();

        // First iteration - evaluates if images refer to another image
        // and collects all that referenced UIDs for the next step
        for (StagedDicomSeries series : stagedDicomSeriesList) {
            referencedSopInstanceUids.addAll(series.getReferencedUidSet());

            // more than one image && more than one referenced Series -> Series is a split candidate
            if (series.getAvailableDicomImages().size() > 1 && series.getReferencedUidSet().size() > 1) {
                // series with images that refer to different other series will be split into virtual nodes
                for (int i = 0; i < series.getReferencedUidSet().size(); i++) {
                    String referencedUid = (String) series.getReferencedUidSet().toArray()[i];
                    splitCandidatesList.add(new StagedDicomSeriesVirtualSeries(series, referencedUid));
                }

            } else {
                // Series has not more than one image - split not necessary
                this.stagedDicomSeriesList.add(series);

            }
        }

        // second iteration splits a series if images are referenced by different other series images
        // unreferenced images will be packed into one virtual series
        for (StagedDicomSeries splitCandidate : splitCandidatesList) {
            if (splitCandidate.getAvailableDicomImages().size() > 0) {
                List<DicomImage> unreferencedImages = new ArrayList<>();
                for (DicomImage image : splitCandidate.getAvailableDicomImages()) {
                    if (image.getSopInstanceUID() != null) {
                        String uid = image.getSopInstanceUID();
                        if (referencedSopInstanceUids.contains(uid)) {
                            List<DicomImage> imageList = new ArrayList<>();
                            imageList.add(image);
                            this.stagedDicomSeriesList.add(
                                    new StagedDicomSeriesVirtualSeries(splitCandidate, imageList)
                            );
                        } else {
                            unreferencedImages.add(image);
                        }
                    }
                }

                // all unreferenced images will be bundled to one series
                if (unreferencedImages.size() > 0) {
                    this.stagedDicomSeriesList.add(
                            new StagedDicomSeriesVirtualSeries(splitCandidate, unreferencedImages)
                    );
                }
            }


        }

    }

    /**
     * Evaluates if at least one of the series has images.
     *
     * @return true if a series has images
     */
    public boolean hasDicomSeriesDetailData() {

        if (this.updated) {
            return true;
        }

        for (StagedDicomSeries series : this.stagedDicomSeriesList) {
            if (series.getAvailableDicomImages() != null) {
                if (series.getAvailableDicomImages().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Build a tree
     *
     * @return tree representation of StagedDicomSeries
     */
    public TreeNode build() {
        if (!hasDicomSeriesDetailData()) {
            return buildBasicTree();
        }

        this.rootNode = new DefaultTreeNode();
        List<TreeNode> treeNodeArray = new ArrayList<>();
        HashMap<String, TreeNode> treeNodeHashMap = new HashMap<>();

        createNodeHashMapWithIdsAsKey(treeNodeArray, treeNodeHashMap);

        List<TreeNode> unassignedNodeList = new ArrayList<>();

        addNodesAsChildrenToReferencedNodes(treeNodeArray, treeNodeHashMap, unassignedNodeList);

        addUnassignedNodesAsChildrenToRootNode(unassignedNodeList);

        return this.rootNode;
    }

    private void addNodesAsChildrenToReferencedNodes(List<TreeNode> treeNodeArray, HashMap<String, TreeNode> treeNodeHashMap, List<TreeNode> unassignedNodeList) {
        for (TreeNode node : treeNodeArray) {
            StagedDicomSeries series = (StagedDicomSeries) node.getData();

            if (series.getAvailableDicomImages() != null) {

                if (series.getAvailableDicomImages().size() == 0) {
                    unassignedNodeList.add(node);
                } else {
                    for (DicomImage image : series.getAvailableDicomImages()) {
                        // DicomImage getReferencedDicomSeriesUID returns null per default
                        if (image.getReferencedDicomSeriesUID() == null) {
                            unassignedNodeList.add(node);
                        } else {
                            // RT related DicomImage subclasses return the referenced DicomSeriesUID
                            addNodeAsChildOfReferencedParent(treeNodeHashMap, node, image.getReferencedDicomSeriesUID());
                        }
                    }
                }

            } else {
                unassignedNodeList.add(node);
            }

        }
    }

    private void createNodeHashMapWithIdsAsKey(List<TreeNode> treeNodeArray, HashMap<String, TreeNode> treeNodeHashMap) {
        for (StagedDicomSeries series : this.stagedDicomSeriesList) {
            TreeNode node = new DefaultTreeNode(series);
            treeNodeArray.add(node);

            if (series.getClinicalSeriesUid() == null || series.getClinicalSeriesUid().isEmpty()) {
                treeNodeHashMap.put(series.getStageOneSeriesUid(), node);
            } else {
                treeNodeHashMap.put(series.getClinicalSeriesUid(), node);
            }

            if (series.getAvailableDicomImages() != null) {
                for (DicomImage image : series.getAvailableDicomImages()) {
                    treeNodeHashMap.put(image.getSopInstanceUID(), node);
                }
            }
        }
    }

    private void addUnassignedNodesAsChildrenToRootNode(List<TreeNode> unassignedNodeList) {
        for (TreeNode node : unassignedNodeList) {
            if (node.getParent() == null) {
                this.rootNode.getChildren().add(node);
            }
        }
    }

    private void addNodeAsChildOfReferencedParent(HashMap<String, TreeNode> treeNodeHashMap, TreeNode node, String referencedNodeUid) {
        TreeNode referencedNode = treeNodeHashMap.get(referencedNodeUid);
        if (referencedNode != null) {
            referencedNode.getChildren().add(node);
        } else {
            this.rootNode.getChildren().add(node);
        }
    }

    /**
     * Fallback method that creates a tree representation that has all nodes directly under the root node
     * ordered by modality.
     *
     * @return tree representation of StagedDicomSeries
     */
    private TreeNode buildBasicTree() {
        List<StagedDicomSeries> rtStructSeriesList = new ArrayList<>();
        List<StagedDicomSeries> ctSeriesList = new ArrayList<>();
        List<StagedDicomSeries> rtPlanSeriesList = new ArrayList<>();
        List<StagedDicomSeries> rtImageSeriesList = new ArrayList<>();
        List<StagedDicomSeries> otherSeriesList = new ArrayList<>();

        for (StagedDicomSeries series : this.stagedDicomSeriesList) {
            switch (series.getSeriesModality()) {
                case DICOM_CT:
                    ctSeriesList.add(series);
                    break;
                case DICOM_RTSTRUCT:
                    rtStructSeriesList.add(series);
                    break;
                case DICOM_RTPLAN:
                    rtPlanSeriesList.add(series);
                    break;
                case DICOM_RTIMAGE:
                    rtImageSeriesList.add(series);
                    break;
                default:
                    otherSeriesList.add(series);
            }
        }

        for (StagedDicomSeries series : ctSeriesList) {
            addSeriesNodeToRootNode(series);
        }

        for (StagedDicomSeries series : rtStructSeriesList) {
            addSeriesNodeToRootNode(series);
        }

        for (StagedDicomSeries series : rtPlanSeriesList) {
            addSeriesNodeToRootNode(series);
        }

        for (StagedDicomSeries series : rtImageSeriesList) {
            addSeriesNodeToRootNode(series);
        }

        for (StagedDicomSeries series : otherSeriesList) {
            addSeriesNodeToRootNode(series);
        }

        return this.rootNode;
    }

    private void addSeriesNodeToRootNode(StagedDicomSeries series) {
        TreeNode seriesNode = new DefaultTreeNode(series);
        this.rootNode.getChildren().add(seriesNode);
    }

}
