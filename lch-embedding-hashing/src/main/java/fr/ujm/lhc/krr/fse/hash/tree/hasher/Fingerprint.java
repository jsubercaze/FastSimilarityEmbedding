package fr.ujm.lhc.krr.fse.hash.tree.hasher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.HammingDistance;


/**
 * FingerPrint is a BitSet of a given length, since BitSet is of unspecified
 * length.
 * 
 * @author Julien Subercaze, P. Bamba
 * 
 *         2012, 2013
 * 
 * 
 */
public class Fingerprint extends BitSet implements Serializable {
	private static final long serialVersionUID = -7015960384111188668L;
	private int length;

	public Fingerprint() {
		length = 0;
	}

	public Fingerprint(final int length, final BitSet b) {
		this.length = length;
		for (int i = 0; i < b.size(); i++) {
			this.set(i, b.get(i));
		}
	}

	/**
	 * Fingerprint with all size bits set to zero.
	 * 
	 * @param length
	 */
	public Fingerprint(final int length) {
		super(length);
		this.length = length;
	}

	/**
	 * Copy Constructor - avoid cloning
	 * 
	 * @param signature
	 */
	public Fingerprint(final Fingerprint signature) {
		super(signature.length);
		length = signature.length;
		for (int i = 0; i < signature.length(); i++) {
			if (signature.get(i)) {
				this.set(i);
			}
		}
	}

	public Fingerprint(final boolean[] array) {
		super(array.length);
		length = array.length;
		for (int i = 0; i < array.length; i++) {
			final boolean b = array[i];
			this.set(i, b);
		}
	}

	/**
	 * Construct a Fingerprint out of a given int value. The fingerprint does
	 * not used more bit than necessary (log_2(value) is used to determine the
	 * necessary number of bits.
	 * 
	 * @param value
	 *            The int to encode as a fingerprint
	 * @return a corresponding Fingerprint instance of the value, which
	 *         initialized length and values w.r.t to value parameter.
	 */
	public static Fingerprint makeFingerprint(final int value) {
		final int bitsRequired = requiredBitsForEncoding(value);
		final Fingerprint tmp = new Fingerprint(bitsRequired);
		for (int j = bitsRequired - 1; j >= 0; j--) {
			final int aBit = value;
			if ((aBit & 1 << j) > 0) {
				tmp.set(bitsRequired - j - 1);
			}
		}
		return tmp;
	}

	/**
	 * Construct a Fingerprint out of a given int value. Adds trailing zero to
	 * match the length if required
	 * 
	 * @param value
	 *            The int to encode as a fingerprint
	 * @return a corresponding Fingerprint instance of the value, which
	 *         initialized length and values w.r.t to value parameter.
	 */
	public static Fingerprint makeFingerprint(final int value, final int length) {
		final int bitsRequired = requiredBitsForEncoding(value);
		if (length < bitsRequired) {
			throw new OutOfRangeException(length, bitsRequired,
					Integer.MAX_VALUE);
		}
		final Fingerprint tmp = new Fingerprint(length);
		for (int j = bitsRequired - 1; j >= 0; j--) {
			final int aBit = value;
			if ((aBit & 1 << j) > 0) {
				tmp.set(length - bitsRequired + bitsRequired - j - 1);
			}
		}
		return tmp;
	}

	/**
	 * To compute log2(value). From
	 * <a>http://stackoverflow.com/questions/3305059
	 * /how-do-you-calculate-log-base-2-in-java-for-integers</a>
	 */
	/*
	 * public static int binlog(int value) // returns 0 for bits=0 { int log =
	 * 0; if ((value & 0xffff0000) != 0) { value >>>= 16; log = 16; } if (value
	 * >= 256) { value >>>= 8; log += 8; } if (value >= 16) { value >>>= 4; log
	 * += 4; } if (value >= 4) { value >>>= 2; log += 2; } return log + (value
	 * >>> 1); }
	 */

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if (this.get(i)) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the length of the fingerprint. Contains all values, including
	 * leading and trailing zeroes.
	 * 
	 * @return the length of the fingerprint
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Append the fingerprint f to the current fingerprint.
	 * 
	 * @param f
	 */
	public void append(final Fingerprint f) {
		if (f == null || f.getLength() == 0) {
			return;
		}
		for (int i = 0; i < f.getLength(); i++) {
			this.set(length + i, f.get(i));
		}
		length += f.getLength();
	}

	public void setLength(final int length) {
		this.length = length;
	}

	/**
	 * 
	 * @param size
	 * @return the number of bits required to encode the given number
	 */
	public static int requiredBitsForEncoding(int size) {
		int count = 0;
		while (size != 0) {
			size = size >>> 1;
			count++;
		}
		return count;
	}

	/**
	 * 
	 * @param value
	 *            original value from which we want to generate all fingerprint
	 *            with a distance of cste.
	 * @param nbBits
	 *            fingerprint number of bits
	 * @param cste
	 *            expected distance between fingerprint of value and all values
	 *            inserted in the returned lsit.
	 * @return all values whose fingerprint as a distance of cste to the
	 *         fingerprint of <b>value</b>.
	 */
	public static List<Integer> listFingerprintsCandidate(final int value,
			final int nbBits, final int cste) {

		// fail fast if nbBits < cste.
		if (nbBits < cste) {
			return Collections.emptyList();
		}

		final List<Integer> candidates = new ArrayList<Integer>();
		// make fingerprint of value
		final Fingerprint first = Fingerprint.makeFingerprint(value, nbBits);

		// generate all fingerprints with cste and only cst bit switching and
		// add them to the returned set.
		// brute force for now. As of time of writing i'm not even sure this wil
		// be usefull in the end
		final int rangeUpperBound = (int) (Math.pow(2, nbBits) - 1);
		for (int i = 0; i < rangeUpperBound; i++) {
			final Fingerprint second = Fingerprint.makeFingerprint(i, nbBits);
			if (new HammingDistance().distance(first, second) == cste) {
				candidates.add(i);
			}
		}
		return candidates;
	}

}
