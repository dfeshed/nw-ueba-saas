package com.rsa.netwitness.presidio.automation.config;

import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

public enum AWS_Config {
    S3_CONFIG;

    private Lazy<Properties> propertiesHolder = new Lazy<>();
    private Supplier<Properties> properties = () -> propertiesHolder.getOrCompute(this::getResource);

    public String accessKey = properties.get().getProperty("access_key");
    public String secretKey = properties.get().getProperty("secret_key");

    private Properties getResource() {
        String RESOURCE_NAME = "aws_key.properties";
        Properties props = new Properties();

        try(InputStream resourceStream = AWS_Config.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            props.load(Objects.requireNonNull(resourceStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
