package fortscale.utils.recordreader;

/**
 * A record reader extracts the value of a certain field from a given record. If the record is flat, the field path is
 * simply the key of the required value. If the record is hierarchical, the field path is a concatenation of all keys
 * leading to the required value. The field class is the expected type of the required value.
 *
 * Created by Lior Govrin on 04/06/2017.
 */
public interface RecordReader<T> {
	/**
	 * Get the value of "fieldPath" from "record".
	 *
	 * @param record     The record from which the value is extracted
	 * @param fieldPath  The key of the value (or a concatenation of the keys leading to the value)
	 * @param fieldClass The expected type of the value
	 * @return The extracted value
	 */
	<U> U get(T record, String fieldPath, Class<U> fieldClass);
}
