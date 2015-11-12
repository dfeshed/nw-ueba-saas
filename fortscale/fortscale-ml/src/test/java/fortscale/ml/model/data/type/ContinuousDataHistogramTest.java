package fortscale.ml.model.data.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContinuousDataHistogramTest {
	@Test
	public void should_add_new_value_to_histogram() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram();

		Assert.assertEquals(0, histogram.getCount(42));
		histogram.add(42, 100);
		Assert.assertEquals(100, histogram.getCount(42));

		Assert.assertEquals(0, histogram.getCount(3.14));
		histogram.add(3.14);
		Assert.assertEquals(1, histogram.getCount(3.14));
	}

	@Test
	public void should_update_existing_value_in_histogram() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram();

		histogram.add(1.9, 86);
		Assert.assertEquals(86, histogram.getCount(1.9));
		histogram.add(1.9, 14);
		Assert.assertEquals(100, histogram.getCount(1.9));
		histogram.add(1.9);
		Assert.assertEquals(101, histogram.getCount(1.9));

		histogram.add(14.5);
		Assert.assertEquals(1, histogram.getCount(14.5));
		histogram.add(14.5, 87);
		Assert.assertEquals(88, histogram.getCount(14.5));
		histogram.add(14.5);
		Assert.assertEquals(89, histogram.getCount(14.5));
	}

	@Test
	public void should_add_successfully_generic_histograms_after_init() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram();

		Map<String, Double> input1 = new HashMap<>();
		input1.put("1", 10.0);
		input1.put("2", 20.0);
		input1.put("3", 30.0);

		histogram.add(input1);
		Assert.assertEquals(10, histogram.getCount(1));
		Assert.assertEquals(20, histogram.getCount(2));
		Assert.assertEquals(30, histogram.getCount(3));

		Map<Object, Object> input2 = new HashMap<>();
		input2.put(1, 1);
		input2.put(2, 2);
		input2.put(3, 3);

		histogram.add(input2);
		Assert.assertEquals(11, histogram.getCount(1.0));
		Assert.assertEquals(22, histogram.getCount(2.0));
		Assert.assertEquals(33, histogram.getCount(3.0));
	}

	@Test
	public void should_add_successfully_a_generic_histogram_when_there_are_already_values() {
		ContinuousDataHistogram histogram = new ContinuousDataHistogram();

		histogram.add(-100.0);
		histogram.add(-200.0, 2);

		Map<Integer, Integer> input = new HashMap<>();
		input.put(-100, 10);
		input.put(-200, 20);

		histogram.add(input);
		Assert.assertEquals(11, histogram.getCount(-100));
		Assert.assertEquals(22, histogram.getCount(-200));
	}
}
