package com.rsa.netwitness.presidio.automation.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AutomationConf {

    public static final Path USER_DIR = Paths.get(System.getProperty("user.dir"));
    public static final Path LOG_GEN_OUTPUT = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "netwitness_events_gen");
    public static final Path DEPLOYMENT_SCRIPTS_DIR = Paths.get(USER_DIR.toAbsolutePath().toString(), "target", "scripts", "deployment");

    public static final String UEBA_HOST = getEnvOrTryResolveNameElseDefault("UEBA_HOST", "localhost");
    public static final String UEBA_IP = tryToResolveIpElseDefault();

    public static final String SSH_USERNAME = getEnvOrDefault("SSH_USERNAME", "precidio");
    public static final String SSH_PASSWORD = getEnvOrDefault("SSH_PASSWORD", "netwitness");
    public static final String SSH_ROOT_USERNAME = getEnvOrDefault("SSH_ROOT_USERNAME", "root");
    public static final String SSH_ROOT_PASSWORD = getEnvOrDefault("SSH_ROOT_PASSWORD", "netwitness");
    public static final String OUTPUT_REST_PORT = "8882";

    public static final String OUTPUT_REST_URL = "http://".concat(UEBA_IP).concat(":").concat(OUTPUT_REST_PORT);


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