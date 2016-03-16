/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.distance.bitset;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Computes the hamming distance between two bitsets.
 * 
 * @author Julien Subercaze
 * 
 *         15 oct. 2013
 * 
 * 
 */
public class HammingDistance implements Distance<BitSet>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 942826630758274012L;

	@Override
	public int distance(final BitSet bitset1, final BitSet bitset2) {
		final BitSet res = (BitSet) bitset1.clone();
		res.xor(bitset2);

		return res.cardinality();
	}
}
