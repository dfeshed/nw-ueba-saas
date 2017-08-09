package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.functions.AggrFeatureHistogramFunc;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatedFeatureEventsConfUtilService {
	private static final String OUTPUT_AGGREGATED_FEATURE_CONF_NAME_SUFFIX = "histogram";

	private String eventTypeFieldValue;
	private String contextFieldName;

	public AggregatedFeatureEventsConfUtilService(String eventTypeFieldValue, String contextFieldName) {
		this.eventTypeFieldValue = eventTypeFieldValue;
		this.contextFieldName = contextFieldName;
	}

	public String buildOutputBucketConfName(AggregatedFeatureEventConf conf){
		return String.format("%s_%s", conf.getBucketConfName(), conf.getName());
	}

	public String buildOutputBucketDataSource(AggregatedFeatureEventConf conf){
		return String.format("%s.%s", eventTypeFieldValue, buildFullAggregatedFeatureEventConfName(conf));
	}

	public String buildOutputAggregatedFeatureConfName(AggregatedFeatureEventConf conf){
		return String.format("%s_%s", conf.getName(), OUTPUT_AGGREGATED_FEATURE_CONF_NAME_SUFFIX);
	}

	public String buildAggregatedFeatureContextFieldName(String contextName){
		return String.format("%s.%s", contextFieldName, contextName);
	}

	public AggregatedFeatureConf createOutputAggregatedFeatureConf(AggregatedFeatureEventConf conf) throws ParseException, JsonProcessingException{
		String name = buildOutputAggregatedFeatureConfName(conf);
		Map<String, List<String>> featureNamesMap = new HashMap<>();
		List<String> features = new ArrayList<>();
		features.add(buildFullAggregatedFeatureEventConfName(conf));
		featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, features);
		ObjectMapper mapper = new ObjectMapper();
		String aggrFeatureFuncJsonStr = mapper.writeValueAsString(new AggrFeatureHistogramFunc());
		JSONObject aggrFeatureFuncJsonObject = (JSONObject) JSONValue.parseWithException(aggrFeatureFuncJsonStr);
		return new AggregatedFeatureConf(name, featureNamesMap, aggrFeatureFuncJsonObject);
	}

	public static String buildFullAggregatedFeatureEventName(String bucketConfName, String aggregatedFeatureEventName){
		return String.format("%s.%s", bucketConfName, aggregatedFeatureEventName);
	}

	public static String buildFullAggregatedFeatureEventConfName(AggregatedFeatureEventConf conf){
		return buildFullAggregatedFeatureEventName(conf.getBucketConfName(), conf.getName());
	}
}
