package de.dktk.dd.rpb.portal.web.util;

import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Helper class for operations on the trees of Primefaces TreeNodes
 */
public class PrimeFacesTreeHelper {

    /**
     * Iterates to the tree ( of Primefaces TreeNodes) up to the root node and
     * collects all data from the nodes to a list, and removes duplicates.
     *
     * @param treeNodesArray Array of TreeNodes
     * @param <T>            data of the node
     * @return List<T> List of the data referenced by the nodes and their parent nodes
     */
    public static <T> List<T> extractAllDataIncludingParents(TreeNode[] treeNodesArray) {
        List extractedDataList = new ArrayList<T>();
        HashSet<T> extractedDataObjects = new HashSet<>();

        if (treeNodesArray == null) {
            return extractedDataList;
        }

        for (TreeNode node : treeNodesArray) {
            TreeNode currentNode = node;
            // iterate to the root
            while (currentNode != null) {

                if (currentNode.getData() != null) {
                    T parentSeries = (T) currentNode.getData();
                    extractedDataObjects.add(parentSeries);
                }
                // climb up one level
                currentNode = currentNode.getParent();
            }
        }

        extractedDataList.addAll(extractedDataObjects);
        return extractedDataList;
    }
}
