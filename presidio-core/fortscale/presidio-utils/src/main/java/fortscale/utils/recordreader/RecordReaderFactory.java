package fortscale.utils.recordreader;

import fortscale.utils.recordreader.transformation.Transformation;

import java.util.Map;

/**
 * Each {@link RecordReader} should have its own {@link RecordReaderFactory}. Given a record, the factory returns the
 * corresponding reader (that contains the record). Since the reader probably handles a specific type of records, it's
 * the factory's responsibility to check the type of the given record.
 *
 * Created by Lior Govrin on 18/06/2017.
 */
public interface RecordReaderFactory {
	/**
	 * @return the type of records that are handled by the readers created by this factory. For example,
	 * if this factory creates enriched record readers, then the method will return the class of enriched records
	 */
	Class<?> getRecordClass();

	/**
	 * @param record          the given record
	 * @param transformations a map containing the transformations that are used when fields are missing
	 * @return a reader containing the given record
	 */
	RecordReader getRecordReader(Object record, Map<String, Transformation<?>> transformations);
}
