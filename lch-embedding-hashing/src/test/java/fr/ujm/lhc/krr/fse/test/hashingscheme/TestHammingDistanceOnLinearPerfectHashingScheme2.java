/**
 * 
 */
package fr.ujm.lhc.krr.fse.test.hashingscheme;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.LinearPerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.PerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * Test the {@link HammingDistance} on the following tree with the
 * {@link PerfectHashingScheme}:
 * 
 * <pre>
 * *                   A 
 *                     | 
 *                     B 
 *                   / | \ \ 
 *                   C D  E F 
 *                   |      | 
 *                   G      H
 * 
 * </pre>
 * 
 * @author Julien Subercaze
 * 
 *         18 oct. 2013
 * 
 * 
 */
public class TestHammingDistanceOnLinearPerfectHashingScheme2 {
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(TestHammingDistanceOnLinearPerfectHashingScheme2.class);

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
		TreeNode<String> th = new TreeNode<String>("h");
		ta.addChild(tb);
		tb.addChild(tc);
		tb.addChild(td);
		tb.addChild(te);
		tb.addChild(tf);
		tc.addChild(tg);
		tf.addChild(th);
		// Compute Hash of the tree
		final LinearPerfectHashingScheme hasher = new LinearPerfectHashingScheme();
		final TreeHashFingerprintResult res = hasher.hash(tree);
		//

		res.setDistance(new HammingDistance());// In fact useless, since it is
		System.out.println(res);

//		System.out.println("---Good one---");
//		PerfectHashingScheme perfectHashingScheme = new PerfectHashingScheme();
//		TreeHashFingerprintResult hash = perfectHashingScheme.hash(tree);
//		System.out.println(hash);
//
//		final List<TreeHasher> lists = new ArrayList<>();
//		lists.add(new LinearPerfectHashingScheme());
//		final TreeEvaluator eval = new TreeEvaluator(lists,
//				new PerfectHashingScheme(), tree, true);
//		final List<TreeEvaluationResult> result = eval.fullEvaluation();
//		for (final TreeEvaluationResult rese : result) {
//			System.out.println(rese);
//		}

	}
}
