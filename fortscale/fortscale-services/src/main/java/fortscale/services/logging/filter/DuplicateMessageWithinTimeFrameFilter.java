package fortscale.services.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Marker;

import java.util.concurrent.TimeUnit;

/**
 * @author gils
 * 28/03/2016
 */
public class DuplicateMessageWithinTimeFrameFilter extends TurboFilter{

    private Cache<String, Integer> msgCache;

    private static final int DEFAULT_ALLOWED_REPETITIONS = 5;

    @Override
    public void start() {
        msgCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .weakKeys()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();

        super.start();
    }

    @Override
    public void stop() {
        msgCache.cleanUp();
        msgCache = null;

        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level,
                              String format, Object[] params, Throwable t) {
        Integer currCount = msgCache.getIfPresent(format);

        if (currCount == null) {
            currCount = 0;
        }

        msgCache.put(format, currCount + 1);

        if (currCount <= DEFAULT_ALLOWED_REPETITIONS) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }
}
