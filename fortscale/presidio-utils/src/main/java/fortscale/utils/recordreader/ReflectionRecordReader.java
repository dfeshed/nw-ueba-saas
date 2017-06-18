package fortscale.utils.recordreader;

import fortscale.utils.logging.Logger;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * A record reader that extracts the value of a certain field from its record using reflection.
 *
 * Created by Lior Govrin on 05/06/2017.
 */
public class ReflectionRecordReader implements RecordReader {
	private static final Logger logger = Logger.getLogger(ReflectionRecordReader.class);
	private static final String DEFAULT_FIELD_PATH_DELIMITER = "\\.";

	private Object record;
	private String fieldPathDelimiter;

	/**
	 * C'tor.
	 *
	 * @param record             the record from which values are extracted
	 * @param fieldPathDelimiter this record reader's field path delimiter (evaluated as a regular expression)
	 */
	public ReflectionRecordReader(@NotNull Object record, @NotNull String fieldPathDelimiter) {
		this.record = record;
		this.fieldPathDelimiter = fieldPathDelimiter;
	}

	/**
	 * Default c'tor (default field path delimiter is used).
	 *
	 * @param record the record from which values are extracted
	 */
	public ReflectionRecordReader(@NotNull Object record) {
		this(record, DEFAULT_FIELD_PATH_DELIMITER);
	}

	/**
	 * @see RecordReader#get(String, Class)
	 */
	@Override
	public <T> T get(String fieldPath, Class<T> fieldClass) {
		Object value = record;

		for (String key : fieldPath.split(fieldPathDelimiter)) {
			try {
				Field field = findField(value.getClass(), key);
				value = getValue(field, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				String format = "Cannot extract the value of {} from {}. Record = {}, field path = {}.";
				logger.error(format, key, value, record, fieldPath, e);
				return null;
			}
		}

		if (!fieldClass.isInstance(value)) {
			logger.error("Extracted value {} is not an instance of {}.", value, fieldClass.getName());
			return null;
		}

		return fieldClass.cast(value);
	}

	// Use Spring's reflection utils to find the field in the class, or in any of its superclasses up to Object.
	private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
		Field field = ReflectionUtils.findField(clazz, name);

		if (field == null) {
			String s = String.format("Class %s does not have a field %s.", clazz.getName(), name);
			throw new NoSuchFieldException(s);
		}

		return field;
	}

	// Return the value of "field" from "object". Make sure "field" is accessible when extracting the value.
	private static Object getValue(Field field, Object object) throws IllegalAccessException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		Object value = field.get(object);
		field.setAccessible(accessible);
		return value;
	}
}
