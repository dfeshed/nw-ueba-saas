package fortscale.utils.recordreader.exception;

import fortscale.utils.recordreader.transformation.Transformation;

/**
 * Signals that a required field for a certain {@link Transformation} was not found in the given class.
 *
 * Created by Lior Govrin on 24/06/2017.
 */
public class RequiredFieldNotFoundException extends ReflectiveOperationException {
	public RequiredFieldNotFoundException(String fieldName, Class<?> clazz) {
		super(String.format("Class %s does not have a field %s.", clazz.getName(), fieldName));
	}
}
