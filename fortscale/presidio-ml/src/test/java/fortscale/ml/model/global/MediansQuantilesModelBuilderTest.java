//package fortscale.ml.model.prevalance.field;
//
//import fortscale.ml.model.prevalance.FieldModel;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static fortscale.ml.model.prevalance.field.PopulationQuantilesModelBuilderTest.initBuilder;
//import static fortscale.ml.model.prevalance.field.PopulationQuantilesModelBuilderTest.mockPrevalanceModel;
//
//public class MediansQuantilesModelBuilderTest {
//	@Test
//	public void builder_should_build_and_return_a_correct_model() {
//		// Initialize builder
//		MediansQuantilesModelBuilder builder = new MediansQuantilesModelBuilder();
//		initBuilder(builder);
//
//		// Feed builder
//		Map<Double, Long> distribution = new HashMap<>();
//		for (int i = 1; i <= 15; i++) {
//			distribution.put((double)i, 1L);
//		}
//		builder.feedBuilder(mockPrevalanceModel(distribution, 15L)); // median = 8
//
//		distribution = new HashMap<>();
//		for (int i = 16; i <= 30; i++) {
//			distribution.put((double)i, 1L);
//		}
//		builder.feedBuilder(mockPrevalanceModel(distribution, 15L)); // median = 23
//
//		distribution = new HashMap<>();
//		for (int i = 31; i <= 40; i++) {
//			distribution.put((double)i, 1L);
//		}
//		builder.feedBuilder(mockPrevalanceModel(distribution, 10L)); // median = 35
//
//		// Build model - population (of medians) is {8, 23, 35}
//		FieldModel fieldModel = builder.buildModel();
//
//		// Assert
//		Assert.assertEquals(0.01, fieldModel.calculateScore(7.0), 0.0);
//		Assert.assertEquals(0.17, fieldModel.calculateScore(8.0), 0.0);
//		Assert.assertEquals(0.34, fieldModel.calculateScore(22.0), 0.0);
//		Assert.assertEquals(0.50, fieldModel.calculateScore(23.0), 0.0);
//		Assert.assertEquals(0.67, fieldModel.calculateScore(34.0), 0.0);
//		Assert.assertEquals(0.84, fieldModel.calculateScore(35.0), 0.0);
//		Assert.assertEquals(1.00, fieldModel.calculateScore(36.0), 0.0);
//	}
//}
