package fr.ujm.lhc.krr.fse.hash.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing a tree. Minimum required functions are implemented.
 * Provides toolbox for DFS labeling, depth setting and a lookup table using
 * nodes hashcode.
 * 
 * @author Julien Subercaze
 * @author Christophe Gravier, <>
 * 
 *         * 
 *         Tree.
 */
public class Tree<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1873058036845677697L;
	// Dictionary for fast lookup
	Set<TreeNode<T>> nodeset;
	TreeNode<T> root;
	int nodes; // Number of nodes in the tree
	int maxdepth;

	public Tree(final TreeNode<T> node) {
		this.root = node;
		node.setTree(this);
		nodeset = new LinkedHashSet<TreeNode<T>>();
		nodeset.add(root);
	}

	/**
	 * Label the tree, set the depth and fill the lookup table
	 * 
	 */
	public void prepare() {
		if (nodeset == null || nodeset.size() == 0) {
			forcePrepare();
		}
	}

	public void forcePrepare() {
		final TreeNode<T> current = root;
		nodes = 0;
		final int depth = 0;
		if (nodeset == null) {
			nodeset = new LinkedHashSet<TreeNode<T>>();
		}
		nodeset.add(root);
		label(current, depth);

	}

	/**
	 * Add the node to the tree
	 * 
	 * @param node
	 * @return false if the node is already in the tree
	 */
	public final boolean addNode(final TreeNode<T> node) {
		if (node.getLabel() == -1) {
			node.setLabel(nodes + 1);
		}
		if (nodeset == null) {
			nodeset = new HashSet<>();
			nodeset.add(node);
			return true;
		} else if (nodeset.contains(node)) {
			return false;
		}
		nodeset.add(node);
		nodes++;
		return true;
	}

	private final void label(final TreeNode<T> node, final int depth) {
		nodes++;
		nodeset.add(node);
		node.setLabel(nodes);
		node.setDepth(depth);
		maxdepth = depth > maxdepth ? depth : maxdepth;
		for (final TreeNode<T> child : node.getChildren()) {
			label(child, depth + 1);
		}
	}

	public String toJSONString() {
		if (root != null) {
			final StringBuilder sb = new StringBuilder();
			root.toJsonString(sb, "");
			return sb.toString();
		}
		return null;
	}

	/**
	 * 
	 * @return the root node of the tree
	 */
	public TreeNode<T> getRoot() {
		return root;
	}

	/**
	 * Sets of nodes Useful for error estimation
	 * 
	 * @return the set of hashcodes of nodes in the tree.
	 */
	public Set<TreeNode<T>> nodes() {
		return nodeset;
	}

	public int getMaxdepth() {
		return maxdepth;
	}

	/**
	 * 
	 * @return a pre order traversal ordered list of nodes
	 */
	public List<TreeNode<T>> preOrderTraversal() {
		this.prepare();
		final List<TreeNode<T>> preorder = new ArrayList<>(nodes);
		preOrderTraverse(this.root, preorder);
		return preorder;
	}

	private void preOrderTraverse(final TreeNode<T> node,
			final List<TreeNode<T>> preorder) {
		preorder.add(node);
		for (final TreeNode<T> child : node.getChildren()) {
			preOrderTraverse(child, preorder);
		}
	}

	/**
	 * Lookup for a node, in the subtree starting at startNode, which has a
	 * given value of type T given in parameter. This is especially relevant for
	 * ontologies like nell in which the taxonomy is not structured but implicit
	 * in the kb file (for instance, collections of triples. Meaning you can
	 * meet children before their parent and vice versa).
	 * 
	 * @param startNode
	 *            where to start.
	 * @param value
	 *            the value of the node that is searched in the tree.
	 * @return The TreeNode instance found, null otherwise.
	 */
	public TreeNode<T> lookForNode(final TreeNode<T> startNode, final T value) {

		if (startNode.getNodeValue().equals(value)) {
			return startNode;
		}

		for (final TreeNode<T> child : startNode.getChildren()) {
			final TreeNode<T> candidate = lookForNode(child, value);
			if (candidate != null) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Same as {@link #lookForNode(Object)}, but if no node is found with the
	 * given value, a new node with this value is created and inserted as a root
	 * child.
	 */
	public TreeNode<T> lookForNodeAndCreateIfMissing(
			final TreeNode<T> startNode, final T value) {
		TreeNode<T> elem = lookForNode(startNode, value);
		if (elem == null) {
			elem = new TreeNode<T>(value);
			this.getRoot().addChild(elem);
		}
		return elem;
	}

}
