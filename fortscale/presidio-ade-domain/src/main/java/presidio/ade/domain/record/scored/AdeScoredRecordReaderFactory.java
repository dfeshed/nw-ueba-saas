package presidio.ade.domain.record.scored;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;
import presidio.ade.domain.record.AdeRecordReader;

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
     * @see RecordReaderFactory#getRecordReader(Object)
     */
    @Override
    public RecordReader getRecordReader(Object record) {
        return new AdeScoredRecordReader((AdeScoredRecord) record);
    }
}
