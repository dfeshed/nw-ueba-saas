package com.rsa.netwitness.presidio.automation.config;

import com.google.common.collect.ImmutableList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AutomationConf {

    public static final Path USER_DIR = Paths.get(System.getProperty("user.dir"));
    public static final Path LOG_GEN_OUTPUT = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "netwitness_events_gen");
    public static final Path DEPLOYMENT_SCRIPTS_DIR = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "scripts", "deployment");
    public static final Path TARGET_DIR = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "log");

    public static final String UEBA_HOST = getEnvOrTryResolveNameElseDefault("UEBA_HOST", "localhost");
    public static final String UEBA_IP = tryToResolveIpElseDefault();

    public static final String SSH_USERNAME = getEnvOrDefault("SSH_USERNAME", "presidio");
    public static final String SSH_PASSWORD = getEnvOrDefault("SSH_PASSWORD", "netwitness");
    public static final String SSH_ROOT_USERNAME = getEnvOrDefault("SSH_ROOT_USERNAME", "root");
    public static final String SSH_ROOT_PASSWORD = getEnvOrDefault("SSH_ROOT_PASSWORD", "netwitness");
    public static final String OUTPUT_REST_PORT = "8882";

    public static final String OUTPUT_REST_URL = "http://".concat(UEBA_IP).concat(":").concat(OUTPUT_REST_PORT);

    public static final boolean IS_JENKINS_RUN = System.getenv().containsKey("JENKINS_HOME");
    public static final boolean LOCAL_MONGO_CONF_FLAG = System.getenv().containsKey("LOCAL_MONGO_CONF");
    public static final boolean IS_MONGO_PASSWORD_ENCRYPTED = System.getenv().getOrDefault("IS_MONGO_PASSWORD_ENCRYPTED", "true").equalsIgnoreCase("true");
    public static final boolean USE_AWS_KMS = System.getenv().getOrDefault("USE_AWS_KMS", "false").equalsIgnoreCase("true");

    public static final ImmutableList<String> CORE_SCHEMAS_TO_PROCESS = ImmutableList.copyOf(
            System.getenv().getOrDefault("SCHEMAS_TO_PROCESS", "ACTIVE_DIRECTORY,AUTHENTICATION,FILE,PROCESS,REGISTRY,TLS").split("\\s*,\\s*"));

    public static final ImmutableList<String> CORE_ENTITIES_TO_PROCESS = ImmutableList.copyOf(
            System.getenv().getOrDefault("ENTITIES_TO_PROCESS", "userId,ja3,sslSubject").split("\\s*,\\s*"));


    // Use OUTPUT_REST_URL
    @Deprecated
    public static String getOutputRestIpAndPort() {
        return UEBA_IP.concat(":").concat(OUTPUT_REST_PORT);
    }


    private static String getEnvOrDefault(String key, String def) {
        String val = System.getenv(key);
        return val == null ? def : key;
    }

    private static String getEnvOrTryResolveNameElseDefault(String key, String def) {
        String val = System.getenv(key);

        if (val == null) {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            return val;
        }
        return def;
    }

    private static String tryToResolveIpElseDefault() {
        try {
            return InetAddress.getByName(UEBA_HOST).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
}