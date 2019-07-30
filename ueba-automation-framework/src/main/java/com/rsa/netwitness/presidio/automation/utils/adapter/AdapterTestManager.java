package com.rsa.netwitness.presidio.automation.utils.adapter;


import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.domain.config.MongoPropertiesReader;
import com.rsa.netwitness.presidio.automation.utils.common.SedUtil;
import com.rsa.netwitness.presidio.automation.utils.common.TerminalCommands;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.rsa.netwitness.presidio.automation.common.helpers.RunCmdUtils.printLogFile;
import static org.assertj.core.api.Assertions.assertThat;

public class AdapterTestManager {
    @Autowired
    private MongoTemplate mongoTemplate;
    private MongoPropertiesReader mongoPropertiesReader;

    public static final String PROPERTIES_CONFIGURATION = "/var/lib/netwitness/presidio/flume/conf/adapter/";
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

    public static final String NW_LOG_PLAYER_APP = "NwLogPlayer";
    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";

    public static final String PRESIDIO_ADAPTER_APP = "java -Xms2048m -Xmx2048m -Duser.timezone=UTC -cp /var/lib/netwitness/presidio/batch/presidio-adapter.jar -Dloader.main=presidio.adapter.FortscaleAdapterApplication org.springframework.boot.loader.PropertiesLauncher";

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
        Process adapterProcess = TerminalCommands.runCommand(flumeHome + PRESIDIO_ADAPTER_APP, true, Consts.PRESIDIO_DIR, "run",
                "--fixed_duration_strategy " + getFixedDuration(timeFrame), "--start_date " + start.toString(), "--end_date " + end.toString(), "--schema " + schema,
                " &> " + logPath);

        //TODO: fix issue - when working with sshmanager.properties, ssh.connectionIP different from localhost, process exit value is 1, even if adapter exits with 0.
        //Assert.assertEquals(0, adapterProcess.exitValue(), "Shell command failed. exit value: " + adapterProcess.exitValue());

        //Replace mongoSource flume properties to sdkSource flume properties
        printLogFile(logPath);
        setProdMode();
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
    public void setMongoPropertiesToMongoSource() {
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
            SedUtil.replaceTextInFile(file, Consts.PRESIDIO_DIR, "mongoSource.host=.*", "mongoSource.host=" + mongoHostName);
            SedUtil.replaceTextInFile(file, Consts.PRESIDIO_DIR, "mongoSource.dbName=.*", "mongoSource.dbName=" + mongoDBName);
            SedUtil.replaceTextInFile(file, Consts.PRESIDIO_DIR, "mongoSource.username=.*", "mongoSource.username=" + mongoUserName);
            SedUtil.replaceTextInFile(file, Consts.PRESIDIO_DIR, "mongoSource.password=.*", "mongoSource.password=" + mongoPassword);
            SedUtil.replaceTextInFile(file, Consts.PRESIDIO_DIR, "mongoSource.port=.*", "mongoSource.port=" + mongoHostPort);
        });
    }


    public void setTestMode() {
        String command = "cp -f " + TEST_AUTHENTICATION_CONFIGURATION + " " + AUTHENTICATION_CONFIGURATION + ";"
                + "cp -f " + TEST_ACTIVE_DIRECTORY_CONFIGURATION + " " + ACTIVE_DIRECTORY_CONFIGURATION + ";"
                + "cp -f " + TEST_FILE_CONFIGURATION + " " + FILE_CONFIGURATION + ";"
                + "cp -f " + TEST_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + TEST_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION + ";"
                + "cp -f " + TEST_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION + ";"
                + "cp -f " + TEST_TLS_CONFIGURATION + " " + TLS_CONFIGURATION;

        TerminalCommands.runCommand(command, true, Consts.PRESIDIO_DIR);
    }

    public void setProdMode() {
        String command = "cp -f " + PROD_AUTHENTICATION_CONFIGURATION + " " + AUTHENTICATION_CONFIGURATION + ";"
                + "cp -f " + PROD_ACTIVE_DIRECTORY_CONFIGURATION + " " + ACTIVE_DIRECTORY_CONFIGURATION + ";"
                + "cp -f " + PROD_FILE_CONFIGURATION + " " + FILE_CONFIGURATION + ";"
                + "cp -f " + PROD_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + PROD_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION + ";"
                + "cp -f " + PROD_TLS_CONFIGURATION + " " + TLS_CONFIGURATION;

        TerminalCommands.runCommand(command, true, Consts.PRESIDIO_DIR);
    }

    public void setTestMode4EndPointOnly() {
        String command = "cp -f " + TEST_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + TEST_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION;

        TerminalCommands.runCommand(command, true, Consts.PRESIDIO_DIR);
    }

    public void setProdMode4EndPointOnly() {
        String command = "cp -f " + PROD_REGISTRY_CONFIGURATION + " " + REGISTRY_CONFIGURATION + ";"
                + "cp -f " + PROD_PROCESS_CONFIGURATION + " " + PROCESS_CONFIGURATION;

        TerminalCommands.runCommand(command, true, Consts.PRESIDIO_DIR);
    }

    public void sendConfiguration(Instant startTime) {
        String node_zero_ip = getNodeZeroIP();

        // sh /opt/rsa/saTools/bin/ueba-server-config -u admin -p netwitness -h 10.4.61.136 -o broker -t 2018-07-18T00:00:00Z -s 'AUTHENTICATION FILE ACTIVE_DIRECTORY'  -v
        String command = "sudo /opt/rsa/saTools/bin/ueba-server-config -u admin -p netwitness -h " + node_zero_ip + " -o broker -t " + startTime.toString() + " -s 'AUTHENTICATION FILE ACTIVE_DIRECTORY PROCESS REGISTRY TLS'  -v -e ";
        Process p = TerminalCommands.runCommand(command, true, Consts.PRESIDIO_DIR);
        assertThat(p.exitValue()).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setTestAutomationConfigParameters() {
        URL url = this.getClass().getClassLoader()
                .getResource("scripts/setConfiguration.sh");

        File file = new File(Objects.requireNonNull(url).getFile());
        String command = "sh " + file.getAbsolutePath();
        Process p = TerminalCommands.runCommand(command, true, "");
        assertThat(p.exitValue()).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public void setBrokerConfiguration() {
        URL url = this.getClass().getClassLoader()
                .getResource("scripts/setBrokerInputConfiguration.sh");

        File file = new File(Objects.requireNonNull(url).getFile());
        String command = "sh " + file.getAbsolutePath();
        Process p = TerminalCommands.runCommand(command, true, "");
        assertThat(p.exitValue()).as("Error exit code for command:\n" + command).isEqualTo(0);
    }

    public String getNodeZeroIP() {
        InetAddress address = null;
        try {
            address = InetAddress.getByName("nw-node-zero");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(address.getHostAddress());
        return  address.getHostAddress();
    }
}
