package fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions;

/**
 * Created by galiar on 19/04/2016.
 */
public class ProcFileReadFailureException extends ProcFileParserException {

    public ProcFileReadFailureException(String message, Throwable cause){
        super(message,cause);
    }
}
