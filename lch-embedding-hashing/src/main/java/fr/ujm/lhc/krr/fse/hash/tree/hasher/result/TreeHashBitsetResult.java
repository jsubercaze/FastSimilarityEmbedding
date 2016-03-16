/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.result;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Store hash results when hashes are in form of BitSets.
 * <p>
 * Distance is by default the Hamming Distance<br>
 * Distance but can overriden using the {@link #setDistance(Distance)} method.
 * 
 * @author Julien Subercaze
 * 
 *         11 oct. 2013
 * 
 * 
 */
public class TreeHashBitsetResult extends TreeHashResult<java.util.BitSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2480197487351716358L;
	private final static Logger LOGGER = Logger
			.getLogger(TreeHashBitsetResult.class);
	private int hashSize;
	private Distance<BitSet> distance;

	/**
	 * @param tree
	 * @param labelizer
	 */
	public TreeHashBitsetResult(Tree<?> tree, TreeHasher labelizer) {
		super(tree, labelizer);
		hashedLookupTable = new LinkedHashMap<TreeNode<?>, BitSet>();
		distance = new HammingDistance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.result.TreeHashResult#size()
	 */
	@Override
	public int size() {

		return hashedLookupTable.size();
	}

	public int getHashSize() {
		return hashSize;
	}

	public void setHashSize(int hashSize) {
		this.hashSize = hashSize;
	}

	public String viewHash(BitSet b) {
		String signature = "";
		for (int i = 0; i < hashSize; i++) {
			if (b.get(i)) {
				signature += "1";
			} else {
				signature += "0";
			}
		}
		return signature;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<TreeNode<?>> keys = hashedLookupTable.keySet();
		LOGGER.debug(keys);
		for (TreeNode<?> key : keys) {

			sb.append(key.getNodeValue() + " : "
					+ viewHash(hashedLookupTable.get(key)) + "\n");

			sb.append(key + " : " + viewHash(hashedLookupTable.get(key)) + "\n");
		}
		return sb.toString();
	}

	/**
	 * Sets the distance measure for the hashes
	 * 
	 * @param distance
	 */
	public void setDistance(Distance<BitSet> distance) {
		this.distance = distance;
	}

	/* (non-Javadoc)
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.result.TreeHashResult#distance(fr.tse.ujm.lt2c.satin.lsh.tree.TreeNode, fr.tse.ujm.lt2c.satin.lsh.tree.TreeNode)
	 */
	@Override
	public int distance(TreeNode<?> node1, TreeNode<?> node2) {
		BitSet bitset1 = hashedLookupTable.get(node1);
		BitSet bitset2 = hashedLookupTable.get(node2);
		return distance.distance(bitset1, bitset2);
	}

	/* (non-Javadoc)
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.result.TreeHashResult#putHash(fr.tse.ujm.lt2c.satin.lsh.tree.TreeNode, java.lang.Object)
	 */
	@Override
	public void putHash(TreeNode<?> node1, BitSet hash) {
		// BitSet b = (BitSet) hash;
		hashedLookupTable.put(node1, hash);

	}

}
