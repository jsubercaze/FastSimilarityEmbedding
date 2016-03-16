package fr.ujm.lhc.krr.fse.hash.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import fr.ujm.lhc.krr.fse.hash.tree.exceptions.CyclicTreeException;
import fr.ujm.lhc.krr.fse.hash.tree.exceptions.NoRootException;

/**
 * ** Use it at your own risk ** Jung does not offer a undirected tree, but only @link
 * {@link Tree}. This class is a undirected tree. The
 * {@link #UndirectedTreeFactory()} allows to build an instance of a
 * {@link UndirectedTree}. Ultimately we should check if the tree is still a
 * tree at each addEdge call. This would however be a performance hindrance. It
 * it therefore not implemented. You should not call addEdge unless you really
 * really know what it is going on, hence the notice "use it at your own risk".
 * 
 * @author Christophe Gravier, <>
 */
public class UndirectedTree<V, E> extends UndirectedSparseGraph<V, E> {

	private static final long serialVersionUID = 1L;

	/**
	 * The root of the tree.
	 */
	private V root = null;

	/**
	 * Build a new undirectedTree, which has the property to be acyclic (check
	 * using {@link #containsCycle(Graph, HashMap)}) and that can be rooted in
	 * the given vertex (the vertex exist).
	 * 
	 * @param g
	 *            the graph from which to build the {@link UndirectedTree}.
	 * @param root
	 *            vertex root for the {@link UndirectedTree}.
	 * @return The associated {@link UndirectedTree}, or null if the graph is
	 *         acyclic or the root doesn't exist in this graph.
	 * @throws CyclicTreeException
	 *             when the graph is cyclic, which is not possible for a tree.
	 * @throws NoRootException
	 *             when the root doesn't belong to the graph, which is not
	 *             possible for a tree.
	 */
	public UndirectedTree<V, E> makeUndirectedGraph(final Graph<V, E> g,
			final V root) throws CyclicTreeException, NoRootException {
		final HashMap<V, String> markedVertices = new HashMap<V, String>(
				g.getVertexCount());

		final Map<V, Set<V>> parentsOfRoots = new HashMap<V, Set<V>>();
		for (final V v : g.getVertices()) {
			parentsOfRoots.put(v, new HashSet<V>());
		}
		if (!g.containsVertex(root)) {
			throw new NoRootException(
					"The graph does not contain the expected root (" + root
							+ "), hence cannot be rooted in this vertex !");
		} else if (containsCycle(g, root, markedVertices, parentsOfRoots)) {
//			throw new CyclicTreeException(
//					"The graph is not acyclic, hence cannot be a tree !");
			return new UndirectedTree<V, E>(g, root);
		} else {
			return new UndirectedTree<V, E>(g, root);
		}
	}

	/**
	 * Return a {@link UndirectedTree} which is an UndirectedSparseGraph. The
	 * tree must be acyclic and the root given in parameter exists in this
	 * graph. This constructor is unsafe as it doesn't test these properties. If
	 * you prefer to test it (yet pay a performance tribute), use:
	 * {@link #makeUndirectedGraph(Graph, Object)} instead.
	 * 
	 * @param g
	 * @param root
	 */
	public UndirectedTree(final Graph<V, E> g, final V root) {

		// set tree root
		this.root = root;

		// copy all vertices and edges.
		for (final V v : g.getVertices()) {
			addVertex(v);
		}

		for (final E e : g.getEdges()) {
			this.addEdge(e, g.getIncidentVertices(e));
		}
	}

	public UndirectedTree() {
		this.root = null;
	}

	/**
	 * Test if a graph contains cycle. Since it is expected to be a rooted tree,
	 * encountering the same node in a DFS means there is a cycle.
	 * 
	 * @param g
	 *            The graph to traverse.
	 * @param markedVertices
	 *            Collection of vertices marked as visited or not. This is
	 *            usually implemented by labeling directly the nodes, yet jung
	 *            vertices doesn't accept labels. This method is therefore much
	 *            slower.
	 * @param parentsOfRoots
	 *            Given the tree is undirected in our case (we want to preserve
	 *            hypo and hypernymy relations), we store in this parameter the
	 *            direct parents already visited.
	 * @return True if the graph contains at least a cycle, false if the graph
	 *         is acyclic.
	 */
	public boolean containsCycle(final Graph<V, E> g, final V root,
			final Map<V, String> markedVertices,
			final Map<V, Set<V>> parentsOfRoots) {

		for (final V v : g.getSuccessors(root)) {
			if (!parentsOfRoots.get(root).contains(v)) {
				if (markedVertices.get(v) != null
						&& markedVertices.get(v).equals("visited")) {
					return true;
				} else {
					markedVertices.put(v, "visited");
					parentsOfRoots.get(v).add(root);
					if (containsCycle(g, v, markedVertices, parentsOfRoots)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the vertex being the root for this tree.
	 * 
	 * @return the root of this tree.
	 */
	public V getRoot() {
		return root;
	}

	/**
	 * A <a href="http://www.json.org/">JSON</a> serializer.
	 * 
	 * @return JSON representation of <code>this</code>
	 */
	public String toJSONString() {
		return "You should implement json serialisation here dude!";
	}
}
