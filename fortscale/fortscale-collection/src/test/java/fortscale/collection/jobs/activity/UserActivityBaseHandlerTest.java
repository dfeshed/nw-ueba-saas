package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketState;
import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateRepository;
import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateService;
import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateServiceImpl;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.activities.UserActivityJobState;
import fortscale.services.UserService;
import fortscale.services.impl.UserServiceImpl;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.engine.NullStatsEngine;
import fortscale.utils.monitoring.stats.engine.StatsEngine;
import fortscale.utils.monitoring.stats.impl.StatsServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.antlr.op.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 14/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class UserActivityBaseHandlerTest {

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

        @Bean
        public UserActivityWorkingHoursHandler userActivityWorkingHoursHandler(){
            return new UserActivityWorkingHoursHandler();
        }

        @Bean
        public UserService userService(){
            return new UserServiceImpl();
        }

        @Bean
        public StatsService statsService(){
            return new StatsServiceImpl(new NullStatsEngine());
        }
//
//        @Bean
//        public AdUserRepository adUserRepository(){
//            return new AdUserRepositoryImpl();
//        }

    }

    @Autowired
    FeatureBucketStateService featureBucketStateService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserActivityWorkingHoursHandler userActivityWorkingHoursHandler;

    @Test
    public void testCalculate(){

        // Setting last agg date to today
        Instant date = Instant.ofEpochMilli(System.currentTimeMillis());
        featureBucketStateService.updateState(date.getEpochSecond(), FeatureBucketState.StateType.LAST_SYNC_DATE);

        userActivityWorkingHoursHandler.calculate(10);
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(Query.query(Criteria.where(UserActivityJobState.ACTIVITY_NAME_FIELD).is(userActivityWorkingHoursHandler.getActivity().name())), UserActivityJobState.class);

        Assert.assertEquals(10, userActivityJobState.getCompletedExecutionDays().size());
        Assert.assertEquals(date.truncatedTo(ChronoUnit.DAYS).getEpochSecond(), userActivityJobState.getCompletedExecutionDays().last().longValue());
    }
}
