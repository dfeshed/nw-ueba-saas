package fortscale.ml.model.builder;

import fortscale.ml.model.PersonalThresholdModel;
import org.junit.Assert;
import org.junit.Test;

public class PersonalThresholdModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenZeroAsDesiredNumOfIndicators() {
		new PersonalThresholdModelBuilder(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsModelBuilderData() {
		new PersonalThresholdModelBuilder(1).build(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenModelBuilderDataOfTheWrongType() {
		new PersonalThresholdModelBuilder(1).build("shoulds be integer");
	}

	@Test
	public void shouldBuildTheModelProperly() {
		int desiredNumOfIndicators = 10;
		int numOfContexts = 100;
		PersonalThresholdModel model = new PersonalThresholdModelBuilder(desiredNumOfIndicators).build(numOfContexts);

		Assert.assertEquals(new PersonalThresholdModel(numOfContexts, desiredNumOfIndicators), model);
	}
}
