package fortscale.utils.process.pidService.exceptions;

/**
 * Created by baraks on 5/2/2016.
 */
public class ErrorAccessingPidFile extends RuntimeException{
    public ErrorAccessingPidFile(String fileName)
    {
        super(String.format("ERROR: could not access pid file %s",fileName));
    }
}
