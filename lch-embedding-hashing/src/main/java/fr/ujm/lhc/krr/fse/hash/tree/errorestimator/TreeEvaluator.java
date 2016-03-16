/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.errorestimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;


/**
 * General methods to evaluate the correlation between a distance hasher and the
 * exact value of the distance. Computes the following correlation coefficient
 * and error measures :
 * <p>
 * <ul>
 * <li>Pearson's correlation : {@link PearsonsCorrelation}</li>
 * <li>Spearman's correlation : {@link SpearmansCorrelation}</li>
 * <li>Kendall Tau correlation</li>
 * <li>Root Mean Square Error</li>
 * <li>Normalized RMSE</li>
 * <li>Expansion/Dilation</li>
 * <li>Contraction</li>
 * <li>Distortion</li>
 * </ul>
 * <p>
 * Results are returned in form of a {@link TreeEvaluationResult}.
 * <p>
 * Correlation measure can be computed on the whole set of pairs using
 * {@link #fullEvaluation()} or on specified number of pairs using
 * {@link #subsetEvaluation(int)} or on a percentage using
 * {@link #sampleEvaluation(double)}.
 * <p>
 * 
 * @author Julien Subercaze
 * 
 *         10 oct. 2013
 * 
 * 
 */
public class TreeEvaluator {

	private final static Logger LOGGER = Logger.getLogger(TreeEvaluator.class);
	List<TreeHasher> hashers;

	private double[] oracleDistances;
	TreeNode<?>[] toEvaluate;
	// private int arraySize;
	TreeHasher heuristicHasher;
	TreeHasher oracleHasher;

	Tree<?> tree;
	private boolean warmup;

	TreeHashResult<BitSet> oracleResults;
	private double oracleThroughput;

	/**
	 * @param hasherToEvaluate
	 *            The hashing scheme one wants to evaluate
	 * @param exactHasher
	 *            The oracle
	 * @param oracleDistance
	 *            Distance measure between oracle hash
	 * @param heuristicDistance
	 *            Distance measure between heuristic hash
	 * @param tree
	 *            The tree on which the evaluation will be performed
	 */
	public TreeEvaluator(List<TreeHasher> hasherToEvaluate,
			TreeHasher exactHasher, Tree<?> tree, boolean warmup) {
		super();
		// this.heuristicHasher = hasherToEvaluate;
		this.hashers = hasherToEvaluate;
		this.oracleHasher = exactHasher;
		this.tree = tree;
		this.warmup = warmup;

	}

