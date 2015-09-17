package fortscale.streaming;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.ConfigException;

import java.util.List;

public final class ConfigUtils {
	public static String getConfigString(Config config, Object key) throws ConfigException {
		String value = config.get(key);
		if (StringUtils.isBlank(value))
			throw new ConfigException("configuration is missing key " + key);
		return value;
	}

	public static boolean isConfigContainKey(Config config, Object key) throws ConfigException {
		String value = config.get(key);
		return StringUtils.isNotEmpty(value);
	}

	public static List<String> getConfigStringList(Config config, String key) throws ConfigException {
		List<String> values = config.getList(key);
		if (values == null || values.isEmpty())
			throw new ConfigException("configuration is missing key " + key);
		return values;
	}

	public static double getConfigDouble(Config config, String key) throws ConfigException {
		Double value = config.getDouble(key);
		if (value == null)
			throw new ConfigException("configuration is missing key " + key);
		else if (value.isNaN())
			throw new ConfigException("value of key " + key + " is not a number");
		return value;
	}

	public static double getConfigPositiveDouble(Config config, String key) throws ConfigException {
		double value = getConfigDouble(config, key);
		if (value <= 0)
			throw new ConfigException("value of key " + key + " must be positive");
		return value;
	}
}
