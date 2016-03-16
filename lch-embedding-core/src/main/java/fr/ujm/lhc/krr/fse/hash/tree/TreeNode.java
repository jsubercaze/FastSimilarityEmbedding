package fr.ujm.lhc.krr.fse.hash.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Julien Subercaze
 * 
 * 
 * 
 *         Node in a tree - Generics in action
 */
public class TreeNode<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4065772434080647048L;
	private Tree<T> tree;
	private T nodeValue;
	private TreeNode<T> parent;
	private int label = -1;
	private List<TreeNode<T>> children;
	private int depth;

	/**
	 * An iterator that is always empty. This is used when an iterator of a leaf
	 * node's children is requested.
	 */

	/**
	 * Creates a tree node that has no parent and no children, but which allows
	 * children.
	 */
	public TreeNode() {
		this(null);
	}

	public TreeNode(final T nodeValue) {
		this.nodeValue = nodeValue;
		this.children = null;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	private void setParent(final TreeNode<T> parent) {
		this.parent = parent;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(final int depth) {
		this.depth = depth;
	}

	/**
	 * @return
	 */
	public boolean isLeaf() {
		if (this.children == null) {
			return true;
		}
		if (this.children.size() == 0) {
			return true;
		}
		return false;
	}

	public T getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(final T nodeValue) {
		this.nodeValue = nodeValue;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(final int label) {
		this.label = label;
	}

	/**
	 * Return true if the child is added and was not already present. ChildNode
	 * is appended to the children list
	 * 
	 * @param child
	 * @return
	 */
	public final boolean addChild(final TreeNode<T> child) {
		if (this.children == null) {
			this.children = new ArrayList<TreeNode<T>>();
		}
		// LinkedHashSet could have been used instead of list, however since we
		// insert once it would be a memory waste
		if (tree == null) {
			this.setTree(parent.getTree());
		}
		if (tree.addNode(child)) {
			children.add(child);
			child.setParent(this);
			child.setTree(this.tree);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * If one wants to modify elements, one should access elements uniquely
	 * 
	 * @return an immutable copy of the list
	 */
	@SuppressWarnings("unchecked")
	public final List<TreeNode<T>> getChildren() {
		if (this.children == null) {
			return Collections.EMPTY_LIST;
		}
		return ImmutableList.copyOf(children);
	}

	@SuppressWarnings("unchecked")
	public final TreeNode<T> getChildAt(final int index) {
		if (index >= children.size()) {
			return (TreeNode<T>) Collections.emptyList();
		}
		;
		return children.get(index);
	}

	@Override
	public final String toString() {
		return "TreeNode [nodeValue=" + nodeValue + ", label=" + label + "]";
	}

	/**
	 * Returns the number of levels above this node -- the distance from the
	 * root to this node. If this node is the root, returns 0.
	 * 
	 * @see #getDepth
	 * @return the number of levels above this node
	 */
	public final int getLevel() {
		TreeNode<T> ancestor = this;
		int levels = 0;

		while ((ancestor = ancestor.getParent()) != null) {
			levels++;
		}

		return levels;
	}

	public Tree<T> getTree() {
		return tree;
	}

	public void setTree(final Tree<T> tree) {
		this.tree = tree;
	}

	/**
	 * Does not use any JSON libs, Stringbuilder for speed
	 * 
	 * @return a JSON of the node and its children
	 */
	public final void toJsonString(final StringBuilder sb, String prefix) {
		prefix += "\t";
		sb.append(prefix + "{\n");
		sb.append(prefix + "\"node\": \"" + this.nodeValue.toString() + "\",\n");
		sb.append(prefix + "\"label\": \"" + label + "\"");
		if (children != null && children.size() > 0) {
			sb.append(",\n");
		} else {
			sb.append("\n");
		}
		if (children != null && children.size() > 0) {
			sb.append(prefix + "\"children\":[\n");
			for (int i = 0; i < children.size(); i++) {
				final TreeNode<T> child = children.get(i);
				child.toJsonString(sb, new String(prefix));
				if (i != children.size() - 1) {
					sb.append(prefix + ",\n");
				}
			}

			sb.append(prefix + "]\n");
		}
		sb.append(prefix + "}\n");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TreeNode)) {
			return false;
		}
		final TreeNode<?> node = (TreeNode<?>) obj;
		if (this.label == node.label) {
			return true;
		}
		return this == node;
	}

	@Override
	public int hashCode() {
		return this.label;
	}

}
