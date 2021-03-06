package com.rsa.netwitness.presidio.automation.data.processing.broker;

import com.rsa.netwitness.presidio.automation.config.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogPlayerHelper {

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
            getLogPlayerSentRecords.apply( new SshHelper().uebaHostExec().run(logPlayerSendDirCmd.apply(folderPath)).output.get(0));
}
