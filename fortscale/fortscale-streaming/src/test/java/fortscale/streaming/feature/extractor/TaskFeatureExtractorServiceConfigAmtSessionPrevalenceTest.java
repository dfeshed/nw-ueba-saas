package fortscale.streaming.feature.extractor;

import org.apache.samza.config.Config;
import org.junit.Test;

import fortscale.streaming.TaskTestUtil;

public class TaskFeatureExtractorServiceConfigAmtSessionPrevalenceTest extends TaskFeatureExtractorServiceConfigTestBase{
	
	@Test
	public void testSanity() throws Exception{
		String taskConfigPropertiesFilePath = "config/raw-events-prevalence-stats-task.properties";
		Config config = TaskTestUtil.buildPrevalenceTaskConfig(taskConfigPropertiesFilePath, "amtsession");
		buildFeatureExtractionServiceFromTaskConfig(config, null);
	}

}
