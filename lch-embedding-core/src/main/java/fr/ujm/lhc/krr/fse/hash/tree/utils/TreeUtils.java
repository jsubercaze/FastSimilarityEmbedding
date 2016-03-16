package fr.ujm.lhc.krr.fse.hash.tree.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.commons.io.FileUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.ujm.lhc.krr.fse.hash.tree.Tree;
import fr.ujm.lhc.krr.fse.hash.tree.UndirectedTree;

/**
 * * Misc methods for tree
 * <p>
 * <ul>
 * <li>Convert tree to JSON</li>
 * <li>Save and read from File</li>
 * </ul>
 * <p>
 * Makes extensive use of Google GSON Library
 * 
 * 
 * @author Julien Subercaze
 * 
 * 
 * 
 */
public class TreeUtils<V, E> {

	public static <T> boolean saveTreeAsJsonFile(Tree<T> tree, File file,
			Class<T> cls) {
		try {
			FileUtils.writeStringToFile(file, treeToJSONString(tree, cls));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Convert the Tree to a JSON String
	 * 
	 * @param tree
	 * @param cls
	 * @return
	 */
	public static <T> String treeToJSONString(Tree<T> tree, Object o) {
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(new ExclusionStrategy() {

					public boolean shouldSkipClass(Class<?> clazz) {
						return false;
					}

					/**
					 * Avoid circular definition of parent
					 */
					public boolean shouldSkipField(FieldAttributes f) {
						return f.getName().equals("parent")
								|| f.getName().equals("nodeset")
								|| f.getName().equals("tree");
					}

				})
				/**
				 * Use serializeNulls method if you want To serialize null
				 * values By default, Gson does not serialize null values
				 */
				.serializeNulls().create();

		Type listType = new TypeToken<Tree<String>>() {
		}.getType();
		return gson.toJson(tree, listType).toString();
	}

	public static <T> Tree<T> readTreeFromJsonFile(File f, Class<T> nodeType)
			throws IOException {
		if (f.exists()) {
			Tree<T> t = readTreeFromJSONString(FileUtils.readFileToString(f),
					nodeType);
			t.prepare();
			return t;
		} else {
			throw new FileNotFoundException("File " + f.getAbsolutePath()
					+ " not found");
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> Tree<T> readTreeFromJSONString(final String s,
			final Class<T> nodeType) {
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(new ExclusionStrategy() {

					public boolean shouldSkipClass(final Class<?> clazz) {
						return false;
					}

					/**
					 * Avoid circular definition of parent
					 */
					public boolean shouldSkipField(final FieldAttributes f) {
						return f.getName().equals("parent");
					}

				})

				/**
				 * Use serializeNulls method if you want To serialize null
				 * values By default, GSON does not serialize null values
				 */
				.serializeNulls().create();
		Type listType = new TypeToken<Tree<String>>() {
		}.getType();
		return (Tree<T>) gson.fromJson(s, listType);
	}

	/**
	 * In-memory models mapping function that converts a {@link Tree} instance
	 * into a {@link UndirectedTree} instance.
	 * 
	 * @param someTree
	 *            a {@link Tree} to get converted
	 * @return
	 */
	public UndirectedTree<V, E> convertToUndirectedTree(Tree<String> someTree) {
		// TODO Auto-generated method stub
		return null;
	}
}
