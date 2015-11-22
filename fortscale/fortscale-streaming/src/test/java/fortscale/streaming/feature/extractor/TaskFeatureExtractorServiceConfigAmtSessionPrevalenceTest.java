package fortscale.streaming.feature.extractor;

import fortscale.streaming.TaskTestUtil;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.apache.samza.config.Config;
import org.junit.Test;

public class TaskFeatureExtractorServiceConfigAmtSessionPrevalenceTest extends TaskFeatureExtractorServiceConfigTestBase{
	
	@Test
	public void testSanity() throws Exception{
		String taskConfigPropertiesFilePath = "config/raw-events-prevalence-stats-task.properties";
		Config config = TaskTestUtil.buildPrevalenceTaskConfig(taskConfigPropertiesFilePath, new StreamingTaskDataSourceConfigKey("amtsession", "AMTSessionizeStreamTask"));
		buildFeatureExtractionServiceFromTaskConfig(config, null);
	}

}
