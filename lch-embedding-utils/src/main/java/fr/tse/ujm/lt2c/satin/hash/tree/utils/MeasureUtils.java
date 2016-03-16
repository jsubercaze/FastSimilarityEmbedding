package fr.tse.ujm.lt2c.satin.hash.tree.utils;

import java.util.List;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;

public class MeasureUtils {
	/**
	 * 
	 * @param tree
	 * @return the branching factors of the tree
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
