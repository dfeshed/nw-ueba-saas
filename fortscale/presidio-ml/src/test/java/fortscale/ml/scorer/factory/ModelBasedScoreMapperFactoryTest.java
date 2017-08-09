package fortscale.ml.scorer.factory;

import com.google.common.collect.Sets;
import fortscale.common.feature.Feature;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.scorer.ModelBasedScoreMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ModelBasedScoreMapperConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.ml.scorer.record.TestAdeRecord;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ScorerFactoriesTestConfig.class)
@Ignore
public class ModelBasedScoreMapperFactoryTest {
	@MockBean
	private ModelConfService modelConfService;

	@Autowired
	private EventModelsCacheService modelCacheService;

	@Autowired
	private ModelBasedScoreMapperFactory modelBasedScoreMapperFactory;

	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

	private FactoryService<IContextSelector> contextSelectorFactoryService = new FactoryService<>();
	private Scorer baseScorerMock = Mockito.mock(Scorer.class);
	private IScorerConf baseScorerConf;

	@Before
	public void setUp() {
		baseScorerConf = new IScorerConf() {
			@Override
			public String getName() {
				return "base-scorer";
			}

			@Override
			public String getFactoryName() {
				return "baseScorerFactoryName";
			}
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNull() {
		modelBasedScoreMapperFactory.getProduct(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalConfType() {
		modelBasedScoreMapperFactory.getProduct(() -> "factory-name");
	}

	public ModelBasedScoreMapper createScorer(String scorerName, Map<Double, Double> mapping, boolean isGlobal) {
		scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorerMock);
		String modelName = "model-name";
		ModelBasedScoreMapperConf conf = new ModelBasedScoreMapperConf(scorerName, new ModelInfo(modelName), baseScorerConf);

		AbstractDataRetrieverConf dataRetrieverConf = new AbstractDataRetrieverConf(10, Collections.emptyList()) {
			@Override
			public String getFactoryName() {
				return "dummy-data-retriever-factory-name";
			}
		};

		IContextSelectorConf contextSelectorConf = () -> "dummy-context-selector-factory-name";
		IModelBuilderConf modelBuilderConf = () -> "dummy-model-factory-name";
		ModelConf modelConf = new ModelConf(
				"dummy-model-conf",
				isGlobal ? null : contextSelectorConf,
				dataRetrieverConf,
				modelBuilderConf
		);
		when(modelConfService.getModelConf(modelName)).thenReturn(modelConf);
		String contextFieldName = "context";

		dataRetrieverFactoryService.register(modelConf.getDataRetrieverConf().getFactoryName(),
				factoryConfig -> new AbstractDataRetriever(dataRetrieverConf) {
					@Override
					public ModelBuilderData retrieve(String contextId, Date endTime) {
						return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
					}

					@Override
					public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
						return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
					}

					@Override
					public Set<String> getEventFeatureNames() {
						return Collections.singleton("feature name");
					}

					@Override
					public List<String> getContextFieldNames() {
						return Collections.singletonList(contextFieldName);
					}

					@Override
					public String getContextId(Map<String, String> context) {
						return null;
					}
				});

		if (!isGlobal) {
			contextSelectorFactoryService.register(modelConf.getContextSelectorConf().getFactoryName(),
					factoryConfig -> timeRange -> Sets.newHashSet("some_user_context"));
		}

		ScoreMappingModel model = new ScoreMappingModel();
		model.init(mapping);
		when(modelCacheService.getModel(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(model);
		return modelBasedScoreMapperFactory.getProduct(conf);
	}

	public ModelBasedScoreMapper createScorer(String scorerName, boolean isGlobal) {
		return createScorer(scorerName, new HashMap<>(), isGlobal);
	}

	public ModelBasedScoreMapper createScorer(Map<Double, Double> mapping, boolean isGlobal) {
		return createScorer("scorerName", mapping, isGlobal);
	}

	public ModelBasedScoreMapper createScorer() {
		return createScorer("scorerName", false);
	}

	@Test
	public void shouldCreateScorerWithTheRightName() throws Exception {
		String scorerName = "scorerName";
		Assert.assertEquals(scorerName, createScorer(scorerName, false).getName());
	}

	@Test
	public void shouldDelegateToBaseScorerStatedByConfiguration() throws Exception {
		AdeRecordReader adeRecordReader = new TestAdeRecord().setContext("context value").getAdeRecordReader();
		double score = 56;
		Mockito.when(baseScorerMock.calculateScore(eq(adeRecordReader))).thenReturn(new FeatureScore("name", score));
		Assert.assertEquals(score, createScorer().calculateScore(adeRecordReader).getScore(), 0.0001);
	}

	@Test
	public void shouldUseScoreMappingModelStatedByConfiguration() throws Exception {
		AdeRecordReader adeRecordReader = new TestAdeRecord().setContext("context value").getAdeRecordReader();
		double score = 56;
		Mockito.when(baseScorerMock.calculateScore(eq(adeRecordReader))).thenReturn(new FeatureScore("name", score));
		HashMap<Double, Double> mapping = new HashMap<>();
		double mappedScore = 50;
		mapping.put(score, mappedScore);
		Assert.assertEquals(mappedScore, createScorer(mapping, false).calculateScore(adeRecordReader).getScore(), 0.0001);
	}

	@Test
	public void shouldCreateGlobalScorerWithNoContextFieldNames() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> contextFieldNames = (List<String>)Whitebox.getInternalState(createScorer("scorerName", true), "contextFieldNames");
		Assert.assertEquals(Collections.emptyList(), contextFieldNames);
	}
}
