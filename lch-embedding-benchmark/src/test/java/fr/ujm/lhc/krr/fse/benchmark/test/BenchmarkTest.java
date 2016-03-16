package fr.ujm.lhc.krr.fse.benchmark.test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.ujm.lhc.krr.fse.benchmark.speedup.HashwordnetBenchmark;


/**
 * Testing class {@link HashwordnetBenchmark}
 * 
 * @author Christophe Gravier
 * 
 */
public class BenchmarkTest {

    @Test
    public void initPairsTest() {
        HashwordnetBenchmark hw = new HashwordnetBenchmark();
        hw.setNbPairs(2);
        hw.initDictionary();
        hw.initPairs();
        assertEquals(2, hw.getPairs().size());
    }

}
