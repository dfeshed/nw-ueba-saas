package fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions;

/**
 * Created by galiar on 19/04/2016.
 */
public class ProcFileParserBadNumberFormatException extends ProcFileParserException {

    public ProcFileParserBadNumberFormatException(String message, Throwable cause){
        super(message,cause);
    }

}
