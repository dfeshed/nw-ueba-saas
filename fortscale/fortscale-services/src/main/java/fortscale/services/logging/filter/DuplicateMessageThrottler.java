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
 * This filter detects duplicate messages and drops them in the following conditions:
 * 1. The number of message repetitions exceeds the number of the declared allowed repetitions
 * AND
 * 2. The level of the arrived log message is at or bellow the declared log level
 * AND
 * 3. The time gap between the last log event and the current log event with the same format is no longer than {x} minutes (configurable).
 *
 * This way we will achieve filtering of duplicate messages until we have reached "silence" for a period of time, then the messages will appear again.
 *
 * Note that the log messages are considered duplicated taking into account parameterized logging. E.g.:
 * logger.debug("This is my message {} out of 10", entry);
 *
 *
 * Example of usage:
 * 		<turboFilter class="fortscale.services.logging.filter.DuplicateMessageWithinTimeFrameFilter">
 *           <allowedRepetitions>10</allowedRepetitions>
 *           <timeFrameInMinutes>5</timeFrameInMinutes>
 *           <level>INFO</level>
 *      </turboFilter>
 *
 * @author gils
 * 28/03/2016
 */
public class DuplicateMessageThrottler extends TurboFilter{

    private Cache<String, Integer> logMsgsCache;

    // default values
    private static final int DEFAULT_ALLOWED_REPETITIONS = 100;
    private static final int DEFAULT_NUM_OF_MSG_ENTRIES = 5000;
    private static final int DEFAULT_TIMEFRAME_IN_MINUTES = 10;
    private static final Level DEFAULT_LEVEL = INFO;

    // number of allowed log message repetitions
    private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;

    // the expiration time in minutes of the cache entries
    private int timeFrameInMinutes = DEFAULT_TIMEFRAME_IN_MINUTES;

    // filter will be applied for all log messages that are at or bellow this level
    private Level level = DEFAULT_LEVEL;

    @Override
    public void start() {
        // Internally, the log messages and their correspondent number of occurrences are stored in a cache and the keys are expired after {x} minutes period
        logMsgsCache = CacheBuilder.newBuilder()
                .maximumSize(DEFAULT_NUM_OF_MSG_ENTRIES)
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

    /*
     * Decision method of the Filter.
     * Note that naturally this method will be called from multiple threads hence it must be thread-safe
     */
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
