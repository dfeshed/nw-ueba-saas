package fortscale.ml.scorer.record;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;

/**
 * A factory that creates {@link JsonAdeRecordReader}s.
 *
 * Created by Lior Govrin on 14/06/2017.
 */
public class JsonAdeRecordReaderFactory implements RecordReaderFactory {
	/**
	 * @see RecordReaderFactory#getRecordClass()
	 */
	@Override
	public Class<?> getRecordClass() {
		return JsonAdeRecord.class;
	}

	/**
	 * @see RecordReaderFactory#getRecordReader(Object)
	 */
	@Override
	public RecordReader getRecordReader(Object record) {
		return new JsonAdeRecordReader((JsonAdeRecord)record);
	}
}
