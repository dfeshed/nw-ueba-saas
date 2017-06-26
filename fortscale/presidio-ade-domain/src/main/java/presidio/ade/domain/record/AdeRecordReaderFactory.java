package presidio.ade.domain.record;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;

/**
 * A factory that creates {@link AdeRecordReader}s.
 *
 * Created by Lior Govrin on 19/06/2017.
 */
public class AdeRecordReaderFactory implements RecordReaderFactory {
	/**
	 * @see RecordReaderFactory#getRecordClass()
	 */
	@Override
	public Class<?> getRecordClass() {
		return AdeRecord.class;
	}

	/**
	 * @see RecordReaderFactory#getRecordReader(Object)
	 */
	@Override
	public RecordReader getRecordReader(Object record) {
		return new AdeRecordReader((AdeRecord)record);
	}
}
