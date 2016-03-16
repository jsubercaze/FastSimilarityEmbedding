package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Implementation of the preorder graycode heuristic by Chen et al in :
 * <p>
 * Chen, W. K., & Stallmann, M. F. (1995). On embedding binary trees into
 * hypercubes. Journal of Parallel and Distributed Computing, 24(2), 132-138.
 * </p>
 * 
 * The class generates the graycode for the number of bits required to encode
 * the sets of node in the tree, i.e. log2(|V|). Code values are assigned to the
 * tree nodes using a pre order traversal.
 * 
 * 
 * @author Julien Subercaze
 * 
 *        
 * 
 * 
 */
public class PreOrderGrayCodeHasher implements TreeHasher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2606476881790351744L;
	private final static Logger LOGGER = Logger
			.getLogger(PreOrderGrayCodeHasher.class);
	int hashSize;
	private final Distance<BitSet> distance = new HammingDistance();

	public PreOrderGrayCodeHasher() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.tse.ujm.lt2c.satin.lsh.tree.hasher.TreeHasher#hash(fr.tse.ujm.lt2c
	 * .satin.lsh.tree.Tree)
	 */
	public TreeHashFingerprintResult hash(Tree<?> tree) {
		this.hashSize = countBits(tree.nodes().size());
		TreeHashFingerprintResult result = new TreeHashFingerprintResult(tree,
				this);
		this.hashSize = countBits(tree.nodes().size());
		LOGGER.debug("number of bits required " + hashSize);
		LOGGER.debug("Generating gray codes");
		List<Fingerprint> grayCode = generateGrayCode();
		LOGGER.debug("Assign codes");
		Iterator<Fingerprint> grayIterator = grayCode.iterator();
		for (TreeNode<?> node : tree.preOrderTraversal()) {
			result.putHash(node, grayIterator.next());
		}
		return result;
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

	/**
	 * Generates the gray code for the hash length
	 * 
	 * @return list of fingerprints constituing the gray code for the given tree
	 */
	public List<Fingerprint> generateGrayCode() {
		List<Fingerprint> printList = new ArrayList<>(1 << hashSize);
		//
		boolean[] s = new boolean[hashSize];
		// Arrays.fill(s, true);
		int i, j, m, g;
		for (i = 0; i < 1 << hashSize; i++) {
			g = i ^ (i / 2);
			m = 1 << (hashSize - 1);
			for (j = 0; j < hashSize; ++j) {
				s[j] = (g & m) == 0 ? false : true;
				m >>= 1;
			}
			printList.add(new Fingerprint(s));
		}
		return printList;
	}

}
