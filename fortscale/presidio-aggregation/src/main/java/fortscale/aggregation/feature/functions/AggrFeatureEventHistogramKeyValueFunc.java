package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.util.GenericHistogram;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * This aggregation event function combines histograms from multiple feature buckets, each histogram maps a key to its
 * number of appearances (key -> counter). If the combined histogram is empty, the function returns null, otherwise it
 * returns the number of appearances of a certain key (defined as a static parameter in the function's ASL).
 * <p>
 * For example - An aggregation event that is "number of successful deletions":
 * [1] In the feature buckets, filter in enriched records of type
 *     "deletion" and define a histogram for the values of "result".
 * [2] If the combined histogram of a certain time range is empty, the aggregation event of that time
 *     range will not be generated (there weren't any type of deletions, nor successful nor failed).
 * [3] Otherwise, the function will output the value of the histogram key "success" (0 if the key doesn't exist).
 *
 * @author Lior Govrin
 */
@JsonTypeName(AggrFeatureEventHistogramKeyValueFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
		creatorVisibility = JsonAutoDetect.Visibility.ANY,
		fieldVisibility = JsonAutoDetect.Visibility.NONE,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureEventHistogramKeyValueFunc extends AbstractAggrFeatureEventHistogram {
	public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_key_value_func";
	public static final String KEY_FIELD_NAME = "key";

	private final String key;

	/**
	 * C'tor.
	 *
	 * @param key The key whose number of appearances (counter) is required
	 */
	@JsonCreator
	public AggrFeatureEventHistogramKeyValueFunc(@JsonProperty(KEY_FIELD_NAME) String key) {
		Assert.hasText(key, "Function is missing the key whose value needs to be retrieved.");
		this.key = key;
	}

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram genericHistogram) {
		AggrFeatureValue aggrFeatureValue = null;

		if (genericHistogram != null) {
			// Get the underlying map from key to number of appearances (counter)
			Map<String, Double> histogramMap = genericHistogram.getHistogramMap();

			if (histogramMap != null && !histogramMap.isEmpty()) {
				Double counter = histogramMap.get(key);
				counter = counter == null ? 0 : counter;

				// If counter is NaN or infinite, act as if there weren't any counts at all
				if (!counter.isNaN() && !counter.isInfinite()) {
					aggrFeatureValue = new AggrFeatureValue(counter, (long)genericHistogram.getTotalCount());
				}
			}
		}

		return aggrFeatureValue;
	}
}
