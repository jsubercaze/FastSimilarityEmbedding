/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.kb.parsers.impl.wordnet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;

/**
 * Convert a Wordnet DB into a Tree for later hashing. Uses P. Bamba heuristic
 * for building a tree from the lattice. The heuristics is called in
 * {@link #getChildren(NounSynset)}.
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class WordnetTreeCreator {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(WordnetTreeCreator.class);

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
	/**
	 * For dat labels
	 */
	static int counter = 0;

	public static synchronized void createTree(final String input,
			final String output) {
		LOGGER.info("Creating tree");
		inputDirectory = input;
		outputFile = output;
		System.setProperty("wordnet.database.dir", input);
		database = WordNetDatabase.getFileInstance();
		visited = new HashSet<>();
		counter = 0;
		// Build the tree
		final Tree<String> tree = createTree();
		// Dump to file
		TreeUtils.saveTreeAsJsonFile(tree, new File(output), String.class);
		System.out.println(tree.nodes().size());
	}

	/**
	 * Create the tree for the
	 * 
	 * @return
	 */
	public static Tree<String> createTree() {
		final TreeNode<String> rootNode = new TreeNode<String>("entity");
		final NounSynset root = (NounSynset) database.getSynsets("entity",
				SynsetType.NOUN)[0];
		final Tree<String> tree = new Tree<String>(rootNode);
		rootNode.setTree(tree);
		parseNode(root, rootNode);
		// Dump the tree to file

		return tree;
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
					++counter;
					final TreeNode<String> child = new TreeNode<String>(""
							+ synset.getId());
					rootNode.addChild(child);
					parseNode(nounSynset, child);
				}
			} catch (final Exception e) {

				return;
			}

		}

	}

	/**
	 * Gets all the children which have the most specific relationship with the
	 * given synset. This means that for all the children the noun synset might
	 * have, if the child has the minimum number of siblings through its
	 * relationship with the noun synset, it's kept.
	 * 
	 * @param nounSynset
	 *            The NounSynset to get the children from
	 * @return An <code>ArrayList</code> of children
	 */
	private static NounSynset[] getChildren(final NounSynset nounSynset) {
		final ArrayList<NounSynset> children = new ArrayList<NounSynset>();
		final NounSynset[][] tempChildren = { nounSynset.getHyponyms(),
				nounSynset.getInstanceHyponyms(),
				nounSynset.getMemberMeronyms(), nounSynset.getPartMeronyms(),
				getNounRegionMembers(nounSynset),
				nounSynset.getSubstanceMeronyms(),
				getNounTopicMembers(nounSynset),
				getNounUsageMembers(nounSynset) };
		for (final NounSynset[] temp : tempChildren) {
			for (final NounSynset child : temp) {
				if (child != null) {
					if (getMinSiblings(child) == temp.length) {
						children.add(child);
					}
				}
			}
		}
		return children.toArray(new NounSynset[children.size()]);
	}

	/**
	 * Gets the minimum number of siblings a noun synset has through all the
	 * relationships it might have with other noun synsets. The considered
	 * relationships are:
	 * <ul>
	 * <li>Hypernymy</li>
	 * <li>Instance Hypernymy</li>
	 * <li>Member Holonymy</li>
	 * <li>Part Holonymy</li>
	 * <li>Region</li>
	 * <li>Substance Holonymy</li>
	 * <li>Topic</li>
	 * <li>Usage</li> </ol>
	 * 
	 * @param synset
	 *            the <code>NounSynset</code>
	 * @return the minimum number of siblings it has
	 */
	private static int getMinSiblings(final NounSynset synset) {
		int minSiblings = Integer.MAX_VALUE;
		int tempSiblings;
		if (synset.getHypernyms().length != 0) {
			final NounSynset hypernym = synset.getHypernyms()[0];
			minSiblings = hypernym.getHyponyms().length;
			// System.out.println("MinSiblings Hyper: "+minSiblings);
		}
		if (synset.getInstanceHypernyms().length != 0) {
			final NounSynset instanceHypernym = synset.getInstanceHypernyms()[0];
			tempSiblings = instanceHypernym.getInstanceHyponyms().length;
			minSiblings = Math.min(minSiblings, tempSiblings);
			// System.out.println("MinSiblings Inst: "+minSiblings);
		}
		if (synset.getMemberHolonyms().length != 0) {
			final NounSynset memberHolonym = synset.getMemberHolonyms()[0];
			tempSiblings = memberHolonym.getMemberMeronyms().length;
			minSiblings = Math.min(minSiblings, tempSiblings);
			// System.out.println("MinSiblings Memb: "+minSiblings);
		}
		if (synset.getPartHolonyms().length != 0) {
			final NounSynset partHolonym = synset.getPartHolonyms()[0];
			tempSiblings = partHolonym.getPartMeronyms().length;
			minSiblings = Math.min(minSiblings, tempSiblings);
			// System.out.println("MinSiblings Part: "+minSiblings);
		}
		if (synset.getRegions().length != 0) {
			final NounSynset region = synset.getRegions()[0];
			tempSiblings = getNounRegionMembers(region).length;
			minSiblings = Math.min(minSiblings, tempSiblings);
			// System.out.println("MinSiblings Reg: "+minSiblings);
		}
		if (synset.getSubstanceHolonyms().length != 0) {
			final NounSynset substanceHolonym = synset.getSubstanceHolonyms()[0];
			tempSiblings = substanceHolonym.getSubstanceMeronyms().length;
			minSiblings = Math.min(minSiblings, tempSiblings);
			// System.out.println("MinSiblings Sub: "+minSiblings);
		}
		if (synset.getTopics().length != 0) {
			final NounSynset topic = synset.getTopics()[0];
			tempSiblings = getNounTopicMembers(topic).length;
			// System.out.println("TempSiblings Top: "+tempSiblings);
			minSiblings = Math.min(minSiblings, tempSiblings);
		}
		if (synset.getUsages().length != 0) {
			final NounSynset usage = synset.getUsages()[0];
			tempSiblings = getNounUsageMembers(usage).length;
			minSiblings = Math.min(minSiblings, tempSiblings);
		}
		// System.out.println("MinSiblings: "+minSiblings);
		return minSiblings;
	}

	/**
	 * Gets only the region members which {@link SynsetType} is
	 * <code>NounSynset</code>
	 * 
	 * @param nounSynset
	 *            The noun synset to get region members from
	 * @return The noun synset region members.
	 * @see NounSynset#getRegionMembers()
	 */
	private static NounSynset[] getNounRegionMembers(final NounSynset nounSynset) {
		return getNounMembers(nounSynset.getRegionMembers());
	}

	/**
	 * Gets only the topic members which {@link SynsetType} is
	 * <code>NounSynset</code>
	 * 
	 * @param nounSynset
	 *            The noun synset to get topic members from
	 * @return The noun synset region members.
	 * @see NounSynset#getTopicMembers()
	 */
	private static NounSynset[] getNounTopicMembers(final NounSynset nounSynset) {
		return getNounMembers(nounSynset.getTopicMembers());
	}

	/**
	 * Gets only the usage members which {@link SynsetType} is
	 * <code>NounSynset</code>
	 * 
	 * @param nounSynset
	 *            The noun synset to get topic members from
	 * @return The noun synset region members.
	 * @see NounSynset#getUsageMembers()
	 */
	private static NounSynset[] getNounUsageMembers(final NounSynset nounSynset) {
		return getNounMembers(nounSynset.getUsageMembers());
	}

	private static NounSynset[] getNounMembers(final Synset[] members) {
		final NounSynset[] nounMembers = new NounSynset[members.length];
		int i = 0;
		for (final Synset member : members) {
			if (member.getType() == SynsetType.NOUN) {
				nounMembers[i] = (NounSynset) member;
				i++;
			}
		}
		return nounMembers;
	}

}
