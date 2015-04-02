package fortscale.streaming.feature.extractor;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

public class TaskFeatureExtractorServiceConfigAmtSessionWriteTest extends TaskFeatureExtractorServiceConfigTestBase{
	private static final String AMT_HOST_FIELD_NAME = "amt_host";
	private static final String SOURCE_IP_FIELD_NAME = "source_ip";
	
	@Test
	public void testSanity() throws Exception{
		buildFeatureExtractionServiceFromTaskConfig("config/amtsessions-events-write.properties");
	}

	
	@Test
	public void testAmtHostExist() throws Exception{
		FeatureExtractionService featureExtractionService = buildFeatureExtractionServiceFromTaskConfig("config/amtsessions-events-write.properties");
		
		JSONObject jsonObject = new JSONObject();
		String hostName = "testhost";
		jsonObject.put(AMT_HOST_FIELD_NAME, hostName);
		jsonObject.put(SOURCE_IP_FIELD_NAME, "192.22.33.4");
		
		String ret = (String) featureExtractionService.extract(AMT_HOST_FIELD_NAME, jsonObject);
		
		Assert.assertEquals(hostName, ret);
	}
	
	@Test
	public void testAmtHostBlank() throws Exception{
		FeatureExtractionService featureExtractionService = buildFeatureExtractionServiceFromTaskConfig("config/amtsessions-events-write.properties");
		
		JSONObject jsonObject = new JSONObject();
		String hostName = "  ";
		String sourceIp = "192.22.33.4";
		jsonObject.put(AMT_HOST_FIELD_NAME, hostName);
		jsonObject.put(SOURCE_IP_FIELD_NAME, sourceIp);
		
		String ret = (String) featureExtractionService.extract(AMT_HOST_FIELD_NAME, jsonObject);
		
		Assert.assertEquals(sourceIp, ret);
	}
}
