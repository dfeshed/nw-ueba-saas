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
 *
 * Stats topic engine spring configuration class.
 *
 * It has 3 beans:
 *    1. Stats topic engine properties configurer
 *    2. Kafaka topic events producer
 *    3. Stats topic engine
 *
 * Created by gaashh on 5/2/16.
 */

@Configuration
public class StatsTopicEngineConfig {

    @Value("${fortscale.monitoring.stats.engine.topic.topicName}")
    String topicName;

    @Value("${fortscale.monitoring.stats.engine.metricGroupBatchWriteSize}")
    long metricGroupBatchWriteSize;

    @Value("${fortscale.monitoring.stats.engine.messageSizeWarningThreshold}")
    long messageSizeWarningThreshold;

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

    /**
     *
     * Bean function for the topic writer used by the stats engine
     *
     * @return
     */
    @Bean
    // protected ensures no one will use this bean by mistake
    protected KafkaEventsWriter StatsEngineKafkaEventsWriter() {
        return new KafkaEventsWriter(topicName);
    }

    /**
     *
     * The main bean function, creates a stats topic engine and hook it to the topic writer
     *
     * @return
     */
    @Bean
    public StatsTopicEngine statsTopicEngine() {

        // Get the topic writer
        KafkaEventsWriter kafkaEventsWriter = StatsEngineKafkaEventsWriter();

        // Create the engine
        StatsTopicEngine statsTopicEngine  = new StatsTopicEngine(kafkaEventsWriter, metricGroupBatchWriteSize, messageSizeWarningThreshold);

        return statsTopicEngine;
    }

}
