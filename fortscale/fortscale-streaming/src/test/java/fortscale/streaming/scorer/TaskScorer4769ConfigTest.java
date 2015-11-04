package fortscale.streaming.scorer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.junit.Test;

import fortscale.streaming.TaskTestUtil;


public class TaskScorer4769ConfigTest extends TaskScorerConfigTest{

	
	@Test
	public void testSanity() throws IOException{
		Config config = TaskTestUtil.buildTaskConfig("config/4769-prevalance-stats.properties");
		Config fieldsSubset = config.subset("fortscale.");
		Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", "4769"));
		dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
		
		buildScorersFromTaskConfig(dataSourceConfig);
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
