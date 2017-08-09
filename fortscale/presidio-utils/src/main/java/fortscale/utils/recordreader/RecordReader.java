package fortscale.utils.recordreader;

/**
 * A record reader extracts the value of a certain field from its record. If the record is flat, the field path is
 * simply the key of the required value. If the record is hierarchical, the field path is a concatenation of all keys
 * leading to the required value. The field class is the expected type of the required value.
 *
 * Created by Lior Govrin on 04/06/2017.
 */
public interface RecordReader {
	/**
	 * Get the value of "fieldPath" from this reader's record.
	 *
	 * @param fieldPath  The key of the value (or a concatenation of the keys leading to the value)
	 * @param fieldClass The expected type of the value
	 * @return The extracted value
	 */
	<T> T get(String fieldPath, Class<T> fieldClass);

	/**
	 * Get the value of "fieldPath" from this reader's record, without knowing the type.
	 *
	 * @param fieldPath The key of the value (or a concatenation of the keys leading to the value)
	 * @return The extracted value
	 */
	default Object get(String fieldPath) {
		return get(fieldPath, Object.class);
	}
}
