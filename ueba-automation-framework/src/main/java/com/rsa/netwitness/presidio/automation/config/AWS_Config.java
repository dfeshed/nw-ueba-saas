package com.rsa.netwitness.presidio.automation.config;

import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

public enum AWS_Config {
    S3_CONFIG;

    public static final Number UPLOAD_INTERVAL_MINUTES = 5;

    private final String RESOURCE_NAME = "aws_key.properties";
    private Lazy<Properties> propertiesHolder = new Lazy<>();
    private Supplier<Properties> properties = () -> propertiesHolder.getOrCompute(this::getResource);

    public Supplier<String> accessKey = () -> getOrThrow("access_key");

    public  Supplier<String> secretKey = () -> getOrThrow("secret_key");

    public  Supplier<String> bucket = () -> getOrThrow("bucket");


    public String getTenant() {
        return properties.get().getOrDefault("tenant", "acme").toString();
    }

    public String getAccount() {
        return properties.get().getOrDefault("account", "123456789001").toString();
    }

    public String getRegion() {
        return properties.get().getOrDefault("region", "us-east-2").toString();
    }


    private Properties getResource() {
        Properties props = new Properties();

        try(InputStream resourceStream = AWS_Config.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            props.load(Objects.requireNonNull(resourceStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }


    private String getOrThrow(String key) {
        String value = properties.get().getProperty(key);

        if (value==null || value.isBlank()) {
            throw new RuntimeException(key.concat(" property value is not set in ").concat(RESOURCE_NAME));
        } else {
            return value;
        }
    }
}
