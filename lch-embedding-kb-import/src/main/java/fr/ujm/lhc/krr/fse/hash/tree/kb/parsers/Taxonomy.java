package fr.ujm.lhc.krr.fse.hash.tree.kb.parsers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public abstract class Taxonomy<V,E> implements Parsable<V,E> {

	protected void dumpToTempFile(File targetFile, String jsonTaxonomy) throws IOException {
		FileUtils.writeStringToFile(targetFile, jsonTaxonomy);
	}
}