package fortscale.streaming.feature.extractor;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IPv4FeatureAdjustorTest {
	private static final String JSON_TO_TEST = "{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}";
	private static final int SUBNET_MASK_VALUE_FOR_JSON_TEST = 20;

	private FeatureAdjustor buildFeatureAdjustor(int subnetMask){
		return new IPv4FeatureAdjustor(subnetMask);
	}
	
	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(SUBNET_MASK_VALUE_FOR_JSON_TEST);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(featureAdjustor);

		//{"type":"ipv4_feature_adjustor","subnetMask":20}
		Assert.assertNotNull(json);
		
		Assert.assertEquals(JSON_TO_TEST, json);
	}
	
	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = JSON_TO_TEST.getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		FeatureAdjustor featureAdjustorActual = mapper.readValue(json, FeatureAdjustor.class);
		
		FeatureAdjustor featureAdjustorExpected = buildFeatureAdjustor(SUBNET_MASK_VALUE_FOR_JSON_TEST);
		
		Assert.assertEquals(featureAdjustorExpected, featureAdjustorActual);
	}
	
	@Test
	public void testClass20SubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(20);
		
		String adjustedVal = (String) featureAdjustor.adjust("82.165.195.70", null);
		
		Assert.assertEquals("82.165.192.0", adjustedVal);
	}
	
	@Test
	public void testZeroSubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(0);
		
		String adjustedVal = (String) featureAdjustor.adjust("82.165.195.70", null);
		
		Assert.assertEquals("0.0.0.0", adjustedVal);
	}
	
	@Test
	public void test31SubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(31);
		
		String adjustedVal = (String) featureAdjustor.adjust("82.165.195.171", null);
		
		Assert.assertEquals("82.165.195.170", adjustedVal);
	}
	
	@Test
	public void test24SubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(24);
		
		String adjustedVal = (String) featureAdjustor.adjust("82.165.195.171", null);
		
		Assert.assertEquals("82.165.195.0", adjustedVal);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNegativeSubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(-5);
		
		featureAdjustor.adjust("82.165.195.70", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAbove31SubnetMask(){
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(32);
		
		featureAdjustor.adjust("82.165.195.70", null);
	}
}
