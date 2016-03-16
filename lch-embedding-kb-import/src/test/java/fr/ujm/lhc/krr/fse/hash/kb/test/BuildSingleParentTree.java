/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.kb.test;

import fr.ujm.lhc.krr.fse.hash.tree.kb.parsers.impl.wordnet.WordnetTreeCreatorSimple;

/**
 * Build the augmented Wordnet
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class BuildSingleParentTree {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		WordnetTreeCreatorSimple.createTree("c:/temp/wordnetUS/WN3", "c:/temp/wordnetUS/simple3.tree");

	}
}
