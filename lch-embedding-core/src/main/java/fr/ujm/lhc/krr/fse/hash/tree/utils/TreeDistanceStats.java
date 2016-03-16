package fr.ujm.lhc.krr.fse.hash.tree.utils;

import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import fr.ujm.lhc.krr.fse.hash.tree.UndirectedTree;


/**
 * Stats on an {@link UndirectedTree}, mainly :
 * <ul>
 * <li>Table of distanes associating a distance size with the number of distances mathcing this size. This is stored in
 * {@link TreeDistanceStats#distances}</li>
 * <li>The actual list of constraints on distances matching two edges identified by their indexed number in a pre-order DFS, with their distance. This
 * is stored in {@link TreeDistanceStats#constraints}</li>
 * </ul>
 * 
 * @author Christophe Gravier, <>
 * @param <V>
 */
public class TreeDistanceStats<V> {

	Multiset<Integer> distances = null;
	Table<V, V, Integer> constraints = null;
	public TreeDistanceStats(Multiset<Integer> distances, Table<V, V, Integer> constraints) {
		super();
		this.distances = distances;
		this.constraints = constraints;
	}

	/**
	 * Call this when you are sure you are no longer needing the {@link TreeDistanceStats#distances} member. It will be set to null, so that the
	 * garbage collector will handle it later.
	 */
	public void clearDistancesCount() {
		this.distances = null;
	}

	public Multiset<Integer> getDistances() {
		return distances;
	}

	public void setDistances(Multiset<Integer> distances) {
		this.distances = distances;
	}

	public Table<V, V, Integer> getConstraints() {
		return constraints;
	}

	public void setConstraints(Table<V, V, Integer> constraints) {
		this.constraints = constraints;
	}

}
