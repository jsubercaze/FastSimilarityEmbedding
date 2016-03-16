/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.exact;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.LCABitsetDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Perfect hashing scheme that preserves Hamming distance. It makes use of an
 * 
 * 
 * <pre>
 *      A
 *     /|\
 *    B D E
 *    |
 *    C
 * </pre>
 * 
 * 
 * <p>
 * Each node is index by its path from the root i.e. :
 * <ul>
 * <li>A : 1000</li>
 * <li>B : 11001</li>
 * <li>C : 11000</li>
 * <li>D : 10101</li>
 * <li>E : 10011</li>
 * <ul>
 * <p>
 * Distance between the hashed can be computed by the
 * {@link LCABitsetDistance#distance(BitSet, BitSet, int, int)}.
 * 
 * @author Julien Subercaze
 * 
 *         19 oct. 2013
 * 
 * 
 */
public final class PerfectHashingScheme implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9045425400279802849L;
	private int signaturesize = 0;
	private final static Logger LOGGER = Logger
			.getLogger(PerfectHashingScheme.class);

	private final Distance<BitSet> distance = new HammingDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	public final TreeHashFingerprintResult hash(final Tree<?> tree) {
		TreeHashFingerprintResult result = new TreeHashFingerprintResult(tree,
				this);
		hash(tree.getRoot(), new Fingerprint(0), 0, result);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(result);
		}
		System.out.println(result.size());
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
	private final ComputedBranch hash(final TreeNode<?> node,
			final Fingerprint signature, int length,
			final TreeHashFingerprintResult result) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("\n");
			LOGGER.trace("-------------" + node.getNodeValue()
					+ "---------------");
			LOGGER.trace("-------------------------------");
			LOGGER.trace("Hashing node " + node.getNodeValue() + " given hash "
					+ signature + " current length " + length);
		}
		int childrenSize = node.getChildren().size();
		signaturesize = signature.getLength() > signaturesize ? signature
				.length() : signaturesize;
		// LOGGER.debug("Children size" + childrenSize);
		if (childrenSize == 0) {
			ComputedBranch res = new ComputedBranch();
			res.addNode(node);
			result.putHash(node, signature);
			// LOGGER.debug("Returning " + res);
			// LOGGER.debug("Result at this point \n" + result);
			return res;
		}
		// Compute the fingerprint to append
		Fingerprint adjustment = new Fingerprint(childrenSize);

		// LOGGER.debug("Signature before child appending " + signature);
		signature.set(length, length + childrenSize);
		adjustment.set(0, childrenSize);
		signature.setLength(length + childrenSize);
		int oldlength = length;
		length += childrenSize;
		// LOGGER.debug("Signature after child appending " + signature);
		result.putHash(node, signature);
		ComputedBranch[] branches = new ComputedBranch[childrenSize];
		// Similarly, the Array of Fingerprint to append to these branches
		Fingerprint[] prints = new Fingerprint[childrenSize];
		// LOGGER.debug("Hashing children of  " + node.getNodeValue());
		for (int i = 0; i < childrenSize; i++) {
			Fingerprint b = (Fingerprint) signature.clone();
			// Discard one bit
			// if (childrenSize > 1)
			b.set(oldlength + i, false);
			// LOGGER.debug("Child " + node.getChildAt(i).getNodeValue() + " : "
			// + b);
			branches[i] = hash(node.getChildAt(i), b, length, result);
			// LOGGER.debug("-------------Back to " + node.getNodeValue());
			// Update the current signature of the father so that it would not
			// be required to later update the following childs
			// LOGGER.debug("Received cb from child " + branches[i]);
			if (branches[i] == null || branches[i].getFingerPrint() == null)
				continue;
			// LOGGER.debug("Signature of " + node.getNodeValue()
			// + " before appending " + signature);
			signature.append(branches[i].getFingerPrint());
			length += branches[i].getFingerPrint().getLength();
			// LOGGER.debug("Signature of " + node.getNodeValue()
			// + " after appending " + signature);
			// Update adjustment
			// LOGGER.debug("Adjustment before appending " + adjustment);

			adjustment.append(branches[i].getFingerPrint());
			// LOGGER.debug("Adjustment after appending " + adjustment);
			// Set the computed branches and fingerprints accordingly
			for (int j = 0; j < i; j++) {
				if (prints[j] == null)
					prints[j] = new Fingerprint(0);
				prints[j].append(branches[i].getFingerPrint());
			}
			// LOGGER.debug("Computed Branches for " + node.getNodeValue()
			// + " at " + i + ": " + Arrays.toString(branches));
			// LOGGER.debug("Prints for the nodes " + Arrays.toString(prints));
		}
		// When all children are computed, update the fingerprints of the
		// computed branches by appending their prints
		// signature.append(adjustment);
		ComputedBranch toreturn = new ComputedBranch();
		toreturn.setFingerPrint(adjustment);
		toreturn.addNode(node);
		for (int i = 0; i < node.getChildren().size(); i++) {
			ComputedBranch branch = branches[i];
			if (branch == null)
				continue;
			// LOGGER.debug("Updating branch " + branch);
			List<TreeNode<?>> nodes = branch.getComputedNodes();
			if (i < node.getChildren().size() - 1) {
				for (TreeNode<?> child : nodes) {
					Fingerprint b = result.getHashForNode(child);
					Fingerprint toAppend = prints[i];
					if (!(b == null || toAppend == null)) {
						// LOGGER.debug("Before update hash for "
						// + child.getNodeValue() + " value set " + b);
						b.append(toAppend);
						// LOGGER.debug("Updated hash for " +
						// child.getNodeValue()
						// + " value set " + b);
					}/*
					 * else { //LOGGER.debug("Print null pour " +
					 * child.hashCode()); //LOGGER.debug(result.getKeySet());
					 * //LOGGER.debug(result.getHashForNode(child)); }
					 */

				}
				prints[i] = null;
			}
			// prints=null;
			// Merge all branches into the link one
			toreturn.mergeNodes(branches[i]);
			branches[i].clear();
			branches[i] = null;
		}
		branches = null;
		LOGGER.debug("Returning " + toreturn);
		// LOGGER.debug("Result at this point \n" + result);
		return toreturn;
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

	public Distance<BitSet> getDistance() {
		return distance;
	}

}

