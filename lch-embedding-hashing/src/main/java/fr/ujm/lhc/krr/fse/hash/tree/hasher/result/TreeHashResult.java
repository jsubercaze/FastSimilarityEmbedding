/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.result;

import java.io.Serializable;
import java.util.Map;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * @author Julien Subercaze
 * 
 *         10 oct. 2013
 * 
 *         Result of the tree labelling operation.
 * 
 *         Associated with the distance to compare the Values
 * 
 *         Is used as input for the error measurement
 */
public abstract class TreeHashResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4633571702985477796L;
	/**
	 * The tree that has been hashed to generate this result
	 */
	protected Tree<?> tree;
	/**
	 * The hashing scheme used to generate this results.
	 */
	protected TreeHasher labelizer;
	/**
	 * Map for storing hash results. Key is the unique identifier of the node,
	 * usually its hashcode, if well defined
	 */
	protected Map<TreeNode<?>, T> hashedLookupTable;

	/**
	 * @param tree
	 * @param labelizer
	 */
	public TreeHashResult(final Tree<?> tree, final TreeHasher labelizer) {
		super();
		this.tree = tree;
		this.labelizer = labelizer;
	}

	/**
	 * 
	 * @param label1
	 * @param label2
	 * @return the distance between two keys of the table. -1 if one or both are
	 *         not present
	 */
	public abstract int distance(TreeNode<?> node1, TreeNode<?> node2);

	/**
	 * Adds a hash result to the table
	 * 
	 * @param label
	 * @param hash
	 */
	public abstract void putHash(TreeNode<?> node1, T hash);

	/**
	 * 
	 * @return the number of entries <Integer, Hash>
	 */
	public abstract int size();

	public Tree<?> getTree() {
		return tree;
	}

	public TreeHasher getLabelizer() {
		return labelizer;
	}

	/**
	 * 
	 * @param hashcode
	 * @return the hash of a node identified by its hashcode
	 */
	public T getHashForNode(final TreeNode<?> node) {
		return hashedLookupTable.get(node);
	}

	public Map<TreeNode<?>, T> getHashedLookupTable() {
		return hashedLookupTable;
	}

}