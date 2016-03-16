/**
 * 
 */
package fr.ujm.lhc.krr.fse.hash.tree.hasher.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashFingerprintResult;


/**
 * Dump Hashwordnet tree on file system
 * 
 * @author Julien Subercaze
 * 
 */
public class DumpTree {

	public static void dump(final TreeHashFingerprintResult treehash,
			final File f) {
		try {
			final ObjectOutput output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(f)));
			output.writeObject(treehash);
			output.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