/**
 * 
 * @author Julien Subercaze
 * 
 *         20 oct. 2013
 * 
 * 
 */
final class ComputedBranch {

	private List<TreeNode<?>> computedNodes;
	private Fingerprint fingerPrint;

	/**
	 * @param computedNodes
	 * @param appendedSignature
	 */
	public ComputedBranch() {
		super();
		this.computedNodes = new ArrayList<TreeNode<?>>();
		this.fingerPrint = new Fingerprint(0);
	}

	public void clear() {
		this.fingerPrint.clear();
		this.fingerPrint = null;
		this.computedNodes.clear();
		this.computedNodes = null;

	}

	/**
	 * @param computedNodes
	 * @param fingerPrint
	 */
	public ComputedBranch(List<TreeNode<?>> computedNodes,
			Fingerprint fingerPrint) {
		super();
		this.computedNodes = computedNodes;
		this.fingerPrint = fingerPrint;
	}

	/**
	 * Copy constructor
	 * 
	 * @param tocopy
	 */
	public ComputedBranch(ComputedBranch tocopy) {
		this(tocopy.computedNodes, tocopy.fingerPrint);
	}

	/**
	 * Merge only the nodes of the computed branch, leaving the fingerprint
	 * unchanged
	 * 
	 * @param computedBranch
	 */
	public void mergeNodes(final ComputedBranch computedBranch) {
		this.computedNodes.addAll(computedBranch.getComputedNodes());

	}

	public Fingerprint getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(Fingerprint fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public List<TreeNode<?>> getComputedNodes() {
		return computedNodes;
	}

	public void setComputedNodes(final List<TreeNode<?>> computedNodes) {
		this.computedNodes = computedNodes;
	}

	public void merge(final ComputedBranch cb) {
		this.computedNodes.addAll(cb.getComputedNodes());
		this.fingerPrint.append(cb.getFingerPrint());
	}

	public boolean isEmpty() {
		return computedNodes.size() == 0;
	}

	public void addNode(TreeNode<?> node) {
		computedNodes.add(node);
	}

	@Override
	public String toString() {
		return "ComputedBranch [computedNodes=" + computedNodes
				+ ", fingerPrint=" + fingerPrint + "]";
	}

};
