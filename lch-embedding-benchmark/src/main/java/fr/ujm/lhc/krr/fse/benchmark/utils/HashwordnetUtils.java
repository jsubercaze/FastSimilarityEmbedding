package fr.ujm.lhc.krr.fse.benchmark.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.ConfigurationException;

import org.apache.lucene.util.OpenBitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Longs;

import fr.ujm.lhc.krr.fse.benchmark.core.exceptions.ParsingException;
import fr.ujm.lhc.krr.fse.benchmark.core.exceptions.PropertiesParsingException;

/**
 * Utility functions for internal behavior of the benchmark.
 * 
 * @author Christophe Gravier
 * 
 */
public class HashwordnetUtils {

	/**
	 * Property file for hashwordnet benchmark that shoudl be in the classapth
	 * at runtime.
	 */
	public final static String configurationFile = "benchmark.properties";

	private final static String hashwordnetProperty = "HASHWORDNET_URL";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HashwordnetUtils.class);

	/**
	 * Ya shall not call the default constructor since {@link HashwordnetUtils}
	 * is a utilty class.
	 */
	private HashwordnetUtils() {

	}

	/**
	 * Parse a string holding a synsetid and its binary code, tab-delimited and
	 * add it to a HashWordnet dictionary. If the synset was already known, its
	 * binary code is simply overwritten.
	 * 
	 * @param hashsynsets
	 *            the hashWordnet dictionary in which to put or update the
	 * @param line
	 *            tab-delimited pair of synsetid and its binary code.
	 * @throws ParsingException
	 *             when the line contains more or less than 2 token after a
	 *             tokenization based on a tabulation.
	 */
	public static void insertFromCsvLine(Map<Integer, OpenBitSet> hashsynsets,
			String line) throws ParsingException {

		StringTokenizer st = new StringTokenizer(line, "\t");

		// fail fast.
		if (st.countTokens() != 2) {
			throw new ParsingException("line " + line
					+ " is badly formatted (shoud be synsetid\\tbinarycode)");
		}

		String synsetid = st.nextToken();
		String binaryCode = st.nextToken();

		try {
			long[] binaryStringToLongArray = HashwordnetUtils
					.binaryStringToLongArray(binaryCode);
			hashsynsets.put(Integer.parseInt(synsetid), new OpenBitSet(
					binaryStringToLongArray, binaryStringToLongArray.length));
		} catch (NumberFormatException e) {
			LOGGER.error("Error converting synsetid (" + synsetid
					+ ") to an integer", e);
		}

	}

	/**
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws org.apache.commons.configuration2.ex.ConfigurationException
	 */
	public static String readHashwordnetProperty()
			throws PropertiesParsingException {

		InputStream resourceAsStream = HashwordnetUtils.class.getClassLoader()
				.getResourceAsStream(configurationFile);
		if (resourceAsStream == null) {
			throw new PropertiesParsingException("Sorry, unable to find "
					+ configurationFile);
		}

		// load a properties file from class path, inside static method
		Properties prop = new Properties();
		try {
			prop.load(resourceAsStream);
		} catch (IOException e) {
			throw new PropertiesParsingException(
					"Sorry, unable to parse property file " + configurationFile);
		}

		return prop.getProperty(hashwordnetProperty);
	}

	/**
	 * Transform a binary string of an arbitrary size in an array of
	 * <code>long</code>.<br />
	 * For example, the binary string (space inserted for the sake of
	 * readability, those are not remove by the function !) :
	 * 
	 * <pre>
	 * 01 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
	 * </pre>
	 * 
	 * is converted to an array <code>array</code> so that array[0] = 16 and
	 * array[1] = 0. (the method fills with heading zeros if needed in order to
	 * fit 64-bit long).
	 * 
	 * @param binary
	 *            the string to format.
	 * @return the array of long corresponding to the binary string
	 *         representation.
	 */
	public static long[] binaryStringToLongArray(String binary) {
		BigInteger bi = new BigInteger(binary, 2);
		byte[] b = bi.toByteArray();

		int headingZeros = 8 - b.length % 8;

		byte[] padded = new byte[b.length + headingZeros];

		int i = 0;
		while (i < headingZeros) {
			padded[i] = 0x00;
			i++;
		}
		while (i < b.length + headingZeros) {
			padded[i] = b[i - headingZeros];
			i++;
		}

		int size = (b.length + headingZeros) / 8;
		long[] lgs = new long[size];
		for (int k = 0; k < size; k++) {
			lgs[k] = Longs.fromByteArray(Arrays.copyOfRange(padded, k * 8,
					k * 8 + 8));
		}
		return lgs;
	}
}
