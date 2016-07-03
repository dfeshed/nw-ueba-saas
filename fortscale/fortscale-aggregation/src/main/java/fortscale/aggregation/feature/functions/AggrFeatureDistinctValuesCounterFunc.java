package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;

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

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
		if (removeNA && histogram.get(OrganizationActivityLocationDocument.NOT_AVAILABLE_VALUE) != null) {
			return new AggrFeatureValue(histogram.getN() - 1, (long)histogram.getTotalCount());
		}
		return new AggrFeatureValue(histogram.getN(), (long)histogram.getTotalCount());
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

}