package fr.ujm.lhc.krr.fse.benchmark.speedup;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.util.OpenBitSet;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ujm.lhc.krr.fse.benchmark.core.exceptions.ParsingException;
import fr.ujm.lhc.krr.fse.benchmark.core.exceptions.PropertiesParsingException;
import fr.ujm.lhc.krr.fse.benchmark.core.pojos.HashwordnetWordPair;
import fr.ujm.lhc.krr.fse.benchmark.utils.HashwordnetLoader;
import fr.ujm.lhc.krr.fse.benchmark.utils.HashwordnetUtils;



/**
 * 
 * This is the <a href="http://hg.openjdk.java.net/code-tools/jmh">JMH</a> benchmark of Hashwordnet
 * pairwise word semantic similarity.
 * 
 * @author Christophe Gravier
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(timeUnit = TimeUnit.MILLISECONDS)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
public class HashwordnetBenchmark {

    @Param({ "1000", "10000", "100000", "1000000" })
    private int nbPairs;

    private static final Logger LOGGER = LoggerFactory.getLogger(HashwordnetBenchmark.class);

    private List<HashwordnetWordPair> pairs;
    private Map<Integer, OpenBitSet> dico;
    private String onlineHashwordnet = null;

    public HashwordnetBenchmark() {
        this.pairs = new ArrayList<HashwordnetWordPair>();
    }

    /**
     * Fixture to initialize the dictionary before the benchmark.
     */
    @Setup(Level.Trial)
    public void initDictionary() {
        try {
            this.onlineHashwordnet = HashwordnetUtils.readHashwordnetProperty();
            HashwordnetLoader hwl = new HashwordnetLoader(this.onlineHashwordnet);
            hwl.load();
            this.dico = hwl.getHashsynsets();
        } catch (MalformedURLException e) {
            LOGGER.error("Cannot benchmark since configured hashwordnet url (" + this.onlineHashwordnet + ") is wrongly formatted", e);
        } catch (ParsingException e) {
            LOGGER.error("Cannot parse benchmark.properties file.", e);
        } catch (PropertiesParsingException e) {
            LOGGER.error("Cannot find " + HashwordnetUtils.configurationFile + " file.", e);
        }
    }

    /**
     * Fixture to initialize a set of word pairs on which this iteration will made Hamming distance
     * computation benchmark.
     */
    @Setup(Level.Iteration)
    public void initPairs() {
        this.pairs = new ArrayList<HashwordnetWordPair>();
        LOGGER.info("Generating " + this.nbPairs + " word pairs for this Hashwordnet iteration.");
        Integer[] allSynsets = this.dico.keySet().toArray(new Integer[this.dico.keySet().size()]);
        Random rdm = new Random();
        for (int i = 0; i < this.nbPairs; i++) {
            int rdmIdxWord1 = rdm.nextInt(this.dico.keySet().size());
            int rdmIdxWord2 = rdm.nextInt(this.dico.keySet().size());
            this.pairs.add(new HashwordnetWordPair(allSynsets[rdmIdxWord1], allSynsets[rdmIdxWord2]));
        }
    }

    /**
     * 
     * 
     * @param pairs
     * @param dico
     * @return a dummy value in order to deal with dead code issue (see <i>dead code</i> section <a
     *         href="http://java-performance.info/jmh/">here</a>)
     */
    @Benchmark
    public int benchmarkHashwordnet() {
        int similarity = 0;
        for (HashwordnetWordPair couple : this.pairs) {
            OpenBitSet w1 = this.dico.get(couple.getWord1());
            OpenBitSet w2 = this.dico.get(couple.getWord2());
            similarity = similarity(w1, w2);
        }
        return similarity;
    }

    public int getNbPairs() {
        return this.nbPairs;
    }

    public void setNbPairs(int nbPairs) {
        this.nbPairs = nbPairs;
    }

    public List<HashwordnetWordPair> getPairs() {
        return this.pairs;
    }

    public void setPairs(List<HashwordnetWordPair> pairs) {
        this.pairs = pairs;
    }

    public Map<Integer, OpenBitSet> getDico() {
        return this.dico;
    }

    public void setDico(Map<Integer, OpenBitSet> dico) {
        this.dico = dico;
    }

    public String getOnlineHashwordnet() {
        return this.onlineHashwordnet;
    }

    public void setOnlineHashwordnet(String onlineHashwordnet) {
        this.onlineHashwordnet = onlineHashwordnet;
    }

    /**
     * Compute the Hamming distance between two BitSet. None are affected by the method call.
     * 
     * @param w1
     * @param w2
     * @return the Hamming distance between <code>w1</code> and <code>w2</code>
     */
    public static int similarity(OpenBitSet w1, OpenBitSet w2) {
        return (int) OpenBitSet.xorCount(w1, w2);
    }

}
