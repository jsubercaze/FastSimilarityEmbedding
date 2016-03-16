/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package fr.ujm.lhc.krr.fse.benchmark.speedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import fr.ujm.lhc.krr.fse.benchmark.utils.leacock.SynsetPair;

/**
 * 
 * This is the benchmark of <a href=
 * "https://scholar.google.com/scholar?q=Combining+local+context+and+WordNet+similarity+for+word+sense+identification"
 * >Leacock's semantic similarity based on wordnet</a>, using <a
 * href="https://code.google.com/p/ws4j/">WS4j implementation</a>. pairwise word semantic
 * similarity.
 * 
 * @author Christophe Gravier
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(timeUnit = TimeUnit.MILLISECONDS)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
public class LeacockWs4jBenchmark {

    @Param({ "1000", "10000", "100000", "1000000" })
    private int nbPairs;

    private final String coreWordnetNounsFile = "nounscorewordnet.txt";

    private ILexicalDatabase db = null;
    private RelatednessCalculator relatednessCalculator = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(LeacockWs4jBenchmark.class);

    private List<SynsetPair> pairs;

    public LeacockWs4jBenchmark() {
        this.pairs = new ArrayList<>();

    }

    /**
     * Fixture to initialize the dictionary before the benchmark.
     */
    @Setup(Level.Trial)
    public void initDictionary() {
        this.db = new NictWordNet();
        this.relatednessCalculator = new LeacockChodorow(this.db);
    }

    /**
     * Fixture to initialize a set of word pairs on which this iteration will made Hamming distance
     * computation benchmark.
     */
    @Setup(Level.Iteration)
    public void initPairs() {
        this.pairs = new ArrayList<SynsetPair>();

        LOGGER.info("Generating " + this.nbPairs + " word pairs for this Leacock Chodorow iteration.");

        List<String> words = loadCoreNouns();
        LOGGER.info("Loaded " + words.size() + " core nouns.");

        Random rdm = new Random();
        for (int i = 0; i < this.nbPairs; i++) {
            int rdmIdxWord1 = rdm.nextInt(words.size());
            int rdmIdxWord2 = rdm.nextInt(words.size());
            Concept c1 = this.db.getMostFrequentConcept(words.get(rdmIdxWord1), "n");
            Concept c2 = this.db.getMostFrequentConcept(words.get(rdmIdxWord2), "n");
            this.pairs.add(new SynsetPair(c1, c2));
        }
    }

    /**
     * Load the corpus of core Wordnet nouns, a line separated text file named present in
     * <code>this.coreWordnetNounsFile</code> the classpath (default is located in
     * src/main/resources).
     * 
     * @return the list of core Wordnet nouns
     */
    private List<String> loadCoreNouns() {
        List<String> words = new ArrayList<>();
        String line = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(LeacockWs4jBenchmark.class.getClassLoader().getResourceAsStream(
                this.coreWordnetNounsFile)));) {
            while ((line = in.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot load core wordnet noun", e);
        }
        return words;
    }

    /**
     * @param pairs
     * @param dico
     * @return a dummy value in order to deal with dead code issue (see <i>dead code</i> section <a
     *         href="http://java-performance.info/jmh/">here</a>)
     */
    @Benchmark
    public double benchmarkLeacockWs4j() {
        double similarity = 0;
        for (SynsetPair couple : this.pairs) {
            Concept synset1 = couple.getSynset1();
            Concept synset2 = couple.getSynset2();
            similarity = this.relatednessCalculator.calcRelatednessOfSynset(synset1, synset2).getScore();
        }
        return similarity;
    }

    public int getNbPairs() {
        return this.nbPairs;
    }

    public void setNbPairs(int nbPairs) {
        this.nbPairs = nbPairs;
    }

    public ILexicalDatabase getDb() {
        return this.db;
    }

    public void setDb(ILexicalDatabase db) {
        this.db = db;
    }

    public RelatednessCalculator getRelatednessCalculator() {
        return this.relatednessCalculator;
    }

    public void setRelatednessCalculator(RelatednessCalculator relatednessCalculator) {
        this.relatednessCalculator = relatednessCalculator;
    }

    public List<SynsetPair> getPairs() {
        return this.pairs;
    }

    public void setPairs(List<SynsetPair> pairs) {
        this.pairs = pairs;
    }

    public String getCoreWordnetNounsFile() {
        return this.coreWordnetNounsFile;
    }
}
