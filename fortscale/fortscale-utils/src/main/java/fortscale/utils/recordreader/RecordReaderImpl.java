package fortscale.utils.recordreader;

import fortscale.utils.logging.Logger;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * A record reader that extracts the value of a certain field from a given record using reflection.
 *
 * Created by Lior Govrin on 05/06/2017.
 */
public class RecordReaderImpl<T> implements RecordReader<T> {
	private static final Logger logger = Logger.getLogger(RecordReaderImpl.class);
	private static final String DEFAULT_FIELD_PATH_DELIMITER = "\\.";

	private String fieldPathDelimiter;

	/**
	 * Default c'tor.
	 * This record reader will use the default field path delimiter.
	 */
	public RecordReaderImpl() {
		this.fieldPathDelimiter = DEFAULT_FIELD_PATH_DELIMITER;
	}

	/**
	 * C'tor.
	 * @param fieldPathDelimiter The field path delimiter that this record reader will use
	 *                           (evaluated as a regular expression and cannot be null)
	 */
	public RecordReaderImpl(@NotNull String fieldPathDelimiter) {
		this.fieldPathDelimiter = fieldPathDelimiter;
	}

	/**
	 * @see RecordReader#get(Object, String, Class).
	 */
	@Override
	public <U> U get(T record, String fieldPath, Class<U> fieldClass) {
		Object value = record;

		for (String key : fieldPath.split(fieldPathDelimiter)) {
			try {
				Field field = findField(value.getClass(), key);
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				value = field.get(value);
				field.setAccessible(accessible);
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
		if (field == null) throw new NoSuchFieldException();
		return field;
	}
}
