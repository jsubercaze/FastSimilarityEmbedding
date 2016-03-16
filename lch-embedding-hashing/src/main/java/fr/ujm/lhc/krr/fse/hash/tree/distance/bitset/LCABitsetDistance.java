/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.distance.bitset;

import java.util.BitSet;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.ExactPathHasher;


/**
 * Compute the Lowest Common Ancestor Depth between two bitsets.
 * <p>
 * Uses two {@code andNot}. Methods are mechanically sympathical for the
 * processor. Intrinsincs are used as much as possible.
 * 
 * @author Julien Subercaze
 * 
 *         15 oct. 2013
 * 
 * 
 */
public class LCABitsetDistance implements Distance<BitSet> {

	private final static Logger LOGGER = Logger
			.getLogger(ExactPathHasher.class);

	int bitsPerDepth;

	/**
	 * @param bitsPerDepth
	 *            The number of bits to represent the hash of a single node
	 */
	public LCABitsetDistance(int bitsPerDepth) {
		super();
		LOGGER.debug("Instatiating with " + bitsPerDepth);
		this.bitsPerDepth = bitsPerDepth;
	}

	/**
	 * Return the depth of the Lowest Common Ancestor between two given bitsets.
	 * Uses two {@code andNot}.
	 * 
	 * @param bitset1
	 * @param bitset2
	 * @param i 
	 * @param bitsRequired
	 *            bits required for a node
	 * @return the depth of the LCA
	 */
	private long depthLCA(BitSet bitset1, BitSet bitset2, int maxdepth) {
		BitSet copy1 = (BitSet) bitset1.clone();
		BitSet copy3 = (BitSet) bitset1.clone();
		BitSet copy2 = (BitSet) bitset2.clone();
		BitSet copy4 = (BitSet) bitset1.clone();
		LOGGER.debug(copy1);
		LOGGER.debug(copy2);
		int lgt = (maxdepth) * bitsPerDepth;
		if (copy3.length() > lgt)
			copy3.set(lgt, copy3.length(), false);
		LOGGER.debug("cp 1 " + copy1);
		copy1.andNot(copy4);
		if (copy4.length()>lgt)
			copy4.set(lgt, copy4.length(), false);
		LOGGER.debug("cp 2 " + copy2);
		copy2.andNot(copy3);
		int pos1 = copy1.nextSetBit(0);
		int pos2 = copy2.nextSetBit(0);
		LOGGER.debug(copy1);
		LOGGER.debug(copy2);
		LOGGER.debug("Position " + pos1);
		LOGGER.debug("Position " + pos2);
		BitSet b = new BitSet();
		b.set(Math.max(pos1, pos2));
		LOGGER.debug("Depth of LCA = " + (computeDepth(b) - 1));
		return (computeDepth(b) - 1);
	}

	/**
	 * Distance as hops in the tree
	 * 
	 * @param bitset1
	 *            The first {@link BitSet}
	 * @param bitset2
	 *            The second {@link BitSet}
	 * @param depth1
	 *            Depth of the first {@link BitSet}
	 * @param depth2
	 *            Depth of the second {@link BitSet}
	 * @return The distance between the two nodes identified by their
	 *         {@link BitSet} signature
	 */
	private int distance(BitSet bitset1, BitSet bitset2, int depth1, int depth2) {
		int LCA = (int) depthLCA(bitset1, bitset2, Math.min(depth1, depth2));
		LOGGER.debug("LCA Depth " + LCA);
		LOGGER.debug("Distance " + (depth1 + depth2 - (2 * LCA)));
		return depth1 + depth2 - (2 * LCA);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.distance.bitset.Distance#distance(java
	 * .lang.Object, java.lang.Object)
	 */
	public int distance(BitSet hash1, BitSet hash2) {
		int depth1 = computeDepth(hash1);
		int depth2 = computeDepth(hash2);
		LOGGER.debug("depth1 " + depth1);
		LOGGER.debug("depth2 " + depth2);
		return distance(hash1, hash2, depth1, depth2);
	}

	/**
	 * @param hash1
	 * @return
	 */
	private int computeDepth(BitSet hash1) {
		return (int) Math.ceil((double) hash1.length() / bitsPerDepth);
	}
}
