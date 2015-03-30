package fortscale.streaming.feature.extractor;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.utils.ConversionUtils;

public class EventFeatureExtractorTest {
	private static final String ORIGINAL_FIELD_NAME = "org";
	private static final String NORMALIZED_FIELD_NAME = "norm";
	private static final String DURATION_FIELD_NAME = "duration";
	private static final int DURATION_ADDITION_IN_MIN = 270;
	private static final String CONSTANT_VALUE_FEATURE_ADJUSTOR_VALUE = "5.9";
	
	private static final String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"originalFieldName\":\"org\",\"normalizedFieldName\":\"norm\",\"featureAdjustorPriorityList\":[{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"rate_feature_adjustor\",\"durationAdditionInMin\":270,\"durationFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]},{\"type\":\"const_val_feature_adjustor\",\"constantValue\":\"5.9\"}]}";
	
	private EventFeatureExtractor buildEventFeatureExtractor(){
		PatternReplacementFeatureAdjustor patternReplacementFeatureAdjustor1 = new PatternReplacementFeatureAdjustor("_", ".");
		PatternReplacementFeatureAdjustor patternReplacementFeatureAdjustor2 = new PatternReplacementFeatureAdjustor("prefix", "");
		RateFeatureAdjustor rateFeatureAdjustor = new RateFeatureAdjustor(DURATION_ADDITION_IN_MIN, DURATION_FIELD_NAME);
		InverseValueFeatureAdjustor inverseValueFeatureAdjustor = new InverseValueFeatureAdjustor(0.1);
		List<FeatureAdjustor> featureAdjustorList = new ArrayList<>();
		featureAdjustorList.add(patternReplacementFeatureAdjustor1);
		featureAdjustorList.add(patternReplacementFeatureAdjustor2);
		featureAdjustorList.add(rateFeatureAdjustor);
		featureAdjustorList.add(inverseValueFeatureAdjustor);
		ChainFeatureAdjustor chainFeatureAdjustor = new ChainFeatureAdjustor(featureAdjustorList);
		ConstantValueFeatureAdjustor constantValueFeatureAdjustor = new ConstantValueFeatureAdjustor(CONSTANT_VALUE_FEATURE_ADJUSTOR_VALUE);
		
		List<FeatureAdjustor> featureAdjustorPriorityList = new ArrayList<>();
		featureAdjustorPriorityList.add(chainFeatureAdjustor);
		featureAdjustorPriorityList.add(constantValueFeatureAdjustor);
		return new EventFeatureExtractor(ORIGINAL_FIELD_NAME, NORMALIZED_FIELD_NAME, featureAdjustorPriorityList);
	}

	
	@Test
	public void serialize_to_json() throws JsonProcessingException{
		EventFeatureExtractor eventFeatureExtractor = buildEventFeatureExtractor();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(eventFeatureExtractor);

		//{"originalFieldName":"org","normalizedFieldName":"norm","featureAdjustorList":[{"type":"chain_feature_adjustor","featureAdjustorList":[{"type":"pattern_replacment_feature_adjustor","pattern":"_","replacement":"."},{"type":"pattern_replacment_feature_adjustor","pattern":"prefix","replacement":""},{"type":"rate_feature_adjustor","durationAdditionInMin":270,"durationFieldName":"duration"},{"type":"inv_val_feature_adjustor","denominator":0.1}]},{"type":"const_val_feature_adjustor","constantValue":"5.9"}]}
		Assert.assertNotNull(json);
		
		Assert.assertEquals(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST, json);
	}
	
	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		EventFeatureExtractor eventFeatureExtractorActual = mapper.readValue(json, EventFeatureExtractor.class);
		
		EventFeatureExtractor eventFeatureExtractorExpected = buildEventFeatureExtractor();
		
		Assert.assertEquals(eventFeatureExtractorExpected, eventFeatureExtractorActual);
	}
	
	@Test
	public void testFeatureExtractorWithFirstPriorityAdjustments(){
		EventFeatureExtractor eventFeatureExtractor = buildEventFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DURATION_FIELD_NAME, 4.5);
		jsonObject.put(ORIGINAL_FIELD_NAME, "prefix0_9");
		
		Double ret = ConversionUtils.convertToDouble(eventFeatureExtractor.extract(jsonObject));
		
		Assert.assertEquals(5D, ret,0.0);
		Assert.assertEquals(5D, jsonObject.get(NORMALIZED_FIELD_NAME));
	}
	
	@Test
	public void testFeatureExtractorWithSecondPriorityAdjustments(){
		EventFeatureExtractor eventFeatureExtractor = buildEventFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DURATION_FIELD_NAME, 4.5);
		jsonObject.put(ORIGINAL_FIELD_NAME, "prefix");
		
		String ret = (String) eventFeatureExtractor.extract(jsonObject);
		Assert.assertEquals(CONSTANT_VALUE_FEATURE_ADJUSTOR_VALUE, ret);
		Assert.assertEquals(CONSTANT_VALUE_FEATURE_ADJUSTOR_VALUE, jsonObject.get(NORMALIZED_FIELD_NAME));
	}
}
