package fortscale.monitoring.external.stats.collector.parsers;

/**
 * Created by galiar on 17/04/2016.
 *
 * Exception class for proc files parsing errors.
 * mostly passed from the porc parser to the different collectors.
 */
public class ProcFileParserException extends Exception {

    private String filename;

    public ProcFileParserException(String filename){
        super(String.format( "Couldn't parse proc file: {}" , filename));
        this.filename = filename;
    }

}
