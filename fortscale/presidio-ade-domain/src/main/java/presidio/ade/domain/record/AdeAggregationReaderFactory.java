package presidio.ade.domain.record;

import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.transformation.Transformation;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.Map;

/**
 * A factory that creates {@link AdeRecordReader}s.
 *
 * Created by Lior Govrin on 19/06/2017.
 */
public class AdeAggregationReaderFactory implements RecordReaderFactory {
	/**
	 * @see RecordReaderFactory#getRecordClass()
	 */
	@Override
	public Class<?> getRecordClass() {
		return AdeAggregationRecord.class;
	}

	/**
	 * @see RecordReaderFactory#getRecordReader(Object, Map)
	 */
	@Override
	public RecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations) {
		return new AdeAggregationReader((AdeAggregationRecord)record, transformations);
	}
}
