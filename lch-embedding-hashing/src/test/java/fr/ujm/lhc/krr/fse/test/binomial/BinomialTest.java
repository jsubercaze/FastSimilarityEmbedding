/**
 * 
 */
package fr.ujm.lhc.krr.fse.test.binomial;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.log4j.Logger;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;


/**
 * @author Julien Subercaze
 *
 * 
 *
 * 
 */
public class BinomialTest {
	private final static Logger LOGGER = Logger.getLogger(BinomialTest.class);
	public static final int BITS = 8;
	public static Map<Integer, Integer> counter;

	public static void main(String[] args) {
		// Generate all numbers
		int total = (int) Math.pow(2, BITS);
		// CountMap
		initializeMap(BITS);
		Fingerprint[] prints = new Fingerprint[total];
		for (int i = 0; i < total; i++) {
			prints[i] = intToFingerprint(i);
		}
		// Compute pairwise distances
		int k = 0;
		int pairs = prints.length * (prints.length - 1) / 2;
		HammingDistance d = new HammingDistance();
		int[] distances = new int[pairs];
		for (int i = 0; i < total; i++) {
			prints[i] = intToFingerprint(i);
		}
		// System.out.println(Arrays.toString(prints));
		for (int i = 0; i < total; i++) {
			for (int j = i + 1; j < total; j++) {
				distances[k] = d.distance(prints[i], prints[j]);
				incrementValue(distances[k]);
			}
		}
		for (int i = 1; i <= BITS; i++) {
			LOGGER.debug("i");
			long gros = BNI5(i, BITS);
			if (gros != counter.get(i)) {
				LOGGER.debug("Formula not working for B(" + BITS + "," + i
						+ ")");
				LOGGER.debug("Expected " + counter.get(i));
				LOGGER.debug("Got " + gros);
			} else
				LOGGER.debug("Formula  working for B(" + BITS + "," + i + ") "
						+ gros);
		}
		LOGGER.debug("Bits : " + BITS + " " + counter);
	}

	/**
	 * 
	 */
	private static void initializeMap(int total) {
		counter = new HashMap<>();
		for (int i = 1; i <= total; i++) {
			counter.put(i, 0);
		}

	}

	private static void incrementValue(int distance) {
		counter.put(distance, counter.get(distance) + 1);
	}

	private static Fingerprint intToFingerprint(int k) {
		Fingerprint print = new Fingerprint(BITS);
		for (int j = BITS - 1; j >= 0; j--) {
			int gros = k;
			// ("j :" + j);
			if ((gros & (1 << j)) > 0) {
				// ("Setting");
				print.set((BITS - j) - 1);

			}
		}
		return print;
	}

	@SuppressWarnings("unused")
	private static int BNI(int i, int n) {
		int val = n * (int) Math.pow(2, n - 1);
		LOGGER.debug("Val " + val);
		for (int k = 2; k <= i - 1; k++) {
			val *= (n - (i - k)) / (i - (k - 1));
		}
		return val;
	}

	@SuppressWarnings("unused")
	private static int BNI2(int i, int n) {
		if (i == 1)
			return (int) (BITS * Math.pow(2, BITS - 1));
		else {
			int under = BNI2((i - 1), n);
			LOGGER.debug("Brought up " + under);
			return (under * (BITS - (i - 1))) / i;
		}

	}

	@SuppressWarnings("unused")
	private static int BNI3(int i, int n) {
		int val = n * (int) Math.pow(2, n - 1);
		LOGGER.debug("Val " + val);
		for (int k = 2; k <= i; k++) {
			val *= (n + (i - k)) / (k);
		}
		return val;
	}

	/** The working one.
	 * 
	 * @param i
	 * @param n
	 * @return
	 */
	@SuppressWarnings("unused")
	private static long BNI4(int i, int n) {
		return (int) Math.pow(2, n - 1)
				* (ArithmeticUtils.factorial(n))
				/ ((ArithmeticUtils.factorial(n - i)) * ArithmeticUtils
						.factorial(i));
	}

	private static long BNI5(int i, int n) {
		return (int) Math.pow(2, n - 1)
				* fr.ujm.lhc.krr.fse.hash.tree.hasher.utils.MathUtils
						.fallingFactorialPower(BITS, i)
				/ ArithmeticUtils.factorial(i);
	}
}
