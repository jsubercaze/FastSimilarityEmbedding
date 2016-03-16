/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.errorestimator;

/**
 * Beans for evaluation result storage
 * 
 * @author Julien Subercaze
 * 
 *         16 oct. 2013
 * 
 * 
 */
public class TreeEvaluationResult {

	double pearsonsCorrelation;
	double spearmansCorrelation;
	double kendallTausCorrelation;
	double rmse;
	double normalizedRmse;
	int bitsOracle;
	int bitsHeuristic;
	double throughputOracle;
	double throughputHeuristic;
	double expansion;
	double contraction;
	double distortion;
	private String name;

	/**
	 * 
	 */
	public TreeEvaluationResult(String name) {
		super();
		this.name = name;
	}

	public int getBitsOracle() {
		return bitsOracle;
	}

	public void setBitsOracle(int bitsOracle) {
		this.bitsOracle = bitsOracle;
	}

	public int getBitsHeuristic() {
		return bitsHeuristic;
	}

	public void setBitsHeuristic(int bitsHeuristic) {
		this.bitsHeuristic = bitsHeuristic;
	}

	public double getNormalizedRmse() {
		return normalizedRmse;
	}

	public void setNormalizedRMSE(double normalizedRmse) {
		this.normalizedRmse = normalizedRmse;
	}

	public double getRmse() {
		return rmse;
	}

	public void setRMSE(double rmse) {
		this.rmse = rmse;
	}

	public double getPearsonsCorrelation() {
		return pearsonsCorrelation;
	}

	public void setPearsonsCorrelation(double pearsonsCorrelation) {
		this.pearsonsCorrelation = pearsonsCorrelation;
	}

	public double getSpearmansCorrelation() {
		return spearmansCorrelation;
	}

	public void setSpearmansCorrelation(double spearmansCorrelation) {
		this.spearmansCorrelation = spearmansCorrelation;
	}

	public double getKendallTausCorrelation() {
		return kendallTausCorrelation;
	}

	public void setKendallTausCorrelation(double kendallTausCorrelation) {
		this.kendallTausCorrelation = kendallTausCorrelation;
	}

	public double getThroughputOracle() {
		return throughputOracle;
	}

	public void setThroughputOracle(double throughputOracle) {
		this.throughputOracle = throughputOracle;
	}

	public double getThroughputHeuristic() {
		return throughputHeuristic;
	}

	public void setThroughputHeuristic(double throughputHeuristic) {
		this.throughputHeuristic = throughputHeuristic;
	}

	public double getExpansion() {
		return expansion;
	}

	public void setExpansion(double expansion) {
		this.expansion = expansion;
	}

	public double getContraction() {
		return contraction;
	}

	public void setContraction(double contraction) {
		this.contraction = contraction;
	}

	public double getDistortion() {
		return distortion;
	}

	public void setDistortion(double distortion) {
		this.distortion = distortion;
	}

	@Override
	public String toString() {
		return "EvaluationResult [pearsonsCorrelation=" + pearsonsCorrelation
				+ ", spearmansCorrelation=" + spearmansCorrelation
				+ ", kendallTausCorrelation=" + kendallTausCorrelation
				+ ", rmse=" + rmse + ", normalizedRmse=" + normalizedRmse
				+ ", bitsOracle=" + bitsOracle + ", bitsHeuristic="
				+ bitsHeuristic + ", throughputOracle=" + throughputOracle
				+ ", throuthputHeuristic=" + throughputHeuristic + ", name="
				+ name + "]\n";
	}

	public String[] toStringArray() {
		String[] res = new String[12];
		res[0] = name;
		res[1] = "" + bitsHeuristic;
		res[2] = "" + normalizedRmse;
		res[3] = "" + rmse;
		res[4] = "" + pearsonsCorrelation;
		res[5] = "" + spearmansCorrelation;
		res[6] = "" + kendallTausCorrelation;
		res[7] = "" + throughputHeuristic;
		res[8] = "" + throughputOracle;
		res[9] = "" + expansion;
		res[10] = "" + contraction;
		res[11] = "" + distortion;
		return res;
	}

}
