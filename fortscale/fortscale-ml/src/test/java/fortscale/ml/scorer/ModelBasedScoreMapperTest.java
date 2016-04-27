package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.factory.ScoreMapperFactory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelBasedScoreMapperTest {

    @Configuration
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    static class ContextConfiguration {
        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            return new FactoryService<>();
        }

        @Bean
        public ScoreMapperFactory scoreMapperFactory() {
            return new ScoreMapperFactory();
        }

        @Bean
        public EventModelsCacheService eventModelsCacheService() {
            return new EventModelsCacheService();
        }

        @Bean
        public FeatureExtractService featureExtractService() {
            return Mockito.mock(FeatureExtractService.class);
        }

        @Bean
        public ModelsCacheService modelsCacheService() {
            return Mockito.mock(ModelsCacheService.class);
        }
    }

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Autowired
    private FeatureExtractService featureExtractService;

    @Autowired
    private ModelsCacheService modelsCacheService;

    private Scorer baseScorer;
    private Event eventMessage;
    private long evenEpochTime;
    private IScorerConf baseScorerConf;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
        eventMessage = Mockito.mock(Event.class);
        evenEpochTime = 1234;
        baseScorerConf = new IScorerConf() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getFactoryName() {
                return "base scorer factory name";
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsScorerName() {
        new ModelBasedScoreMapper(
                null,
                "model name",
                Collections.singletonList("context field name"),
                "feature name",
                baseScorerConf
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyStringAsFeatureName() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                "",
                baseScorerConf
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyStringAsModelName() {
        new ModelBasedScoreMapper(
                "scorer name",
                "",
                Collections.singletonList("context field name"),
                "feature name",
                baseScorerConf
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyContextFieldNames() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                Collections.emptyList(),
                "feature name",
                baseScorerConf
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorerConf() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                "feature name",
                null
        );
    }

    @Test
    public void shouldDelegateToBaseScorerUsingMappingInModel() throws Exception {
        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorer);

        double score = 56;
        FeatureScore baseScore = new FeatureScore("base score", score);
        Mockito.when(baseScorer.calculateScore(eventMessage, evenEpochTime)).thenReturn(baseScore);
        String featureScoreName = "mapped score";
        String contextFieldName = "context field name";
        ModelBasedScoreMapper scorer = new ModelBasedScoreMapper(
                featureScoreName,
                "model name",
                Collections.singletonList(contextFieldName),
                "feature name",
                baseScorerConf
        );

        when(eventMessage.getContextFields(Mockito.anyList())).thenReturn(Collections.singletonMap(contextFieldName, "feature name"));
        ScoreMappingModel model = new ScoreMappingModel();
        Map<Double, Double> mapping = new HashMap<>();
        double mappedScore = 97;
        mapping.put(score, mappedScore);
        model.init(mapping);
        when(modelsCacheService.getModel(
                Mockito.any(Feature.class),
                Mockito.anyString(),
                Mockito.anyMapOf(String.class, String.class),
                Mockito.anyLong())
        ).thenReturn(model);

        FeatureScore featureScore = scorer.calculateScore(eventMessage, evenEpochTime);
        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(mappedScore, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }
}
