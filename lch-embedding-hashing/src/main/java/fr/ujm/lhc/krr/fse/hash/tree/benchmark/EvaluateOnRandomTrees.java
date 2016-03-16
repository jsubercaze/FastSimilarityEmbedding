package fr.ujm.lhc.krr.fse.hash.tree.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluationResult;
import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.TreeEvaluator;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.exact.LinearPerfectHashingScheme;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasherEnhanced;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.PreOrderGrayCodeHasher;
import au.com.bytecode.opencsv.CSVWriter;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeGenerator;

/**
 * @author Julien Subercaze
 * 
 *         16 oct. 2013
 * 
 * 
 */
public class EvaluateOnRandomTrees {
	private final static Logger LOGGER = Logger
			.getLogger(EvaluateOnRandomTrees.class);

	public static final int NUMBER_OF_RANDOM_TREES = 5;
	public static final int MIN_NODES = 500;
	public static final int MAX_NODES = 3000;

	public static void main(String[] args) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("random" + ".csv"), ';');

		String title = "Name#Bits#N-RMSE#RMSE#Pearson#Spearman#Kendall#ThroughputHeuristic (ms per 100K)#ThroughputOracle  (ms per 100K)#Expansion#Contraction#Distortion";
		for (int i = 0; i < NUMBER_OF_RANDOM_TREES; i++) {
			Random r = new Random();
			int numNodes = r.nextInt(MAX_NODES - MIN_NODES);
			numNodes += MIN_NODES;
			LOGGER.debug("Nodes " + numNodes);
			Tree<String> tree = TreeGenerator.generateTree(numNodes);
			// Prepare CSV result

			writer.writeNext(title.split("#"));
			List<TreeHasher> lists = new ArrayList<>();
			// lists.add(new AdaptiveLeftHasher());
			lists.add(new AdaptiveLeftHasherEnhanced(true, true, true));
			// lists.add(new AdaptiveLeftHasherTurboEnhanced(true, true, true));
			// lists.add(new AdaptivePermutationHasher());
			lists.add(new PreOrderGrayCodeHasher());
			lists.add(new LinearPerfectHashingScheme());
			// -----------------------
			TreeEvaluator eval = new TreeEvaluator(lists,
					new LinearPerfectHashingScheme(), tree, false);
			List<TreeEvaluationResult> result = eval.fullEvaluation();
			for (TreeEvaluationResult res : result) {
				writer.writeNext(res.toStringArray());
			}

		}
		writer.close();
	}
}
