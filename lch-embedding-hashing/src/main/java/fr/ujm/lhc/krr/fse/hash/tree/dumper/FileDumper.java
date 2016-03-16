package fr.ujm.lhc.krr.fse.hash.tree.dumper;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import fr.ujm.lhc.krr.fse.hash.tree.hasher.TreeHasher;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.result.TreeHashResult;
import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.TreeNode;
import fr.ujm.lhc.krr.fse.hash.tree.utils.TreeUtils;


public class FileDumper {

	@SuppressWarnings("unchecked")
	public static void dumpTreeHashToFile(File input, File output, TreeHasher e) {
		try {
			Tree<String> treeFromJsonFile = TreeUtils.readTreeFromJsonFile(
					input, null);
			TreeHashResult<String> hash = e.hash(treeFromJsonFile);
			Map<TreeNode<?>, ?> hashedLookupTable = hash.getHashedLookupTable();
			StringBuffer sb = new StringBuffer();
			System.out.println(hashedLookupTable.size());
			for (Entry<TreeNode<?>, ?> entry : hashedLookupTable.entrySet()) {
				sb.append(entry.getKey().getNodeValue().toString());
				sb.append("\t");
				sb.append(entry.getValue().toString());
				sb.append("\r\n");
			}
			FileUtils.writeStringToFile(output, sb.toString(),"ascii");
			System.out.println(output.length());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
