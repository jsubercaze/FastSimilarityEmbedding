package fr.ujm.lhc.krr.fse.benchmark.utils.leacock;

import edu.cmu.lti.lexical_db.data.Concept;

/**
 * Basically a POJO holding a pair of {@link Concept}.
 * 
 * @author Christophe Gravier
 * 
 */
public class SynsetPair {

    private Concept synset1;
    private Concept synset2;

    public SynsetPair(Concept synset1, Concept synset2) {
        super();
        this.synset1 = synset1;
        this.synset2 = synset2;
    }

    public Concept getSynset1() {
        return this.synset1;
    }

    public void setSynset1(Concept synset1) {
        this.synset1 = synset1;
    }

    public Concept getSynset2() {
        return this.synset2;
    }

    public void setSynset2(Concept synset2) {
        this.synset2 = synset2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.synset1 == null) ? 0 : this.synset1.hashCode());
        result = prime * result + ((this.synset2 == null) ? 0 : this.synset2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SynsetPair other = (SynsetPair) obj;
        if (this.synset1 == null) {
            if (other.synset1 != null)
                return false;
        } else if (!this.synset1.equals(other.synset1))
            return false;
        if (this.synset2 == null) {
            if (other.synset2 != null)
                return false;
        } else if (!this.synset2.equals(other.synset2))
            return false;
        return true;
    }
}
