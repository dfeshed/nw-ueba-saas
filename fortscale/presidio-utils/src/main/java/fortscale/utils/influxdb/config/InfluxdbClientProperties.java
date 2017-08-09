package fortscale.utils.influxdb.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class InfluxdbClientProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("influxdb.rest.loglevel","NONE");
        properties.put("influxdb.ip","127.0.0.1");
        properties.put("influxdb.port","2203");
        properties.put("influxdb.user","admin");
        properties.put("influxdb.password","");
        properties.put("influxdb.db.readTimeout.seconds",65);
        properties.put("influxdb.db.writeTimeout.seconds",65);
        properties.put("influxdb.db.connectTimeout.seconds",65);
        properties.put("influxdb.db.batch.actions",50);
        properties.put("influxdb.db.batch.flushInterval",1);
        properties.put("influxdb.client.id","metricsAdapter");
        return properties;
    }
}
