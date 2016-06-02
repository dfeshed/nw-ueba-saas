package fortscale.monitoring.external.stats.samza.collector.service.config;

import fortscale.monitoring.external.stats.samza.collector.service.SamzaMetricsCollectorService;
import fortscale.monitoring.external.stats.samza.collector.service.impl.SamzaMetricsCollectorServiceImpl;
import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.external.stats.samza.collector.topicReader.config.SamzaMetricsTopicSyncReaderConfig;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({SamzaMetricsTopicSyncReaderConfig.class})

public class SamzaMetricsCollectorServiceConfig {

    @Value("${fortscale.samzametricscollector.kafka.read.sleepBetweenRetries.millis}")
    private long waitBetweenReadRetries;
    @Value("${fortscale.samzametricscollector.kafka.read.sleepBetweenEmptyMessages.millis}")
    private long waitBetweenEmptyReads;

    @Autowired
    StatsService statsService;
    @Autowired
    SamzaMetricsTopicSyncReader samzaMetricsTopicSyncReader;

    @Bean(destroyMethod = "shutDown")
    SamzaMetricsCollectorService samzaMetricsCollectorService() {
        return new SamzaMetricsCollectorServiceImpl(statsService, samzaMetricsTopicSyncReader, waitBetweenReadRetries, waitBetweenEmptyReads, true);
    }

    @Bean
    private static PropertySourceConfigurer samzaMetricsCollectorServiceEnvironmentPropertyConfigurer() {
        Properties properties = SamzaMetricsCollectorServiceProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(SamzaMetricsCollectorServiceProperties.class, properties);

        return configurer;
    }


}
