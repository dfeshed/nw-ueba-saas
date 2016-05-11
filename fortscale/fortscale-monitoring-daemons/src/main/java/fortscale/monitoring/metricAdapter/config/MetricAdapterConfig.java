package fortscale.monitoring.metricAdapter.config;


import fortscale.monitoring.config.MonitoringProcessGroupCommonConfig;
import fortscale.monitoring.grafana.init.config.GrafanaInitConfig;
import fortscale.monitoring.metricAdapter.MetricAdapter;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterMetrics;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterMetricsService;
import fortscale.monitoring.samza.metricWriter.SamzaMetricWriter;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.influxdb.config.InfluxdbClientConfig;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.samza.topicReader.config.SamzaMetricsTopicSyncReaderConfig;
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
@Import({InfluxdbClientConfig.class, SamzaMetricsTopicSyncReaderConfig.class, MonitoringProcessGroupCommonConfig.class, GrafanaInitConfig.class})
public class MetricAdapterConfig {

    @Value("${metricadapter.version.major}")
    private long metricsAdapterMajorVersion;
    @Value("${metricadapter.db.name}")
    private String dbName;
    @Value("${metricadapter.db.fortscale.retention.name}")
    private String retentionName;
    @Value("${metricadapter.db.fortscale.retention.primary_retention.duration}")
    private String retentionDuration;
    @Value("${metricadapter.db.fortscale.retention.primary_retention.replication}")
    private String retentionReplication;
    @Value("#{'${metricadapter.db.write.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenWriteRetries;
    @Value("#{'${metricadapter.db.init.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenInitRetries;
    @Value("#{'${metricadapter.kafka.read.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenReadRetries;
    @Value("${metricadapter.kafka.metric.enginedata.name}")
    private String engineDataMetricName;
    @Value("${metricadapter.kafka.metric.enginedata.package}")
    private String engineDataMetricPackage;
    @Value("${metricadapter.initiationwaittime.seconds}")
    private long initiationWaitTimeInSeconds;

    @Autowired
    private InfluxdbClient influxdbClient;
    @Autowired
    private SamzaMetricsTopicSyncReader samzaMetricsTopicSyncReader;
    @Autowired
    private MetricAdapterMetricsService metricAdapterMetricsService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private SamzaMetricWriter samzaMetricWriter;
    @Bean
    public MetricAdapterMetricsService metricAdapterMetricsService() {
        return new MetricAdapterMetricsService(statsService,"metricAdapter");
    }

    @Bean
    public SamzaMetricWriter samzaMetricWriter()
    {
        return new SamzaMetricWriter(statsService);
    }

    @Bean(destroyMethod = "shutDown")
    MetricAdapter metricAdapter() {
        return new MetricAdapter(initiationWaitTimeInSeconds, influxdbClient, samzaMetricsTopicSyncReader,samzaMetricWriter, metricAdapterMetricsService, metricsAdapterMajorVersion, dbName, retentionName, retentionDuration, retentionReplication, waitBetweenWriteRetries, waitBetweenInitRetries, waitBetweenReadRetries, engineDataMetricName, engineDataMetricPackage, true);
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
