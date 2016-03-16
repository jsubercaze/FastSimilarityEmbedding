package fr.ujm.lhc.krr.fse.test.hashingscheme;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.LCABitsetDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.ExactPathHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashBitsetResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Test the HOP distance on the following tree : 
 *                     A 
 *                     | 
 *                     B 
 *                   / | \ \ 
 *                   C D  E F 
 *                   |      | 
 *                   G      H
 * 
 * @author Julien Subercaze
 * 
 *         18 oct. 2013
 * 
 * 
 */
public class TestLCAHashing {
	private final static Logger LOGGER = Logger
			.getLogger(TestLCAHashing.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		LOGGER.debug("Test LCA Hashing");
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
		ExactPathHasher hasher = new ExactPathHasher();
		// AdaptiveLeftHasher hasher = new AdaptiveLeftHasher();
		TreeHashBitsetResult res = hasher.hash(tree);
		res.setDistance(new LCABitsetDistance(hasher.getBitsRequired()));
		// All top down
		assertEquals(1, res.distance(ta, tb));
		assertEquals(2, res.distance(ta, tc));
		assertEquals(3, res.distance(ta, tg));
		// Sides
		assertEquals(3, res.distance(tc, th));
		assertEquals(4, res.distance(tg, th));
	}

}
