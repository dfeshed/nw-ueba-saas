package fortscale.utils.influxdb.config;

import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.influxdb.impl.InfluxdbServiceImpl;
import fortscale.utils.influxdb.metrics.InfluxdbMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Properties;

@Configuration
@EnableRetry
public class InfluxdbClientConfig {
    @Value("${influxdb.ip}")
    private String ip;
    @Value("${influxdb.port}")
    private String port;
    @Value("${influxdb.user}")
    private String user;
    @Value("${influxdb.password}")
    private String password;
    @Value("${influxdb.rest.loglevel}")
    private String logLevel;
    @Value("${influxdb.db.readTimeout.seconds}")
    private long readTimeout;
    @Value("${influxdb.db.writeTimeout.seconds}")
    private long writeTimeout;
    @Value("${influxdb.db.connectTimeout.seconds}")
    private long connectTimeout;
    @Value("${influxdb.db.batch.actions}")
    private int batchActions;
    @Value("${influxdb.db.batch.flushInterval}")
    private int flushInterval;

    @Autowired
    StatsService statsService;


    @Bean
    InfluxdbService influxdbClient() {
        return new InfluxdbServiceImpl(ip, port, logLevel, readTimeout, writeTimeout, connectTimeout, batchActions, flushInterval,user,password, new InfluxdbMetrics(statsService));
    }

    @Bean (name = "influxdbClientPropertySourceConfigurer")
    private static PropertySourceConfigurer influxdbClientEnvironmentPropertyConfigurer() {
        Properties properties = InfluxdbClientProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(InfluxdbClientConfig.class, properties);

        return configurer;
    }
}
