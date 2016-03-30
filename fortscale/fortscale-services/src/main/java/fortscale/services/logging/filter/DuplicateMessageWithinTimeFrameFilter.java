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
 * This filter detects duplicate messages and beyond a certain number of repetitions within a timeframe, it drops repeated messages.
 * Internally, the log messages and their correspondent number of occurrences are stored in a cache and the keys are expired after {x} minutes period.
 *
 * Example of usage:
 * 		<turboFilter class="fortscale.services.logging.filter.DuplicateMessageWithinTimeFrameFilter">
 *           <allowedRepetitions>10</allowedRepetitions>
 *           <numOfMsgEntries>100</numOfMsgEntries>
 *           <timeFrameInMinutes>2</timeFrameInMinutes>
 *           <level>INFO</level>
 *      </turboFilter>
 *
 * @author gils
 * 28/03/2016
 */
public class DuplicateMessageWithinTimeFrameFilter extends TurboFilter{

    private Cache<String, Integer> logMsgsCache;

    private static final int DEFAULT_ALLOWED_REPETITIONS = 10;

    private static final int DEFAULT_NUM_OF_MSG_ENTRIES = 5000;

    private static final int DEFAULT_TIMEFRAME_IN_MINUTES = 10;

    private static final Level DEFAULT_LEVEL = INFO;

    private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;

    private int numOfMsgEntries = DEFAULT_NUM_OF_MSG_ENTRIES;

    private int timeFrameInMinutes = DEFAULT_TIMEFRAME_IN_MINUTES;

    private Level level = DEFAULT_LEVEL;

    // info level

    @Override
    public void start() {
        logMsgsCache = CacheBuilder.newBuilder()
                .maximumSize(numOfMsgEntries)
                .expireAfterWrite(timeFrameInMinutes, TimeUnit.MINUTES)
                .build();

        super.start();
    }

    @Override
    public void stop() {
        logMsgsCache.cleanUp();
        logMsgsCache = null;

        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level,
                              String format, Object[] params, Throwable t) {
        if (format == null) {
            return FilterReply.NEUTRAL;
        }

        if (level.isGreaterOrEqual(this.level) && !level.equals(this.level)) { // ==> i.e. strictly greater
            return FilterReply.NEUTRAL;
        }

        Integer previousCount = logMsgsCache.asMap().putIfAbsent(format, 1);

        if (previousCount == null) {
            return FilterReply.NEUTRAL;
        }

        if (previousCount < allowedRepetitions) {
            logMsgsCache.asMap().computeIfPresent(format, (key, value) -> (previousCount < allowedRepetitions) ? value + 1 : value);

            return FilterReply.NEUTRAL;
        }
        else {
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
