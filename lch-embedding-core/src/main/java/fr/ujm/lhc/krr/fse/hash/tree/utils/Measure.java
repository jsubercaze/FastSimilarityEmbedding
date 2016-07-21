package fr.ujm.lhc.krr.fse.hash.tree.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Measure {

	List<Double> values;

	boolean uptodate = false;

	double[] array;

	StandardDeviation std = new StandardDeviation();
	Mean mean = new Mean();

	public Measure() {
		super();
		this.values = new ArrayList<Double>();
	}

	public void addValue(double value) {
		// Skip leaves
		if (value > 0.0) {
			values.add(value);
			uptodate = false;
		}
	}

	/**
	 * 
	 * @return the average branching factor
	 */
	public double getAverage() {
		update();
		return mean.evaluate(array);
	}

	/**
	 * 
	 * @return the standard deviation of the branching factor
	 */
	public double getStd() {
		update();
		return std.evaluate(array);
	}

	private void update() {
		if (!uptodate) {
			array = new double[values.size()];
			int i = 0;
			for (Double value : values) {
				array[i++] = value;
			}
			uptodate = true;
		}
	}

	/**
	 * 
	 * @return the histogram
	 */
	public Map<Double, Integer> histogram() {
		@SuppressWarnings("unchecked")
		Map<Double, Integer> cardinalityMap = CollectionUtils
				.getCardinalityMap(values);
		SortedMap<Double, Integer> sorted = new TreeMap<>();
		sorted.putAll(cardinalityMap);
		return sorted;
	}

	@Override
	public String toString() {
		return "Measure [std=" + this.getStd() + ", mean=" + this.getAverage()
				+ ", histogram=" + this.histogram() + "]";
	}

	public double getMax() {
		update();
		return NumberUtils.max(array);
	}

	public double getMin() {
		update();
		return NumberUtils.min(array);
	}

}
