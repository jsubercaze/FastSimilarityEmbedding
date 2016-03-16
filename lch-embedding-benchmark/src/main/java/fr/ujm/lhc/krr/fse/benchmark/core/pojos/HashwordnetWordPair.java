package fr.ujm.lhc.krr.fse.benchmark.core.pojos;
/**
 * 
 * @author Christophe Gravier
 *
 */
public class HashwordnetWordPair {

	int word1;
	int word2;

	/**
	 * Shall not be called. Use
	 * {@link HashwordnetWordPair#HashwordnetWordPair(Integer, Integer)}
	 * instead.
	 */
	@SuppressWarnings("unused")
	private HashwordnetWordPair() {

	}

	public HashwordnetWordPair(Integer word1, Integer word2) {
		super();
		this.word1 = word1;
		this.word2 = word2;
	}

	public Integer getWord1() {
		return word1;
	}

	public void setWord1(Integer word1) {
		this.word1 = word1;
	}

	public Integer getWord2() {
		return word2;
	}

	public void setWord2(Integer word2) {
		this.word2 = word2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + word1;
		result = prime * result + word2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashwordnetWordPair other = (HashwordnetWordPair) obj;
		if (word1 != other.word1)
			return false;
		if (word2 != other.word2)
			return false;
		return true;
	}

}
