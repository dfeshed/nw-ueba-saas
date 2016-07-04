package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.util.GenericHistogram;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureDistinctValuesCounterFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureDistinctValuesCounterFunc extends AbstractAggrFeatureEventHistogram {

	private static final String FEATURE_DISTINCT_VALUES = "distinct_values";

    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_distinct_values_counter_func";

    private boolean includeValues = false;
	private boolean removeNA = false;

	private List<String> additionalNAValues = Collections.emptyList();

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
		if (removeNA) {
			int numOfNAFeatureValues = countNumOfNAFeatureValues(histogram);

			return new AggrFeatureValue(histogram.getN() - numOfNAFeatureValues, (long)histogram.getTotalCount());
		}
		return new AggrFeatureValue(histogram.getN(), (long)histogram.getTotalCount());
	}

	private int countNumOfNAFeatureValues(GenericHistogram histogram) {
		Set<String> naValuesToExclude = new HashSet<>();

		// first add the generic N/A values which exist in the histogram to exclusion list.
		naValuesToExclude.addAll(AggGenericNAFeatureValues.getNAValues().stream().filter(naValue -> histogram.getHistogramMap().containsKey(naValue)).collect(Collectors.toList()));

		// than add the existing additional N/A values to exclusion list
		naValuesToExclude.addAll(additionalNAValues.stream().filter(additionalNAValue -> histogram.getHistogramMap().containsKey(additionalNAValue)).collect(Collectors.toList()));

		return naValuesToExclude.size();
	}

	@Override
	protected void fillAggrFeatureValueWithAdditionalInfo(AggrFeatureValue aggrFeatureValue,
			GenericHistogram histogram) {
    	super.fillAggrFeatureValueWithAdditionalInfo(aggrFeatureValue, histogram);
    	if (includeValues) {
            aggrFeatureValue.putAdditionalInformation(FEATURE_DISTINCT_VALUES, histogram.getObjects());
        }
    }

	// Getter and Setter are only for unit tests
	public boolean getIncludeValues() {
		return this.includeValues;
	}

	public void setIncludeValues(boolean includeValues) {
		this.includeValues = includeValues;
	}

	public boolean getRemoveNA() {
		return this.removeNA;
	}

	public void setRemoveNA(boolean removeNA) {
		this.removeNA = removeNA;
	}

	public List<String> getAdditionalNAValues() {
		return additionalNAValues;
	}

	public void setAdditionalNAValues(List<String> additionalNAValues) {
		this.additionalNAValues = additionalNAValues;
	}
}