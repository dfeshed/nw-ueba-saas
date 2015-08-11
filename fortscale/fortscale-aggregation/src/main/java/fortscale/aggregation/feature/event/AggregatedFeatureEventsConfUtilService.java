package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.functions.AggrFeatureHistogramFunc;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

@Component
public class AggregatedFeatureEventsConfUtilService {
	private static final String OUTPUT_AGGREGATED_FEATURE_CONF_NAME_SUFFIX = "histogram";
	
	@Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
	
	public String buildOutputBucketConfName(AggregatedFeatureEventConf conf){
		return String.format("%s_%s", conf.getBucketConfName(), conf.getName());
	}
	
	public String buildOutputBucketDataSource(AggregatedFeatureEventConf conf){
		return String.format("%s.%s", eventTypeFieldValue, buildOutputAggregatedFeatureConfGroupByFieldName(conf));
	}
	
	public String buildOutputAggregatedFeatureConfName(AggregatedFeatureEventConf conf){
		return String.format("%s_%s", conf.getName(), OUTPUT_AGGREGATED_FEATURE_CONF_NAME_SUFFIX);
	}
	
	public String buildOutputAggregatedFeatureConfGroupByFieldName(AggregatedFeatureEventConf conf){
		return String.format("%s.%s", conf.getBucketConfName(), conf.getName());
	}
	
	public AggregatedFeatureConf createOutputAggregatedFeatureConf(AggregatedFeatureEventConf conf) throws ParseException, JsonProcessingException{
		String name = buildOutputAggregatedFeatureConfName(conf);
		Map<String, List<String>> featureNamesMap = new HashMap<>();
		List<String> features = new ArrayList<>();
		features.add(buildOutputAggregatedFeatureConfGroupByFieldName(conf));
		featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, features);
		ObjectMapper mapper = new ObjectMapper();
		String aggrFeatureFuncJsonStr = mapper.writeValueAsString(new AggrFeatureHistogramFunc());
		JSONObject aggrFeatureFuncJsonObject = (JSONObject) JSONValue.parseWithException(aggrFeatureFuncJsonStr);
		AggregatedFeatureConf aggregatedFeatureConf = new AggregatedFeatureConf(name, featureNamesMap, aggrFeatureFuncJsonObject);
		
		return aggregatedFeatureConf;
	}
}
