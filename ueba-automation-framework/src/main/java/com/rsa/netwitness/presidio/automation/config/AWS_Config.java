package com.rsa.netwitness.presidio.automation.config;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

public enum AWS_Config {
    S3_CONFIG;

    public static final Number UPLOAD_INTERVAL_MINUTES = 5;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AWS_Config.class);
    private final String RESOURCE_NAME = "aws_key.properties";
    private Lazy<Properties> propertiesHolder = new Lazy<>();
    private Supplier<Properties> properties = () -> propertiesHolder.getOrCompute(this::getResource);

    public String getAccessKey() {
        return getEnvOrFileOrDefault("access_key", "");
    }

    public String getSecretKey() {
        return getEnvOrFileOrDefault("secret_key", "");
    }

    public String getBucket() {
        return getEnvOrFileOrDefault("bucket", "");
    }


    public String getTenant() {
        return getEnvOrFileOrDefault("tenant", "acme");
    }

    public String getAccount() {
        return getEnvOrFileOrDefault("account", "");
    }

    public String getRegion() {
        return getEnvOrFileOrDefault("region", "us-east-2");
    }


    private Properties getResource() {
        Properties props = new Properties();

        try (InputStream resourceStream = AWS_Config.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            props.load(Objects.requireNonNull(resourceStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private String getEnvOrFileOrDefault(String key, String def) {
        String getenv = System.getenv(key);
        String fromFileOrDefault = properties.get().getOrDefault(key, def).toString();

        if ( !(getenv == null || getenv.isBlank())) {
            return getenv;
        } else {
            if (fromFileOrDefault.isBlank()) {
                LOGGER.warn("S3 property missing: " + key);
                return fromFileOrDefault;
            } else {
                return fromFileOrDefault;
            }
        }
    }
}
