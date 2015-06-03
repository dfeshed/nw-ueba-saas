package fortscale.ml.model.prevalance.field;

import junit.framework.Assert;
import org.junit.Test;

public class QuantilesModelTest {
	@Test
	public void quantiles_model_should_return_the_correct_quantile_indexes_when_all_values_are_unique() {
		// Arrange
		QuantilesModel quantilesModel = new QuantilesModel();
		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
			quantilesModel.setQuantile(i, (double)i);
		}

		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
			// Act
			double score = quantilesModel.calculateScore(i);

			// Assert
			Assert.assertEquals((double)i, score);
		}
	}

	@Test
	public void quantiles_model_should_return_the_correct_quantile_indexes_when_all_values_are_equal() {
		// Arrange
		QuantilesModel quantilesModel = new QuantilesModel();
		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
			quantilesModel.setQuantile(i, 42.0);
		}

		// Act
		double score = quantilesModel.calculateScore(21.0);
		// Assert
		Assert.assertEquals(1.0, score);

		// Act
		score = quantilesModel.calculateScore(42.0);
		// Assert
		Assert.assertEquals(51.0, score); // median index

		// Act
		score = quantilesModel.calculateScore(84.0);
		// Assert
		Assert.assertEquals(100.0, score);
	}

	@Test
	public void quantiles_model_should_return_the_correct_quantile_indexes_when_there_is_an_interval_of_equal_values() {
		// Make sure number of quantiles is set to 100
		Assert.assertEquals(100, QuantilesModel.NUM_OF_QUANTILES);

		// Arrange
		QuantilesModel quantilesModel = new QuantilesModel();
		for (int i = 1; i <= 31; i++) {
			quantilesModel.setQuantile(i, i * 10.0);
		}
		for (int i = 32; i <= 60; i++) {
			quantilesModel.setQuantile(i, 555.0);
		}
		for (int i = 61; i <= 100; i++) {
			quantilesModel.setQuantile(i, i * 10.0);
		}

		// Act
		double score = quantilesModel.calculateScore(125.0);
		// Assert
		Assert.assertEquals(13.0, score);

		// Act
		score = quantilesModel.calculateScore(400.0);
		// Assert
		Assert.assertEquals(32.0, score);

		// Act
		score = quantilesModel.calculateScore(555.0);
		// Assert
		Assert.assertEquals(46.0, score); // median index

		// Act
		score = quantilesModel.calculateScore(600.0);
		// Assert
		Assert.assertEquals(61.0, score);

		// Act
		score = quantilesModel.calculateScore(925.0);
		// Assert
		Assert.assertEquals(93.0, score);

		// Act
		score = quantilesModel.calculateScore(1000.0);
		// Assert
		Assert.assertEquals(100.0, score);

		// Act
		score = quantilesModel.calculateScore(1111.0);
		// Assert
		Assert.assertEquals(100.0, score);
	}
}
