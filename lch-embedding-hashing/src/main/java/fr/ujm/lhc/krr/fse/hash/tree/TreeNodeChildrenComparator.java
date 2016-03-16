/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree;

import java.util.Comparator;

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
public class TreeNodeChildrenComparator implements Comparator {

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		TreeNode<?> t1 = (TreeNode<?>) o1;
		TreeNode<?> t2 = (TreeNode<?>) o2;
		return -Integer.compare(t1.getChildren().size(), t2.getChildren()
				.size());
	}

}
