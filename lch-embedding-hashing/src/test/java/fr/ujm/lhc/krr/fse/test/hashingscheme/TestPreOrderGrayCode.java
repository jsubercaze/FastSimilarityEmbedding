/**
 * 
 */
package fr.ujm.lhc.krr.fse.test.hashingscheme;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.PreOrderGrayCodeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Test the {@link HammingDistance} on the following tree with the {@link AdaptiveLeftHasher}: 
 *                     A 
 *                     | 
 *                     B 
 *                   / | \ \ 
 *                   C D  E F 
 *                   |      | 
 *                   G      H
 *                   
 *   Which is encoded as :
 * 
 *                          01000000
 *                             | 
 *                            010100000
 *                   /         |       \       \ 
 *               010100100   010101000 010101100  010110000 
 *                   |                         | 
 *               010100101                    010110001
 *   
 * @author Julien Subercaze
 *
 * 18 oct. 2013
 *
 * 
 */
public class TestPreOrderGrayCode {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(TestPreOrderGrayCode.class);
	@Test
	public void test() {
		TreeNode<String> ta = new TreeNode<String>("a");
		Tree<String> tree = new Tree<String>(ta);
		TreeNode<String> tb = new TreeNode<String>("b");
		TreeNode<String> tc = new TreeNode<String>("c");
		TreeNode<String> td = new TreeNode<String>("d");
		TreeNode<String> te = new TreeNode<String>("e");
		TreeNode<String> tf = new TreeNode<String>("f");
		TreeNode<String> tg = new TreeNode<String>("g");
		TreeNode<String> th = new TreeNode<String>("g");
		ta.addChild(tb);
		tb.addChild(tc);
		tb.addChild(td);
		tb.addChild(te);
		tb.addChild(tf);
		tf.addChild(th);
		tc.addChild(tg);
		// Compute Hash of the tree
		PreOrderGrayCodeHasher hasher = new PreOrderGrayCodeHasher();
		TreeHashFingerprintResult res = hasher.hash(tree);
		System.out.println(res);
																	// distance is 4																// is 4
	}
}
