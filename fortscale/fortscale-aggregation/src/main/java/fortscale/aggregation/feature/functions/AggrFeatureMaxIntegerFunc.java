package fortscale.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.ConversionUtils;

/**
 * Created by orend on 26/07/2015.
 */

@JsonTypeName(AggrFeatureMaxIntegerFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureMaxIntegerFunc implements IAggrFeatureFunction, IAggrFeatureEventFunction {
	public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_max_value_func";
	private static final String UPDATE_AGGR_COMPARED_VALUE_FIELD_NAME = "comparedValue";
	private static final String UPDATE_AGGR_VALUE_FIELD_NAME = "value";
	private static final String UPDATE_AGGR_ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";
	
	private static final String CALC_AGGR_MAX_VALUE_FEATURE_FIELD_NAME = "maxValueFeature";
	

	@Override
	public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
		if (aggregatedFeatureConf == null || aggrFeature == null) {
			return null;
		}

		AggrFeatureMaxValue aggrFeatureMaxValue = (AggrFeatureMaxValue) aggrFeature.getValue();

		List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(UPDATE_AGGR_COMPARED_VALUE_FIELD_NAME);
		Feature featureForComparison = features.get(featureNames.get(0));
		Integer comparedValue = (Integer)featureForComparison.getValue();
		if (aggrFeatureMaxValue == null || comparedValue > aggrFeatureMaxValue.getComparedValue()) {
			aggrFeatureMaxValue = createAggrFeatureMaxValue(aggrFeatureMaxValue, aggregatedFeatureConf, features, comparedValue);
			aggrFeature.setValue(aggrFeatureMaxValue);
		}

		return aggrFeatureMaxValue;
	}
	
	private AggrFeatureMaxValue createAggrFeatureMaxValue(AggrFeatureMaxValue prevAggrFeatureMaxValue, AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Integer comparedValue){
		List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(UPDATE_AGGR_VALUE_FIELD_NAME);
		Feature keyFeature = features.get(featureNames.get(0));
		long total = 1;
		if(prevAggrFeatureMaxValue != null){
			total = incrementTotalNumberOfEvents(prevAggrFeatureMaxValue, 1);
		}
		AggrFeatureMaxValue ret = new AggrFeatureMaxValue(keyFeature.getValue(), comparedValue, total);
		featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(UPDATE_AGGR_ADDITIONAL_INFO_FIELD_NAME);
		for (String featureName : featureNames) {
			ret.putAdditionalInformation(featureName, features.get(featureName));
		}
		
		return ret;
	}
	
	private long incrementTotalNumberOfEvents(AggrFeatureMaxValue aggrFeatureMaxValue, long inc){
		long ret = inc;
		Integer total = ConversionUtils.convertToInteger(aggrFeatureMaxValue.getAdditionalInformation(AggrFeatureValue.AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS));
		if(total != null){
			ret = total + inc;
		}

		return ret;
	}
	

	@Override
	public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
			return null;
		}

		String aggregatedFeatureName = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(CALC_AGGR_MAX_VALUE_FEATURE_FIELD_NAME).get(0);
		AggrFeatureMaxValue retAggrFeatureMaxValue = null;
		long total = 0;
		for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
			Feature aggrFeature = aggrFeatures.get(aggregatedFeatureName);
			if (aggrFeature != null) {
				AggrFeatureMaxValue tmp = (AggrFeatureMaxValue) aggrFeature.getValue();
				if(tmp!=null){
					if(retAggrFeatureMaxValue == null || retAggrFeatureMaxValue.getComparedValue() < tmp.getComparedValue()) {
						retAggrFeatureMaxValue = tmp;
					}
					total = incrementTotalNumberOfEvents(tmp, total);
				}
			}
		}
		
		if(total > 0){
			retAggrFeatureMaxValue.setTotal(total);
		}
		
		Feature resFeature = new Feature(aggrFeatureEventConf.getName(), retAggrFeatureMaxValue);
		
		
		return resFeature;
	}

	
	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
	public class AggrFeatureMaxValue extends AggrFeatureValue{
		private Integer comparedValue;
		
		public AggrFeatureMaxValue(Object value, Integer comparedValue, Long total){
			super(value, total);
			this.comparedValue = comparedValue;
		}
		
		

		public Integer getComparedValue() {
			return comparedValue;
		}

		public void setComparedValue(Integer comparedValue) {
			this.comparedValue = comparedValue;
		}
		
	}
}
