package fr.ujm.lhc.krr.fse.hash.tree.kb.main;

import fr.ujm.lhc.krr.fse.hash.tree.kb.parsers.impl.wordnet.WordnetTreeCreatorSimple;
/**
 * 
 * @author Julien Subercaze
 *
 */
public class WordnetToTree {

	
	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("Usage Main wordnetDirectory outputFile");
			System.exit(-1);
		}
		WordnetTreeCreatorSimple.createTree(args[0],
				args[1]);
	}
}
