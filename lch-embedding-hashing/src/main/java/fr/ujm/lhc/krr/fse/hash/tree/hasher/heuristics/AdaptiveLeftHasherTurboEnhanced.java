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
 * Modified version of the {@link AdaptiveLeftHasherEnhanced} that allows to add
 * a bit for encoding children when the bound is too tight.
 * 
 * 
 * 
 * @author Julien Subercaze
 * 
 * 
 * 
 */
public class AdaptiveLeftHasherTurboEnhanced implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4980206146185843607L;
	private final static Logger LOGGER = Logger
			.getLogger(AdaptiveLeftHasherTurboEnhanced.class);
	int hashSize; // Bitset being a vector, we keep this value updated
	boolean sortValues;
	boolean sortChildren;
	private final Distance<BitSet> distance = new HammingDistance();

	/**
	 * @param sortValues
	 * @param sortChildren
	 */
	public AdaptiveLeftHasherTurboEnhanced(final boolean sortValues,
			final boolean sortChildren) {
		super();
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

		final TreeHashFingerprintResult result = new TreeHashFingerprintResult(
				tree, this);

		// Set 0 for root node
		hash(tree.getRoot(), new Fingerprint(0), result);
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
	 * Enhance the left adaptative indexing. Favors the maintaining of
	 * horizontal distance in the tree. Appends the bit value of the children
	 * position to the father signature. Starts at 01.
	 * 
	 * @param root
	 * @param bitset
	 * @param length
	 * @param result
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void hash(final TreeNode<?> node, final Fingerprint bitset,
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
			hash(node.getChildAt(0), b, result);
		} else {
			// Go through the children
			final int size = node.getChildren().size() + 1;
			int bitsRequired = countBits(size);
			// Check if left space is sufficient
			final int freeRoom = 1 << bitsRequired
					- (node.getChildren().size() + 1);
			if ((double) freeRoom / (double) (1 << bitsRequired) < 0.3D) {
				LOGGER.debug("Too compact, add a bit");
				bitsRequired++;
			}

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
				// If l is a good value then it as bitsrequired-1 bits on
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
				// Append the signature
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
				hash((TreeNode<?>) childs.get(i), b, result);
			}
		}

	}

	/**
	 * Count the number of bits require to code an integer.
	 * 
	 * @param size
	 * @return the number of bits required to code that integer
	 */
	private int countBits(int size) {
		int count = 0;
		while (size != 0) {
			size = size >>> 1;
			count++;
		}
		return count;
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
