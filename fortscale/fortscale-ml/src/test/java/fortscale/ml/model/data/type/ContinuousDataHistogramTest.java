package fortscale.ml.model.data.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContinuousDataHistogramTest {
	private Date startTime = new Date(1420070400000L);
	private Date endTime = new Date(1420156800000L);

	private void assertHistogramCount(ContinuousDataHistogram histogram, double value, long expectedCount) {
		Assert.assertEquals(expectedCount, Math.round(histogram.getCount(value)));
	}

	@Test
	public void should_add_new_value_to_histogram() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);

		assertHistogramCount(histogram, 42, 0);
		histogram.add(42, 100);
		assertHistogramCount(histogram, 42, 100);

		assertHistogramCount(histogram, 3.14, 0);
		histogram.add(3.14);
		assertHistogramCount(histogram, 3.14, 1);
	}

	@Test
	public void should_update_existing_value_in_histogram() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);

		histogram.add(1.9, 86);
		assertHistogramCount(histogram, 1.9, 86);
		histogram.add(1.9, 14);
		assertHistogramCount(histogram, 1.9, 100);
		histogram.add(1.9);
		assertHistogramCount(histogram, 1.9, 101);

		histogram.add(14.5);
		assertHistogramCount(histogram, 14.5, 1);
		histogram.add(14.5, 87);
		assertHistogramCount(histogram, 14.5, 88);
		histogram.add(14.5);
		assertHistogramCount(histogram, 14.5, 89);
	}

	@Test
	public void should_add_successfully_generic_histograms_after_init() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);

		Map<String, Double> input1 = new HashMap<>();
		input1.put("1", 10.0);
		input1.put("2", 20.0);
		input1.put("3", 30.0);

		histogram.add(input1);
		assertHistogramCount(histogram, 1, 10);
		assertHistogramCount(histogram, 2, 20);
		assertHistogramCount(histogram, 3, 30);

		Map<Object, Object> input2 = new HashMap<>();
		input2.put(1, 1);
		input2.put(2, 2);
		input2.put(3, 3);

		histogram.add(input2);
		assertHistogramCount(histogram, 1, 11);
		assertHistogramCount(histogram, 2, 22);
		assertHistogramCount(histogram, 3, 33);
	}

	@Test
	public void should_add_successfully_a_generic_histogram_when_there_are_already_values() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);

		histogram.add(-100.0);
		histogram.add(-200.0, 2);

		Map<Integer, Integer> input = new HashMap<>();
		input.put(-100, 10);
		input.put(-200, 20);

		histogram.add(input);
		assertHistogramCount(histogram, -100, 11);
		assertHistogramCount(histogram, -200, 22);
	}
}
