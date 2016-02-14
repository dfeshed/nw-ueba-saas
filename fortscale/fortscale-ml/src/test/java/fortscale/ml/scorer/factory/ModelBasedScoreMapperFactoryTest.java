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
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ModelBasedScoreMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ModelBasedScoreMapperConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ModelBasedScoreMapperFactoryTest {

    @Autowired
    private ModelBasedScoreMapperFactory modelBasedScoreMapperFactory;

	@Autowired
	private FeatureExtractService featureExtractService;

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

	@Autowired
	private ModelConfService modelConfService;

	@Autowired
	private ModelsCacheService modelsCacheService;

	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    private Scorer baseScorerMock = Mockito.mock(Scorer.class);

    private IScorerConf baseScorerConf = new IScorerConf() {
        @Override public String getName() {
            return "base-scorer";
        }
        @Override public String getFactoryName() {
            return "baseScorerFactoryName";
        }
    };


    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNull() {
        modelBasedScoreMapperFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenIllegalConfType() {
        modelBasedScoreMapperFactory.getProduct(() -> "factory-name");
    }

    public ModelBasedScoreMapper createScorer(String scorerName, Map<Double, Double> mapping) {
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
		IModelBuilderConf modelBuilderConf = () -> "dummy-model-factory-name";
		ModelConf modelConf = new ModelConf("dummy-model-conf", dataRetrieverConf, modelBuilderConf);
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

		when(featureExtractService.extract(Mockito.anySetOf(String.class), Mockito.any(Event.class)))
				.thenReturn(Collections.singletonMap(contextFieldName, new Feature("feature name", "value")));

		ScoreMappingModel model = new ScoreMappingModel();
		model.init(mapping);
		when(modelsCacheService.getModel(Mockito.any(Feature.class), Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong()))
				.thenReturn(model);

        return modelBasedScoreMapperFactory.getProduct(conf);
    }

	public ModelBasedScoreMapper createScorer(String scorerName) {
		return createScorer(scorerName, new HashMap<>());
	}

	public ModelBasedScoreMapper createScorer(Map<Double, Double> mapping) {
		return createScorer("scorerName", mapping);
	}

	public ModelBasedScoreMapper createScorer() {
		return createScorer("scorerName");
	}

    @Test
    public void shouldCreateScorerWithTheRightName() throws Exception {
        String scorerName = "scorerName";
        Assert.assertEquals(scorerName, createScorer(scorerName).getName());
    }

    @Test
    public void shouldDelegateToBaseScorerStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));

        Assert.assertEquals(score, createScorer().calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }

    @Test
    public void shouldUseScoreMappingModelStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));
        HashMap<Double, Double> mapping = new HashMap<>();
        double mappedScore = 50;
        mapping.put(score, mappedScore);

        Assert.assertEquals(mappedScore, createScorer(mapping).calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }
}
