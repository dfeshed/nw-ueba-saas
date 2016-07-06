package fortscale.utils.process.logger;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public interface FSEnhancedLoggerService {


    /**
     * changes logger file name to be ${logfilename}_${sequenceNumber}.log in order to support multi-process logging
     * @param sequenceNumber process sequence number concatenated to file
     */
    void updateFSEnhancedRollingFileAppender(String sequenceNumber);

}
