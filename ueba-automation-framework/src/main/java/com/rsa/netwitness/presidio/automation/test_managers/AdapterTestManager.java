package com.rsa.netwitness.presidio.automation.test_managers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsa.netwitness.presidio.automation.domain.config.MongoPropertiesReader;
import com.rsa.netwitness.presidio.automation.file.SedSshUtil;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import fortscale.common.general.Schema;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.rsa.netwitness.presidio.automation.config.EnvironmentProperties.ENVIRONMENT_PROPERTIES;
import static com.rsa.netwitness.presidio.automation.domain.config.Consts.PRESIDIO_DIR;
import static com.rsa.netwitness.presidio.automation.file.LogSshUtils.printLogIfError;
import static org.assertj.core.api.Assertions.assertThat;

public class AdapterTestManager {
    public static final String PROPERTIES_CONFIGURATION = "/var/lib/netwitness/presidio/flume/conf/adapter/";
    public static final String NW_LOG_PLAYER_APP = "NwLogPlayer";
    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";
    public static final String PRESIDIO_ADAPTER_APP = "java -Xms2048m -Xmx2048m -Duser.timezone=UTC -cp /var/lib/netwitness/presidio/batch/presidio-adapter.jar -Dloader.main=presidio.adapter.FortscaleAdapterApplication org.springframework.boot.loader.PropertiesLauncher";
    private static final String PROPERTIES = ".properties";
    private static final String TEST = ".test" + PROPERTIES;
    private static final String PROD = ".prod" + PROPERTIES;

    private static final String TEST_AUTHENTICATION_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.AUTHENTICATION.getName() + TEST;
    private static final String TEST_ACTIVE_DIRECTORY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.ACTIVE_DIRECTORY.getName() + TEST;
    private static final String TEST_FILE_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.FILE.getName() + TEST;
    private static final String TEST_PROCESS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.PROCESS.getName() + TEST;
    private static final String TEST_REGISTRY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.REGISTRY.getName() + TEST;
    private static final String TEST_TLS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.TLS.getName() + TEST;

    private static final String PROD_AUTHENTICATION_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.AUTHENTICATION.getName() + PROD;
    private static final String PROD_ACTIVE_DIRECTORY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.ACTIVE_DIRECTORY.getName() + PROD;
    private static final String PROD_FILE_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.FILE.getName() + PROD;
    private static final String PROD_PROCESS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.PROCESS.getName() + PROD;
    private static final String PROD_REGISTRY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.REGISTRY.getName() + PROD;
    private static final String PROD_TLS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.TLS.getName() + PROD;

