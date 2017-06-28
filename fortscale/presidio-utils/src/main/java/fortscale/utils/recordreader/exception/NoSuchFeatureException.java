package fortscale.utils.recordreader.exception;

/**
 * Signals that the class doesn't have a field with the specified name,
 * and that there is also no transformation configured for this feature.
 *
 * Created by Lior Govrin on 24/06/2017.
 */
public class NoSuchFeatureException extends ReflectiveOperationException {
	public NoSuchFeatureException(String featureName, Class<?> clazz) {
		super(String.format("Feature %s cannot be extracted from %s, because the class has no such field, " +
				"and there is no transformation configured for this feature.", featureName, clazz.getName()));
	}
}
