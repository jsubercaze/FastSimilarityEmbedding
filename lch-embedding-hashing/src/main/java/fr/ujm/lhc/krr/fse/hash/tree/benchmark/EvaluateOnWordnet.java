/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluationResult;
import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluator;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.PerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasherEnhanced;
import au.com.bytecode.opencsv.CSVWriter;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.utils.MeasureUtils;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;

/**
 * @author Julien Subercaze
 * 
 *         25 oct. 2013
 * 
 * 
 */
public class EvaluateOnWordnet {

	public static final int SUBSET_SIZE = 2000;
	private final static Logger LOGGER = Logger.getLogger(EvaluateOnWordnet.class);

	public static void main(final String[] args) throws IOException {
		LOGGER.info("Evaluation on Wordnet with subset size = " + SUBSET_SIZE);
		// Prepare CSV result
		final CSVWriter writer = new CSVWriter(new FileWriter("groTestSimple5.csv"), ';');
		LOGGER.info("Reading Wordnet tree");
		final Tree<String> wordnet = TreeUtils.readTreeFromJsonFile(new File("C:/temp/wordnetUS/simple.tree"), null);
		LOGGER.info("Branching factor " + MeasureUtils.branchingFactor(wordnet));
		LOGGER.info("Depth " + MeasureUtils.depth(wordnet));
		LOGGER.info("Leaves depth" + MeasureUtils.leavesDepth(wordnet));
		LOGGER.info("Finished reading");
		final String title = "Name#Bits#N-RMSE#RMSE#Pearson#Spearman#Kendall#ThroughputHeuristic (ms per 100K)#ThroughputOracle  (ms per 100K)#Expansion#Contraction#Distortion";
		writer.writeNext(title.split("#"));
		final List<TreeHasher> lists = new ArrayList<>();
		// lists.add(new LinearPerfectHashingScheme());
		// lists.add(new AdaptiveLeftHasher());
		lists.add(new AdaptiveLeftHasherEnhanced(true, false, false));
		lists.add(new AdaptiveLeftHasherEnhanced(true, true, false));
		lists.add(new AdaptiveLeftHasherEnhanced(true, false, true));
		lists.add(new AdaptiveLeftHasherEnhanced(true, true, true));
		// lists.add(new AdaptiveLeftHasherEnhanced(false, false, false));
		// lists.add(new AdaptiveLeftHasherEnhanced(false, true, false));
		// lists.add(new AdaptiveLeftHasherEnhanced(false, false, true));
		// lists.add(new AdaptiveLeftHasherEnhanced(false, true, true));
		// lists.add(new AdaptiveLeftHasherEnhancedWeighted(false, false,
		// false));
		// lists.add(new AdaptiveLeftHasherEnhancedWeighted(false, true,
		// false));
		// lists.add(new AdaptiveLeftHasherEnhancedWeighted(false, false,
		// true));
		// lists.add(new AdaptiveLeftHasherEnhancedWeighted(false, true, true));
		// lists.add(new AdaptiveLeftHasherV2());
		// lists.add(new AdaptiveLeftHasherTurboEnhanced(true, true));
		// lists.add(new PreOrderGrayCodeHasher());
		// lists.add(new AdaptivePermutationHasher());
		// -----------------------
		final TreeEvaluator eval = new TreeEvaluator(lists, new PerfectHashingScheme(), wordnet, false);
		final List<TreeEvaluationResult> result = eval.randomSubsetEvaluation(SUBSET_SIZE);
		for (final TreeEvaluationResult res : result) {
			writer.writeNext(res.toStringArray());
		}
		writer.close();
	}
}
