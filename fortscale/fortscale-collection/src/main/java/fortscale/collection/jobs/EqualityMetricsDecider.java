package fortscale.collection.jobs;

import fortscale.utils.kafka.IMetricsDecider;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EqualityMetricsDecider implements IMetricsDecider {
	private Set<Map.Entry<String, Object>> keyToExpectedValueEntries;

	/**
	 * @param keyToExpectedValueMap A mapping from a key to its expected value.
	 *                              This map cannot be null and cannot be empty.
	 *                              The keys cannot be null.
	 */
	public EqualityMetricsDecider(Map<String, Object> keyToExpectedValueMap) {
		Assert.notEmpty(keyToExpectedValueMap);
		keyToExpectedValueEntries = keyToExpectedValueMap.entrySet();
	}

	@Override
	public boolean decide(JSONObject metrics) {
		if (metrics == null) {
			return false;
		}

		for (Map.Entry<String, Object> entry : keyToExpectedValueEntries) {
			String key = entry.getKey();
			Object expectedValue = entry.getValue();

			if (!Objects.equals(metrics.get(key), expectedValue)) {
				return false;
			}
		}

		return true;
	}
}
