package fr.ujm.lhc.krr.fse.hash.tree.kb.parsers;

import org.apache.commons.configuration.ConfigurationException;

import edu.uci.ics.jung.graph.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.UndirectedTree;

/**
 * @author Christophe Gravier, <> Taxonomies we are parsing implements this
 *         behavior.
 * @param <E>
 */

public interface Parsable<V, E> {

	/**
	 * Read a parsable knowledge base from the filesystem. The taxonomies
	 * location are located in src/main/resources/taxonomies.properties, which
	 * is parsed using apache configuration, hence the possible exception.
	 * 
	 * @thorws {@link ConfigurationException} in case the file is not found or
	 *         the <b>nell.path</b> property is not found.
	 * @return the <i>is-a taxonomy</i> in this ontology in the format of our
	 *         in-memory model described in the {@link Tree} class.
	 */
	UndirectedTree<V, E> loadFromFile() throws ConfigurationException;
}
