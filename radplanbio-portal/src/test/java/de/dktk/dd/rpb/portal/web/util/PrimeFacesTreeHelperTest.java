package de.dktk.dd.rpb.portal.web.util;

import org.junit.Test;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrimeFacesTreeHelperTest {

    // region extractAllDataIncludingParents

    @Test
    public void null_object_results_in_empty_list() {
        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(null);

        assertNotNull(resultList);
    }

    @Test
    public void empty_tree_nodes_array_results_in_empty_list() {
        TreeNode[] treeNodes = new TreeNode[0];
        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(treeNodes);

        assertNotNull(resultList);
    }

    @Test
    public void single_node_results_in_single_node() {
        List<TreeNode> treeNodeList = new ArrayList<>();

        TreeNode node = new DefaultTreeNode();
        treeNodeList.add(node);

        TreeNode[] treeNodes = new TreeNode[treeNodeList.size()];
        treeNodeList.toArray(treeNodes);

        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(treeNodes);

        assertNotNull(resultList);
    }

    @Test
    public void single_node_with_child_results_in_single_data_item() {
        List<TreeNode> treeNodeList = new ArrayList<>();
        String parentString = new String("parent");
        String firstChildString = new String("first child");

        TreeNode parentNode = new DefaultTreeNode(parentString);
        TreeNode firstChildNode = new DefaultTreeNode(firstChildString);
        firstChildNode.setParent(parentNode);

        treeNodeList.add(parentNode);

        TreeNode[] treeNodes = new TreeNode[treeNodeList.size()];
        treeNodeList.toArray(treeNodes);

        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(treeNodes);

        assertEquals(1, resultList.size());
        assertEquals(parentString, resultList.get(0));
    }

    @Test
    public void single_node_with_parent_results_in_two_data_items() {
        List<TreeNode> treeNodeList = new ArrayList<>();
        String parentString = new String("parent");
        String firstChildString = new String("first child");

        TreeNode parentNode = new DefaultTreeNode(parentString);
        TreeNode firstChildNode = new DefaultTreeNode(firstChildString);
        firstChildNode.setParent(parentNode);

        treeNodeList.add(firstChildNode);

        TreeNode[] treeNodes = new TreeNode[treeNodeList.size()];
        treeNodeList.toArray(treeNodes);

        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(treeNodes);

        assertEquals(2, resultList.size());
        assertEquals(parentString, resultList.get(0));
        assertEquals(firstChildString, resultList.get(1));
    }

    @Test
    public void two_nodes_with_parent_results_in_tree_data_items_without_duplicated_parent() {
        List<TreeNode> treeNodeList = new ArrayList<>();
        String parentString = new String("parent");
        String firstChildString = new String("first child");
        String secondChildString = new String("second child");

        TreeNode parentNode = new DefaultTreeNode(parentString);
        TreeNode firstChildNode = new DefaultTreeNode(firstChildString);
        TreeNode secondChildNode = new DefaultTreeNode(secondChildString);
        firstChildNode.setParent(parentNode);
        secondChildNode.setParent(parentNode);

        treeNodeList.add(firstChildNode);
        treeNodeList.add(secondChildNode);

        TreeNode[] treeNodes = new TreeNode[treeNodeList.size()];
        treeNodeList.toArray(treeNodes);

        List resultList = PrimeFacesTreeHelper.extractAllDataIncludingParents(treeNodes);

        assertEquals(3, resultList.size());
        assertEquals(parentString, resultList.get(0));
        assertEquals(secondChildString, resultList.get(1));
        assertEquals(firstChildString, resultList.get(2));
    }

    // end region

}