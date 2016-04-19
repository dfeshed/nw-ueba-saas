package fortscale.monitoring.metricAdapter.init;

import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 * InfluxDB default initialization handling
 */
@Configurable(preConstruction=true)
public class InfluxDBStatsInit {
    private static final Logger logger = Logger.getLogger(InfluxDBStatsInit.class);

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

    /**
     * run all init steps
     */
    public  void  init()  {
        createDefaultDB();
        createDefaultDBRetention();
    }

    /**
     * creates default influxdb database if it's not already exist
     * the following request is generated: HTTP GET http://hostname/query?u=admin&p=&q=CREATE+DATABASE+IF+NOT+EXISTS+${influxdb.db.name}
     */
    protected void createDefaultDB()
    {
        logger.info("creating default influxdb: %s",dbName);
        influxdbClient.createDatabase(dbName);
    }

    /**
     * creates default influxdb db retention if it's not already exist
     * the follwing request is generated: HTTP GET http://hostname/query?u=admin&p=&db=${influxdb.db.name}&q=CREATE+RETENTION+POLICY+${influxdb.db.fortscale.retention.name}n+ON+${influxdb.db.name}+DURATION+${influxdb.db.fortscale.retention.fortscale_retention.duration}+REPLICATION+1+DEFAULT
     */
    protected void createDefaultDBRetention()  {
        logger.info("creating default influxdb retention: retention name: %s,db name: %s, retentionDuration: %s, retentionReplication: %s",retentionName,dbName,retentionDuration,retentionReplication);
        influxdbClient.createRetention(retentionName,dbName,retentionDuration,retentionReplication);
    }

}
