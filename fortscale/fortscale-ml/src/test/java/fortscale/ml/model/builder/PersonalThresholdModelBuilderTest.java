package fortscale.ml.model.builder;

import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.model.PersonalThresholdModelBuilderData;
import org.junit.Assert;
import org.junit.Test;

public class PersonalThresholdModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsModelBuilderData() {
		new PersonalThresholdModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenModelBuilderDataOfTheWrongType() {
		new PersonalThresholdModelBuilder().build("should be PersonalThresholdModelBuilderData");
	}

	@Test
	public void shouldBuildTheModelProperly() {
		int numOfContexts = 100;
		double organizationKTopProbOfHighScore = 0.9;
		int numOfOrganizationScores = 1000;
		PersonalThresholdModelBuilderData modelBuilderData = new PersonalThresholdModelBuilderData()
				.setNumOfContexts(numOfContexts)
				.setOrganizationKTopProbOfHighScore(organizationKTopProbOfHighScore)
				.setNumOfOrganizationScores(numOfOrganizationScores);
		PersonalThresholdModel model = new PersonalThresholdModelBuilder().build(modelBuilderData);

		Assert.assertEquals(new PersonalThresholdModel(numOfContexts, organizationKTopProbOfHighScore, numOfOrganizationScores), model);
	}
}
