package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.accumulator.TestMongoConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 12/12/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class FeatureBucketStateServiceTest {
    @Configuration
    @Import({
            TestMongoConfig.class
    })
    @Profile("test")
    @EnableMongoRepositories(basePackageClasses = FeatureBucketStateRepository.class)
    public static class springConfig {

        @Autowired
        FeatureBucketStateRepository repository;

        @Bean
        public FeatureBucketStateService featureBucketStateService()
        {
            return new FeatureBucketStateServiceImpl(repository);
        }
    }

    @Autowired
    FeatureBucketStateService featureBucketStateService;

    @Test
    public void testUpdateState_NoExistingValue(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateState(time, FeatureBucketState.StateType.LAST_SYNC_DATE);

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState(FeatureBucketState.StateType.LAST_SYNC_DATE);
        FeatureBucketState expected = new FeatureBucketState(Instant.ofEpochSecond(time).truncatedTo(ChronoUnit.DAYS), FeatureBucketState.StateType.LAST_SYNC_DATE);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getAggregationFeatureStateDate());
        Assert.assertEquals(expected.getAggregationFeatureStateDate().getDate(), actual.getAggregationFeatureStateDate().getDate());
    }

    @Test
    public void testUpdateState_UpdateNextDay(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateState(time, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Instant date = Instant.ofEpochSecond(time).truncatedTo(ChronoUnit.DAYS);
        date.plus(Duration.ofDays(1));

        // Updating with next date
        featureBucketStateService.updateState(date.getEpochSecond(), FeatureBucketState.StateType.LAST_SYNC_DATE);

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState(FeatureBucketState.StateType.LAST_SYNC_DATE);
        FeatureBucketState expected = new FeatureBucketState(date, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getAggregationFeatureStateDate());
        Assert.assertEquals(expected.getAggregationFeatureStateDate().getDate(), actual.getAggregationFeatureStateDate().getDate());
    }

    @Test
    public void testUpdateState_UpdatePrevDay(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateState(time, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Instant date = Instant.ofEpochSecond(time).truncatedTo(ChronoUnit.DAYS);
        Instant dayBefore = date.minus(Duration.ofDays(1));

        // Updating with next date
        featureBucketStateService.updateState(dayBefore.getEpochSecond(), FeatureBucketState.StateType.LAST_SYNC_DATE);

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState(FeatureBucketState.StateType.LAST_SYNC_DATE);
        FeatureBucketState expected = new FeatureBucketState(date, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getAggregationFeatureStateDate());
        Assert.assertEquals(expected.getAggregationFeatureStateDate().getDate(), actual.getAggregationFeatureStateDate().getDate());
    }

    @Test
    public void testUpdateState_NoUpdateNeeded(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateState(time, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Instant date = Instant.ofEpochSecond(time).truncatedTo(ChronoUnit.DAYS);
        FeatureBucketState firstUpdate = featureBucketStateService.getFeatureBucketState(FeatureBucketState.StateType.LAST_SYNC_DATE);

        // Updating with next date
        featureBucketStateService.updateState(date.getEpochSecond(), FeatureBucketState.StateType.LAST_SYNC_DATE);

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState(FeatureBucketState.StateType.LAST_SYNC_DATE);
        FeatureBucketState expected = new FeatureBucketState(date, FeatureBucketState.StateType.LAST_SYNC_DATE);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getAggregationFeatureStateDate());
        Assert.assertEquals(expected.getAggregationFeatureStateDate().getDate(), actual.getAggregationFeatureStateDate().getDate());
        Assert.assertEquals(firstUpdate.getAggregationFeatureStateDate().getModifiedAt(), actual.getAggregationFeatureStateDate().getModifiedAt());
    }
}
