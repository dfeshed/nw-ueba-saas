package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
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
import java.util.List;

import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class PersonalThresholdModelScorerTest {

    @Configuration
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
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

    private AbstractModelScorer baseScorer;
    private IScorerConf baseScorerConf;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(AbstractModelScorer.class);
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
        new PersonalThresholdModelScorer(
                null,
                "model name",
                Collections.singletonList("context field name"),
                baseScorerConf,
                99999
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyStringAsModelName() {
        new PersonalThresholdModelScorer(
                "scorer name",
                "",
                Collections.singletonList("context field name"),
                baseScorerConf,
                99999
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullContextFieldNames() {
        new PersonalThresholdModelScorer(
                "scorer name",
                "model name",
                null,
                baseScorerConf,
                99999
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorerConf() {
        new PersonalThresholdModelScorer(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                null,
                99999
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenZeroOsMaxRatioFromUniformThreshold() {
        new PersonalThresholdModelScorer(
                "scorer name",
                "model name",
                Collections.singletonList("context field name"),
                baseScorerConf,
                0
        );
    }

	private FeatureScore calculateScore(String featureScoreName,
										FeatureScore baseScore,
										PersonalThresholdModel personalThresholdModel,
										Model baseScorerModel) throws Exception {
		return calculateScore(featureScoreName,
				baseScore,
				personalThresholdModel,
				baseScorerModel,
				99999);
	}

    private FeatureScore calculateScore(String featureScoreName,
                                        FeatureScore baseScore,
                                        PersonalThresholdModel personalThresholdModel,
                                        Model baseScorerModel,
                                        double maxRatioFromUniformThreshold) throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorer);
        Mockito.when(baseScorer.calculateScore(eventMessage, evenEpochTime)).thenReturn(baseScore);
        Mockito.when(baseScorer.getModel(eventMessage, evenEpochTime)).thenReturn(baseScorerModel);
        String contextFieldName = "context field name";
        List<String> contextFieldNames = Collections.singletonList(contextFieldName);

        when(modelsCacheService.getModel(
                (Feature) Mockito.isNull(),
                Mockito.anyString(),
                Mockito.anyMapOf(String.class, String.class),
                Mockito.anyLong())
        ).thenReturn(personalThresholdModel);

        when(eventMessage.getContextFields(contextFieldNames))
                .thenReturn(Collections.singletonMap(contextFieldName, "context field value"));

        return new PersonalThresholdModelScorer(
                featureScoreName,
                "model name",
                contextFieldNames,
                baseScorerConf,
                maxRatioFromUniformThreshold
        ).calculateScore(eventMessage, evenEpochTime);
    }

    @Test
    public void shouldDelegateToBaseScorerUsingCalibrationInModel() throws Exception {
        PersonalThresholdModel personalThresholdModel = new PersonalThresholdModel(100, 1000, 0.9);
        String featureScoreName = "calibrated score";
        Model baseScorerModel = () -> 10;
        double maxRatioFromUniformThreshold = 99999;
        double threshold = personalThresholdModel.calcThreshold(baseScorerModel.getNumOfSamples(), maxRatioFromUniformThreshold);
        FeatureScore baseScore = new FeatureScore("base score", threshold);

        FeatureScore featureScore = calculateScore(featureScoreName, baseScore, personalThresholdModel, baseScorerModel, maxRatioFromUniformThreshold);

        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(50, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }

    @Test
    public void shouldDelegateToBaseScorerUsingZeroCalibrationIfModelIsNull() throws Exception {
        String featureScoreName = "calibrated score";
        double score = 0.95;
        FeatureScore baseScore = new FeatureScore("base score", score);

        FeatureScore featureScore = calculateScore(featureScoreName, baseScore, null, () -> 10);

        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(0, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }
}