    private static final String AUTHENTICATION_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.AUTHENTICATION.getName() + PROPERTIES;
    private static final String ACTIVE_DIRECTORY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.ACTIVE_DIRECTORY.getName() + PROPERTIES;
    private static final String FILE_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.FILE.getName() + PROPERTIES;
    private static final String PROCESS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.PROCESS.getName() + PROPERTIES;
    private static final String REGISTRY_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.REGISTRY.getName() + PROPERTIES;
    private static final String TLS_CONFIGURATION = PROPERTIES_CONFIGURATION + Schema.TLS.getName() + PROPERTIES;

    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AdapterTestManager.class.getName());
    @Autowired
    private MongoTemplate mongoTemplate;
    private MongoPropertiesReader mongoPropertiesReader;
    private SshHelper sshHelper = new SshHelper();

    public AdapterTestManager(MongoPropertiesReader mongoPropertiesReader) {
        this.mongoPropertiesReader = mongoPropertiesReader;
    }

    /***
     * Clear data from Input collections
     */
    public void clearInputCollections(String schemaName) {
        String schemaNameInCollectionName = (schemaName.equalsIgnoreCase("active_directory") ? "activedirectory" : schemaName.toLowerCase());
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if ((collectionName.startsWith("input_")) &&
                    // workaround for ADE collections naming convention: in some cases it is in Camel case, in other - with underscore
                    (collectionName.toLowerCase().contains(schemaNameInCollectionName) ||
                            collectionName.toLowerCase().contains(schemaName.toLowerCase()))) {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

    public void clearAllCollections() {
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if (!collectionName.startsWith("management_ttl")) {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

    public void process(Instant start, Instant end, String timeFrame, String schema) {
        //Replace sdkSource flume properties to mongoSource flume properties
        String flumeHome = "export FLUME_HOME=/var/netwitness/presidio/flume/ ; ";
        String logPath = "/tmp/presidio-adapter_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";

        // Runs adapter for entire events time range at once
        SshResponse adapterProcess = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(flumeHome + PRESIDIO_ADAPTER_APP,
                "run", "--fixed_duration_strategy " + getFixedDuration(timeFrame),
                "--start_date " + start.toString(), "--end_date " + end.toString(), "--schema " + schema,
                "> " + logPath);

        printLogIfError(logPath);
        assertThat(adapterProcess.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }

    public void processEventsInIntervals(Instant startDate, Instant endDate, ChronoUnit interval, String schema) {
        /** Use this method when there are too much events for processing in one adapter run **/
        Instant currentProcessTimeFrom = startDate;
        Instant currentProcessTimeTo = currentProcessTimeFrom.plus(1, interval);
        while (currentProcessTimeFrom.isBefore(endDate)) {
            System.out.println(String.format("Schema: %s Processing interval: %s - %s", schema, currentProcessTimeFrom.toString(), currentProcessTimeTo.toString()));
            process(currentProcessTimeFrom, currentProcessTimeTo, "hourly", schema);
            currentProcessTimeFrom = currentProcessTimeTo;
            currentProcessTimeTo = currentProcessTimeFrom.plus(1, interval);
        }
    }


    private String getFixedDuration(String timeFrame) {
        String fixed_duration;
        if (timeFrame.equals("hourly"))
            fixed_duration = "3600";
        else if (timeFrame.equals("daily"))
            fixed_duration = "86400";
        else fixed_duration = "";
        return fixed_duration;
    }

    /**
     * fetch mongo properties and set to mongoSource files
     */
    public void submitMongoDbDetailsIntoAdapterConfigurationProperties() {
        String mongoHostName = mongoPropertiesReader.getMongoHostName();
        int mongoHostPort = mongoPropertiesReader.getMongoHostPort();
        String mongoDBName = mongoPropertiesReader.getMongoDBName();
        String mongoPassword = mongoPropertiesReader.getMongoPassword();
        String mongoUserName = mongoPropertiesReader.getMongoUserName();

        List<String> files = Arrays.asList(TEST_AUTHENTICATION_CONFIGURATION,
                TEST_ACTIVE_DIRECTORY_CONFIGURATION,
                TEST_FILE_CONFIGURATION,
                TEST_REGISTRY_CONFIGURATION,
                TEST_PROCESS_CONFIGURATION,
                TEST_TLS_CONFIGURATION
        );


        files.forEach(file -> {
            SedSshUtil.replaceTextInFile(file, PRESIDIO_DIR, "mongoSource.host=.*", "mongoSource.host=" + mongoHostName);
            SedSshUtil.replaceTextInFile(file, PRESIDIO_DIR, "mongoSource.dbName=.*", "mongoSource.dbName=" + mongoDBName);
            SedSshUtil.replaceTextInFile(file, PRESIDIO_DIR, "mongoSource.username=.*", "mongoSource.username=" + mongoUserName);
            SedSshUtil.replaceTextInFile(file, PRESIDIO_DIR, "mongoSource.password=.*", "mongoSource.password=" + mongoPassword);
            SedSshUtil.replaceTextInFile(file, PRESIDIO_DIR, "mongoSource.port=.*", "mongoSource.port=" + mongoHostPort);
        });
    }


    public void backupProductionAdapterConfigurationProperties() {
        String command =
                "cp -n " + AUTHENTICATION_CONFIGURATION + " " + PROD_AUTHENTICATION_CONFIGURATION + ";"
                        + "cp -n " + ACTIVE_DIRECTORY_CONFIGURATION + " " + PROD_ACTIVE_DIRECTORY_CONFIGURATION + ";"
                        + "cp -n " + FILE_CONFIGURATION + " " + PROD_FILE_CONFIGURATION + ";"
                        + "cp -n " + REGISTRY_CONFIGURATION + " " + PROD_REGISTRY_CONFIGURATION + ";"
                        + "cp -n " + PROCESS_CONFIGURATION + " " + PROD_PROCESS_CONFIGURATION + ";"
                        + "cp -n " + TLS_CONFIGURATION + " " + PROD_TLS_CONFIGURATION;

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void setAdapterConfigurationPropertiesToTestMode() {
        backupProductionAdapterConfigurationProperties();

        String command =
                "cp -f " + TEST_AUTHENTICATION_CONFIGURATION + " " + AUTHENTICATION_CONFIGURATION + ";"
                        + "cp -f " + TEST_ACTIVE_DIRECTORY_CONFIGURATION + " " + ACTIVE_DIRECTORY_CONFIGURATION + ";"
                        + "cp -f " + TEST_FILE_CONFIGURATION + " " + FILE_CONFIGURATION + ";"
                        + "cp -f " + TEST_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                        + "cp -f " + TEST_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION + ";"
                        + "cp -f " + TEST_TLS_CONFIGURATION + " " + TLS_CONFIGURATION;

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void setAdapterConfigurationPropertiesToProductionMode() {
        backupProductionAdapterConfigurationProperties();

        String command =
                "cp -f " + PROD_AUTHENTICATION_CONFIGURATION + " " + AUTHENTICATION_CONFIGURATION + ";"
                        + "cp -f " + PROD_ACTIVE_DIRECTORY_CONFIGURATION + " " + ACTIVE_DIRECTORY_CONFIGURATION + ";"
                        + "cp -f " + PROD_FILE_CONFIGURATION + " " + FILE_CONFIGURATION + ";"
                        + "cp -f " + PROD_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                        + "cp -f " + PROD_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION + ";"
                        + "cp -f " + PROD_TLS_CONFIGURATION + " " + TLS_CONFIGURATION;

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void setTestMode4EndPointOnly() {
        String command = "cp -f " + TEST_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + TEST_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION;

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void setProdMode4EndPointOnly() {
        String command = "cp -f " + PROD_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + PROD_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION;

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void runUebaServerConfigScript(Instant startTime) {
        String broker = ENVIRONMENT_PROPERTIES.brokerIp();
        /** Disabled forwarding **/
        String alertsForwardingFlag = ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp().isEmpty() ? "" : "-e";

        // sh /opt/rsa/saTools/bin/ueba-server-config -u admin -p netwitness -h 10.4.61.136 -o broker -t 2018-07-18T00:00:00Z -s 'AUTHENTICATION FILE ACTIVE_DIRECTORY'  -v
        String command = "sudo /opt/rsa/saTools/bin/ueba-server-config -u admin -p netwitness -h "
                + broker + " -o broker -t " + startTime.toString()
                + " -s 'AUTHENTICATION FILE ACTIVE_DIRECTORY PROCESS REGISTRY TLS' -e -v ";

        SshResponse p = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
        assertThat(p.exitCode).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setEngineConfigurationParametersToTestingValues() {
        URL url = this.getClass().getClassLoader()
                .getResource("scripts/setConfiguration.sh");

        File file = new File(Objects.requireNonNull(url).getFile());
        String command = "sh " + file.getAbsolutePath();
        SshResponse p = sshHelper.uebaHostExec().run(command);
        assertThat(p.exitCode).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setBrokerConfigurationForAdapterAndTransformer() {
        URL url = this.getClass().getClassLoader()
                .getResource("scripts/setBrokerInputConfiguration.sh");

        File file = new File(Objects.requireNonNull(url).getFile());
        String command = "sh " + file.getAbsolutePath();
        SshResponse p = sshHelper.uebaHostExec().run(command);
        assertThat(p.exitCode).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setMongoConfigurationForTransformer() {
        URL url = this.getClass().getClassLoader()
                .getResource("scripts/setMongoInputConfiguration.sh");

        File file = new File(Objects.requireNonNull(url).getFile());
        String command = "sh " + file.getAbsolutePath();
        SshResponse p = sshHelper.uebaHostExec().run(command);
        assertThat(p.exitCode).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setBuildingModelsRange(int enriched_records_days, int feature_aggregation_records_days, int smart_records_days) {
        String workflows_default_file = "/etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json";
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(workflows_default_file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject workflows = (JSONObject) obj;
        JSONObject components = (JSONObject) workflows.get("components");
        JSONObject ade = (JSONObject) components.get("ade");
        JSONObject models = (JSONObject) ade.get("models");

        JSONObject enriched_records = (JSONObject) models.get("enriched_records");
        JSONObject feature_aggregation_records = (JSONObject) models.get("feature_aggregation_records");
        JSONObject smart_records = (JSONObject) models.get("smart_records");
        enriched_records.put("min_data_time_range_for_building_models_in_days", feature_aggregation_records_days);
        feature_aggregation_records.put("min_data_time_range_for_building_models_in_days", enriched_records_days);
        smart_records.put("min_data_time_range_for_building_models_in_days", smart_records_days);

        models.put("enriched_records", enriched_records);
        models.put("feature_aggregation_records", feature_aggregation_records);
        models.put("smart_records", smart_records);
        ade.put("models", models);
        components.put("ade", ade);
        workflows.put("components", components);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(workflows_default_file), workflows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backupTransformerConfig() {
        String command = " mkdir -p /var/netwitness/presidio/flume/conf/adapter/transformers/backup " +
                "&& cp -n /var/netwitness/presidio/flume/conf/adapter/transformers/*.json " +
                "/var/netwitness/presidio/flume/conf/adapter/transformers/backup/";

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }

    public void restoreDefaultTransformerConfig() {
        backupTransformerConfig();
        String command = "cp -f  /var/netwitness/presidio/flume/conf/adapter/transformers/backup/*.json " +
                "/var/netwitness/presidio/flume/conf/adapter/transformers/";

        sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(command);
    }
}
