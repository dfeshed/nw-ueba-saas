package fortscale.streaming;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TaskTestUtil {

	public static Config buildTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		final Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(new File(taskConfigPropertiesFilePath));
		
		properties.load(fileInputStream);
		
		Map<String,String> propMap = new HashMap<>();
		for(Object key: properties.keySet()){
			String keyStr = (String) key;
			propMap.put(keyStr, properties.getProperty(keyStr));
		}

		return new MapConfig(propMap);
	}
	
	public static Config buildPrevalenceTaskConfig(String taskConfigPropertiesFilePath, StreamingTaskDataSourceConfigKey configKey) throws IOException{
		Config config = buildTaskConfig(taskConfigPropertiesFilePath);
		
		Config fieldsSubset = config.subset("fortscale.events.");
		Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", configKey.getDataSource() + "_MultipleEventsPrevalenceModelStreamTask"));
		dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
		
		return dataSourceConfig;
	}
	
	private static Config addPrefixToConfigEntries(Config config, String prefix) {
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
