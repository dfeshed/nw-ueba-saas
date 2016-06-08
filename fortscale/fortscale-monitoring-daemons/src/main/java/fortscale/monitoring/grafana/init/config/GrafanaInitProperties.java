package fortscale.monitoring.grafana.init.config;

import java.util.Properties;


public class GrafanaInitProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.grafana.db.source.file.path","/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-monitoring-daemons/target/resources/stats/grafana/db/grafana.db");
        properties.put("fortscale.grafana.db.destination.file.path","/hadoop/fortscale/monitoring/grafana/data/grafana.db");
        properties.put("fortscale.grafana.db.destination.file.override",true);
        properties.put("fortscale.grafana.db.destination.file.override.rotate",5);
        return properties;
    }
}
