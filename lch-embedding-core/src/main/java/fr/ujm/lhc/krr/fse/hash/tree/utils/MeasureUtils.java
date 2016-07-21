package fr.ujm.lhc.krr.fse.hash.tree.utils;

import java.util.List;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;

public class MeasureUtils {
	/**
	 * 
	 * @param tree
	 * @return the branching factors of the tree
	 */
	public static Measure branchingFactor(Tree tree) {
		List<TreeNode> preOrderTraversal = tree.preOrderTraversal();
		Measure measure = new Measure();
		for (TreeNode<?> treeNode : preOrderTraversal) {
			if (treeNode.getChildren().size() > 0) {
				measure.addValue(treeNode.getChildren().size());
			}
		}
		return measure;
	}

	/**
	 * 
	 * @param tree
	 * @return depths (mean, std) for all nodes
	 */
	public static Measure depth(Tree tree) {
		List<TreeNode> preOrderTraversal = tree.preOrderTraversal();
		Measure measure = new Measure();
		for (TreeNode treeNode : preOrderTraversal) {
			measure.addValue(treeNode.getDepth());
		}
		return measure;
	}

	/**
	 * 
	 * @param tree
	 * @return depths (mean, std) for leaves only
	 */
	public static Measure leavesDepth(Tree tree) {
		List<TreeNode> preOrderTraversal = tree.preOrderTraversal();
		Measure measure = new Measure();
		for (TreeNode treeNode : preOrderTraversal) {
			if (treeNode.getChildren().size() == 0) {
				measure.addValue(treeNode.getDepth());
			}
		}
		return measure;
	}
}
