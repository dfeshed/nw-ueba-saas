package com.rsa.netwitness.presidio.automation.context;

import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Supplier;

public enum EnvironmentProperties {
    ENVIRONMENT_PROPERTIES;

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(EnvironmentProperties.class.getName());

    private static final String ENV_PROPERTIES_PATH = "/home/presidio/env.properties";

    private static final String LOG_DECODER = "log-decoder";
    private static final String ESA_ANALYTICS_SERVER = "esa-analytics-server";
    private static final String BROKER = "broker";
    private static final String PRESIDIO_AIRFLOW = "presidio-airflow";
    private static final String MONGO_PRESIDIO = "mongo-presidio";

    public String logDecoderIp() {
        return property(LOG_DECODER);
    }

    public String esaAnalyticsServerIp() {
        return property(ESA_ANALYTICS_SERVER);
    }

    public String brokerIp() {
        return property(BROKER);
    }

    public String presidioAirflowIp() {
        return property(PRESIDIO_AIRFLOW);
    }

    public String mongoPresidioIp() {
        return property(MONGO_PRESIDIO);
    }


    private String property(String propertyName) {
        Properties prop = envPropertiesHolder.getOrCompute(propertiesSupplier);
        if (prop.isEmpty()) {
            LOGGER.error("Failed to load Properties.");
            return null;
        } else {
            return prop.getOrDefault(propertyName, "").toString();
        }
    }

    private Lazy<Properties> envPropertiesHolder = new Lazy<>();

    private Supplier<Properties> propertiesSupplier = this::load;

    private Properties load() {
        Properties properties = new Properties();
        try {
            LOGGER.info("Loading env.properties");
            properties.load(new FileInputStream(ENV_PROPERTIES_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
