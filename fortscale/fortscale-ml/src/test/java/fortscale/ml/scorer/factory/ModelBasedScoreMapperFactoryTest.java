package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ModelBasedScoreMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ModelBasedScoreMapperConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

import static org.mockito.Mockito.when;

public class ModelBasedScoreMapperFactoryTest {

	private static ClassPathXmlApplicationContext testContextManager;

	private ModelBasedScoreMapperFactory modelBasedScoreMapperFactory;
	private FeatureExtractService featureExtractService;
	private FactoryService<Scorer> scorerFactoryService;
	private ModelConfService modelConfService;
	private ModelsCacheService modelsCacheService;
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	private Scorer baseScorerMock = Mockito.mock(Scorer.class);
    private IScorerConf baseScorerConf;


	@BeforeClass
	public static void setUpClass() {
		testContextManager = new ClassPathXmlApplicationContext(
				"classpath*:META-INF/spring/scorer-factory-tests-context.xml");
	}

	@Before
	public void setUp() {
		modelBasedScoreMapperFactory = testContextManager.getBean(ModelBasedScoreMapperFactory.class);
		featureExtractService = testContextManager.getBean(FeatureExtractService.class);
		scorerFactoryService = testContextManager.getBean(FactoryService.class);
		modelConfService = testContextManager.getBean(ModelConfService.class);
		modelsCacheService = testContextManager.getBean(ModelsCacheService.class);
		dataRetrieverFactoryService = testContextManager.getBean(FactoryService.class);
		contextSelectorFactoryService = testContextManager.getBean(FactoryService.class);

		baseScorerConf = new IScorerConf() {
			@Override public String getName() {
				return "base-scorer";
			}
			@Override public String getFactoryName() {
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
		ModelBasedScoreMapperConf conf = new ModelBasedScoreMapperConf(
                scorerName,
                new ModelInfo(modelName),
                baseScorerConf
        );

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
				dataRetrieverConf,
				isGlobal ? null : contextSelectorConf,
				modelBuilderConf
		);
		when(modelConfService.getModelConf(modelName)).thenReturn(modelConf);
		String contextFieldName = "context field name";
		dataRetrieverFactoryService.register(modelConf.getDataRetrieverConf().getFactoryName(),
				factoryConfig -> new AbstractDataRetriever(dataRetrieverConf) {
					@Override
					public Object retrieve(String contextId, Date endTime) {
						return null;
					}

					@Override
					public Object retrieve(String contextId, Date endTime, Feature feature) {
						return null;
					}

					@Override
					public String getContextId(Map<String, String> context) {
						return null;
					}

					@Override
					public Set<String> getEventFeatureNames() {
						return Collections.singleton("feature name");
					}

					@Override
					public List<String> getContextFieldNames() {
						return Collections.singletonList(contextFieldName);
					}
				});
		if (!isGlobal) {
			contextSelectorFactoryService.register(modelConf.getContextSelectorConf().getFactoryName(), factoryConfig ->
					(startTime, endTime) -> Collections.singletonList("some_user_context"));
		}

		when(featureExtractService.extract(Mockito.anySetOf(String.class), Mockito.any(Event.class)))
				.thenReturn(Collections.singletonMap(contextFieldName, new Feature("feature name", "value")));

		ScoreMappingModel model = new ScoreMappingModel();
		model.init(mapping);
		when(modelsCacheService.getModel(Mockito.any(Feature.class), Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong()))
				.thenReturn(model);

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
        Event eventMessage = Mockito.mock(Event.class);
		when(eventMessage.getContextFields(Mockito.anyList())).thenReturn(Collections.singletonMap("context field name", "context value"));

		long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));

        Assert.assertEquals(score, createScorer().calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }

    @Test
    public void shouldUseScoreMappingModelStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
		when(eventMessage.getContextFields(Mockito.anyList())).thenReturn(Collections.singletonMap("context field name", "context value"));
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));
        HashMap<Double, Double> mapping = new HashMap<>();
        double mappedScore = 50;
        mapping.put(score, mappedScore);

        Assert.assertEquals(mappedScore, createScorer(mapping, false).calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }

	@Test
	public void shouldCreateGlobalScorerWithNoContextFieldNames() throws Exception {
		List<String> contextFieldNames = (List<String>) Whitebox.getInternalState(createScorer("scorerName", true),
				"contextFieldNames");
		Assert.assertEquals(Collections.emptyList(), contextFieldNames);
	}
}
