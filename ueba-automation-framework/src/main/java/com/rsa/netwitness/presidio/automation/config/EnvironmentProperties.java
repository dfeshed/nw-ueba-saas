package com.rsa.netwitness.presidio.automation.config;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.USER_DIR;
import static org.assertj.core.api.Assertions.assertThat;

public enum EnvironmentProperties {
    ENVIRONMENT_PROPERTIES;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(EnvironmentProperties.class);

    public static final Path ENV_PROPERTIES_PATH = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "environment.properties");
    public static final String REMOTE_SCRIPT_PATH = "/home/presidio";

    private static final String ADMIN_SERVER = "admin-server";
    private static final String LOG_DECODER = "log-decoder";
    private static final String ESA_ANALYTICS_SERVER = "esa-analytics-server";
    private static final String BROKER = "broker";
    private static final String PRESIDIO_AIRFLOW = "presidio-airflow";
    private static final String MONGO_PRESIDIO = "mongo-presidio";

    private Lazy<Properties> envPropertiesHolder = new Lazy<>();
    private Supplier<Properties> propertiesSupplier = this::load;



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

    private String adminServerIp() {
        return property(ADMIN_SERVER);
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

    private Properties load() {
        Properties properties = new Properties();
        boolean fileExist = ENV_PROPERTIES_PATH.toFile().exists();
        Predicate<String> automationConfNotMatchResoledIP = resolvedIp -> !AutomationConf.UEBA_IP.equals(resolvedIp);

        LOGGER.info("Loading env.properties from: " + ENV_PROPERTIES_PATH.toString());

        try {
            if (fileExist) {
                properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));

                if (automationConfNotMatchResoledIP.test(adminServerIp())) {
                    resolve();
                    properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));
                }

            } else {
                resolve();
                properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void resolve() throws IOException {
        SshResponse sshResponse =  new SshHelper().uebaHostRootExec().setUserDir(REMOTE_SCRIPT_PATH).run("sh env_properties_manager.sh --create");
        assertThat(sshResponse.exitCode).isEqualTo(0);
        List<String> output = sshResponse.output.stream().filter(e -> e.contains("=")).sorted().collect(Collectors.toList());
        Files.write(ENV_PROPERTIES_PATH, output , StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
