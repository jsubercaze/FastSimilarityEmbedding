package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.BitSet;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Efficient implementation of the Adaptive Left Hasher by P. Bamba. Uses some
 * simple bit twiddling to increase performance.
 * 
 * 
 * 
 * @author Julien Subercaze
 * 
 *         15 oct. 2013
 * 
 * 
 */
public class AdaptiveLeftHasher implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943370648728240253L;

	private final static Logger LOGGER = Logger
			.getLogger(AdaptiveLeftHasher.class);

	int hashSize = 1; // Bitset being a vector, we keep this value updated

	private final Distance<BitSet> distance = new HammingDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	public TreeHashFingerprintResult hash(Tree<?> tree) {

		TreeHashFingerprintResult result = new TreeHashFingerprintResult(tree,
				this);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Hashsize " + hashSize);
		hash(tree.getRoot(), new Fingerprint(0), result);
		uniformsize(result);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Hashsize " + hashSize);
		return result;
	}

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
	private void hash(final TreeNode<?> node, final Fingerprint bitset,
			final TreeHashFingerprintResult result) {
		result.putHash(node, bitset);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("After inserting :" + result);
		if (node.getChildren().size() == 0)
			return;

		// Go through the children
		int size = node.getChildren().size() + 1;
		int bitsRequired = countBits(size);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(node.getNodeValue() + " " + bitset.toString()
					+ " | children " + node.getChildren().size() + " bits : "
					+ bitsRequired);
		// Keep updated the length of the hash
		hashSize = bitset.getLength() > hashSize ? bitset.getLength()
				: hashSize;
		// Iterate over children to compute signatures and recursive calls
		for (int i = 0; i < size - 1; i++) {
			int k = i + 1;
			// Copy bitset for signature
			final Fingerprint b = new Fingerprint(bitset);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(node.getChildAt(i).getNodeValue() + " appending "
						+ k);
			// Need to append i as binary to the current bitset - Reverse walk
			// in the
			// integer

			for (int j = bitsRequired - 1; j >= 0; j--) {
				int gros = k;
				// LOGGER.debug("j :" + j);
				if ((gros & (1 << j)) > 0) {
					// LOGGER.debug("Setting");
					b.set(bitset.getLength() + (bitsRequired - j) - 1);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(node.getChildAt(i).getNodeValue() + " after " + b);
				LOGGER.debug("Enter");
			}

			// Go to the child
			hash(node.getChildAt(i), b, result);
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

	public Distance<BitSet> getDistance() {
		return distance;
	}

}
