package fortscale.streaming;

import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TaskTestUtil {
	public static Config buildTaskConfig(String taskConfigPropertiesFilePath) throws IOException {
		final Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(new File(taskConfigPropertiesFilePath));
		properties.load(fileInputStream);
		Map<String, String> propMap = new HashMap<>();

		for (Object key : properties.keySet()) {
			String keyStr = (String)key;
			propMap.put(keyStr, properties.getProperty(keyStr));
		}

		return new MapConfig(propMap);
	}
}
