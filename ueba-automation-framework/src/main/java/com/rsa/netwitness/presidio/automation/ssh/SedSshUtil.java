package com.rsa.netwitness.presidio.automation.ssh;

import java.util.regex.Matcher;

public class SedSshUtil {

    /**
     * Replace #pattern by #replacement in #filePath file
     * @param filePath filePath
     * @param executePath executePath
     * @param pattern pattern
     * @param replacement replacement
     */
    public static void replaceTextInFile(String filePath, String executePath, String pattern, String replacement ){
        replacement = replacement.replaceAll("/", Matcher.quoteReplacement("\\/"));
        String hostCommand = "sed -i 's/" + pattern + "/" + replacement + "/g' " + filePath;
        TerminalCommandsSshUtils.runCommand(hostCommand, true, executePath);
    }

}