	private List<TreeEvaluationResult> evaluate() {
		if (warmup)
			computeOracle();
		computeOracle();
		
		LOGGER.debug("Hashed oracle");

		// Hash the tree and get Results
		List<TreeEvaluationResult> res = new ArrayList<>(hashers.size());
		for (TreeHasher t : hashers) {
			res.add(evaluateHasher(t));
			
		}
		return res;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void computeOracle() {
		// Hash the tree
		oracleResults = oracleHasher.hash(tree);
		Distance<BitSet> oracleDistance = (Distance<BitSet>) oracleHasher
				.getDistance();
		// arraySize = toEvaluate.length;
		//
		int pairs = toEvaluate.length * (toEvaluate.length - 1) / 2;
		oracleDistances = new double[pairs];
		int k = 0;
		// Compute the distances
		long time = System.currentTimeMillis();
		BitSet oracleHash1, oracleHash2;
		for (int i = 0; i < toEvaluate.length; i++) {
			oracleHash1 = oracleResults.getHashForNode(toEvaluate[i]);
			for (int j = i + 1; j < toEvaluate.length; j++) {
				oracleHash2 = oracleResults.getHashForNode(toEvaluate[j]);
				oracleDistances[k] = oracleDistance.distance(oracleHash1,
						oracleHash2);
				k++;
			}
		}
		LOGGER.info("Computed oracle");
		LOGGER.debug(Arrays.toString(toEvaluate));
		LOGGER.debug(Arrays.toString(oracleDistances));
		time = System.currentTimeMillis() - time;
		oracleThroughput = ((double) time / (double) pairs) * 100000.0;
	}

	@SuppressWarnings("unchecked")
	private TreeEvaluationResult evaluateHasher(TreeHasher heuristicHasher) {
		double expansion = -1;
		double contraction = -1;
		TreeHashResult<BitSet> heuristicResults = (TreeHashResult<BitSet>) heuristicHasher
				.hash(tree);

		Distance<BitSet> heuristicDistance = (Distance<BitSet>) heuristicHasher
				.getDistance();
		LOGGER.info("Hashed heuristic "
				+ heuristicHasher.getClass().getSimpleName());
		int pairs = toEvaluate.length * (toEvaluate.length - 1) / 2;
		double[] heuristicDistances = new double[pairs];
		int k = 0;
		// Initialize results value

		BitSet heuristicHash1, heuristicHash2;
		long time = System.currentTimeMillis();
		double _expansion, _contraction;
		for (int i = 0; i < toEvaluate.length; i++) {
			heuristicHash1 = heuristicResults.getHashForNode(toEvaluate[i]);
			for (int j = i + 1; j < toEvaluate.length; j++) {
				heuristicHash2 = heuristicResults.getHashForNode(toEvaluate[j]);
				heuristicDistances[k] = heuristicDistance.distance(
						heuristicHash1, heuristicHash2);
				// Compute contraction and expansions
				_expansion = ((double) heuristicDistances[k])
						/ ((double) oracleDistances[k]);
				_contraction = ((double) oracleDistances[k])
						/ ((double) heuristicDistances[k]);
				expansion = expansion < _expansion ? _expansion : expansion;
				contraction = contraction < _contraction ? _contraction
						: contraction;
				// Go to next value
				k++;
			}
		}
		time = System.currentTimeMillis() - time;
		double heuristicThroughput = ((double) time) / ((double) pairs)
				* 1000000D;
		// Compute the different correlation ranks : pearson, spearman and
		// kendall tau
		double pearson = new PearsonsCorrelation().correlation(oracleDistances,
				heuristicDistances);
		double spearman = new SpearmansCorrelation().correlation(
				oracleDistances, heuristicDistances);
		// double kendall = KendallTau.compute(oracleDistances,
		// heuristicDistances);
		TreeEvaluationResult res = new TreeEvaluationResult(heuristicHasher
				.getClass().getSimpleName());
		res.setContraction(contraction);
		res.setExpansion(expansion);
		res.setDistortion(expansion * contraction);
		res.setKendallTausCorrelation(0.0);
		res.setPearsonsCorrelation(pearson);
		res.setSpearmansCorrelation(spearman);
		double rmse = computeRMSE(heuristicDistances);
		double normalizedRMSE = rmse / (tree.getMaxdepth() * 2);
		res.setRMSE(rmse);
		res.setNormalizedRMSE(normalizedRMSE);
		res.setBitsOracle(oracleHasher.signatureSize());
		res.setBitsHeuristic(heuristicHasher.signatureSize());
		res.setThroughputHeuristic(heuristicThroughput);
		res.setThroughputOracle(oracleThroughput);
		return res;
	}

	/**
	 * @return
	 */
	private double computeRMSE(double[] heuristicDistances) {
		double mse = 0;
		for (int i = 0; i < oracleDistances.length; i++) {
			mse += Math.pow(oracleDistances[i] - heuristicDistances[i], 2);
		}
		mse /= oracleDistances.length;
		return Math.sqrt(mse);
	}

	public List<TreeEvaluationResult> randomSampleEvaluation(double percentage) {
		return null;
	}

	/**
	 * Compute the evaluation on a number of nodes that will be randomly
	 * selected among all the nodes in the tree
	 * 
	 * The cost of the comparison is numberOfNodes^2
	 * 
	 * @param numberOfNodes
	 * @return
	 */
	public List<TreeEvaluationResult> randomSubsetEvaluation(int numberOfNodes) {
		// Sets of nodes to be computed
		toEvaluate = new TreeNode[numberOfNodes];
		// Get all the nodes identifier as linkedlist for faster removal
		List<TreeNode<?>> remaining = new LinkedList<>();
		remaining.addAll(tree.nodes());
		if (numberOfNodes > remaining.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		Random random = new Random();

		for (int i = 0; i < numberOfNodes; i++) {
			int index = random.nextInt(remaining.size());
			toEvaluate[i] = remaining.get(index);
			remaining.remove(index);
		}
		return evaluate();
	}

	/**
	 * Compute the correlation measurement for the complete set of node pairs.
	 * This method should be use carefully since its generate N^2 pairs, where N
	 * is the number of nodes in the tree.
	 * 
	 * For large trees, one should consider using
	 * {@link #sampleEvaluation(double)} or {@link #subsetEvaluation(int)} for
	 * evaluation on a subset of the pairs
	 * 
	 * @return
	 */
	public List<TreeEvaluationResult> fullEvaluation() {
		// Get all the nodes identifier
		tree.prepare();
		toEvaluate = tree.nodes().toArray(new TreeNode[tree.nodes().size()]);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(Arrays.toString(toEvaluate));
		return evaluate();
	}
}
