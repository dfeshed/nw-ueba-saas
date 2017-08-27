package fortscale.utils.fixedduration;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 6/11/17.
 */
public class FixedDurationStrategyUtils {
    private static final Logger logger = Logger.getLogger(FixedDurationStrategyUtils.class);
    /**
     * partitions time range by strategy. i.e. given time range of two days and hourly strategy -> 48 partitions will be returned
     */
    public static List<TimeRange> splitTimeRangeByStrategy(TimeRange timeRange,FixedDurationStrategy strategy)
    {
        //validating that the given time range fit to the fixed duration strategy.
        long strategyDurationInSec = strategy.toDuration().getSeconds();
        if(timeRange.getStart().getEpochSecond()%strategyDurationInSec != 0){
            String message = String.format("start time does not fit to the fixed duration strategy. time range: %s, fixed duration strategy: %s", timeRange, strategy.toStrategyName());
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        if(timeRange.getEnd().getEpochSecond()%strategyDurationInSec != 0){
            String message = String.format("end time does not fit to the fixed duration strategy. time range: %s, fixed duration strategy: %s", timeRange, strategy.toStrategyName());
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        //partitions time range by strategy. i.e. given time range of two days and hourly strategy -> 48 partitions will be returned
        List<TimeRange> splitTimeRangeList = new LinkedList<>();
        Instant start = timeRange.getStart();
        while(start.isBefore(timeRange.getEnd()))
        {
            Instant currentEnd = start.plus(strategy.toDuration());
            splitTimeRangeList.add(new TimeRange(start, currentEnd));
            start = currentEnd;
        }
        return splitTimeRangeList;
    }
}
