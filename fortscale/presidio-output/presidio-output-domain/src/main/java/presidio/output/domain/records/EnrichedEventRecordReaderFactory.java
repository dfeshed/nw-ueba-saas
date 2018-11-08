package presidio.output.domain.records;

import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.ReflectionRecordReader;
import fortscale.utils.recordreader.transformation.Transformation;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.Collections;
import java.util.Map;

/**
 * A factory that creates ReflectionRecordReader to read EnrichedEventRecords.
 */
public class EnrichedEventRecordReaderFactory implements RecordReaderFactory {

    private Map<String, Transformation<?>> defaultTransformations;

    public EnrichedEventRecordReaderFactory( Map<String, Transformation<?>> defaultTransformations) {
        this.defaultTransformations = defaultTransformations;
    }

    public EnrichedEventRecordReaderFactory() {
        this.defaultTransformations = Collections.emptyMap();
    }

	/**
	 * @see RecordReaderFactory#getRecordClass()
	 */
	@Override
	public Class<?> getRecordClass() {
		return EnrichedEvent.class;
	}

	/**
	 * @see RecordReaderFactory#getRecordReader(Object, Map)
	 */
	@Override
	public ReflectionRecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations) {
		return new ReflectionRecordReader(record, transformations);
	}

	public ReflectionRecordReader getRecordReader(Object record) {
		return new ReflectionRecordReader(record, defaultTransformations);
	}
}
