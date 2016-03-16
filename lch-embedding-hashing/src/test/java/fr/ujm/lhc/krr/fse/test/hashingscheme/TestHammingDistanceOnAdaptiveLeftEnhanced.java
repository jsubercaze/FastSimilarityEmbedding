/**
 * 
 */
package fr.ujm.lhc.krr.fse.test.hashingscheme;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluationResult;
import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluator;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.LinearPerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.PerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasherEnhanced;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Test the {@link HammingDistance} on the following tree with the
 * {@link AdaptiveLeftHasherEnhanced}:
 * 
 * <pre>
 *                     A 
 *                     | 
 *                     B 
 *                   / | \ \ 
 *                   C D  E F 
 *                 |||      ||| 
 *                 GHI      JKL
 *                  
 * </pre>
 * 
 * Which is encoded as :
 * 
 * 
 * @author Julien Subercaze
 * 
 *         18 oct. 2013
 * 
 * 
 */
public class TestHammingDistanceOnAdaptiveLeftEnhanced {

	private final static Logger LOGGER = Logger
			.getLogger(TestHammingDistanceOnAdaptiveLeftEnhanced.class);

	@Test
	public void test() {
		LOGGER.debug("Starting test on adaptive left enhanced");
		TreeNode<String> ta = new TreeNode<String>("a");
		Tree<String> tree = new Tree<String>(ta);
		TreeNode<String> tb = new TreeNode<String>("b");
		TreeNode<String> tc = new TreeNode<String>("c");
		TreeNode<String> td = new TreeNode<String>("d");
		TreeNode<String> te = new TreeNode<String>("e");
		TreeNode<String> tf = new TreeNode<String>("f");
		TreeNode<String> tg = new TreeNode<String>("g");
		TreeNode<String> th = new TreeNode<String>("h");
		TreeNode<String> ti = new TreeNode<String>("i");
		TreeNode<String> tj = new TreeNode<String>("j");
		TreeNode<String> tk = new TreeNode<String>("k");
		TreeNode<String> tl = new TreeNode<String>("l");
		ta.addChild(tb);
		tb.addChild(tc);
		tb.addChild(td);
		tb.addChild(te);
		tb.addChild(tf);
		tc.addChild(tg);
		tc.addChild(th);
		tc.addChild(ti);
		tf.addChild(tj);
		tf.addChild(tk);
		tf.addChild(tl);
		
		// Compute Hash of the tree
		AdaptiveLeftHasherEnhanced hasher = new AdaptiveLeftHasherEnhanced(
				true, true, true);
		TreeHashFingerprintResult res = hasher.hash(tree);
		res.setDistance(new HammingDistance());// In fact useless, since it is
		System.out.println(res);
		LinearPerfectHashingScheme linearPerfectHashingScheme = new LinearPerfectHashingScheme();
		TreeHashFingerprintResult hash = linearPerfectHashingScheme.hash(tree);
		System.out.println(hash);
		PerfectHashingScheme perfectHashingScheme = new PerfectHashingScheme();
		TreeHashFingerprintResult hash2 = perfectHashingScheme.hash(tree);
		System.out.println(hash2);
		// Compute distances
		// All top down
//		assertEquals(1, res.distance(ta, tb));// Real distance is 1
//		assertEquals(2, res.distance(ta, tc));// Real distance is 2
//		assertEquals(3, res.distance(ta, tg));// Real distance is 3
//		// Sides
//		assertEquals(3, res.distance(tc, th));// Real
//												// distance
//												// is 3
//		assertEquals(2, res.distance(tg, th));// Real
//												// distance is 4 // is 4
		// Compute Evaluation
		final List<TreeHasher> lists = new ArrayList<>();
		lists.add(new AdaptiveLeftHasherEnhanced(	false, false, false));
		lists.add(new AdaptiveLeftHasherEnhanced(	true, true, true));
		final TreeEvaluator eval = new TreeEvaluator(lists,
				new PerfectHashingScheme(), tree, true);
		final List<TreeEvaluationResult> result = eval.fullEvaluation();
		for (final TreeEvaluationResult rese : result) {
			System.out.println(rese);
		}

	}
}
