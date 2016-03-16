package fr.ujm.lhc.krr.fse.save;

import java.io.File;

import fr.ujm.lhc.krr.fse.hash.tree.dumper.FileDumper;
import fr.ujm.lhc.krr.fse.hash.tree.hasher.heuristics.AdaptiveLeftHasherEnhanced;


public class CreateDump {

	public static void main(String[] args) {
		FileDumper.dumpTreeHashToFile(
				new File("c:/temp/wordnetUS/simple.tree"), new File(
						"data/wordnetEmbedding.txt"),
				new AdaptiveLeftHasherEnhanced(true, true, true));
	}
}
