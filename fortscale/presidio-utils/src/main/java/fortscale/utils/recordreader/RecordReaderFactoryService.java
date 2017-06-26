package fortscale.utils.recordreader;

import fortscale.utils.recordreader.transformation.Transformation;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * A service that given a record returns its corresponding reader. When initialized, the service should receive all
 * known record reader factories. If the service does not contain a reader factory for a certain record, it returns a
 * default record reader that uses reflection. The service should also receive all known transformations upon
 * initialization. The transformations are passed to the chosen factory when creating a record reader.
 *
 * Created by Lior Govrin on 18/06/2017.
 */
public class RecordReaderFactoryService {
	private Map<String, RecordReaderFactory> recordClassNameToReaderFactoryMap;
	private Collection<Transformation<?>> transformations;

	/**
	 * C'tor.
	 *
	 * @param recordReaderFactories a collection of all known record reader factories
	 * @param transformations       a collection of all known transformations
	 */
	public RecordReaderFactoryService(
			@NotNull Collection<RecordReaderFactory> recordReaderFactories,
			@NotNull Collection<Transformation<?>> transformations) {

		this.recordClassNameToReaderFactoryMap = new HashMap<>(recordReaderFactories.size());
		this.transformations = transformations;

		for (RecordReaderFactory recordReaderFactory : recordReaderFactories) {
			recordClassNameToReaderFactoryMap.put(recordReaderFactory.getRecordClass().getName(), recordReaderFactory);
		}
	}

	/**
	 * Get the corresponding reader for the given record. The service searches for a matching reader factory according
	 * to the record's class. It searches for all parent classes up to Object (level by level, superclass first,
	 * interfaces second), until an existing record reader factory is found.
	 *
	 * @param record the record for which a reader is needed
	 * @return the record reader
	 */
	public RecordReader getRecordReader(@NotNull Object record) {
		Queue<Class<?>> classes = new LinkedList<>();
		classes.add(record.getClass());

		while (!classes.isEmpty()) {
			Class<?> nextClass = classes.poll();
			String nextClassName = nextClass.getName();

			if (recordClassNameToReaderFactoryMap.containsKey(nextClassName)) {
				return recordClassNameToReaderFactoryMap.get(nextClassName).getRecordReader(record);
			} else {
				// Add the superclass first
				Class<?> superClass = nextClass.getSuperclass();
				if (superClass != null) classes.add(superClass);

				// Add all interfaces second
				for (Class<?> interfaceClass : nextClass.getInterfaces()) {
					if (interfaceClass != null) classes.add(interfaceClass);
				}
			}
		}

		// Return a default reflection record reader if none of the parent classes had a matching factory
		return new ReflectionRecordReader(record, transformations);
	}
}
