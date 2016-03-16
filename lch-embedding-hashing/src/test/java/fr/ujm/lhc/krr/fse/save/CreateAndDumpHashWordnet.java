/**
 * 
 */
package fr.ujm.lhc.krr.fse.save;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasherEnhanced;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.utils.DumpTree;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;


/**
 * @author Julien Subercaze
 * 
 */
public class CreateAndDumpHashWordnet {
	public static void main(final String[] args) throws IOException {
		// Load the wordnet tree
		final PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
		final Resource res = r
				.getResource("classpath:/wordnets/nouns/WN21.tree");
		final Tree<String> tree = TreeUtils.readTreeFromJsonFile(res.getFile(),
				null);
		// Hash it
		final AdaptiveLeftHasherEnhanced hasher = new AdaptiveLeftHasherEnhanced(
				true,true, true);
		final TreeHashFingerprintResult result = hasher.hash(tree);
		// Save the
		DumpTree.dump(result, new File("hashedWN.obj"));
	}
}
