package com.rsa.netwitness.presidio.automation.log_player;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rsa.netwitness.presidio.automation.common.helpers.RunCmdUtils.runCmd;
import static com.rsa.netwitness.presidio.automation.log_player.PropertiesHolder.LOG_DECODER_IP;

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
        String logDecoderIp = PropertiesHolder.getInstance().getEnvProperties().getProperty(LOG_DECODER_IP);
        return "NwLogPlayer -s " + logDecoderIp + " -d " + folderPath;
    };

    public static Function<String,Long> runLogPlayerAndGetRecordsCountResult = folderPath ->
            getLogPlayerSentRecords.apply(runCmd(logPlayerSendDirCmd.apply(folderPath)).get(0));
}
