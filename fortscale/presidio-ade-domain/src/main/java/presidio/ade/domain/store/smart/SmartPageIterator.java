package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * Created by efratn on 24/07/2017.
 */
//TODO- ADE team to change this ugly implementation
public class SmartPageIterator<U extends EntityEvent> implements PageIterator<U> {

    private final SmartDataStore smartDataStore;
    private TimeRange timeRange;
    private int smartScoreThreshold;

    public SmartPageIterator(SmartDataStore smartDataStore, TimeRange timeRange, int smartScoreThreshold) {
        this.smartDataStore = smartDataStore;
        this.timeRange = timeRange;
        this.smartScoreThreshold = smartScoreThreshold;

    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<U> next() {
        //TODO- this should be removed from here..doing it ugly for now...
        return (List<U>) smartDataStore.readSmarts(timeRange, smartScoreThreshold);
    }
}
