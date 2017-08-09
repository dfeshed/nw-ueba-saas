package fortscale.utils.fixedduration;

import fortscale.utils.time.TimeRange;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 6/11/17.
 */
public class FixedDurationStrategyUtils {
    /**
     * partitions time range by strategy. i.e. given time range of two days and hourly strategy -> 48 partitions will be returned
     */
    public static List<TimeRange> splitTimeRangeByStrategy(TimeRange timeRange,FixedDurationStrategy strategy)
    {
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
