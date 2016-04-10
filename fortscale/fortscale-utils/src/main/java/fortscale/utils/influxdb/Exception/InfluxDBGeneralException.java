package fortscale.utils.influxdb.Exception;

/**
 * Created by baraks on 4/10/2016.
 */
public class InfluxDBGeneralException extends Exception {
    public InfluxDBGeneralException(String cmd)
    {
        super(String.format("ERROR: could not execute %s on influxdb",cmd));
    }
}

