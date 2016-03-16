/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.utils;

import java.util.Random;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * @author Julien Subercaze
 *
 * 
 *
 * 
 */
public class TreeGenerator {

	/** Generate a random tree of the given size
	 * 
	 * @param size
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Tree<String> generateTree(int size) {
		TreeNode<String>[] arraynodes = new TreeNode[size];
		int generated = 0;
		Random r = new Random();
		Tree<String> tree = null;
		while (generated < size) {
			arraynodes[generated] = new TreeNode<String>("" + generated);
			if (generated > 0) {
				// Set randomly the father
				int fatherIndex = r.nextInt(generated);

				arraynodes[fatherIndex].addChild(arraynodes[generated]);
			} else {
				// Initialize the tree with current node
				tree = new Tree<String>(arraynodes[0]);
			}
			generated++;
		}

		return tree;
	}

}
