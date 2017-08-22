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

    private final Set<String> contextIds;

    private final AggregatedDataReader dataReader;
    private final TimeRange timeRange;
    private final Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet;
    private boolean hasNext;

    public AggregatedRecordsPageIterator(AggregatedDataReader dataReader, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange) {
        this.dataReader = dataReader;
        this.aggregatedDataPaginationParamSet = aggregatedDataPaginationParamSet;
        this.contextIds = contextIds;
        this.timeRange = timeRange;
        // we assume daily/hourly time range
        Duration timeRangeDuration = Duration.between(timeRange.getStart(), timeRange.getEnd());
        Assert.isTrue(timeRangeDuration.equals(FixedDurationStrategy.HOURLY.toDuration()) ||
                timeRangeDuration.equals(FixedDurationStrategy.DAILY.toDuration()),"AggregatedRecordsPageIterator assumes daily/hourly range");
        this.hasNext = true;
    }


    /**
     *
     * @return false, since each page holds all the data for set of contexts
     */
    @Override
    public boolean hasNext() {
        boolean result = false;
        if(hasNext)
        {
            result = true;
            hasNext = false;
        }
        return result;
    }

    @Override
    public List<U> next() {
        List<U> readRecords = this.dataReader.readRecords(this.aggregatedDataPaginationParamSet, this.contextIds,this.timeRange);
        return readRecords;
    }
}
