package fortscale.aggregation.feature.functions;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

/**
 * Some util functions which are valuable when testing AggrFeature*Func classes.
 */
public class AggrFeatureTestUtils {

	public static Map<String, Feature> createFeatureMap(final ImmutablePair<String, Object>... featureValues) {
		Map<String, Feature> featureMap = new HashMap<>();
		for (ImmutablePair<String, Object> featureValue : featureValues) {
			Object value = featureValue.getRight();
			if (value instanceof String) {
				featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (String) value));
			} else if (value instanceof Number) {
				featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (Number) value));
			} else if (value == null || (value instanceof FeatureValue)) {
				featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (FeatureValue) value));
			} else {
				throw new IllegalArgumentException();
			}
		}
		return featureMap;
	}
}
