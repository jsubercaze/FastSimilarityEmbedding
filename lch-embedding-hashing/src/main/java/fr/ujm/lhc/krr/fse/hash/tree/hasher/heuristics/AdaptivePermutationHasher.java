package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.BitSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashBitsetResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Inspired by the AdaptiveLeftIndexer. Distance between siblings is exact. Code
 * is way larger than adapativeLeft. Two zeroes are consecutively moved between
 * the appended bits for each node.
 * 
 * @author Julien Subercaze
 * 
 *         15 oct. 2013
 * 
 * 
 */
public class AdaptivePermutationHasher implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger
			.getLogger(AdaptivePermutationHasher.class);
	int hashSize; // Bitset being a vector, we keep this value updated
	private final Distance<BitSet> distance = new HammingDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	public TreeHashBitsetResult hash(Tree<?> tree) {

		TreeHashBitsetResult result = new TreeHashBitsetResult(tree, this);
		BitSet bitset = new BitSet();
		bitset.set(0); // Set 1 for root node
		int length = 1;
		hash(tree.getRoot(), bitset, length, result);
		result.setHashSize(hashSize);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Hashsize " + hashSize);
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
	private void hash(TreeNode<?> node, BitSet bitset, int length,
			TreeHashBitsetResult result) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Hashing node " + node.getNodeValue());
		result.putHash(node, bitset);
		if (node.getChildren().size() == 0)
			return;

		// Go through the children
		int size = node.getChildren().size();
		if (size == 1) {
			BitSet b = (BitSet) bitset.clone();
			b.set(length);
			length = length + 1;
			hash(node.getChildAt(0), b, length, result);
		} else {

			int bitsRequired = size;
			int newlength = length + bitsRequired;
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(node.getNodeValue() + " " + bitset.toString()
						+ " | children " + node.getChildren().size()
						+ " bits : " + bitsRequired);
			// Keep updated the length of the hash
			hashSize = length + bitsRequired + 1 > hashSize ? length
					+ bitsRequired + 1 : hashSize;

			for (int i = 0; i < size; i++) {

				// Copy bitset for signature
				BitSet b = (BitSet) bitset.clone();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("B before " + b + " appending " + i);
				// Need to append i as binary to the current bitset - Reverse
				// walk
				// in the
				// integer
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("length " + length);
				int k = i;
				for (int j = 0; j < bitsRequired; j++) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("j :" + j);
					if (j == k) {
						b.set(length + j);
						// b.set(length + j + 1);
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Hash for "
							+ node.getChildAt(i).getNodeValue() + " is " + b);
					LOGGER.debug("Enter");
				}
				if (LOGGER.getLevel() == Level.DEBUG) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(System.in));
					try {
						br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Go to the child
				hash(node.getChildAt(i), b, newlength, result);
			}
		}
	}

	/* (non-Javadoc)
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
