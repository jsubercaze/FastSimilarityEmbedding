package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.TreeNodeChildrenComparator;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * 
 * @author Julien Subercaze
 * 
 */
public class VariableBitsRankingByBranch extends AdaptiveLeftHasherEnhanced {
	/**
	 * 
	 */
	private static final long serialVersionUID = 33135641834054475L;
	private final static Logger LOGGER = Logger
			.getLogger(VariableBitsRankingByBranch.class);
	/**
	 * Number of bits of the base hashing
	 */
	int baseBits;
	/**
	 * Number of bits of the final signature
	 */
	int expectedBits;
	/**
	 * Set of nodes that are authorized to use an additional bit
	 */
	Set<Integer> authorizedNodes;
	/**
	 * Assign a value to each to decide who gets the allocation
	 */
	List<LabelValue> branchSizeValue;

	public VariableBitsRankingByBranch(boolean onemorebit, boolean sortValues,
			boolean sortChildren) {
		super(onemorebit, sortValues, sortChildren);
	}

	public VariableBitsRankingByBranch(boolean onemorebit, boolean sortValues,
			boolean sortChildren, int baseBits, int expectedBits) {
		super(onemorebit, sortValues, sortChildren);
		this.baseBits = baseBits;
		this.expectedBits = expectedBits;
	}

	@Override
	protected void init(Tree<?> tree) {
		branchSizeValue = new ArrayList<>(tree.nodes().size());
		authorizedNodes = new HashSet<>();
		// Number of nodes
		super.init(tree);
		//int deltaBits = expectedBits - baseBits;
		// Rank nodes by value
		computeBranchSize(tree.getRoot());
		computeValue();
		Collections.sort(branchSizeValue);
		System.out.println(branchSizeValue.subList(0, 20));
		for (int i = 0; i < 500; i++) {
			authorizedNodes.add(branchSizeValue.get(i).label);
		}
	}

	private void computeValue() {

	}

	private int computeBranchSize(TreeNode<?> node) {
		List<?> children = node.getChildren();
		if (children.size() == 0) {
			branchSizeValue.add(new LabelValue(node.getLabel(), 0));
			return 1;
		}
		int childs = 0;
		for (Object object : children) {
			TreeNode<?> child = (TreeNode<?>) object;
			childs += computeBranchSize(child);
		}
		int size = node.getChildren().size();
		if (size < 5) {
			size = 1;
		}
		branchSizeValue.add(new LabelValue(node.getLabel(), childs
				* Math.log(size)));
		return childs;
	}

	@Override
	public TreeHashFingerprintResult hash(Tree<?> tree) {
		// Rank tree nodes
		return super.hash(tree);
	}

	/**
	 * CountBits adaptative
	 */
	@Override
	protected int countBits(TreeNode<?> node) {
		int value = bitsFor(node.getChildren().size() + 1);
		//if ((authorizedNodes.contains(node.getLabel()))) {
			value+=6;
			// LOGGER.info("One more bits for" + node);
	//	}
		return value;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void hashHelper(final TreeNode<?> node, final Fingerprint bitset,
			final TreeHashFingerprintResult result) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Receiving from parent " + bitset);
		}

		result.putHash(node, bitset);
		// Updatesize
		hashSize = bitset.getLength() > hashSize ? bitset.getLength()
				: hashSize;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("After inserting :" + result);
		}
		if (node.getChildren().size() == 0) {
			return;
		}
		if (node.getChildren().size() == 1) {
			final Fingerprint b = new Fingerprint(bitset);
			b.set(b.getLength());
			b.setLength(b.getLength() + 1);
			hashHelper(node.getChildAt(0), b, result);
		} else {
			// Go through the children
			final int size = node.getChildren().size() + 1;
			final int bitsRequired = countBits(node);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(node.getNodeValue() + " " + bitset.toString()
						+ " | children " + node.getChildren().size()
						+ " bits : " + bitsRequired);
			}
			// Separate signature between good (i.e) distance respecting and
			// others
			final List<Fingerprint> goodvalues = new ArrayList<>(bitsRequired);
			final List<Fingerprint> othervalues = new ArrayList<>(
					1 << (bitsRequired - 1));

			final List<?> childre = node.getChildren();
			final List childs = new ArrayList<>(childre.size());
			childs.addAll(childre);
			// Sort the children by number of childs
			if (sortChildren) {
				Collections.sort(childs, new TreeNodeChildrenComparator());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sorted nodes by children " + childs);
			}
			Fingerprint full = null;
			// Generate all possible fingerprints
			for (int l = 1; l < 1 << bitsRequired; l++) {
				final Fingerprint tmp = new Fingerprint(bitsRequired);
				for (int j = bitsRequired - 1; j >= 0; j--) {
					final int gros = l;
					// LOGGER.debug("j :" + j);
					if ((gros & 1 << j) > 0) {
						// LOGGER.debug("Setting");
						tmp.set(bitsRequired - j - 1);

					}
				}
				// If l is a good value then it has bitsrequired-1 bits on
				if (Integer.bitCount(l) == 1) {
					goodvalues.add(tmp);
				} else if (Integer.bitCount(l) < bitsRequired) {
					othervalues.add(tmp);
				} else {
					full = tmp;
				}
			}

			// Sort by number of bits on if required
			if (sortValues) {
				Collections.sort(othervalues, new FingerprintBitOnComparator());
			}
			othervalues.add(full);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Good values : " + goodvalues);
				LOGGER.debug("Other values : " + othervalues);
			}
			// Iterate over children to compute signatures and recursive calls
			for (int i = 0; i < size - 1; i++) {
				// Copy bitset for signature
				final Fingerprint b = new Fingerprint(bitset);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Fingerprint received" + b);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Good values : " + goodvalues);
					LOGGER.debug("Other values : " + othervalues);
				}
				// Append the signature, good values first, rest after
				if (goodvalues.size() > 0) {
					b.append(goodvalues.get(0));
					goodvalues.remove(0);
				} else {
					b.append(othervalues.get(0));
					othervalues.remove(0);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Node " + node.getChildAt(i)
							+ " has signature " + b);
				}
				hashHelper((TreeNode<?>) childs.get(i), b, result);
			}
		}

	}
}

class LabelValue implements Comparable<LabelValue> {

	int label;
	double value;

	public LabelValue(int label, double value) {
		super();
		this.label = label;
		this.value = value;
	}

	@Override
	public int compareTo(LabelValue o) {
		return -Double.compare(value, o.value);
	}

	@Override
	public String toString() {
		return "LabelValue [label=" + label + ", value=" + value + "]";
	}

}
