package fortscale.monitoring.external.stats.linux.collector.parsers.exceptions;

/**
 * Created by galiar on 19/04/2016.
 */
public class ProcFileBadReadingException extends ProcFileParserException {

    public ProcFileBadReadingException(String message, Throwable cause){
        super(message,cause);
    }

}
