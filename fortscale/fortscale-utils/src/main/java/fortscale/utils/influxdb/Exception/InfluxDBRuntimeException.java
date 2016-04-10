package fortscale.utils.influxdb.Exception;

/**
 * Created by baraks on 4/10/2016.
 */
public class InfluxDBRuntimeException extends RuntimeException{
    public InfluxDBRuntimeException(String cmd, Throwable cause)
    {
        super(String.format("ERROR: could not execute %s on influxdb",cmd),cause);
    }
}

