package fortscale.streaming;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.ConfigException;

public final class ConfigUtils {

	public static String getConfigString(Config config, Object key) throws ConfigException {
		String value = config.get(key);
		if (StringUtils.isEmpty(value))
			throw new ConfigException("configuration is missing key " + key);
		return value;
	}
	
	
	public static List<String> getConfigStringList(Config config, String key) throws ConfigException {
		List<String> values = config.getList(key);
		if (values==null || values.isEmpty())
			throw new ConfigException("configuration is missing key " + key);
		return values;
	}
}
