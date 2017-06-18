package fortscale.utils.recordreader;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A service that given a record returns its corresponding reader. When initialized, the service should receive all
 * known record readers. If the service does not contain a reader for a certain record, it returns a default record
 * reader that uses reflection.
 *
 * Created by Lior Govrin on 18/06/2017.
 */
public class RecordReaderService<T> {
	private Map<String, RecordReader> recordClassNameToReaderMap;

	/**
	 * C'tor.
	 *
	 * @param recordReaders a collection of all known record readers (cannot contain nulls).
	 *                      The service registers each record reader in the collection
	 */
	public RecordReaderService(@NotNull Collection<RecordReader> recordReaders) {
		recordClassNameToReaderMap = new HashMap<>(recordReaders.size());

		for (RecordReader recordReader : recordReaders) {
			Assert.notNull(recordReader, "The collection of record readers cannot contain nulls.");
			recordClassNameToReaderMap.put(recordReader.getRecordClassName(), recordReader);
		}
	}

	/**
	 * Get the corresponding reader for the given record.
	 *
	 * @param record the record for which a reader is needed
	 * @return the record reader
	 */
	public RecordReader getRecordReader(@NotNull T record) {
		RecordReader recordReader = recordClassNameToReaderMap.get(record.getClass().getName());
		return recordReader == null ? new ReflectionRecordReader(record) : recordReader;
	}
}
