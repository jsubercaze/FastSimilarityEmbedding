/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.distance.bitset;

/**
 * Interface for distance comparison
 * 
 * @author Julien Subercaze
 * 
 *         16 oct. 2013
 * 
 * 
 */
public interface Distance<T> {

	public int distance(T hash1, T hash2);
}
