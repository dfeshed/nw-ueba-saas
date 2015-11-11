package fortscale.ml.model.data.type;

import fortscale.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class ContinuousDataHistogram {
	private Map<Double, Long> histogram;

	public ContinuousDataHistogram() {
		histogram = new HashMap<>();
	}

	public void add(double value, long count) {
		if (count > 0) {
			Long oldCount = histogram.get(value);

			if (oldCount == null) {
				histogram.put(value, count);
			} else {
				histogram.put(value, oldCount + count);
			}
		}
	}

	public void add(double value) {
		add(value, 1);
	}

	public void add(Map<?, ?> histogram) {
		if (histogram != null) {
			for (Map.Entry<?, ?> entry : histogram.entrySet()) {
				Double value = ConversionUtils.convertToDouble(entry.getKey());
				Double count = ConversionUtils.convertToDouble(entry.getValue());

				if (value != null && !value.isInfinite() && !value.isNaN() &&
						count != null && !count.isInfinite() && !count.isNaN()) {
					add(value, count.longValue());
				}
			}
		}
	}

	public long getCount(double value) {
		if (histogram.containsKey(value)) {
			return histogram.get(value);
		} else {
			return 0;
		}
	}

	public Map<Double, Long> getMap() {
		return histogram;
	}
}
