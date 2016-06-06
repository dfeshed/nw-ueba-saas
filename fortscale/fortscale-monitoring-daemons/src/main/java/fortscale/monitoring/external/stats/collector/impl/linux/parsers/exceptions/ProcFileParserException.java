package fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions;

/**
 * Created by galiar on 17/04/2016.
 *
 * Exception class for proc files parsing errors.
 * mostly passed from the porc parser to the different collectors.
 */
public abstract class ProcFileParserException extends Exception {

    public ProcFileParserException(String message,Throwable cause){
        super(message,cause);
    }

}
