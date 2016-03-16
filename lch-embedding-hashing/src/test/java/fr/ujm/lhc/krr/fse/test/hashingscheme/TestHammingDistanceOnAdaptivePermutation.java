/**
 * 
 */
package fr.ujm.lhc.krr.fse.test.hashingscheme;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptivePermutationHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashBitsetResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Test the {@link HammingDistance} on the following tree with the {@link AdaptivePermutationHasher}: 
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
 *                            011
 *                   /         |       \       \ 
 *               01111000000   01100110000 01100001100  01100000011 
 *                   |                                          | 
 *               011110000001                                 011000000111
 *   
 * @author Julien Subercaze
 *
 * 18 oct. 2013
 *
 * 
 */
public class TestHammingDistanceOnAdaptivePermutation {

	private final static Logger LOGGER = Logger
			.getLogger(TestHammingDistanceOnAdaptivePermutation.class);
	
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
		AdaptivePermutationHasher hasher = new AdaptivePermutationHasher();
		TreeHashBitsetResult res = hasher.hash(tree);
		res.setDistance(new HammingDistance());// In fact useless, since it is
		LOGGER.debug(res);
		assertEquals(1D, 1D, 0D);
		// Compute distances
		// All top down

		assertEquals(1, res.distance(ta, tb));// Real
																	// distance
																	// is 1
		assertEquals(2, res.distance(ta, tc));// Real
																	// distance
																	// is 2
		assertEquals(3, res.distance(ta, tg));// Real
																	// distance
																	// is 3
		// Same childs
		assertEquals(2, res.distance(tc, td));// Real
																	// distance
																	// is 2
		assertEquals(2, res.distance(tc, te));// Real
																	// distance
																	// is 2
		assertEquals(2, res.distance(tc, tf));// Real
																	// distance
																	// is 2

		// Sides
		assertEquals(3, res.distance(tc, th));// Real
																	// distance
																	// is 3
		assertEquals(2, res.distance(tg, th));// Real
																	// distance
																	// is 4
		
	
	}
}
