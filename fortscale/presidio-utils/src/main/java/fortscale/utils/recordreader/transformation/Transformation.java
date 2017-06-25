package fortscale.utils.recordreader.transformation;

import java.util.Collection;
import java.util.Map;

/**
 * A transformation is a function that takes a group of existing
 * fields (one or more) and transforms them into a new feature.
 *
 * Created by Lior Govrin on 22/06/2017.
 */
public interface Transformation<T> {
	/**
	 * @return the name of the new feature
	 */
	String getFeatureName();

	/**
	 * @return the names of the existing fields that are required for the transformation
	 */
	Collection<String> getRequiredFieldNames();

	/**
	 * Transform the given group of existing fields into a new feature of type T.
	 *
	 * @param requiredFieldNameToValueMap a map from a name of an existing field that
	 *                                    is required for the transformation to its value
	 * @return the value of the new feature
	 */
	T transform(Map<String, Object> requiredFieldNameToValueMap);
}
