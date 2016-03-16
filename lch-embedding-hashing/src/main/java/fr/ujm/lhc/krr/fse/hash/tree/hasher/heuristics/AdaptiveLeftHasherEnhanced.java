package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.TreeNodeChildrenComparator;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Modified version of the {@link AdaptiveLeftHasher} that performs a better
 * hash assignment while maintaining the same hash length.
 * 
 * The algorithm performs a preorder traversal with the following labeling
 * procedure :
 * <ol>
 * <li>Get the hash assigned by parent</li>
 * <li>Determine how many bits are required to encode the children :
 * log2(|children|)</li>
 * <li>Select the best permutation so that in the following order
 * <ol>
 * <li>Distances to parent equal 1</li>
 * <li>Distances among children equal 2</li>
 * </ol>
 * </li>
 * <li>Label children by appending their hash to the parents one</li>
 * <li>Recursive call</li>
 * </ol>
 * 
 * 
 * 
 * 
 * @author Julien Subercaze
 * 
 * 
 * 
 */
public class AdaptiveLeftHasherEnhanced implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9139729687321199738L;

	private final static Logger LOGGER = Logger
			.getLogger(AdaptiveLeftHasherEnhanced.class);
	int hashSize; // Bitset being a vector, we keep this value updated
	protected boolean sortValues;
	protected boolean sortChildren;
	private final Distance<BitSet> distance = new HammingDistance();

	protected final boolean onemorebit;

	/**
	 * @param sortValues
	 * @param sortChildren
	 */
	public AdaptiveLeftHasherEnhanced(final boolean onemorebit,
			final boolean sortValues, final boolean sortChildren) {
		super();
		this.onemorebit = onemorebit;
		this.sortValues = sortValues;
		this.sortChildren = sortChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	@Override
	public TreeHashFingerprintResult hash(final Tree<?> tree) {
		hashSize=0;
		
		init(tree);
		final TreeHashFingerprintResult result = new TreeHashFingerprintResult(
				tree, this);

		// Set 01 for root node
		hashHelper(tree.getRoot(), new Fingerprint(0), result);
		// LOGGER.debug("Hashsize " + hashSize);
		uniformsize(result);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(result);
		}
		return result;
	}

	/**
	 * @param result
	 * 
	 */
	private void uniformsize(final TreeHashFingerprintResult result) {
		for (final Fingerprint f : result.values()) {
			f.setLength(hashSize);
		}
	}

	/**
	 * Left adaptative indexing from Patrick. Favors the maintaining of
	 * horizontal distance in the tree. Appends the bit value of the children
	 * position to the father signature. Starts at 01.
	 * 
	 * @param root
	 * @param bitset
	 * @param length
	 * @param result
	 */
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
					1 << bitsRequired);

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

	protected void init(Tree<?> tree) {
		// For extension purpose

	}

	/**
	 * Count the number of bits require to code an integer.
	 * 
	 * @param size
	 * @return the number of bits required to code that integer
	 */
	protected int countBits(TreeNode<?> node) {
		return bitsFor(node.getChildren().size() + 1) + (onemorebit ? 1 : 0);

	}

	protected int bitsFor(int i) {
		return 32 - Integer.numberOfLeadingZeros(i);
	}

	protected int countBits2(TreeNode<?> node) {
		int base = countBits(node);
		int addition = 0;
		if (node.getChildren().size() + 1 > 30) {
			addition = countBits(node);
		}
		return base + addition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#signatureSize()
	 */
	@Override
	public int signatureSize() {
		return hashSize;
	}

	@Override
	public Distance<BitSet> getDistance() {
		return distance;
	}

}
