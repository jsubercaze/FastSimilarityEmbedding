/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.kb.test;

import fr.ujm.lhc.krr.fse.hash.tree.kb.parsers.impl.wordnet.WordnetTreeCreator;

/**
 * Build the augmented Wordnet
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class BuildWordnetS {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		WordnetTreeCreator.createTree("c:/temp/wordnetUS/WN_plus40k",
				"c:/temp/wordnetUS/40k.tree");

	}
}
