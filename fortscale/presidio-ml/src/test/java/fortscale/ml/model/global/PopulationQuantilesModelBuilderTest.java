//package fortscale.ml.model.prevalance.field;
//
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.ml.model.prevalance.PrevalanceModel;
//import org.apache.samza.config.Config;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class PopulationQuantilesModelBuilderTest {
//	private static final String FIELD_MODEL_NAME = "fieldModelName";
//	private static final String LOCAL_MODEL_NAME = "localModelName";
//	private static final String LOCAL_FIELD_MODEL_NAME = "localFieldModelName";
//
//	protected static void initBuilder(PopulationQuantilesModelBuilder builder) {
//		Config config = mock(Config.class);
//		Config subset = mock(Config.class);
//
//		when(config.subset(String.format("fortscale.model.global.field.model.%s.", FIELD_MODEL_NAME))).thenReturn(subset);
//		when(subset.get("local.model.name")).thenReturn(LOCAL_MODEL_NAME);
//		when(subset.get("local.field.model.name")).thenReturn(LOCAL_FIELD_MODEL_NAME);
//
//		builder.initBuilder(config, FIELD_MODEL_NAME);
//	}
//
//	protected static PrevalanceModel mockPrevalanceModel(Map<Double, Long> distribution, Long totalCount) {
//		PrevalanceModel prevalanceModel = mock(PrevalanceModel.class);
//		ContinuousDataDistribution fieldModel = mock(ContinuousDataDistribution.class);
//
//		when(prevalanceModel.getModelName()).thenReturn(LOCAL_MODEL_NAME);
//		when(prevalanceModel.getFieldModel(LOCAL_FIELD_MODEL_NAME)).thenReturn(fieldModel);
//		when(fieldModel.getDistribution()).thenReturn(distribution);
//
//		// A negative totalCount indicates there's no need to mock getTotalCount()
//		if (totalCount >= 0) {
//			when(fieldModel.getTotalCount()).thenReturn(totalCount);
//		}
//
//		return prevalanceModel;
//	}
//
//	protected static PrevalanceModel mockPrevalanceModel(Map<Double, Long> distribution) {
//		return mockPrevalanceModel(distribution, -1L);
//	}
//
//	@Test
//	public void builder_should_build_and_return_a_correct_model_when_population_consists_7_values() {
//		// Initialize builder
//		PopulationQuantilesModelBuilder builder = new PopulationQuantilesModelBuilder();
//		initBuilder(builder);
//
//		// Feed builder
//		Map<Double, Long> distribution = new HashMap<>();
//		distribution.put(17.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(19.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(13.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(23.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(37.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(29.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(31.0, 1L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		// Build model
//		FieldModel fieldModel = builder.buildModel();
//
//		// Assert
//		Assert.assertEquals(0.01, fieldModel.calculateScore(12.0), 0.0);
//		Assert.assertEquals(0.08, fieldModel.calculateScore(13.0), 0.0);
//		Assert.assertEquals(0.15, fieldModel.calculateScore(16.0), 0.0);
//		Assert.assertEquals(0.22, fieldModel.calculateScore(17.0), 0.0);
//		Assert.assertEquals(0.29, fieldModel.calculateScore(18.0), 0.0);
//		Assert.assertEquals(0.36, fieldModel.calculateScore(19.0), 0.0);
//		Assert.assertEquals(0.43, fieldModel.calculateScore(22.0), 0.0);
//		Assert.assertEquals(0.50, fieldModel.calculateScore(23.0), 0.0);
//		Assert.assertEquals(0.58, fieldModel.calculateScore(28.0), 0.0);
//		Assert.assertEquals(0.65, fieldModel.calculateScore(29.0), 0.0);
//		Assert.assertEquals(0.72, fieldModel.calculateScore(30.0), 0.0);
//		Assert.assertEquals(0.79, fieldModel.calculateScore(31.0), 0.0);
//		Assert.assertEquals(0.86, fieldModel.calculateScore(36.0), 0.0);
//		Assert.assertEquals(0.93, fieldModel.calculateScore(37.0), 0.0);
//		Assert.assertEquals(1.00, fieldModel.calculateScore(38.0), 0.0);
//	}
//
//	@Test
//	public void builder_should_build_and_return_a_correct_model_when_population_consists_100_values() {
//		// Initialize builder
//		PopulationQuantilesModelBuilder builder = new PopulationQuantilesModelBuilder();
//		initBuilder(builder);
//
//		// Feed builder
//		Map<Double, Long> distribution = new HashMap<>();
//		distribution.put(250.0, 10L);
//		distribution.put(150.0, 30L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		distribution = new HashMap<>();
//		distribution.put(200.0, 30L);
//		distribution.put(100.0, 30L);
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		// Build model
//		FieldModel fieldModel = builder.buildModel();
//
//		// Assert
//		Assert.assertEquals(0.01, fieldModel.calculateScore(50.0), 0.0);
//		Assert.assertEquals(0.16, fieldModel.calculateScore(100.0), 0.0);
//		Assert.assertEquals(0.31, fieldModel.calculateScore(125.0), 0.0);
//		Assert.assertEquals(0.46, fieldModel.calculateScore(150.0), 0.0);
//		Assert.assertEquals(0.61, fieldModel.calculateScore(175.0), 0.0);
//		Assert.assertEquals(0.76, fieldModel.calculateScore(200.0), 0.0);
//		Assert.assertEquals(0.91, fieldModel.calculateScore(225.0), 0.0);
//		Assert.assertEquals(0.96, fieldModel.calculateScore(250.0), 0.0);
//		Assert.assertEquals(1.00, fieldModel.calculateScore(300.0), 0.0);
//	}
//
//	@Test
//	public void builder_should_build_and_return_a_correct_model_when_population_consists_100000_values() {
//		// Initialize builder
//		PopulationQuantilesModelBuilder builder = new PopulationQuantilesModelBuilder();
//		initBuilder(builder);
//
//		// Feed builder
//		Map<Double, Long> distribution = new HashMap<>();
//		for (int i = 1; i <= 1000; i++) {
//			distribution.put((double)i, 100L);
//		}
//		builder.feedBuilder(mockPrevalanceModel(distribution));
//
//		// Build model
//		FieldModel fieldModel = builder.buildModel();
//
//		// Assert
//		for (int i = 1; i <= QuantilesModel.NUM_OF_QUANTILES; i++) {
//			Assert.assertEquals(i / 100.0, fieldModel.calculateScore(i * 10.0), 0.0);
//		}
//	}
//}
