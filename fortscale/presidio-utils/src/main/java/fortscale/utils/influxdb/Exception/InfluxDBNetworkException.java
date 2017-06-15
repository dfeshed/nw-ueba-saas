package fortscale.utils.influxdb.Exception;


public class InfluxDBNetworkException extends RuntimeException {
    public InfluxDBNetworkException(String cmd, Throwable cause) {
        super(String.format("ERROR: could not execute %s on influxdb due to network error", cmd), cause);
    }
}
