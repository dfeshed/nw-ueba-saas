package com.rsa.netwitness.presidio.automation.log_player;

import java.io.File;
import java.nio.file.Path;

public class Context {

    private static final String LOG_GEN = System.getProperty("user.dir") + "/target/netwitness_events_gen/";
    public static final Path LOG_GEN_PATH = new File(LOG_GEN).toPath();
}
