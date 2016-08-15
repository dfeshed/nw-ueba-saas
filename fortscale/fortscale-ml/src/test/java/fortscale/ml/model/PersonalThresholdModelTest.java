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
		new PersonalThresholdModel(0, 0.9, 100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenZeroAsOrganizationThreshold() {
		new PersonalThresholdModel(10, 0, 100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenOneAsOrganizationThreshold() {
		new PersonalThresholdModel(10, 1, 100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenZeroAsNumOfOrganizationScores() {
		new PersonalThresholdModel(10, 0.9, 0);
	}

	@Test
	public void shouldHaveInverseLinearDependenceOnNumOfSamples() {
		PersonalThresholdModel model = new PersonalThresholdModel(10, 0.9, 100);
		int numOfSamples = 10;
		int a = 2;
		Assert.assertEquals(
				(1 - model.calcThreshold(numOfSamples)) / a,
				1 - model.calcThreshold(numOfSamples * a),
				0.0001
		);
	}

	@Test
	public void shouldGetSameNumOfIndicatorsComparedToUniformThreshold() {
		List<Integer> numOfSamplesPerContext = IntStream.range(10, 20).boxed().collect(Collectors.toList());
		List<Integer> organizationSamples = numOfSamplesPerContext.stream()
				.map(numOfSamples -> IntStream.range(0, numOfSamples).map(i -> numOfSamples))
				.flatMapToInt(Function.identity())
				.boxed()
				.collect(Collectors.toList());
		double organizationThreshold = 0.9;
		PersonalThresholdModel model = new PersonalThresholdModel(numOfSamplesPerContext.size(), organizationThreshold, organizationSamples.size());

		double expectedNumOfIndicatorsUsingPersonalThreshold = organizationSamples.stream()
				// calc its significance level (which is the expected number of indicators)
				.mapToDouble(numOfSamples -> 1 - model.calcThreshold(numOfSamples))
				// use linearity of expectation
				.sum();

		double expectedNumOfIndicatorsUsingUniformThreshold = (1 - organizationThreshold) * organizationSamples.size();
		Assert.assertEquals(expectedNumOfIndicatorsUsingUniformThreshold, expectedNumOfIndicatorsUsingPersonalThreshold, 0.00001);
	}
}
