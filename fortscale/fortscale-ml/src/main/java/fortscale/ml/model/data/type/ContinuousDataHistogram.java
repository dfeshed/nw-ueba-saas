package fortscale.ml.model.data.type;

import fortscale.utils.ConversionUtils;
import org.joda.time.DateTime;
import java.util.HashMap;
import java.util.Map;

public class ContinuousDataHistogram implements IData {
	private DateTime startTime;
	private DateTime endTime;
	private Map<Double, Double> histogram;

	public ContinuousDataHistogram(DateTime startTime, DateTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.histogram = new HashMap<>();
	}

	public void add(double value, double count) {
		if (count > 0) {
			Double oldCount = histogram.get(value);

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

	public double getCount(double value) {
		if (histogram.containsKey(value)) {
			return histogram.get(value);
		} else {
			return 0;
		}
	}

	@Override
	public DateTime getStartTime() {
		return startTime;
	}

	@Override
	public DateTime getEndTime() {
		return endTime;
	}

	public Map<Double, Double> getMap() {
		return histogram;
	}
}
