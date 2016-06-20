package fortscale.utils.influxdb.Exception;


public class InfluxDBRuntimeException extends RuntimeException{
    public InfluxDBRuntimeException(String cmd, Throwable cause)
    {
        super(String.format("ERROR: could not execute %s on influxdb",cmd),cause);
    }
}

