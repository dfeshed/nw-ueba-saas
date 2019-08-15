package com.rsa.netwitness.presidio.automation.context;

import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Supplier;

public enum EnvironmentProperties {
    ENVIRONMENT_PROPERTIES;

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(EnvironmentProperties.class.getName());

    private static final String ENV_PROPERTIES_PATH = "/home/presidio/env.properties";

    private static final String LOG_DECODER = "log-decoder";
    private static final String ESA_ANALYTICS_SERVER = "esa-analytics-server";
    private static final String BROKER = "broker";

    public Supplier<Properties> envProperties = lazily(() -> envProperties = value(load(ENV_PROPERTIES_PATH)));

    public String logDecoderIp() {
        return property(LOG_DECODER);
    }

    public String esaAnalyticsServerIp() {
        return property(ESA_ANALYTICS_SERVER);
    }

    public String brokerIp() {
        return property(BROKER);
    }


    private String property(String propertyName) {
        return envProperties.get().getOrDefault(propertyName, "").toString();
    }

    private interface Lazy<T> extends Supplier<T> {
        Supplier<T> init();

        default T get() {
            return init().get();
        }
    }

    private static <U> Supplier<U> lazily(Lazy<U> lazy) {
        return lazy;
    }

    private static <T> Supplier<T> value(T value) {
        return () -> value;
    }

    private static Properties load(final String path) {
        Properties properties = new Properties();
        try {
            LOGGER.info("Loading env.properties");
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
