package fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions;

/**
 * Created by galiar on 19/04/2016.
 */
public class ProcFileBadFormatException extends ProcFileParserException {

    public ProcFileBadFormatException(String message){
        super(message,null);
    }

    public ProcFileBadFormatException(String message, Throwable cause){
        super(message,cause);
    }

}
