package fortscale.common.feature.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.common.feature.FeatureStringValue;
import org.junit.Assert;
import org.junit.Test;

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
	public void testClass20SubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(20);

		FeatureStringValue adjustedVal = (FeatureStringValue)featureAdjustor.adjust(new FeatureStringValue("82.165.195.70"), null);

		Assert.assertEquals("82.165.192.0", adjustedVal.toString());
	}

	@Test
	public void testZeroSubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(0);

		FeatureStringValue adjustedVal = (FeatureStringValue)featureAdjustor.adjust(new FeatureStringValue("82.165.195.70"), null);

		Assert.assertEquals("0.0.0.0", adjustedVal.toString());
	}

	@Test
	public void test31SubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(31);

		FeatureStringValue adjustedVal = (FeatureStringValue)featureAdjustor.adjust(new FeatureStringValue("82.165.195.171"), null);

		Assert.assertEquals("82.165.195.170", adjustedVal.toString());
	}

	@Test
	public void test24SubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(24);

		FeatureStringValue adjustedVal = (FeatureStringValue)featureAdjustor.adjust(new FeatureStringValue("82.165.195.171"), null);

		Assert.assertEquals("82.165.195.0", adjustedVal.toString());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNegativeSubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(-5);

		featureAdjustor.adjust(new FeatureStringValue("82.165.195.171"), null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAbove31SubnetMask() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(32);

		featureAdjustor.adjust(new FeatureStringValue("82.165.195.171"), null);
	}
}
