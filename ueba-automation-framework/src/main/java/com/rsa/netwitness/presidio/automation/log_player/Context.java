package com.rsa.netwitness.presidio.automation.log_player;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Context {
    private static final Path USER_DIR = Paths.get(System.getProperty("user.dir") );
    public static final Path LOG_GEN_PATH = Paths.get(USER_DIR.toAbsolutePath().toString(),"target", "netwitness_events_gen");
}
