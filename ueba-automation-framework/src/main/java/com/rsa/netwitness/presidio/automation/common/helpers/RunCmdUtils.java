package com.rsa.netwitness.presidio.automation.common.helpers;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RunCmdUtils {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(RunCmdUtils.class.getName());

    public static List<String> runCmd(String cmd) {
        return runCmd(cmd, false);
    }

    public static List<String> runCmd(String cmd, Boolean disableLog) {
        try {
            LOGGER.info(" ++++ runCmd: [" + cmd + "]");
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(new String[]{"sh", "-c", cmd});

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            p.waitFor();
            error.lines().forEach(e -> LOGGER.error(e));
            List<String> result = input.lines().collect(Collectors.toList());
            if (!disableLog) result.forEach(e -> LOGGER.info(e));
            return result;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printLogFile(String logPath) {
        List<String> strings = RunCmdUtils.runCmd("tail -n 50 " + logPath, true);
        boolean noErrorFlag = Objects.requireNonNull(strings).stream().filter(e -> e.contains(" ERROR ")).collect(Collectors.toList()).isEmpty();

        if (!noErrorFlag) strings.forEach(e -> LOGGER.error(e));
        else strings.forEach(e -> LOGGER.debug(e));
    }
}
