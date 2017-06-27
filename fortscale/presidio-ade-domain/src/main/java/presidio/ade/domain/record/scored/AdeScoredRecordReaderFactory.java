package presidio.ade.domain.record.scored;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.transformation.Transformation;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Map;
/**
 * A factory that creates {@link AdeRecordReader}s.
 */
public class AdeScoredRecordReaderFactory implements RecordReaderFactory {
    /**
     * @see RecordReaderFactory#getRecordClass()
     */
    @Override
    public Class<?> getRecordClass() {
        return AdeScoredRecord.class;
    }

    /**
     * @see RecordReaderFactory#getRecordReader(Object, Map)
     */
    @Override
    public RecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations) {
        return new AdeScoredRecordReader((AdeScoredRecord) record);
    }
}
