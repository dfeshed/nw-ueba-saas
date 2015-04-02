package fortscale.streaming.feature.extractor;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InverseValueFeatureAdjustorTest {

	private static final String JSON_TO_TEST = "{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}";
	private static final double DENOMINATOR_FOR_JSON_TEST = 0.1;

	private FeatureAdjustor buildFeatureAdjustor(double denominator){
		return new InverseValueFeatureAdjustor(denominator);
	}
	
	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(featureAdjustor);

		//{"type":"inv_val_feature_adjustor","denominator":0.1}
		Assert.assertNotNull(json);
		
		Assert.assertEquals(JSON_TO_TEST, json);
	}
	
	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		FeatureAdjustor featureAdjustorActual = mapper.readValue(json, FeatureAdjustor.class);
		
		FeatureAdjustor featureAdjustorExpected = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);
		
		Assert.assertEquals(featureAdjustorExpected, featureAdjustorActual);
	}
	
	@Test 
	public void testPositiveDenominator(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);
		
		double adjustedVal = (double) featureAdjustor.adjust(4.9, null);
		
		Assert.assertEquals(0.2, adjustedVal,0.0);
	}
	
	@Test 
	public void testZeroFeatureValue(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);
		
		double adjustedVal = (double) featureAdjustor.adjust(0, null);
		
		Assert.assertEquals(10, adjustedVal,0.0);
	}
	
	@Test 
	public void testZeroDenominator(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(-0.1);
		
		Object adjustedVal = featureAdjustor.adjust(0.1, null);
		
		Assert.assertNull(adjustedVal);
	}
}
