package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(JUnit4.class)
public class PersonalThresholdModelTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenZeroAsNumOfContexts() {
		new PersonalThresholdModel(0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenZeroAsDesiredNumOfIndicators() {
		new PersonalThresholdModel(1, 0);
	}

	@Test
	public void shouldHaveInverseLinearDependenceOnNumOfSamples() {
		PersonalThresholdModel model = new PersonalThresholdModel(100, 100);
		int numOfSamples = 10;
		int a = 2;
		Assert.assertEquals(
				(1 - model.calcThreshold(numOfSamples)) / a,
				1 - model.calcThreshold(numOfSamples * a),
				0.0001
		);
	}

	@Test
	public void shouldSumToDesiredNumOfIndicators() {
		List<Integer> numOfSamplesPerContext = IntStream.range(10, 20).boxed().collect(Collectors.toList());
		int desiredNumOfIndicators = 5;
		PersonalThresholdModel model = new PersonalThresholdModel(numOfSamplesPerContext.size(), desiredNumOfIndicators);
		double expectedNumOfIndicators = numOfSamplesPerContext.stream()
				// map each context to its samples
				.map(numOfSamples -> IntStream.range(0, numOfSamples).map(i -> numOfSamples))
				.flatMapToInt(Function.identity())
				// calc its significance level (which is the expectation of the number of indicators)
				.mapToDouble(numOfSamples -> 1 - model.calcThreshold(numOfSamples))
				// use linearity of expectation
				.sum();
		Assert.assertEquals(desiredNumOfIndicators, expectedNumOfIndicators, 0.00001);
	}
}
