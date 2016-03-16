/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.utils;

import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * @author Julien Subercaze
 *
 * 12 nov. 2013
 *
 * 
 */
public class MathUtils {

	/** Computes the <a href="http://en.wikipedia.org/wiki/Falling_factorial_power">Falling factorial power</a> for given n and k.
	 *  Uses simple product
	 * 
	 * @param n 
	 * @param k
	 * @return
	 */
	public static int fallingFactorialPower(int n, int k) {
		if (k < 0)
			throw new OutOfRangeException(n, 0, Integer.MAX_VALUE);
		if (k == 0)
			return 1;
		int val = n;
		for (int i = 2; i <= k; i++) {
			val *= n - i + 1;
		}

		return val;
	}

	public static void main(String[] args) {
		System.out.println(fallingFactorialPower(5, 4));
	}
}
