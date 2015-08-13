package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.ConversionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by orend on 13/08/2015.
 */

@JsonTypeName(AggrFeatureMaxIntegerFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHasEventsFunc extends AggrFeatureEventNumberOfEventsFunc
{
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_has_events_func";

	@Override
	public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		Feature aggrFeature = super.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
		Feature resFeature;
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)aggrFeature.getValue();
		if (ConversionUtils.convertToInteger(aggrFeatureValue.getValue()) > 0)
		{
			resFeature = new Feature(aggrFeature.getName(), 1);
		}
		else
		{
			resFeature = new Feature(aggrFeature.getName(), 0);
		}
		return resFeature;
	}
}
