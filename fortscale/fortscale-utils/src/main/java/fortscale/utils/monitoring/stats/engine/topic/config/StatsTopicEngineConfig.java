package fortscale.utils.monitoring.stats.engine.topic.config;


import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.monitoring.stats.engine.topic.StatsTopicEngine;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;

/**
 * Created by gaashh on 5/2/16.
 */

@Configuration
public class StatsTopicEngineConfig {

    @Value("${fortscale.monitoring.stats.engine.topic.topicName}")
    String topicName;

    /**
     *
     * StatsTopicEngineProperties property object configurer bean
     *
     * @return
     */
    @Bean
    private static PropertySourceConfigurer statsTopicEnginepropertyConfigurer() {

        // Get the properties object
        Properties properties = StatsTopicEngineProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(StatsTopicEngineConfig.class, properties);

        return configurer;
    }

    @Bean
    // protected ensures no one will use this bean by mistake
    protected KafkaEventsWriter StatsEngineKafkaEventsWriter() {
        return new KafkaEventsWriter(topicName);
    }

    @Bean
    public StatsTopicEngine statsTopicEngine() {
        KafkaEventsWriter kafkaEventsWriter = StatsEngineKafkaEventsWriter();
        StatsTopicEngine statsTopicEngine  = new StatsTopicEngine(kafkaEventsWriter);
        return statsTopicEngine;
    }

}
