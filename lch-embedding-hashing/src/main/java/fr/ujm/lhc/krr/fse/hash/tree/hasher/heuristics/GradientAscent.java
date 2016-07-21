package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;
import fr.ujm.lhc.krr.fse.hash.tree.utils.Measure;
import fr.ujm.lhc.krr.fse.hash.tree.utils.MeasureUtils;

/**
 * Augment the size of the countbits to reach the given word aligned size
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class GradientAscent extends AdaptiveLeftHasherEnhanced {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6050632813094581986L;

	private final static Logger LOGGER = Logger.getLogger(GradientAscent.class);
	/**
	 * Expected number of bits
	 */
	int expected;
	/**
	 * Ascent value
	 */
	double deltaPlus = 0.2D;
	/**
	 * Descent
	 */
	double deltaMinus = deltaPlus / 2;
	/**
	 * Starting value 1.0 is good
	 */
	double coeff = 1D;

	int maxdepth;
	private double upperbound;

	public GradientAscent(boolean onemorebit, boolean sortValues,
			boolean sortChildren, int words) {
		super(onemorebit, sortValues, sortChildren);
		this.expected = words * 64;
	}

	public GradientAscent(boolean onemorebit, boolean sortValues,
			boolean sortChildren, int words, double coeff) {
		super(onemorebit, sortValues, sortChildren);
		this.expected = words * 64;
		this.coeff = coeff;
	}

	@Override
	protected void init(Tree<?> tree) {

		// Compute max values for the given tree
		Measure branchingFactor = MeasureUtils.branchingFactor(tree);
		Measure depth = MeasureUtils.depth(tree);
		// so the max value is the product of the branching factor by the depth
		maxdepth = (int) depth.getMax();
		upperbound = (branchingFactor.getAverage() + branchingFactor.getStd())
				* depth.getMax();
	}

	/**
	 * 
	 * @param tree
	 * @return the hash with the specified number of bits
	 */
	public Double findHash(final Tree<?> tree) {
		int iteration = 0;
		int bits = 0;
		boolean wentOver = false;
		while (bits != expected) {
			LOGGER.info("Hashing with coeff " + coeff + " - Iteration "
					+ iteration);
			TreeHashFingerprintResult hash = hash(tree);
			bits = hash.getFingerprintLength();
			LOGGER.info("Got " + bits + " bits");
			iteration++;
			if (bits < expected) {
				coeff += deltaPlus;
			} else if (bits > expected) {
				if (wentOver) {
					coeff -= deltaMinus;
					return coeff;
				}
				coeff -= deltaMinus;
				wentOver = true;
			} else {
				return coeff;
			}
		}
		// Shit happened
		return null;
	}

	/**
	 * CountBits adaptative
	 */
	@Override
	protected int countBits(TreeNode<?> node) {
		int value = bitsFor(node.getChildren().size() + (onemorebit ? 1 : 0));
		// Add additional value depending on some things
		double coefficient = (maxdepth - node.getDepth())
				* node.getChildren().size();
		int addition = (int) Math.ceil(Math.min((coefficient / upperbound)
				* coeff, 1D)
				* value);
		// System.out.println("Total "+(value + addition));
		return (value + addition);
	}
}
