package fortscale.streaming.feature.extractor;

import java.io.IOException;

import org.apache.samza.config.Config;

import fortscale.streaming.TaskTestUtil;

public class TaskFeatureExtractorServiceConfigTestBase {
	
	protected FeatureExtractionService buildFeatureExtractionServiceFromTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		Config config = TaskTestUtil.buildTaskConfig(taskConfigPropertiesFilePath);
		
		FeatureExtractionService ret = new FeatureExtractionService(config);
		
		return ret;
	}
}
