package com.rsa.netwitness.presidio.automation.config;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public enum AWS_Config {
    S3_CONFIG;

    public static final Number UPLOAD_INTERVAL_MINUTES = 5;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AWS_Config.class);

    public String getBucket() {
        return getSystemOrEnvOrDefault("s3.bucket", "S3_BUCKET", "");
    }
    public String getTenant() {
        return getSystemOrEnvOrDefault("s3.tenant", "S3_TENANT", "acme");
    }
    public String getAccount() {
        return getSystemOrEnvOrDefault("s3.account", "S3_ACCOUNT", "");
    }


    private String getSystemOrEnvOrDefault(String system, String env, String def) {
        String property = System.getProperty(system, "");
        if (property.isBlank()) {
            property = System.getenv().getOrDefault(env, def);
        }
        assertThat(property).withFailMessage(system + " system property s missing").isNotEmpty();

        return property;
    }
}
