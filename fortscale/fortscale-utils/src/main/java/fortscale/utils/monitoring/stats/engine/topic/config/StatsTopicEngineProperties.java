package fortscale.utils.monitoring.stats.engine.topic.config;

import java.util.Properties;

/**
 * Created by gaashh on 5/2/16.
 */
public class StatsTopicEngineProperties {

    static public Properties getProperties() {

         Properties properties = new Properties();

         // Kafaka topic name
         properties.put("fortscale.monitoring.stats.engine.topic.topicName", "metrics");

         return properties;

        }
    }

