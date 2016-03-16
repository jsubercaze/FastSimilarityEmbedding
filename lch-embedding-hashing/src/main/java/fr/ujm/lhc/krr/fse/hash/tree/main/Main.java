package fr.ujm.lhc.krr.fse.hash.tree.main;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Map;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.errorestimator.Correlation;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.GradientAscent;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.PreOrderGrayCodeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.utils.DumpTree;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;

/**
 * Compute results displayed in the paper.
 * 
 * Compute the embedding for the three versions. Dump the best one into a file.
 * Compute and display the correlations on Wordnet Core.
 * 
 * @author Julien Subercaze
 * 
 */
public class Main {

	static Map<Integer, Fingerprint> convertedMap;
	static Distance<BitSet> distance;
	static double size = 128D;
	static String groundTruth = "";

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err
					.println("Usage : Main.java groundtruth inputTree outputFile");
			System.exit(-1);
		}
		// No further check
		groundTruth = args[0];
		// Load
		System.out.println("Loading tree from " + args[1]);
		final Tree<String> tree = TreeUtils.readTreeFromJsonFile(new File(
				args[1]), null);
		System.out.println("Tree loaded");
		// Loading Wordnet Core similarity Ground truth
		System.out.println("Loading Wordnet Core similarity Ground truth");
		// Compute PreOrderGrayCode
		System.out
				.println("----------PreOrder Gray Code Embedding------------");
		PreOrderGrayCodeHasher preOrderGrayCodeHasher = new PreOrderGrayCodeHasher();
		TreeHashFingerprintResult hash = preOrderGrayCodeHasher.hash(tree);
		computeAndDisplayCorrelations(hash);
		// Compute FSE Base
		System.out.println("----------FSE Base------------");
		GradientAscent ascent = new GradientAscent(false, false, false, 2, 0D);
		hash = ascent.hash(tree);
		computeAndDisplayCorrelations(hash);
		// Compute FSE Base
		System.out.println("----------FSE Best------------");
		ascent = new GradientAscent(true, true, true, 2, 1D);
		hash = ascent.hash(tree);
		computeAndDisplayCorrelations(hash);
		// Saving the best embedding
		System.out.println("-----Saving best tree into output file-----");
		DumpTree.dump(hash, new File(args[2]));
	}

	private static void computeAndDisplayCorrelations(
			TreeHashFingerprintResult hash) throws Exception {
		System.out.println("Starting correlation evaluation");
		distance = hash.getDistance();
		size = hash.getFingerprintLength();
		convertedMap = hash.getConvertedMap();
		// Load data from file and compute distances
		int index = 0;
		double[] resTrue = new double[5443351]; // For Wordnet core
		double[] resEstimated = new double[5443351];
		String[] tmp = new String[3];
		int synset1, synset2;
		// "c:/temp/wordnetUS/wordnetlch.csv"
		try (BufferedReader br = Files.newBufferedReader(
				Paths.get(groundTruth), StandardCharsets.UTF_8)) {
			for (String line = null; (line = br.readLine()) != null;) {
				tmp = line.split(";", 3);
				synset1 = Integer.parseInt(tmp[0]);
				synset2 = Integer.parseInt(tmp[1]);
				resTrue[index] = Double.parseDouble(tmp[2]);
				resEstimated[index] = relatedDistance(synset1, synset2);
				index++;
			}
		}
		System.out.println("Pearson: "
				+ Correlation.pearson(resTrue, resEstimated));
		System.out.println("Spearman: "
				+ Correlation.spearman(resTrue, resEstimated));
	}

	private static double relatedDistance(int synset1, int synset2) {
		int dist = distance.distance(convertedMap.get(synset1),
				convertedMap.get(synset2));
		double dist1 = (size - (double) dist) / size;
		double log = Math.log10(dist1 + 1);
		return 2 * log;
	}
}
