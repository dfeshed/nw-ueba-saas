package fortscale.streaming.feature.extractor;

import java.io.IOException;

import org.apache.samza.config.Config;

import fortscale.streaming.TaskTestUtil;

public class TaskFeatureExtractorServiceConfigTestBase {

	protected FeatureExtractionService buildFeatureExtractionServiceFromTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		return buildFeatureExtractionServiceFromTaskConfig(taskConfigPropertiesFilePath, null);
	}

	protected FeatureExtractionService buildFeatureExtractionServiceFromTaskConfig(String taskConfigPropertiesFilePath, String propertyName) throws IOException{
		Config config = TaskTestUtil.buildTaskConfig(taskConfigPropertiesFilePath);
		
		FeatureExtractionService ret = propertyName == null ? new FeatureExtractionService(config) : new FeatureExtractionService(config, propertyName);
		
		return ret;
	}
}
