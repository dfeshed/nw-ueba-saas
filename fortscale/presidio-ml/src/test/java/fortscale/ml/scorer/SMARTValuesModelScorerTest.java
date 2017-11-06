package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.time.Instant;

@RunWith(SpringJUnit4ClassRunner.class)
public class SMARTValuesModelScorerTest {
    @Configuration
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

    private SMARTValuesModelScorer createScorer(String globalModelName, int globalInfluence, double baseScore, Instant modelEndTime) throws Exception {
        SmartWeightsModelScorer baseScorer = Mockito.mock(SmartWeightsModelScorer.class);
        Mockito.when(baseScorer.calculateScore(Mockito.any(AdeRecordReader.class),Mockito.eq(modelEndTime)))
                .thenReturn(new FeatureScore("featureName", baseScore));
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        Mockito.when(baseScorerConf.getFactoryName()).thenReturn("factoryName");
        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorer);
        return new SMARTValuesModelScorer(
                "scorerName",
                "modelName",
                globalModelName,
                1,
                1,
                false,
                baseScorerConf,
                globalInfluence,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateIfNotGivenAdditionalModelName() throws Exception {
        createScorer(null, 0, 50D, Instant.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongModelType() throws Exception {
        Instant modelEndTime = Instant.now();
        SMARTValuesModelScorer scorer = createScorer("additional model name", 0, 50D, modelEndTime);
        scorer.calculateScore(new CategoryRarityModel(), new SMARTValuesModel(), Mockito.mock(AdeRecordReader.class), modelEndTime);
    }

    @Test
    public void shouldGiveZeroScoreIfNotGivenAdditionalModel() throws Exception {
        Instant modelEndTime = Instant.now();
        SMARTValuesModelScorer scorer = createScorer("additional model name", 0, 50D, modelEndTime);
        FeatureScore featureScore = scorer.calculateScore(new SMARTValuesModel(), null, Mockito.mock(AdeRecordReader.class), modelEndTime);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongAdditionalModel() throws Exception {
        Instant modelEndTime = Instant.now();
        SMARTValuesModelScorer scorer = createScorer("additional model name", 0, 50D, modelEndTime);
        scorer.calculateScore(new SMARTValuesModel(), new CategoryRarityModel(), Mockito.mock(AdeRecordReader.class), modelEndTime);
    }

    @Test
    public void shouldGiveScoreWhenEverythingIsOk() throws Exception {
        Instant modelEndTime = Instant.now();
        SMARTValuesModelScorer scorer = createScorer("additional model name", 0, 50D, modelEndTime);
        scorer.calculateScore(new SMARTValuesModel(), new SMARTValuesPriorModel(), Mockito.mock(AdeRecordReader.class), modelEndTime);
    }
}
