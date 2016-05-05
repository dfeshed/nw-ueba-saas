package fortscale.common.feature.extraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.RawEvent;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.utils.ConversionUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/feature-extractors-context-test.xml" })
public class FeatureExtractorsTest {
	private static final String ORIGINAL_FIELD_NAME1 = "org1";
	private static final String ORIGINAL_FIELD_NAME2 = "org2";
	private static final String DENOMINATOR_FIELD_NAME = "duration";
	private static final double ADDITION_TO_DENOMINATOR = 4.5;

	private static final String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST =               "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
	private static final String HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"host\",\"featureAdjustor\":{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"[0-9]+\",\"replacement\":\"\"}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"source_ip\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":24}}]}";
	private static final String PLAIN_FEATURE_EXTRACTOR_JSON_TO_TEST =				"{\n" + "        \"featureExtractorType\": \"event_feature_extractor\",\n" + "        \"fieldName\": \"event_time_utc\",\n" + "        \"featureAdjustor\": {\n" + "          \"type\": \"hour_of_day_feature_adjustor\"\n" + "        }\n" + "      }";
	public static final String FEATURE_NAME = "feature1";
	
	@Value("${impala.table.fields.data.source}")
	private String eventTypeFieldName;
	
	@Autowired
	private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;

	@Autowired
	private FeatureExtractService featureExtractService;

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

		EventFeatureExtractor fe1 = new EventFeatureExtractor(ORIGINAL_FIELD_NAME1, chainFeatureAdjustor);

		IPv4FeatureAdjustor iPv4FeatureAdjustor = new IPv4FeatureAdjustor(20);
		EventFeatureExtractor fe2 = new EventFeatureExtractor(ORIGINAL_FIELD_NAME2, iPv4FeatureAdjustor);

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
	public void testFeatureExtractorWithFirstPriorityAdjustments() throws Exception{
		FeatureExtractor featureExtractor = buildFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DENOMINATOR_FIELD_NAME, 4.5);
		jsonObject.put(ORIGINAL_FIELD_NAME1, "prefix0_9");

		Double ret = ConversionUtils.convertToDouble(featureExtractor.extract(new EventMessage(jsonObject)));

		Assert.assertEquals(5D, ret,0.0);
	}

	@Test
	public void testFeatureExtractorWithSecondPriorityAdjustments() throws Exception{
		FeatureExtractor featureExtractor = buildFeatureExtractor();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ORIGINAL_FIELD_NAME2, "82.165.195.70");

		FeatureStringValue ret = (FeatureStringValue) featureExtractor.extract(new EventMessage(jsonObject));

		Assert.assertEquals("82.165.192.0", ret.toString());
	}

	@Test
	public void testHostPatternFeatureExtraction() throws Exception{
		byte[] json = HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractor = mapper.readValue(json, FeatureExtractor.class);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("host", "m123ofXXXendingwith334");

		FeatureStringValue ret = (FeatureStringValue) featureExtractor.extract(new EventMessage(jsonObject));

		Assert.assertEquals("mofXXXendingwith", ret.toString());
	}

	@Test
	public void testIpFeatureExtraction() throws Exception{
		byte[] json = HOST_AND_SOURCE_IP_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractor = mapper.readValue(json, FeatureExtractor.class);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("source_ip", "82.165.195.70");

		FeatureStringValue ret = (FeatureStringValue)featureExtractor.extract(new EventMessage(jsonObject));

		Assert.assertEquals("82.165.195.0", ret.toString());

	}

	@Test
	public void testHourFeatureExtraction() throws Exception{
		byte[] json = PLAIN_FEATURE_EXTRACTOR_JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureExtractor featureExtractor = mapper.readValue(json, FeatureExtractor.class);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("event_time_utc", "1437210353");

		FeatureNumericValue ret = (FeatureNumericValue)featureExtractor.extract(new EventMessage(jsonObject));

		Assert.assertEquals(9, ret.getValue().intValue());
	}

	@Test
	public void	testFeatureExtractService() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("source_ip", "82.165.195.70");

		Feature feature = featureExtractService.extract(FEATURE_NAME, new EventMessage(jsonObject));
		FeatureStringValue ret = (FeatureStringValue)feature.getValue();

		Assert.assertEquals("82.165.195.0", ret.toString());
	}

	@Test
	public void	testFeatureExtractService_notExistingFeature() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("source_ip", "82.165.195.70");

		String nofeature = "notexistingfeature";
		Feature feature = featureExtractService.extract(nofeature, new EventMessage(jsonObject));

		Assert.assertEquals(null, feature.getValue());
		Assert.assertEquals(nofeature, feature.getName());
	}



	@Test
	public void	testFeatureExtractService_extractFeatureList() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("source_ip", "82.165.195.70");
		jsonObject.put("machine", "mymachine123");

		Set<String> featureNames = new HashSet<>();
		featureNames.addAll(Arrays.asList("feature1", "feature2"));


		Map<String, Feature> res = featureExtractService.extract(featureNames, new EventMessage(jsonObject));


		FeatureStringValue value1 = (FeatureStringValue)(res.get("feature1")).getValue();
		FeatureStringValue value2 = (FeatureStringValue)(res.get("feature2")).getValue();

		Assert.assertEquals("82.165.195.0", value1.toString());
		Assert.assertEquals("mymachine", value2.toString());
	}
	
	@Test
	public void	testFeatureExtractService_extractFeatureList_onKerberosLogin() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(eventTypeFieldName, "kerberos_logins");
		jsonObject.put("client_address", "82.165.195.70");
		jsonObject.put("normalized_username", "normUser1");

		Set<String> featureNames = new HashSet<>();
		featureNames.addAll(Arrays.asList("feature3", "normalized_username"));


		Map<String, Feature> res = featureExtractService.extract(featureNames, new RawEvent(jsonObject, dataEntitiesConfigWithBlackList,"kerberos_logins" ));


		FeatureStringValue value1 = (FeatureStringValue)(res.get("feature3")).getValue();
		FeatureStringValue value2 = (FeatureStringValue)(res.get("normalized_username")).getValue();

		Assert.assertEquals("82.165.195.0", value1.toString());
		Assert.assertEquals("normUser1", value2.toString());
	}
	
	@Test
	public void	testFeatureExtractService_extractFeature_onKerberosLogin() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(eventTypeFieldName, "kerberos_logins");
		jsonObject.put("client_address", "82.165.195.70");

		Set<String> featureNames = new HashSet<>();
		featureNames.addAll(Arrays.asList("feature3"));

		String featureName = "feature3";
		Feature res = featureExtractService.extract(featureName, new RawEvent(jsonObject, dataEntitiesConfigWithBlackList,"kerberos_logins" ));

		Assert.assertEquals("82.165.195.0", res.getValue().toString());
	}

}
