package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;

import java.util.List;

public class SmartRecordPageIterator<T extends EntityEvent> implements PageIterator<T> {
    private SmartDataStore smartDataStore;
    private TimeRange timeRange;
    private int smartScoreThreshold;

    public SmartRecordPageIterator(SmartDataStore smartDataStore, TimeRange timeRange, int smartScoreThreshold) {
        this.smartDataStore = smartDataStore;
        this.timeRange = timeRange;
        this.smartScoreThreshold = smartScoreThreshold;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> next() {
        return (List<T>)smartDataStore.readSmartRecords(timeRange, smartScoreThreshold);
    }
}
