package com.rsa.netwitness.presidio.automation.config;

import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.assertj.core.util.Lists;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

import static com.rsa.netwitness.presidio.automation.config.EnvironmentProperties.ENVIRONMENT_PROPERTIES;
import static org.assertj.core.api.Assertions.assertThat;

public enum PostgresConf {
    POSTGRES_PROPERTIES;

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PostgresConf.class.getName());

    public String connectionURL() {
        return "jdbc:postgresql://" + AutomationConf.UEBA_HOST + ":" + port() + "/" + dbName();
    }

    public String username() {
        return "airflow";
    }

    public String password() {
        return passwordHolder.getOrCompute(passwordSupplier).get(0);
    }

    public String host() {
        return ENVIRONMENT_PROPERTIES.presidioAirflowIp();
    }

    private String port() {
        return "5432";
    }

    private String dbName() {
        return "airflow";
    }


    private final Lazy<List<String>> passwordHolder = new Lazy<>();

    private Supplier<List<String>> passwordSupplier = this::retrieveFromCfgFile;

    private List<String> retrieveByCurl() {
        LOGGER.info("Going to retrieve Postgres password");

        String getPasswordCmd = "curl -s -u admin:netwitness --insecure https://localhost"
                + "/admin/configurationview/  | grep @127.0.0.1/airflow | grep -oP 'airflow\\:\\K([\\da-z]+)' | head -n 1";

        SshResponse result = new SshHelper().uebaHostExec().run(getPasswordCmd);
        return result.exitCode == 0 ? result.output : Lists.emptyList();
    }

    private List<String> retrieveFromCfgFile() {
        LOGGER.info("Going to retrieve Postgres password");

        String getPasswordCmd = "cat /var/netwitness/presidio/airflow/airflow.cfg  | grep @127.0.0.1/airflow | grep -oP 'airflow\\:\\K([\\da-z]+)' | head -n 1";

        SshResponse result = new SshHelper().uebaHostExec().run(getPasswordCmd);
        assertThat(result.exitCode).as("Exit code").isEqualTo(0);
        assertThat(result.output).hasSize(1);
        assertThat(result.output.get(0)).hasSize(32);
        return result.output;
    }
}
