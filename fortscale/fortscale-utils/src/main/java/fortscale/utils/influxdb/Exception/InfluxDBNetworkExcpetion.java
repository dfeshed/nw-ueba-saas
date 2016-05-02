package fortscale.utils.influxdb.Exception;


public class InfluxDBNetworkExcpetion extends RuntimeException {
    public InfluxDBNetworkExcpetion(String cmd, Throwable cause) {
        super(String.format("ERROR: could not execute %s on influxdb due to network error", cmd), cause);
    }
}
