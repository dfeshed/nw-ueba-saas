package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
//@DirtiesContext
//@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ModelScorerTestsConfig.class)
public class SMARTValuesModelScorerTest {
    @Configuration
//    @EnableSpringConfigured
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

    private SMARTValuesModelScorer createScorer(List<String> additionalModelNames, int globalInfluence, double baseScore) throws Exception {
        Scorer baseScorer = Mockito.mock(Scorer.class);
        Mockito.when(baseScorer.calculateScore(Mockito.any(AdeRecordReader.class)))
                .thenReturn(new FeatureScore("featureName", baseScore));
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        Mockito.when(baseScorerConf.getFactoryName()).thenReturn("factoryName");
        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorer);
        return new SMARTValuesModelScorer(
                "scorerName",
                "modelName",
                additionalModelNames,
                Collections.singletonList("contextFieldName"),
                additionalModelNames.stream()
                        .map(additionalModelName -> Collections.singletonList("contextFieldName"))
                        .collect(Collectors.toList()),
                1,
                1,
                false,
                baseScorerConf,
                globalInfluence,
                scorerFactoryService, eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateIfNotGivenAdditionalModelName() throws Exception {
        createScorer(Collections.emptyList(), 0, 50D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateIfGivenTwoAdditionalModelNames() throws Exception {
        createScorer(Arrays.asList("model 1", "model 2"), 0, 50D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongModelType() throws Exception {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0, 50D);
        scorer.calculateScore(new CategoryRarityModel(), Collections.singletonList(new SMARTValuesModel()), Mockito.mock(AdeRecordReader.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfNotGivenAdditionalModel() throws Exception {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0, 50D);
        scorer.calculateScore(new SMARTValuesModel(), Collections.emptyList(), Mockito.mock(AdeRecordReader.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongAdditionalModel() throws Exception {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0, 50D);
        scorer.calculateScore(new SMARTValuesModel(), Collections.singletonList(new CategoryRarityModel()), Mockito.mock(AdeRecordReader.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenTwoAdditionalModels() throws Exception {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0, 50D);
        scorer.calculateScore(new SMARTValuesModel(), Arrays.asList(new SMARTValuesModel(), new SMARTValuesModel()), Mockito.mock(AdeRecordReader.class));
    }

    @Test
    public void shouldGiveScoreWhenEverythingIsOk() throws Exception {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0, 50D);
        scorer.calculateScore(new SMARTValuesModel(), Collections.singletonList(new SMARTValuesPriorModel()), Mockito.mock(AdeRecordReader.class));
    }
}
