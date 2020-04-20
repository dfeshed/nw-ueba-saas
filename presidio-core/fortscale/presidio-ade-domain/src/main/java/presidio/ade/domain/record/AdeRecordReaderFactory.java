package presidio.ade.domain.record;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.transformation.Transformation;

import java.util.Map;

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
	 * @see RecordReaderFactory#getRecordReader(Object, Map)
	 */
	@Override
	public RecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations) {
		return new AdeRecordReader((AdeRecord)record, transformations);
	}
}
