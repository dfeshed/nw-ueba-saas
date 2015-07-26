package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventNumberOfDistinctValuesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfDistinctValuesFunc extends AggrFeatureHistogramFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_distinct_values_func";
    public final static String FEATURE_NAME = "number_of_distinct_values";
    private final static String FEATURE_DISTINCT_VALUES = "distinct_values";

    private boolean includeValues = false;

    /**
     * Create new feature by running the associated {@link AggrFeatureFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        Feature feature = super.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        if(feature==null || feature.getValue()==null) {
            return null;
        }
        GenericHistogram histogram = (GenericHistogram)feature.getValue();
        JSONObject value = new JSONObject();
        value.put(FEATURE_NAME, histogram.getN());
        if (includeValues) {
            value.put(FEATURE_DISTINCT_VALUES, histogram.getObjects());
        }
        Feature resFeature = new Feature(FEATURE_NAME, value);

        return resFeature;
    }

    // Getter and Setter are only for unit tests
    public boolean getIncludeValues() { return this.includeValues; }
    public void setIncludeValues(boolean includeValues) {
        this.includeValues = includeValues;
    }
}
