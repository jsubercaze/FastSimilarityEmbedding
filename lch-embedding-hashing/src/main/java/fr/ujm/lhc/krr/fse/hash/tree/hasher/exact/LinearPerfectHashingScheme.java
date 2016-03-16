/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.exact;

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
 * Perfect hashing scheme that preserves Hamming distance.
 * 
 * Linear version of the algorithm based on the fact that the size is known in
 * advance.
 * 
 * @author Julien Subercaze
 * 
 *         19 oct. 2013
 * 
 * 
 */
public final class LinearPerfectHashingScheme implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8029039790720787314L;

	private int signaturesize = 0;

	private final static Logger LOGGER = Logger
			.getLogger(LinearPerfectHashingScheme.class);

	private final Distance<BitSet> distance = new HammingDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	@Override
	public final TreeHashFingerprintResult hash(final Tree<?> tree) {
		final TreeHashFingerprintResult result = new TreeHashFingerprintResult(
				tree, this);
		tree.prepare();
		signaturesize = tree.nodes().size()-1;
		// LOGGER.trace("Signature size"+signaturesize);
		hash(tree.getRoot(), new Fingerprint(0), 0, result);
		// Size of the hash will be equal to the number of nodes
		return result;

	}

	/**
	 * Compute the hash for a given node
	 * 
	 * @param childAt
	 * @param result
	 * @param b
	 * @param i
	 * @return
	 */
	private final void hash(final TreeNode<?> node,
			final Fingerprint signature, int length,
			final TreeHashFingerprintResult result) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("\n");
			LOGGER.trace("-------------" + node.getNodeValue()
					+ "---------------");
			LOGGER.trace("-------------------------------");
			LOGGER.trace("Hashing node " + node.getNodeValue() + " given hash "
					+ signature + " current length " + length);
			LOGGER.trace(result);
		}
		final int childrenSize = node.getChildren().size();
		//
		if (childrenSize == 0) {
			signature.set(length, signaturesize);
			signature.setLength(signaturesize);
			result.putHash(node, signature);
			LOGGER.debug("Returning ");
			LOGGER.debug("Result at this point \n" + result);
			return;
		}
		// Compute the fingerprint to append
	//	final Fingerprint adjustment = new Fingerprint(childrenSize);
		// LOGGER.debug("Signature before child appending " + signature);
		signature.set(length, signaturesize + childrenSize);
	//	adjustment.set(0, childrenSize);
		signature.setLength(length + childrenSize);
		final int oldlength = length;
		length += childrenSize;
		// LOGGER.debug("Signature after child appending " + signature);
		// Similarly, the Array of Fingerprint to append to these branches
		// LOGGER.debug("Hashing children of  " + node.getNodeValue());
		for (int i = 0; i < childrenSize; i++) {
			final Fingerprint b = (Fingerprint) signature.clone();
			// Discard one bit
			// if (childrenSize > 1)
			b.set(oldlength + i, false);
			hash(node.getChildAt(i), b, length, result);
			LOGGER.debug("Computed Branches for " + result);
			
		}
		signature.set(oldlength, signaturesize);
		signature.setLength(signaturesize);
		result.putHash(node, signature);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#signatureSize()
	 */
	@Override
	public int signatureSize() {

		return signaturesize;
	}

	@Override
	public Distance<BitSet> getDistance() {
		return distance;
	}

	

}
