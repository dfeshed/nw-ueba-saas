package fortscale.monitoring.external.stats.collector.parsers.exceptions;

/**
 * Created by galiar on 19/04/2016.
 */
public class ProcFileBadNumberFormatException extends ProcFileParserException {


    private String numberTryingToConvert;

    public ProcFileBadNumberFormatException(String message, Throwable cause, String numberTryingToConvert){
        super(message,cause);
        this.numberTryingToConvert = numberTryingToConvert;
    }

    public String getNumberTryingToConvert() {
        return numberTryingToConvert;
    }

}
