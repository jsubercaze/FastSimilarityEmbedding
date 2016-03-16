package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.BitSet;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashBitsetResult;
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
 * 
 * 
 * 
 */
public class AdaptiveLeftHasherV2 implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6592283610159417002L;
	private final static Logger LOGGER = Logger
			.getLogger(AdaptiveLeftHasherV2.class);
	int hashSize; // Bitset being a vector, we keep this value updated
	private final Distance<BitSet> distance = new HammingDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	@Override
	public TreeHashBitsetResult hash(final Tree<?> tree) {
		final TreeHashBitsetResult result = new TreeHashBitsetResult(tree, this);
		final BitSet bitset = new BitSet();
		bitset.set(1); // Set 01 for root node
		final int length = 2;
		result.setHashSize(length);
		hash(tree.getRoot(), bitset, length, result);
		result.setHashSize(hashSize);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Hashsize " + hashSize);
		}
		return result;
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
	private void hash(final TreeNode<?> node, final BitSet bitset,
			final int length, final TreeHashBitsetResult result) {
		result.putHash(node, bitset);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("After inserting :" + result);
		}
		if (node.getChildren().size() == 0) {
			return;
		}

		// Go through the children
		if (node.getChildren().size() == 1) {
			// int lgt = length;
			final BitSet b = (BitSet) bitset.clone();
			b.set(length);
			final int bitsRequired = 1;
			hashSize = length + bitsRequired > hashSize ? length + bitsRequired
					: hashSize;
			hash(node.getChildAt(0), b, length + bitsRequired, result);
		} else {

			final int size = node.getChildren().size() + 1;
			//
			final int bitsRequired = countBits(size);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(node.getNodeValue() + " " + bitset.toString()
						+ " | children " + node.getChildren().size()
						+ " bits : " + bitsRequired);
			}
			// Keep updated the length of the hash
			hashSize = length + bitsRequired > hashSize ? length + bitsRequired
					: hashSize;
			result.setHashSize(hashSize);
			// Iterate over children to compute signatures and recursive calls
			for (int i = 0; i < size - 1; i++) {
				final int k = i + 1;
				// Copy bitset for signature
				final BitSet b = (BitSet) bitset.clone();
				// LOGGER.debug(node.getChildAt(i).getNodeValue()+ " appending "
				// +
				// k);
				// Need to append i as binary to the current bitset - Reverse
				// walk
				// in the
				// integer
				// LOGGER.debug("length " + length);

				for (int j = bitsRequired - 1; j >= 0; j--) {
					final int gros = k;
					// LOGGER.debug("j :" + j);
					if ((gros & 1 << j) > 0) {
						// LOGGER.debug("Setting");
						b.set(length + bitsRequired - j - 1);

					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(node.getChildAt(i).getNodeValue() + " after "
							+ b);
				}
				LOGGER.debug("Enter");

				// Go to the child
				hash(node.getChildAt(i), b, length + bitsRequired, result);
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
