# Fast Semantic Similarity Embedding
Fast LCH word to word similarity Computation

This library (FSE) computes a metric embedding from the Wordnet hypernym shortest path-distance (Leacock and Chodorow/LCH similarity measure) into the Hamming hypercube of dimension 128.

This allows very fast computation of approximate, highly correlated LCH similarities between two words. 
Compared to existing libraries such as [wS4J](https://code.google.com/archive/p/ws4j/), 
FSE is up to 3000 faster and is also way more compact in memory.

## How it works

The relatedness measure proposed by [Leacock and Chodorow](https://scholar.google.com/scholar?q=Combining+local+context+and+WordNet+similarity+for+word+sense+identification) (lch) is `-log (length / (2 * D))`, where `length` is the length of the shortest path between the two synsets (using node-counting) 
and `D` is the maximum depth of the taxonomy.

To compute the distance between two nodes in Wordnet, an algorithm must first compute `length` 
which is the shortest path between the two nodes. This shortest path computation on the Wordnet hypernym lattice is equivalent to a shortest path in a graph, 
i.e. the complexity is `O(|V|+|E|)` using a standard BFS approach.

FSE uses a different approach, it firsts computes an embedding of all Wordnet nodes into the Hamming hypercube. Concretely, each node
is given a 128-bit signature, these signatures have the property that their pairwise Hamming distances are very correlated to their Leacock and Chodorow similarities (Pearson .819; Spearma .82).

Using FSE, the distance between two words is computed like this:
```
distance("dog","cat")
dog:                   |0101110101|
cat:                   |0101110111|
XOR(dog,cat) =         |0000000010|
POPCNT(XOR(dog,cat)) = 1
```

XOR and POPCNT being fast instructions on modern processors, this allows very fast computations of pairwise semantic similarities.
## Project Structure

The project is a Maven project containing different submodules :

| Command | Description |
| --- | --- |
| `lch-embedding` | parent project |
| `lch-embedding-benchmark` | [JMH](http://openjdk.java.net/projects/code-tools/jmh/) Benchmarks to evaluate runtime performance |
| `lch-embedding-core` | basic datastructures used in almost in every module |
| `lch-embedding-hashing` | various algorithms to perform the metric embedding |
| `lch-embedding-jaws` | extended version of [JAWS](http://lyle.smu.edu/~tspell/jaws/) that allows to access Synset IDs |
| `lch-embedding-kb-import` | [Wordnet](https://wordnet.princeton.edu/) import |
| `lch-embedding-utils` | misc, including measures on tree: branching factor, depth,.. |


## Reference

The research behind this project is described in the following [paper](https://hal.archives-ouvertes.fr/hal-01166163/document) :
```
Julien Subercaze, Christophe Gravier, Frédérique Laforest:
On metric embedding for boosting semantic similarity computations. ACL (2) 2015: 8-14
```

