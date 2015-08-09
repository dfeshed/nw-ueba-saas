package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventNumberOfDistinctValuesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfDistinctValuesFunc extends AbstractAggrFeatureEventHistogram {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_distinct_values_func";
    private static final String FEATURE_DISTINCT_VALUES = "distinct_values";

    private boolean includeValues = false;

    /**
     * Create new feature by running the associated {@link IAggrFeatureFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */

    // Getter and Setter are only for unit tests
    public boolean getIncludeValues() { return this.includeValues; }
    public void setIncludeValues(boolean includeValues) {
        this.includeValues = includeValues;
    }

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
		return new AggrFeatureValue(histogram.getN());
	}
	
	@Override
	protected void fillAggrFeatureValueWithAdditionalInfo(AggrFeatureValue aggrFeatureValue, GenericHistogram histogram){
    	super.fillAggrFeatureValueWithAdditionalInfo(aggrFeatureValue, histogram);
    	if (includeValues) {
            aggrFeatureValue.putAdditionalInformation(FEATURE_DISTINCT_VALUES, histogram.getObjects());
        }
    }
}
