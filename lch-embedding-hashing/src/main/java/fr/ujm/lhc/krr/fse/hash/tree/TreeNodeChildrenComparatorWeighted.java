/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree;

import java.util.Comparator;
import java.util.Map;

import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * @author Julien Subercaze
 * 
 *         28 oct. 2013
 * @param <T>
 * 
 * 
 */
@SuppressWarnings("rawtypes")
public class TreeNodeChildrenComparatorWeighted implements Comparator {

	Map<TreeNode<?>, Integer> weight;

	public TreeNodeChildrenComparatorWeighted(Map<TreeNode<?>, Integer> weight) {
		super();
		this.weight = weight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		TreeNode<?> t1 = (TreeNode<?>) o1;
		TreeNode<?> t2 = (TreeNode<?>) o2;
		return -Integer.compare(weight.get(t1), weight.get(t2));
	}

}
