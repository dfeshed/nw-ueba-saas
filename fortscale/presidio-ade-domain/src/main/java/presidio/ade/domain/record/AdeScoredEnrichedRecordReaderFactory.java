package presidio.ade.domain.record;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.transformation.Transformation;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.Map;

/**
 * Created by yarondl on 8/22/17.
 */
public class AdeScoredEnrichedRecordReaderFactory implements RecordReaderFactory {
    /**
     * @see RecordReaderFactory#getRecordClass()
     */
    @Override
    public Class<?> getRecordClass() {
        return AdeScoredEnrichedRecord.class;
    }

    /**
     * @see RecordReaderFactory#getRecordReader(Object, Map)
     */
    @Override
    public RecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations) {
        return new AdeScoredEnrichedRecordReader((AdeScoredEnrichedRecord)record, transformations);
    }
}
