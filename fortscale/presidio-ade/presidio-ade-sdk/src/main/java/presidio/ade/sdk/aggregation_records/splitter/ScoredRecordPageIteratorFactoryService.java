package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.pagination.ScoredRecordPageIterator;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.HashMap;

/**
 * This factory service creates an appropriate {@link ScoredRecordPageIterator} over pages of the underlying
 * {@link AdeScoredRecord}s of a given {@link AdeAggregationRecord} (i.e. score aggregation record).
 */
public class ScoredRecordPageIteratorFactoryService {
    private final ClassToFactoryMap classToFactoryMap;

    public ScoredRecordPageIteratorFactoryService(ClassToFactoryMap classToFactoryMap) {
        this.classToFactoryMap = classToFactoryMap;
    }

    public PageIterator<? extends AdeRecord> getScoredRecordPageIterator(ScoreAggregationRecordDetails details) {
        Class<? extends AdeScoredRecord> clazz = details.getScoredRecordClass();
        ScoredRecordPageIteratorFactory<? extends AdeScoredRecord> factory = classToFactoryMap.get(clazz);

        if (factory == null) {
            String s = String.format("Scored record class %s is not supported.", clazz.getSimpleName());
            throw new IllegalArgumentException(s);
        }

        return factory.getScoredRecordPageIterator(details);
    }

    public static final class ClassToFactoryMap extends HashMap<Class<? extends AdeScoredRecord>, ScoredRecordPageIteratorFactory<? extends AdeScoredRecord>> {}
}
