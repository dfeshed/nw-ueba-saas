package fortscale.utils.process.processInfo.exceptions;


public class ErrorAccessingPidFile extends RuntimeException{
    public ErrorAccessingPidFile(String fileName)
    {
        super(String.format("ERROR: could not access pid file %s",fileName));
    }
}
