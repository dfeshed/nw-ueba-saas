package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AllFeatureExtractorsTest {
	private static final String ORIGINAL_FIELD_NAME1 = "org1";
	private static final String ORIGINAL_FIELD_NAME2 = "org2";
	private static final String NORMALIZED_FIELD_NAME = "norm";
	private static final String DENOMINATOR_FIELD_NAME = "duration";
	private static final double ADDITION_TO_DENOMINATOR = 4.5;
	
	private static final String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"type\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"org1\",\"normalizedFieldName\":\"norm\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"org2\",\"normalizedFieldName\":\"norm\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
	private static final String HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"type\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"host\",\"featureAdjustor\":{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"[0-9]+\",\"replacement\":\"\"}},{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"source_ip\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":24}}]}";
	
	private FeatureExtractor buildFeatureExtractor(){
		PatternReplacementFeatureAdjustor patternReplacementFeatureAdjustor1 = new PatternReplacementFeatureAdjustor("_", ".");
		PatternReplacementFeatureAdjustor patternReplacementFeatureAdjustor2 = new PatternReplacementFeatureAdjustor("prefix", "");
		NumberDividerFeatureAdjustor numberDividerFeatureAdjustor = new NumberDividerFeatureAdjustor(ADDITION_TO_DENOMINATOR, DENOMINATOR_FIELD_NAME);
		InverseValueFeatureAdjustor inverseValueFeatureAdjustor = new InverseValueFeatureAdjustor(0.1);
		List<FeatureAdjustor> featureAdjustorList = new ArrayList<>();
		featureAdjustorList.add(patternReplacementFeatureAdjustor1);
		featureAdjustorList.add(patternReplacementFeatureAdjustor2);
		featureAdjustorList.add(numberDividerFeatureAdjustor);
		featureAdjustorList.add(inverseValueFeatureAdjustor);
		ChainFeatureAdjustor chainFeatureAdjustor = new ChainFeatureAdjustor(featureAdjustorList);
		
		EventFeatureExtractor fe1 = new EventFeatureExtractor(ORIGINAL_FIELD_NAME1, NORMALIZED_FIELD_NAME, chainFeatureAdjustor);
		
		IPv4FeatureAdjustor iPv4FeatureAdjustor = new IPv4FeatureAdjustor(20);
		EventFeatureExtractor fe2 = new EventFeatureExtractor(ORIGINAL_FIELD_NAME2, NORMALIZED_FIELD_NAME, iPv4FeatureAdjustor);
		
		List<FeatureExtractor> featureExtractorList = new ArrayList<>();
		featureExtractorList.add(fe1);
		featureExtractorList.add(fe2);
		return new PriorityContainerFeatureExtractor(featureExtractorList);
	}

	
	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureExtractor featureExtractor = buildFeatureExtractor();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(featureExtractor);

		//{"type":"priority_container_feature_extractor","featureExtractorList":[{"type":"event_feature_extractor","originalFieldName":"org1","normalizedFieldName":"norm","featureAdjustor":{"type":"chain_feature_adjustor","featureAdjustorList":[{"type":"pattern_replacment_feature_adjustor","pattern":"_","replacement":"."},{"type":"pattern_replacment_feature_adjustor","pattern":"prefix","replacement":""},{"type":"number_divider_feature_adjustor","additionToDenominator":4.5,"denominatorFieldName":"duration"},{"type":"inv_val_feature_adjustor","denominator":0.1}]}},{"type":"event_feature_extractor","originalFieldName":"org2","normalizedFieldName":"norm","featureAdjustor":{"type":"ipv4_feature_adjustor","subnetMask":20}}]}
		Assert.assertNotNull(json);
		
		Assert.assertEquals(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST, json);
	}
	
	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractorActual = mapper.readValue(json, FeatureExtractor.class);
		
		FeatureExtractor featureExtractorExpected = buildFeatureExtractor();
		
		Assert.assertEquals(featureExtractorExpected, featureExtractorActual);
	}
	
	@Test
	public void testFeatureExtractorWithFirstPriorityAdjustments(){
		FeatureExtractor featureExtractor = buildFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DENOMINATOR_FIELD_NAME, 4.5);
		jsonObject.put(ORIGINAL_FIELD_NAME1, "prefix0_9");
		
		Double ret = ConversionUtils.convertToDouble(featureExtractor.extract(jsonObject));
		
		Assert.assertEquals(5D, ret,0.0);
		Assert.assertEquals(5D, jsonObject.get(NORMALIZED_FIELD_NAME));
	}
	
	@Test
	public void testFeatureExtractorWithSecondPriorityAdjustments(){
		FeatureExtractor featureExtractor = buildFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ORIGINAL_FIELD_NAME2, "82.165.195.70");
		
		String ret = (String) featureExtractor.extract(jsonObject);
		Assert.assertEquals("82.165.192.0", ret);
		Assert.assertEquals("82.165.192.0", jsonObject.get(NORMALIZED_FIELD_NAME));
	}
	
	@Test
	public void testHostPatternFeatureExtraction() throws Exception{
		byte[] json = HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractor = mapper.readValue(json, FeatureExtractor.class);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("host", "m123ofXXXendingwith334");
		
		String ret = (String) featureExtractor.extract(jsonObject);
		Assert.assertEquals("mofXXXendingwith", ret);
	}
	
	@Test
	public void testIpFeatureExtraction() throws Exception{
		byte[] json = HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractor = mapper.readValue(json, FeatureExtractor.class);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("source_ip", "82.165.195.70");
		
		String ret = (String) featureExtractor.extract(jsonObject);
		Assert.assertEquals("82.165.195.0", ret);
	}
}
