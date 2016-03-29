package fortscale.services.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Marker;

import java.util.concurrent.TimeUnit;

import static ch.qos.logback.classic.Level.INFO;

/**
 * This filter detects duplicate messages and beyond a certain number of repetitions within a timeframe, it drops repeated messages
 *
 * @author gils
 * 28/03/2016
 */
public class DuplicateMessageWithinTimeFrameFilter extends TurboFilter{

    private Cache<String, Integer> msgsCache;

    private static final int DEFAULT_ALLOWED_REPETITIONS = 10;

    private static final int DEFAULT_NUM_OF_MSG_ENTRIES = 5000;

    private static final int DEFAULT_TIMEFRAME_IN_MINUTES = 10;

    private static final Level DEFAULT_UPPER_LEVEL = INFO;

    private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;

    private int numOfMsgEntries = DEFAULT_NUM_OF_MSG_ENTRIES;

    private int timeFrameInMinutes = DEFAULT_TIMEFRAME_IN_MINUTES;

    private Level upperLevel = DEFAULT_UPPER_LEVEL;

    // info level

    @Override
    public void start() {
        msgsCache = CacheBuilder.newBuilder()
                .maximumSize(numOfMsgEntries)
                .expireAfterWrite(timeFrameInMinutes, TimeUnit.MINUTES)
                .build();

        super.start();
    }

    @Override
    public void stop() {
        msgsCache.cleanUp();
        msgsCache = null;

        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level,
                              String format, Object[] params, Throwable t) {
        // TODO check why we get null in this scenario?
        if (format == null) {
            return FilterReply.NEUTRAL;
        }

        if (level.isGreaterOrEqual(upperLevel) && !level.equals(upperLevel)) { // ==> i.e. strictly greater
            return FilterReply.NEUTRAL;
        }

        Integer currCount = msgsCache.getIfPresent(format);

        if (currCount == null) {
            currCount = 0;
        }

        msgsCache.put(format, currCount + 1);

        if (currCount <= DEFAULT_ALLOWED_REPETITIONS) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public int getAllowedRepetitions() {
        return allowedRepetitions;
    }

    public void setAllowedRepetitions(int allowedRepetitions) {
        this.allowedRepetitions = allowedRepetitions;
    }

    public int getNumOfMsgEntries() {
        return numOfMsgEntries;
    }

    public void setNumOfMsgEntries(int numOfMsgEntries) {
        this.numOfMsgEntries = numOfMsgEntries;
    }

    public int getTimeFrameInMinutes() {
        return timeFrameInMinutes;
    }

    public void setTimeFrameInMinutes(int timeFrameInMinutes) {
        this.timeFrameInMinutes = timeFrameInMinutes;
    }

    public Level getUpperLevel() {
        return upperLevel;
    }

    public void setUpperLevel(Level upperLevel) {
        this.upperLevel = upperLevel;
    }
}
