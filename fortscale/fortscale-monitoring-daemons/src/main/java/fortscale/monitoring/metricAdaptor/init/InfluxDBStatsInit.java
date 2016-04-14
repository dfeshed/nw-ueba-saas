package fortscale.monitoring.metricAdaptor.init;

import fortscale.utils.influxdb.InfluxdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable(preConstruction=true)
public class InfluxDBStatsInit {

    @Autowired
    InfluxdbClient influxdbClient;
    @Value("${influxdb.db.name}")
    String dbName;
    @Value("${influxdb.db.fortscale.retention.name}")
    String retentionName;
    @Value("${influxdb.db.fortscale.retention.primary_retention.duration}")
    String retentionDuration;
    @Value("${influxdb.db.fortscale.retention.primary_retention.replication}")
    String retentionReplication;

    public InfluxDBStatsInit()
    {

    }
    public  void  init()
    {
        createDefaultDB();
        createDefaultDBRetention();
    }

    protected void createDefaultDB()
    {
        //generates HTTP GET http://hostname/query?u=admin&p=&q=CREATE+DATABASE+IF+NOT+EXISTS+${influxdb.db.name}
        influxdbClient.createDatabase(dbName);
    }
    protected void createDefaultDBRetention()
    {
        //generates HTTP GET http://hostname/query?u=admin&p=&db=${influxdb.db.name}&q=CREATE+RETENTION+POLICY+${influxdb.db.fortscale.retention.name}n+ON+${influxdb.db.name}+DURATION+${influxdb.db.fortscale.retention.fortscale_retention.duration}+REPLICATION+1+DEFAULT
        influxdbClient.createRetention(retentionName,dbName,retentionDuration,retentionReplication);
    }

}
