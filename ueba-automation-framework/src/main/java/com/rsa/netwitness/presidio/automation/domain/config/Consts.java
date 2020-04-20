package com.rsa.netwitness.presidio.automation.domain.config;

public class Consts {
    public static final String PRESIDIO_DIR = "/var/netwitness/presidio/batch/";

    // Fortscale/Presidio COMMANDS
    public static final String PRESIDIO_OUTPUT = "java -jar presidio-output-processor.jar";
    public static final String PRESIDIO_OUTPUT_FORWARDER = "java -jar presidio-output-forwarder.jar";
    public static final String STATISTICAL_INFORMATION_DIRECTORY_PATH = "/home/presidio/statisticalInformation/";
    public static final String COUNT_DOCUMENTS_COLLECTIONS_MONGODB = "/home/presidio/statisticalInformation/count_document_mongodb.txt";
    public static final String COUNT_AGGREGATION_ALERTS_BY_SEVERITY = "/home/presidio/statisticalInformation/aggregation_alerts_by_severity.txt";
    public static final String COUNT_ALL_USERS_WITH_SCORE = "/home/presidio/statisticalInformation/users_score.txt";
    public static final String COUNT_INDICATORS_EACH_TYPE = "/home/presidio/statisticalInformation/indicators_each_type.txt";

    // Presidio generators folders
    public static final String EXPECTED_JSON_FILES_DIR = "src/test/resources/ExpectedResultFiles/";

    public enum DataSource {
        AUTHENTICATION,
        ACTIVE_DIRECTORY,
        FILE,
        PROCESS,
        REGISTRY,
        IOC
    }
}
