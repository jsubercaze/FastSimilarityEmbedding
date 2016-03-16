package fr.ujm.lhc.krr.fse.benchmark.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.util.OpenBitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import fr.ujm.lhc.krr.fse.benchmark.core.exceptions.ParsingException;



/**
 * 
 * @author Christophe Gravier
 */
public class HashwordnetLoader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HashwordnetLoader.class);

	/**
	 * URL of the hashWordnet dictionary to load in memory.
	 */
	private URL url = null;

	/**
	 * This is the hash Wordnet dictionary. It has been previously hashed and is
	 * loaded for applications in this class. The <code>key</code> is the synset
	 * id as labeled by the <a href="http://lyle.smu.edu/~tspell/jaws/">JAWS</a>
	 * library used by the hasher. The BitSet is the actual binary code
	 * associated to this synset.
	 */
	private Map<Integer, OpenBitSet> hashsynsets = null;

	/**
	 * Use {@link HashwordnetLoader#HashwordnetLoader(String)} instead.
	 */
	@SuppressWarnings("unused")
	private HashwordnetLoader() {
		super();
	}

	public HashwordnetLoader(String url) throws MalformedURLException {
		super();
		this.url = new URL(url);

		/*
		 * Some remarks on the following line : Wordnet31 got 82275 synsets
		 * (hence the HashMap initialization, ceiled) and HashMap is guaranteed
		 * O(1) for reading in this case since the synsets are identified by an
		 * Integer, whose hashCode is the intValue it wraps.
		 */
		this.hashsynsets = new HashMap<>(90_000);
	}

	/**
	 * 
	 * @throws IOException
	 *             when the URL {@link HashwordnetLoader#url} cannot be reached.
	 * @throws ParsingException
	 *             when a line is corrupted. This means that if a single line is
	 *             wrongly formatted, the entire loading process fails.
	 */
	public void load() throws ParsingException {
		List<String> lines;
		try {
			lines = Resources.readLines(this.url, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.error("Cannot access " + this.url, e);
			throw new ParsingException("Cannot access " + this.url);
		}
		for (String line : lines) {
			HashwordnetUtils.insertFromCsvLine(this.hashsynsets, line);
		}
	}

	public URL getUrl() {
		return this.url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Map<Integer, OpenBitSet> getHashsynsets() {
		return hashsynsets;
	}

	public void setHashsynsets(Map<Integer, OpenBitSet> hashsynsets) {
		this.hashsynsets = hashsynsets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.hashsynsets == null) ? 0 : this.hashsynsets.hashCode());
		result = prime * result
				+ ((this.url == null) ? 0 : this.url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof HashwordnetLoader))
			return false;
		HashwordnetLoader other = (HashwordnetLoader) obj;
		if (this.hashsynsets == null) {
			if (other.hashsynsets != null)
				return false;
		} else if (!this.hashsynsets.equals(other.hashsynsets))
			return false;
		if (this.url == null) {
			if (other.url != null)
				return false;
		} else if (!this.url.equals(other.url))
			return false;
		return true;
	}
}
