package fr.ujm.lhc.krr.fse.hash.tree.hasher.exact;

import java.util.BitSet;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.LCABitsetDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashBitsetResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Computes a binary version of the path, uncompressed. The number of bits for
 * each node is the number of bits required to code the largest sibship.
 * Therefore the coding is awfully uncompressed
 * <p>
 * For instance the following tree
 * 
 * <pre>
 *      A
 *     /|\
 *    B D E
 *    |
 *    C
 * </pre>
 * 
 * has a largest sibship [B,D,E] that requires at least 2 bits for encoding.
 * Therefore each node will require 2 bits for encoding.<br>
 * Consequently the hash will be of length : argmax(depth(N)) * (bits for
 * encoding) = 3 *2 = 6 bits.<br>
 * <p>
 * Each node is index by its path from the root i.e. :
 * <ul>
 * <li>A : 010000</li>
 * <li>B : 010100</li>
 * <li>C : 010101</li>
 * <li>D : 011000</li>
 * <li>E : 011100</li>
 * <ul>
 * <p>
 * Distance between the hashed can be computed by the
 * {@link LCABitsetDistance#distance(BitSet, BitSet, int, int)}.
 * 
 * @author Julien Subercaze
 * 
 *         10 oct. 2013
 * 
 * 
 */
public class ExactPathHasher implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4469780327874728402L;

	private final static Logger LOGGER = Logger
			.getLogger(ExactPathHasher.class);

	private Distance<BitSet> distance;

	int maxChilds;// Maximum number of children
	int maxDepth;// Depth of the tree
	int bitsRequired;// Compute number of bits required per node

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	public TreeHashBitsetResult hash(Tree<?> tree) {
		tree.prepare();
		TreeHashBitsetResult result = new TreeHashBitsetResult(tree, this);
		countMaxChilds(tree.getRoot(), 0);
		LOGGER.debug("Max childs : " + maxChilds);
		bitsRequired = countBits(maxChilds);
		LOGGER.debug("Bits required " + bitsRequired);
		result.setHashSize(bitsRequired * maxDepth);
		// Set the distance depending on the current bitsrequiredvalue
		distance = new LCABitsetDistance(bitsRequired);
		// Initialize Bitset for root
		BitSet signature = new BitSet(bitsRequired * maxDepth);
		// Current length of the bitset
		int length = 0;
		LOGGER.debug("Before First signature " + signature);
		signature.set(bitsRequired - 1);
		// appendValue(signature, length, 1, bitsRequired);
		length = bitsRequired - 1;
		LOGGER.debug("First signature " + signature);
		hashNode(tree.getRoot(), bitsRequired, signature, result, length);
		return result;
	}

	/**
	 * The a value for the bitset, respecting allocated bits size for an
	 * integer. Manage padding.
	 * 
	 * @param signature
	 *            signature where the value will be appended
	 * @param length
	 *            current length of the signature
	 * @param value
	 *            value to be set
	 * @param bitsRequired
	 *            bits allowed for integer coding
	 */
	private void appendValue(BitSet signature, int length, int value,
			int bitsRequired) {
		LOGGER.debug("Value to append " + value + " after length " + length);
		for (int j = bitsRequired - 1; j >= 0; j--) {
			LOGGER.debug("j :" + j);
			int gros = value;
			LOGGER.debug(1 << j);
			if ((gros & (1 << j)) > 0) {
				int bit = length + (bitsRequired - j);
				LOGGER.debug("Setting bit " + bit);
				signature.set(bit);
			}
		}
	}

	/**
	 * Hash a node by appending its child number to the current signature.
	 * Padding with zeros if required
	 * 
	 * @param node
	 *            Node to be hashed
	 * @param bits
	 *            Required bits required to code the integer
	 * @param signature
	 *            Signature of the parent
	 * @param length
	 *            length of the parent signature
	 * @param result
	 *            the new signature, i.e. the parent signature with the value
	 *            appended as binary
	 */
	private final void hashNode(final TreeNode<?> node, final int bitsRequired,
			final BitSet signature, final TreeHashBitsetResult result,
			final int length) {
		LOGGER.debug("");
		LOGGER.debug("----------------------------------------------------");
		LOGGER.debug("Hashing node " + node.getNodeValue() + " signature "
				+ signature);
		result.putHash(node, signature);
		if (node.getChildren().size() == 0)
			return;
		int i = 1;
		for (TreeNode<?> child : node.getChildren()) {
			BitSet childSignature = (BitSet) signature.clone();
			appendValue(childSignature, length, i, bitsRequired);
			LOGGER.debug("Signature of child " + child.getNodeValue() + " : "
					+ childSignature);
			hashNode(child, bitsRequired, childSignature, result, length
					+ bitsRequired);
			LOGGER.debug("Back to node " + node.getNodeValue());
			i++;
		}
	}

	/**
	 * Count the maximum number of childs for a tree starting at treeNode. Set
	 * the maximal depth.
	 * 
	 * @param treeNode
	 * 
	 */
	private void countMaxChilds(TreeNode<?> treeNode, int depth) {
		depth++;
		// Update depth
		maxDepth = depth > maxDepth ? depth : maxDepth;
		maxChilds = treeNode.getChildren().size() > maxChilds ? treeNode
				.getChildren().size() : maxChilds;
		for (TreeNode<?> t : treeNode.getChildren()) {
			countMaxChilds(t, depth);
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

	/**
	 * 
	 * @return the number of bits required to encode a node
	 */
	public int getBitsRequired() {
		return bitsRequired;
	}

	/* (non-Javadoc)
	 * @see fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#signatureSize()
	 */
	@Override
	public int signatureSize() {
		return maxDepth * bitsRequired;
	}

	public Distance<BitSet> getDistance() {
		return distance;
	}

}
