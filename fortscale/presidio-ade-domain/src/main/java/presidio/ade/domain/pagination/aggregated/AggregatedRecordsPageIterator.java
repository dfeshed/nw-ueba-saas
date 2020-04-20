package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class AggregatedRecordsPageIterator<U extends AdeAggregationRecord> implements PageIterator<U> {
    private final AggregatedDataReader dataReader;
    private final Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet;
    private final Set<String> contextIds;
    private final TimeRange timeRange;
    private final Double threshold;
    private boolean hasNext;

    public AggregatedRecordsPageIterator(AggregatedDataReader dataReader, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange, Double threshold) {
        this.dataReader = dataReader;
        this.aggregatedDataPaginationParamSet = aggregatedDataPaginationParamSet;
        this.contextIds = contextIds;
        this.timeRange = timeRange;
        this.threshold = threshold;
        // we assume daily/hourly time range
        Duration timeRangeDuration = Duration.between(timeRange.getStart(), timeRange.getEnd());
        Assert.isTrue(
                timeRangeDuration.equals(FixedDurationStrategy.HOURLY.toDuration()) ||
                timeRangeDuration.equals(FixedDurationStrategy.DAILY.toDuration()),
                "AggregatedRecordsPageIterator assumes daily/hourly range");
        this.hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public List<U> next() {
        List<U> records = dataReader.readRecords(aggregatedDataPaginationParamSet, contextIds, timeRange, threshold);
        hasNext = false;
        return records;
    }
}
