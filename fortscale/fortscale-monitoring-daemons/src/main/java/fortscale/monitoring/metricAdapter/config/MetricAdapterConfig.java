package fortscale.monitoring.metricAdapter.config;


import fortscale.monitoring.grafana.init.config.GrafanaInitConfig;
import fortscale.monitoring.metricAdapter.MetricAdapterService;
import fortscale.monitoring.metricAdapter.engineData.topicReader.EngineDataTopicSyncReader;
import fortscale.monitoring.metricAdapter.engineData.topicReader.config.EngineDataTopicSyncReaderConfig;
import fortscale.monitoring.metricAdapter.impl.MetricAdapterServiceImpl;
import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.influxdb.config.InfluxdbClientConfig;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Configuration
@Import({InfluxdbClientConfig.class, EngineDataTopicSyncReaderConfig.class, GrafanaInitConfig.class})
public class MetricAdapterConfig {

    @Value("${fortscale.metricadapter.version.major}")
    private long metricsAdapterMajorVersion;
    @Value("${fortscale.metricadapter.db.name}")
    private String dbName;
    @Value("${fortscale.metricadapter.db.fortscale.retention.name}")
    private String retentionName;
    @Value("${fortscale.metricadapter.db.fortscale.retention.primary_retention.duration}")
    private String retentionDuration;
    @Value("${fortscale.metricadapter.db.fortscale.retention.primary_retention.replication}")
    private String retentionReplication;
    @Value("${fortscale.metricadapter.dbclient.write.sleepBetweenRetries.millis}")
    private long waitBetweenWriteRetries;
    @Value("${fortscale.metricadapter.dbclient.init.sleepBetweenRetries.millis}")
    private long waitBetweenInitRetries;
    @Value("${fortscale.metricadapter.kafka.read.sleepBetweenRetries.millis}")
    private long waitBetweenReadRetries;
    @Value("${fortscale.metricadapter.kafka.read.sleepBetweenEmptyMessages.millis}")
    private long waitBetweenEmptyReads;
    @Value("${fortscale.metricadapter.kafka.metric.enginedata.name}")
    private String engineDataMetricName;
    @Value("${fortscale.metricadapter.kafka.metric.enginedata.package}")
    private String engineDataMetricPackage;
    @Value("${fortscale.metricadapter.initiationwaittime.seconds}")
    private long initiationWaitTimeInSeconds;

    @Autowired
    private InfluxdbService influxdbService;

    @Autowired
    private StatsService statsService;
    @Autowired
    private EngineDataTopicSyncReader engineDataTopicSyncReader;


    @Bean(destroyMethod = "shutDown")
    MetricAdapterService metricAdapter() {
        return new MetricAdapterServiceImpl(statsService, initiationWaitTimeInSeconds, influxdbService,
                engineDataTopicSyncReader, metricsAdapterMajorVersion, dbName, retentionName, retentionDuration,
                retentionReplication, waitBetweenWriteRetries, waitBetweenInitRetries, waitBetweenReadRetries, waitBetweenEmptyReads,
                true);
    }

    @Bean
    private static PropertySourceConfigurer metricAdapterEnvironmentPropertyConfigurer() {
        Properties properties = MetricAdapterProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MetricAdapterConfig.class, properties);

        return configurer;
    }

    @Bean
    public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {

        String[] overridingFileList = {"metricAdapter-overriding.properties"};

        Properties properties = new Properties();
        MainProcessPropertiesConfigurer configurer;
        configurer = new MainProcessPropertiesConfigurer(overridingFileList, properties);

        return configurer;
    }
}
