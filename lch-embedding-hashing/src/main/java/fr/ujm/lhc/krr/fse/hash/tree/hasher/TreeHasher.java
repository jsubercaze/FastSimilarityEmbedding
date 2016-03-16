package fr.ujm.lhc.krr.fse.hash.tree.hasher;

import java.io.Serializable;

import fr.ujm.lhc.krr.fse.hash.tree.distance.bitset.Distance;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;


/**
 * 
 * @author Julien Subercaze
 * 
 *         10 oct. 2013
 * 
 *         Interface for hashing schemes.
 * @param <T>
 */
public interface TreeHasher extends Serializable {

	@SuppressWarnings("rawtypes")
	public TreeHashResult hash(Tree<?> tree);

	public int signatureSize();

	public Distance<?> getDistance();

}
