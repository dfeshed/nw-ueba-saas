package fortscale.utils.process.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicy;
import fortscale.utils.logging.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FSEnhancedLoggerServiceImpl implements FSEnhancedLoggerService {
    private static final Logger logger = Logger.getLogger(FSEnhancedLoggerServiceImpl.class);
    public static final String EARLYLOG_FILE_NAME = "earlylogFile";
    public static final String LOG_FILE_NAME = "logFile";

    /**
     * stops and starts appender (if started)
     * @param appender appender to restart
     */
    private void restartAppender(Appender<ILoggingEvent> appender)
    {
        if(appender.isStarted()) {
            appender.stop();
            appender.start();
        }
    }

    /**
     * restarts all FSEnhancedRollingFileAppender
     */
    private void restartFSEnhancedRollingFileAppender(String processInstanceNumber) {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();

        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext(); ) {
                Appender<ILoggingEvent> appender = index.next();
                if (appender instanceof FSEnhancedRollingFileAppender) {
                    updateFSEnhancedRollingFileAppenderRollingPolicyFilePattern((FSEnhancedRollingFileAppender)appender,processInstanceNumber);
                    restartAppender(appender);
                }
            }
        }
    }

    /**
     * update rolling policy file pattern with appender's file
     *
     * @param appender appender to update
     */
    private void updateFSEnhancedRollingFileAppenderRollingPolicyFilePattern(FSEnhancedRollingFileAppender appender, String processInstanceNumber) {
        RollingPolicy rollingPolicy = appender.getRollingPolicy();
        if (rollingPolicy instanceof FixedWindowRollingPolicy) {
            FixedWindowRollingPolicy fixedWindowRollingPolicy = (FixedWindowRollingPolicy) rollingPolicy;
            String oldFileNamePattern = fixedWindowRollingPolicy.getFileNamePattern();
            Pattern pattern = Pattern.compile("(.*)\\.%i$");
            Matcher matcher = pattern.matcher(oldFileNamePattern);
            if(!matcher.matches())
            {
                throw new RuntimeException(String.format("rolling policy file pattern=%s is is not supported, " +
                        "please create rolling policy from pattern: ${dir}/logFile.log.%%i",oldFileNamePattern));
            }

            String logBaseFileName = Paths.get(appender.getFile()).getFileName().toString();
            String filePattern = oldFileNamePattern.replace(String.format("%s.log",LOG_FILE_NAME),logBaseFileName);

            fixedWindowRollingPolicy.setFileNamePattern(filePattern);
            fixedWindowRollingPolicy.stop();
            fixedWindowRollingPolicy.start();
        } else {
            throw new RuntimeException("missing FixedWindowRollingPolicy at logback configuration");
        }
    }

    /**
     * changes logger file name to be ${logfilename}_${sequenceNumber}.log in order to support multi-process logging
     * @param processInstanceNumber process sequence number concatenated to file
     */
    public void updateFSEnhancedRollingFileAppender(String processInstanceNumber)
    {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();

        logger.info("updating appender with process instance number={}",processInstanceNumber);
        // update FSEnhancedRollingFileAppender with new name
        for ( ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                Appender<ILoggingEvent> appender = index.next();
                if(appender instanceof FSEnhancedRollingFileAppender)
                {
                    FSEnhancedRollingFileAppender fsAppender= ((FSEnhancedRollingFileAppender) appender);

                    if (!fsAppender.getIsMultiProcess())
                    {
                        throw new RuntimeException(String.format("Process is running as MultiProcess, appender=%s " +
                                "configuration is not suitable, please change multiProcess to true", appender.getName()));
                    }
                    // get log file path
                    String filePath = fsAppender.getFile().replace(EARLYLOG_FILE_NAME, LOG_FILE_NAME);
                    // file path splitted at last \\.
                    Pattern pattern = Pattern.compile("(.*)\\.(?=[^.]*$)(.*)");
                    Matcher matcher = pattern.matcher(filePath);
                    String processInstanceLogFilePath = filePath;
                    if(matcher.find()) {
                        if (processInstanceNumber.isEmpty())
                        {
                            processInstanceLogFilePath =
                                    String.format("%s.%s", matcher.group(1), matcher.group(2));
                        }
                        else {
                            processInstanceLogFilePath =
                                    String.format("%s_%s.%s", matcher.group(1), processInstanceNumber, matcher.group(2));
                        }
                    }
                    fsAppender.setFile(processInstanceLogFilePath);
                }
            }

        }
        // update new appenders file paths by restarting them
        restartFSEnhancedRollingFileAppender(processInstanceNumber);
        logger.info("processInstance={} updated in logger appender",processInstanceNumber);
    }

}
