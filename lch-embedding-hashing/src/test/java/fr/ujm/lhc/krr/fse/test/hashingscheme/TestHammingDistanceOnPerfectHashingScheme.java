package fr.ujm.lhc.krr.fse.test.hashingscheme;
///**
// * 
// */
//package fr.ujm.lhc.krr.fse.test.hashingscheme;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Scanner;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//
//import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
//import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.PerfectHashingScheme;
//import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
//import fr.ujm.tse.lt2c.satin.hash.tree.Tree;
//import fr.ujm.tse.lt2c.satin.hash.tree.TreeNode;
//
///**
// * Test the {@link HammingDistance} on the following tree with the
// * {@link PerfectHashingScheme}: A | \ B C | \ \ | D E F G | H
// * 
// * Which is encoded as :
// * 
// * 11 | 0111111
// * 
// * 
// * @author Julien Subercaze
// * 
// *         18 oct. 2013
// * 
// * 
// */
//public class TestHammingDistanceOnPerfectHashingScheme {
//	private final static Logger LOGGER = Logger
//			.getLogger(TestHammingDistanceOnPerfectHashingScheme.class);
//
//	@Test
//	public void test() {
//		Scanner s = new Scanner(System.in);
//		s.nextLine();
//		s.close();
//		TreeNode<String> ta = new TreeNode<String>("a");
//		Tree<String> tree = new Tree<String>(ta);
//		TreeNode<String> tb = new TreeNode<String>("b");
//		TreeNode<String> tc = new TreeNode<String>("c");
//		TreeNode<String> td = new TreeNode<String>("d");
//		TreeNode<String> te = new TreeNode<String>("e");
//		TreeNode<String> tf = new TreeNode<String>("f");
//		TreeNode<String> tg = new TreeNode<String>("g");
//		TreeNode<String> th = new TreeNode<String>("h");
//		ta.addChild(tb);
//		ta.addChild(tc);
//		tb.addChild(td);
//		tb.addChild(te);
//		tb.addChild(tf);
//		tf.addChild(th);
//		tc.addChild(tg);
//		// Compute Hash of the tree
//		PerfectHashingScheme hasher = new PerfectHashingScheme();
//		TreeHashFingerprintResult res = hasher.hash(tree);
//		res.setDistance(new HammingDistance());// In fact useless, since it is
//		LOGGER.debug(res);
//
//		// Compute distances
//		// All top down
//		LOGGER.debug(res);
//		assertEquals(1, res.distance(ta, tb));// Real
//												// distance
//												// is 1
//		assertEquals(1, res.distance(ta, tc));// Real
//												// distance
//												// is 2
//		assertEquals(2, res.distance(ta, tg));// Real
//												// distance
//												// is 3
//		// Same childs
//		assertEquals(3, res.distance(tc, td));// Real
//												// distance
//												// is 2
//		assertEquals(3, res.distance(tc, te));// Real
//												// distance
//												// is 2
//		assertEquals(2, res.distance(td, tf));// Real
//												// distance
//												// is 2
//
//		// Sides
//		assertEquals(4, res.distance(tc, th));// Real
//												// distance
//												// is 3
//		assertEquals(5, res.distance(tg, th));// Real
//												// distance
//												// is 4
//
//	}
// }
