/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.result;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
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
public class TreeHashFingerprintResult extends TreeHashResult<Fingerprint>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7218230635302845966L;

	private final static Logger LOGGER = Logger
			.getLogger(TreeHashFingerprintResult.class);

	private Distance<BitSet> distance;

	public int getFingerprintLength() {
		return hashedLookupTable.entrySet().iterator().next().getValue()
				.getLength();
	}

	/**
	 * @param tree
	 * @param labelizer
	 */
	public TreeHashFingerprintResult(final Tree<?> tree,
			final TreeHasher labelizer) {
		super(tree, labelizer);
		hashedLookupTable = new HashMap<TreeNode<?>, Fingerprint>();
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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final Set<TreeNode<?>> keys = hashedLookupTable.keySet();
		tree.prepare();

		for (final TreeNode<?> key : keys) {
			sb.append(key.getNodeValue() + " : " + hashedLookupTable.get(key)
					+ "\n");
		}
		return sb.toString();
	}

	public Set<TreeNode<?>> getKeySet() {
		return hashedLookupTable.keySet();
	}

	/**
	 * Sets the distance measure for the hashes
	 * 
	 * @param distance
	 */
	public void setDistance(final Distance<BitSet> distance) {
		this.distance = distance;
	}

	public Distance<BitSet> getDistance() {
		return distance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.result.TreeHashResult#distance(
	 * fr.tse.ujm.lt2c.satin.lsh.tree.TreeNode,
	 * fr.tse.ujm.lt2c.satin.lsh.tree.TreeNode)
	 */
	@Override
	public int distance(final TreeNode<?> node1, final TreeNode<?> node2) {
		final BitSet bitset1 = hashedLookupTable.get(node1);
		LOGGER.debug("Bitset 1 " + bitset1);
		final BitSet bitset2 = hashedLookupTable.get(node2);
		LOGGER.debug("Bitset 2 " + bitset2);
		return distance.distance(bitset1, bitset2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.result.TreeHashResult#putHash(fr
	 * .tse.ujm.lt2c.satin.lsh.tree.TreeNode, java.lang.Object)
	 */
	@Override
	public void putHash(final TreeNode<?> node1, final Fingerprint hash) {
		hashedLookupTable.put(node1, hash);

	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Fingerprint> getConvertedMap() {
		Set<TreeNode<?>> keySet = this.hashedLookupTable.keySet();
		Map<Integer, Fingerprint> map = new HashMap<>(keySet.size());
		for (TreeNode<?> treeNode : keySet) {
			Integer val = Integer.parseInt(((TreeNode<String>) treeNode)
					.getNodeValue());
			map.put(val, this.hashedLookupTable.get(treeNode));
		}
		return map;
	}

	public Collection<Fingerprint> values() {
		return hashedLookupTable.values();
	}

	public boolean contains(String value) {
		TreeNode<String> node = new TreeNode<String>(value);
		return hashedLookupTable.keySet().contains(node);
	}

}
