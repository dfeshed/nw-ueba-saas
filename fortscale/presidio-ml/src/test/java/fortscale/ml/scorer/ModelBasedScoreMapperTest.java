package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.record.JsonAdeRecord;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelBasedScoreMapperTest {

    @Configuration
    @EnableSpringConfigured
    @Import(ScorerTestsContext.class)
    static class ContextConfiguration {
        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            return new FactoryService<>();
        }

        @Bean
        public EventModelsCacheService eventModelsCacheService() {
            return new EventModelsCacheService();
        }

        @Bean
        public ModelsCacheService modelsCacheService() {
            return Mockito.mock(ModelsCacheService.class);
        }
    }

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private EventModelsCacheService eventModelsCacheService;
    @Autowired
    private ModelsCacheService modelsCacheService;

    private Scorer baseScorer;
    private IScorerConf baseScorerConf;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
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
                baseScorerConf,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyStringAsFeatureName() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                "",
                baseScorerConf,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyStringAsModelName() {
        new ModelBasedScoreMapper(
                "scorer name",
                "",
                Collections.singletonList("context field name"),
                "feature name",
                baseScorerConf,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullContextFieldNames() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                null,
                "feature name",
                baseScorerConf,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorerConf() {
        new ModelBasedScoreMapper(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                "feature name",
                null,
                scorerFactoryService, eventModelsCacheService);
    }

    private FeatureScore calculateScore(String featureScoreName,
                                        FeatureScore baseScore,
                                        Model model) throws Exception {
        AdeRecord adeRecord = JsonAdeRecord.getJsonAdeRecord("context field name", "feature name");
        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorer);
        when(baseScorer.calculateScore(eq(adeRecord))).thenReturn(baseScore);
        when(modelsCacheService.getModel(anyString(), anyMapOf(String.class, String.class), any(Instant.class))).thenReturn(model);

        return new ModelBasedScoreMapper(
                featureScoreName,
                "model name",
                Collections.singletonList("context field name"),
                "feature name",
                baseScorerConf,
                scorerFactoryService, eventModelsCacheService).calculateScore(adeRecord);
    }

    @Test
    public void shouldDelegateToBaseScorerUsingMappingInModel() throws Exception {
        double score = 56;
        FeatureScore baseScore = new FeatureScore("base score", score);
        ScoreMappingModel model = new ScoreMappingModel();
        Map<Double, Double> mapping = new HashMap<>();
        double mappedScore = 97;
        mapping.put(score, mappedScore);
        model.init(mapping);
        String featureScoreName = "mapped score";

        FeatureScore featureScore = calculateScore(featureScoreName, baseScore, model);

        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(mappedScore, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }

    @Test
    public void shouldDelegateToBaseScorerUsingZeroMappingIfModelIsNull() throws Exception {
        String featureScoreName = "mapped score";
        double score = 56;
        FeatureScore baseScore = new FeatureScore("base score", score);

        FeatureScore featureScore = calculateScore(featureScoreName, baseScore, null);

        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(0, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }
}
