package fortscale.monitoring.grafana.init.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class GrafanaInitProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("grafana.db.source.file.path","resources/stats/grafana/db/grafana.db");
        properties.put("grafana.db.destination.file.path","/hadoop/fortscale/grafana/data/grafana.db");
        return properties;
    }
}
