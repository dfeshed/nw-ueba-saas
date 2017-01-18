package fortscale.aggregation.feature.bucket.state;

import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateRepository;
import fortscale.aggregation.feature.bucket.state.config.FeatureBucketStateServiceConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
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
            MongodbTestConfig.class,
            NullStatsServiceConfig.class,
            FeatureBucketStateServiceConfig.class
    })
    @Profile("test")
    @EnableMongoRepositories(basePackageClasses = FeatureBucketStateRepository.class)
    public static class springConfig {
    }

    @Autowired
    FeatureBucketStateService featureBucketStateService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void testCollectionExists(){
        long time = System.currentTimeMillis()/1000;
        featureBucketStateService.updateFeatureBucketState(time);

        Assert.assertTrue(mongoTemplate.getCollectionNames().contains(FeatureBucketState.COLLECTION_NAME));
    }

    @Test
    public void testUpdateState_NoExistingValue(){
        long time = System.currentTimeMillis()/1000;
        Instant date = Instant.ofEpochSecond(time);

        // Updating first time
        featureBucketStateService.updateFeatureBucketState(time);

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState();
        FeatureBucketState expected = new FeatureBucketState(date.truncatedTo(ChronoUnit.DAYS), date) ;
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getLastClosedDailyBucketDate(), actual.getLastClosedDailyBucketDate());
        Assert.assertEquals(expected.getLastSyncedEventDate(), actual.getLastSyncedEventDate());
    }

    @Test
    public void testUpdateState_UpdateNextDay(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateFeatureBucketState(time);
        Instant date = Instant.ofEpochSecond(time);
        date.plus(Duration.ofDays(1));

        // Updating with next date
        featureBucketStateService.updateFeatureBucketState(date.getEpochSecond());

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState();
        FeatureBucketState expected = new FeatureBucketState(date.truncatedTo(ChronoUnit.DAYS), date);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getLastClosedDailyBucketDate(), actual.getLastClosedDailyBucketDate());
        Assert.assertEquals(expected.getLastSyncedEventDate(), actual.getLastSyncedEventDate());
    }

    @Test
    public void testUpdateState_UpdatePrevDay(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateFeatureBucketState(time);
        Instant date = Instant.ofEpochSecond(time);
        Instant dayBefore = date.minus(Duration.ofDays(1));

        // Updating with next date
        featureBucketStateService.updateFeatureBucketState(dayBefore.getEpochSecond());

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState();
        FeatureBucketState expected = new FeatureBucketState(date.truncatedTo(ChronoUnit.DAYS), date);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getLastClosedDailyBucketDate(), actual.getLastClosedDailyBucketDate());
        Assert.assertEquals(expected.getLastSyncedEventDate(), actual.getLastSyncedEventDate());
    }

    @Test
    public void testUpdateState_NoUpdateNeeded(){
        long time = System.currentTimeMillis()/1000;

        // Updating first time
        featureBucketStateService.updateFeatureBucketState(time) ;
        Instant date = Instant.ofEpochSecond(time);
        FeatureBucketState firstUpdate = featureBucketStateService.getFeatureBucketState();

        // Updating with next date
        featureBucketStateService.updateFeatureBucketState(date.getEpochSecond());

        FeatureBucketState actual = featureBucketStateService.getFeatureBucketState();
        FeatureBucketState expected = new FeatureBucketState(date.truncatedTo(ChronoUnit.DAYS), date);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getLastClosedDailyBucketDate(), actual.getLastClosedDailyBucketDate());
        Assert.assertEquals(firstUpdate.getModifiedAt(), actual.getModifiedAt());
        Assert.assertEquals(expected.getLastSyncedEventDate(), actual.getLastSyncedEventDate());
    }
}
