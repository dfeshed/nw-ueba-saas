package fortscale.monitoring.external.stats.init;

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

    public InfluxDBStatsInit()
    {

    }
    public void createDefaultDB()
    {
        //generates HTTP GET http://hostname/query?u=admin&p=&q=CREATE+DATABASE+IF+NOT+EXISTS+${influxdb.db.name}
        influxdbClient.createDatabase(dbName);
    }
}
