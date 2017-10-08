package fortscale.utils.recordreader;

import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.exception.NoSuchFeatureException;
import fortscale.utils.recordreader.exception.RequiredFieldNotFoundException;
import fortscale.utils.recordreader.transformation.Transformation;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A record reader that extracts the value of a certain field from its record using reflection.
 * If somewhere along the hierarchy a field is missing, this reader will use one of its {@link Transformation}s.
 *
 * Created by Lior Govrin on 05/06/2017.
 */
public class ReflectionRecordReader implements RecordReader {
	private static final Logger logger = Logger.getLogger(ReflectionRecordReader.class);
	private static final String DEFAULT_FIELD_PATH_DELIMITER = "\\.";

	private Object record;
	private Map<String, Transformation<?>> transformations;
	private String fieldPathDelimiter;

	/**
	 * C'tor.
	 *
	 * @param record             the record from which values are extracted
	 * @param transformations    a map containing the transformations that are used when fields are missing
	 * @param fieldPathDelimiter this reader's field path delimiter (evaluated as a regular expression)
	 */
	public ReflectionRecordReader(
			@NotNull Object record,
			@NotNull Map<String, Transformation<?>> transformations,
			@NotNull String fieldPathDelimiter) {

		this.record = record;
		this.transformations = transformations;
		this.fieldPathDelimiter = fieldPathDelimiter;
	}

	/**
	 * C'tor.
	 * There are no transformations configured.
	 * The default field path delimiter is used.
	 *
	 * @param record the record from which values are extracted
	 */
	public ReflectionRecordReader(@NotNull Object record) {
		this(record, Collections.emptyMap(), DEFAULT_FIELD_PATH_DELIMITER);
	}

	/**
	 * C'tor.
	 * The default field path delimiter is used.
	 *
	 * @param record          the record from which values are extracted
	 * @param transformations a map containing the transformations that are used when fields are missing
	 */
	public ReflectionRecordReader(@NotNull Object record, @NotNull Map<String, Transformation<?>> transformations) {
		this(record, transformations, DEFAULT_FIELD_PATH_DELIMITER);
	}

	/**
	 * C'tor.
	 * There are no transformations configured.
	 *
	 * @param record             the record from which values are extracted
	 * @param fieldPathDelimiter this reader's field path delimiter (evaluated as a regular expression)
	 */
	public ReflectionRecordReader(@NotNull Object record, @NotNull String fieldPathDelimiter) {
		this(record, Collections.emptyMap(), fieldPathDelimiter);
	}

	/**
	 * @see RecordReader#get(String, Class)
	 */
	@Override
	public <T> T get(String fieldPath, Class<T> fieldClass) {
		Object value = record;

		for (String key : fieldPath.split(fieldPathDelimiter)) {
			try {
				if(value instanceof Map){
					value = ((Map) value).get(key);
				}
				else {
					// Find a field named "key" in the class, or in any of its superclasses up to Object
					Field field = ReflectionUtils.findField(value.getClass(), key);
					// If there is no such field, extract the value using one of the transformations
					if (field == null) value = getFromTransformation(key, value);
						// Else, extract the value from the field
					else value = getFromField(field, value);
				}
			} catch (RequiredFieldNotFoundException | IllegalAccessException | NoSuchFeatureException e) {
				String format = "Cannot extract the value of {} from {}. Record = {}, field path = {}.";
				logger.error(format, key, value, record, fieldPath, e);
				return null;
			}

			if (value == null) {
				String format = "The value of {} was extracted, but it is null. Record = {}, field path = {}.";
				logger.trace(format, key, record, fieldPath);
				return null;
			}
		}

		if (!fieldClass.isInstance(value)) {
			String format = "Extracted value {} is not an instance of {}. Record = {}, field path = {}.";
			logger.error(format, value, fieldClass.getName(), record, fieldPath);
			return null;
		}

		return fieldClass.cast(value);
	}

	// Find a transformation to "featureName" and extract the value using the required fields from "object".
	private Object getFromTransformation(String featureName, Object object)
			throws RequiredFieldNotFoundException, IllegalAccessException, NoSuchFeatureException {

		if (transformations.containsKey(featureName)) {
			Transformation<?> transformation = transformations.get(featureName);
			Map<String, Object> requiredFieldNameToValueMap = new HashMap<>();

			for (String requiredFieldName : transformation.getRequiredFieldNames()) {
				Field field = ReflectionUtils.findField(object.getClass(), requiredFieldName);
				if (field == null) throw new RequiredFieldNotFoundException(requiredFieldName, object.getClass());
				requiredFieldNameToValueMap.put(requiredFieldName, getFromField(field, object));
			}

			return transformation.transform(requiredFieldNameToValueMap);
		} else {
			throw new NoSuchFeatureException(featureName, object.getClass());
		}
	}

	// Return the value of "field" from "object". Make sure "field" is accessible when extracting the value.
	private static Object getFromField(Field field, Object object) throws IllegalAccessException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		Object value = field.get(object);
		field.setAccessible(accessible);
		return value;
	}
}
