package com.rsa.netwitness.presidio.automation.log_player;

import com.rsa.netwitness.presidio.automation.context.EnvironmentProperties;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rsa.netwitness.presidio.automation.ssh.RunCmdUtils.runCmd;

public class LogPlayerResultUtils {

    public static Function<String,Long>  getLogPlayerSentRecords = logPlayerOutput -> {
        Pattern p = Pattern.compile("LogPlayer finished sending (\\d+) records");
        Matcher m = p.matcher(logPlayerOutput.trim());

        if (m.find( )) {
            return Long.parseLong(m.group(1));
        }
        else {
            return -1L;
        }
    };

    public static Function<String,String> logPlayerSendDirCmd = folderPath -> {
        String logDecoderIp = EnvironmentProperties.ENVIRONMENT_PROPERTIES.logDecoderIp();
        if (logDecoderIp.isEmpty()) throw new RuntimeException("Unable to send data. log-decoder ip is missing from env.properties");
        return "NwLogPlayer -s " + logDecoderIp + " -d " + folderPath;
    };

    public static Function<String,Long> runLogPlayerAndGetRecordsCountResult = folderPath ->
            getLogPlayerSentRecords.apply(runCmd(logPlayerSendDirCmd.apply(folderPath)).get(0));
}
