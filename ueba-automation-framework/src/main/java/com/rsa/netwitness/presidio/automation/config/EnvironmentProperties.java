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
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.USER_DIR;
import static java.util.concurrent.TimeUnit.SECONDS;
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
    private Lazy<Boolean> isBrokerAvailable = new Lazy<>();

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
        boolean canResolveHosts = isBrokerAvailable.getOrCompute(this::checkForBrokerAvailability);

        if (canResolveHosts) {
            Properties prop = envPropertiesHolder.getOrCompute(this::load);
            if (prop.isEmpty()) {
                LOGGER.error("Failed to get env.properties file.");
                return "";
            } else {
                String p = prop.getOrDefault(propertyName, "").toString();
                if (p.isBlank()) {
                    LOGGER.error("Missing env.property key: " + p);
                }
                return p;
            }
        } else {
            return AutomationConf.UEBA_IP;
        }
    }

    private Properties load() {
        Properties properties = new Properties();
        boolean fileExist = ENV_PROPERTIES_PATH.toFile().exists();
        Predicate<String> automationConfNotMatchResoledIP = resolvedIp -> !AutomationConf.UEBA_IP.equals(resolvedIp);

        LOGGER.info("Loading env.properties from: " + ENV_PROPERTIES_PATH.toString());

        try {
            if (fileExist) {
                LOGGER.info("Found properties file from the previous run.");
                properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));
                String presidioAirflowIp = properties.getOrDefault(PRESIDIO_AIRFLOW, "").toString();

                if (automationConfNotMatchResoledIP.test(presidioAirflowIp)) {
                    LOGGER.info("New presidio-server ip [" + presidioAirflowIp + "] doesn't match to existing [" +  AutomationConf.UEBA_IP + "]. Resolving...");
                    resolve();
                    properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));
                }

            } else {
                LOGGER.info("File not found. Resolving...");
                resolve();
                properties.load(new FileInputStream(ENV_PROPERTIES_PATH.toFile()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void resolve() throws IOException {
        SshResponse sshResponse =  new SshHelper().uebaHostRootExec().setUserDir(REMOTE_SCRIPT_PATH).withTimeout(10, SECONDS).run("sh env_properties_manager.sh --create");
        assertThat(sshResponse.exitCode).isEqualTo(0);
        List<String> output = sshResponse.output.stream().filter(e -> e.contains("=")).sorted().collect(Collectors.toList());
        Files.write(ENV_PROPERTIES_PATH, output , StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private boolean checkForBrokerAvailability() {
        SshResponse sshResponse =  new SshHelper().uebaHostRootExec().withTimeout(5, SECONDS).setUserDir(REMOTE_SCRIPT_PATH)
                .run("orchestration-cli-client --list-services");
        LOGGER.info("Broker availability test result = " + sshResponse.exitCode);
        return sshResponse.exitCode == 0;
    }
}
