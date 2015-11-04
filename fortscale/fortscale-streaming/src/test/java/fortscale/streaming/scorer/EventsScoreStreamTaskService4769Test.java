package fortscale.streaming.scorer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.junit.Test;

import fortscale.streaming.TaskTestUtil;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.service.EventsScoreStreamTaskService;

public class EventsScoreStreamTaskService4769Test extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/4769-prevalance-stats.properties");
	}
	
	protected EventsScoreStreamTaskService createEventsScoreStreamTaskService(String taskConfigPropertiesFilePath) throws Exception{
		when(context.getMetricsRegistry()).thenReturn(metricsRegistry);
		when(metricsRegistry.newCounter((String)anyObject(), (String)anyObject())).thenReturn(counter);
		
		Config config = TaskTestUtil.buildTaskConfig(taskConfigPropertiesFilePath);
		Config fieldsSubset = config.subset("fortscale.");
		Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", "4769"));
		dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
		
		EventsScoreStreamTaskService eventsScoreStreamTaskService = new EventsScoreStreamTaskService(dataSourceConfig, context, modelService, new FeatureExtractionService(config));
		return eventsScoreStreamTaskService;
	}
	
	private Config addPrefixToConfigEntries(Config config, String prefix) {
		Map<String, String> newConfigMap = new HashMap<>();
		if(config!=null && StringUtils.isNotBlank(prefix)) {
			for (String oldKey : config.keySet()) {
				String value = config.get(oldKey);
				String newKey = String.format("%s%s", prefix, oldKey);
				newConfigMap.put(newKey, value);
			}
		}
		return new MapConfig(newConfigMap);
	}
}
