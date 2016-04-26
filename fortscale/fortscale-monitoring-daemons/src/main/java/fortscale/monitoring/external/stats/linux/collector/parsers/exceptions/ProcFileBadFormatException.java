package fortscale.monitoring.external.stats.linux.collector.parsers.exceptions;

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
