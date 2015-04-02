package fortscale.streaming.feature.extractor;

import org.junit.Test;

public class TaskFeatureExtractorServiceConfigAmtSessionPrevalenceTest extends TaskFeatureExtractorServiceConfigTestBase{
	
	@Test
	public void testSanity() throws Exception{
		buildFeatureExtractionServiceFromTaskConfig("config/amtsessions-prevalance-stats.properties");
	}

}
