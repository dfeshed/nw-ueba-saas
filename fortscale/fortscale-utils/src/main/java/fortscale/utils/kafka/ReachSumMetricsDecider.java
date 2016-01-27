package fortscale.utils.kafka;

import fortscale.utils.ConversionUtils;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class ReachSumMetricsDecider implements IMetricsDecider {
	private List<String> metricsToSum;
	private long sumToReach;

	/**
	 * @param metricsToSum The keys of the metrics that need to be summed up.
	 *                     This list cannot be null or empty.
	 *                     The keys cannot be null.
	 * @param sumToReach   The sum that needs to be reached (cannot be negative).
	 */
	public ReachSumMetricsDecider(List<String> metricsToSum, long sumToReach) {
		Assert.notEmpty(metricsToSum);
		Assert.isTrue(sumToReach >= 0);

		this.metricsToSum = metricsToSum;
		this.sumToReach = sumToReach;
	}

	@Override
	public boolean decide(JSONObject metrics) {
		if (metrics == null) {
			return false;
		}
		long sum = 0;

		for (String metricToSum : metricsToSum) {
			if (metrics.has(metricToSum)) {
				Long metric = ConversionUtils.convertToLong(metrics.get(metricToSum));

				if (metric != null && metric >= 0) {
					sum += metric;
				}
			}

			if (sum >= sumToReach) {
				return true;
			}
		}

		return false;
	}

	public void updateParams(long sumToReach) {
		this.sumToReach += sumToReach;
	}
}
