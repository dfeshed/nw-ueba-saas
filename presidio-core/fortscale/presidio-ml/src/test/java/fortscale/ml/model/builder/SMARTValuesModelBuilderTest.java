package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SMARTValuesModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new SMARTValuesModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new SMARTValuesModelBuilder().build("");
	}

	@Test
	public void shouldBuildModelCorrectly() {
		Map<Double, Long> smartValueToCountMap = new HashMap<>();
		long numOfZeroValues = 100;
		smartValueToCountMap.put(0D, numOfZeroValues);
		double sumOfValues = 0;
		long numOfPositiveValues = 0;
		for (long i = 1; i < 5; i++) {
			double value = i / 10.0;
			numOfPositiveValues += i;
			sumOfValues += value * i;
			smartValueToCountMap.put(value, i);
		}
		SMARTValuesModel model = (SMARTValuesModel) new SMARTValuesModelBuilder().build(castModelBuilderData(smartValueToCountMap));

		Assert.assertEquals(numOfZeroValues + numOfPositiveValues, model.getNumOfSamples());
		Assert.assertEquals(numOfPositiveValues, model.getNumOfPositiveValues());
		Assert.assertEquals(sumOfValues, model.getSumOfValues(), 0.0001);
	}

	private static SmartValueData castModelBuilderData(Map<Double, Long> map) {
		GenericHistogram histogram = new GenericHistogram();
		map.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
		return new SmartValueData(histogram, Instant.now());
	}
}
