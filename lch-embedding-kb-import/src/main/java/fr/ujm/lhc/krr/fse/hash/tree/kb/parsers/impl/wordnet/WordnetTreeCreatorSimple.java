/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.kb.parsers.impl.wordnet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;

/**
 * Convert a Wordnet DB lattice into a Tree. Uses
 * Julien Subercaze heuristic for building a tree from
 * the lattice. The heuristic is called in {@link #getChildren(NounSynset)}.
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class WordnetTreeCreatorSimple {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(WordnetTreeCreatorSimple.class);

	/**
	 * Where the WordnetDB is stored
	 */
	static String inputDirectory;

	/**
	 * Where to write the .tree file
	 */
	static String outputFile;

	/**
	 * The wordnet database
	 */
	static WordNetDatabase database;
	/**
	 * Avoid graph
	 */
	static Set<NounSynset> visited;

	/***
	 * Stores forbidden hypernyms relation (i.e. when not first hypernym)
	 */
	static Set<MyPair> forbiddenPaths;
	/**
	 * For dat labels
	 */
	static int counter = 0;

	public static synchronized void createTree(final String input,
			final String output) {
		inputDirectory = input;
		outputFile = output;
		System.setProperty("wordnet.database.dir", input);
		LOGGER.info("Reading Wordnet DB");
		database = WordNetDatabase.getFileInstance();
		visited = new HashSet<>();
		counter = 0;
		// Build the tree
		LOGGER.info("Building Tree");
		final Tree<String> tree = createTree();
		// Dump to file
		TreeUtils.saveTreeAsJsonFile(tree, new File(output), String.class);
	}

	/**
	 * Create the tree for the
	 * 
	 * @return
	 */
	public static Tree<String> createTree() {

		final NounSynset root = (NounSynset) database.getSynsets("entity",
				SynsetType.NOUN)[0];
		final TreeNode<String> rootNode = new TreeNode<String>(""
				+ root.getId());
		final Tree<String> tree = new Tree<String>(rootNode);
		LOGGER.info("Creating forbidden paths list");
		rootNode.setTree(tree);
		// DFS
		parseNode(root, rootNode);
		// All are in visited now
		listNotSingleParent();
		// Build the tree
		visited.clear();
		LOGGER.info("Size of forbidden paths " + forbiddenPaths.size());
		buildTree(root, rootNode);
		LOGGER.info("Size of tree " + tree.nodes().size());
		return tree;

	}

	private static void listNotSingleParent() {
		forbiddenPaths = new HashSet<>();
		for (NounSynset nounSynset : visited) {
			int current = nounSynset.getId();
			if (nounSynset.getHypernyms().length > 1) {
				NounSynset[] hypernyms = nounSynset.getHypernyms();
				boolean first = true;
				for (NounSynset nounSynset2 : hypernyms) {
					if (first) {
						first = false;
						continue;
					}
					int key = current;
					int value = nounSynset2.getId();
					MyPair gros = new MyPair(key, value);
					forbiddenPaths.add(gros);
				}
			}
			if (nounSynset.getInstanceHypernyms().length > 1) {
				NounSynset[] hypernyms = nounSynset.getHypernyms();
				boolean first = true;
				for (NounSynset nounSynset2 : hypernyms) {
					if (first) {
						first = false;
						continue;
					}

					int key = current;
					int value = nounSynset2.getId();
					forbiddenPaths.add(new MyPair(key, value));
				}
			}
		}

	}

	/**
	 * @param root
	 * @param rootNode
	 */
	private static void parseNode(final NounSynset synset,
			final TreeNode<String> rootNode) {
		if (!visited.contains(synset)) {
			visited.add(synset);
			// Get the children
			try {
				final NounSynset[] children = getChildren(synset);
				for (final NounSynset nounSynset : children) {
					// Create a node for the synset

					final TreeNode<String> child = new TreeNode<String>(""
							+ synset.getId());
					// rootNode.addChild(child);
					parseNode(nounSynset, child);
				}
			} catch (final Exception e) {
				LOGGER.error("hoho", e);
				return;
			}

		}

	}

	private static void buildTree(final NounSynset synset,
			final TreeNode<String> rootNode) {
		if (!visited.contains(synset)) {
			visited.add(synset);
			// Get the children
			try {
				int current = synset.getId();
				final NounSynset[] children = getChildren(synset);
				for (final NounSynset nounSynset : children) {
					//boolean add = true;
					int son = nounSynset.getId();
					MyPair gros = new MyPair(current, son);
					if (!forbiddenPaths.contains(gros)) {
						// Create a node for the synset
						++counter;
						final TreeNode<String> child = new TreeNode<String>(""
								+ nounSynset.getId());
						rootNode.addChild(child);
						buildTree(nounSynset, child);
					}
				}
			} catch (final Exception e) {
				return;
			}

		}
	}

	private static NounSynset[] getChildren(NounSynset synset) {
		NounSynset[] hyponyms = synset.getHyponyms();
		NounSynset[] instanceHyponyms = synset.getInstanceHyponyms();
		NounSynset[] merged = new NounSynset[hyponyms.length
				+ instanceHyponyms.length];
		System.arraycopy(hyponyms, 0, merged, 0, hyponyms.length);
		System.arraycopy(instanceHyponyms, 0, merged, hyponyms.length,
				instanceHyponyms.length);
		return merged;
	}

	static class MyPair {

		int a, b;

		public MyPair(int a, int b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return a + b;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyPair other = (MyPair) obj;
			if ((a == other.a && b == other.b)
					|| (a == other.b && b == other.a)) {
				return true;
			}
			return false;
		}

	}

}
