/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics;

import java.util.Comparator;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.Fingerprint;


/**
 * @author Julien Subercaze
 *
 * 28 oct. 2013
 *
 * 
 */
public class FingerprintBitOnComparator implements Comparator<Fingerprint> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Fingerprint o1, Fingerprint o2) {
		return Integer.compare(o1.cardinality(), o2.cardinality());
	}

}
