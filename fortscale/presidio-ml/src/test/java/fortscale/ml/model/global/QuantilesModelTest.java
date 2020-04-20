//package fortscale.ml.model.prevalance.field;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//public class QuantilesModelTest {
//	@Test
//	public void quantiles_model_should_return_the_correct_quantile_indexes_when_all_values_are_unique() {
//		// Arrange
//		QuantilesModel quantilesModel = new QuantilesModel();
//		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
//			quantilesModel.setQuantile(i, (double)i);
//		}
//
//		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
//			// Act
//			double score = quantilesModel.calculateScore(i);
//
//			// Assert
//			Assert.assertEquals(i / 100.0, score, 0.0);
//		}
//	}
//
//	@Test
//	public void quantiles_model_should_return_the_correct_quantile_indexes_when_all_values_are_equal() {
//		// Arrange
//		QuantilesModel quantilesModel = new QuantilesModel();
//		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
//			quantilesModel.setQuantile(i, 42.0);
//		}
//
//		// Act
//		double score = quantilesModel.calculateScore(21.0);
//		// Assert
//		Assert.assertEquals(0.01, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(42.0);
//		// Assert
//		Assert.assertEquals(0.51, score, 0.0); // median index
//
//		// Act
//		score = quantilesModel.calculateScore(84.0);
//		// Assert
//		Assert.assertEquals(1.00, score, 0.0);
//	}
//
//	@Test
//	public void quantiles_model_should_return_the_correct_quantile_indexes_when_there_is_an_interval_of_equal_values() {
//		// Make sure number of quantiles is set to 100
//		Assert.assertEquals(100, QuantilesModel.NUM_OF_QUANTILES);
//
//		// Arrange
//		QuantilesModel quantilesModel = new QuantilesModel();
//		for (int i = 1; i <= 31; i++) {
//			quantilesModel.setQuantile(i, i * 10.0);
//		}
//		for (int i = 32; i <= 60; i++) {
//			quantilesModel.setQuantile(i, 555.0);
//		}
//		for (int i = 61; i <= 100; i++) {
//			quantilesModel.setQuantile(i, i * 10.0);
//		}
//
//		// Act
//		double score = quantilesModel.calculateScore(125.0);
//		// Assert
//		Assert.assertEquals(0.13, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(400.0);
//		// Assert
//		Assert.assertEquals(0.32, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(555.0);
//		// Assert
//		Assert.assertEquals(0.46, score, 0.0); // median index
//
//		// Act
//		score = quantilesModel.calculateScore(600.0);
//		// Assert
//		Assert.assertEquals(0.61, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(925.0);
//		// Assert
//		Assert.assertEquals(0.93, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(1000.0);
//		// Assert
//		Assert.assertEquals(1.00, score, 0.0);
//
//		// Act
//		score = quantilesModel.calculateScore(1111.0);
//		// Assert
//		Assert.assertEquals(1.00, score, 0.0);
//	}
//}
