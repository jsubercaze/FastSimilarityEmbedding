package fr.ujm.lhc.krr.fse.benchmark.core.exceptions;

/**
 * 
 * Exception related to unparsable (unreachabale, wrongly formatted, ...) hashwordnet dictionary.
 * 
 * @author Christophe Gravier
 * 
 */
public class ParsingException extends Exception {

    private static final long serialVersionUID = 1L;

    public ParsingException(String msg) {
        super(msg);
    }
}
