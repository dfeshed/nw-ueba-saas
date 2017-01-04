package fortscale.ml.model.selector;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by baraks on 1/3/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("test")
public class AlertTriggeringHighScoreContextSelectorTest {

    private static class SomeSelector extends AlertTriggeringHighScoreContextSelector
    {
        @Override
        public List<String> getContexts(Date startTime, Date endTime) {
            return Arrays.asList("user1","user2","user3");
        }
    }

    @Profile("test")
    @Configuration
    @Import({MongodbTestConfig.class})
    @EnableMongoRepositories(basePackageClasses = AlertsRepository.class,
            includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.domain.core.dao.AlertsRepository*"))
    public static class springConfig
    {
        @Bean
        public MongoDbRepositoryUtil mongoDbRepositoryUtil()
        {
            return new MongoDbRepositoryUtil();
        }
        @Bean
        public AlertTriggeringHighScoreContextSelector alertTriggeringHighScoreContextSelector ()
        {
            return new SomeSelector();
        }
    }

    @Autowired
    private AlertsRepository alertsRepository;
    @Autowired
    private SomeSelector selector;

    @Test
    public void selectorShouldPerformContextsIntersection()
    {
        Instant startDateInstant = Instant.parse("2017-01-09T01:00:00Z");
        long startDateEpoch = startDateInstant.toEpochMilli();
        Instant endDateInstant = Instant.parse("2017-01-09T03:00:00Z");
        long endDateEpoch = endDateInstant.toEpochMilli();
        boolean userScoreContributionFlag = true;
        double userScoreContribution = 90.0;
        AlertTimeframe daily = AlertTimeframe.Daily;
        String entityId = "user1";
        String name = "name";
        String entityName = "user1";
        List<Evidence> evidences = null;
        int evidencesSize = 0;
        int score = 99;
        Severity high = Severity.High;
        AlertStatus alertStatus = AlertStatus.Open;
        AlertFeedback alertFeedback = AlertFeedback.None;
        Alert alert = new Alert(name, startDateEpoch, endDateEpoch, EntityType.Machine, entityName, evidences, evidencesSize, score, high, alertStatus, alertFeedback, entityId, daily, userScoreContribution, userScoreContributionFlag);
        alertsRepository.save(alert);
        Date startDate = Date.from(startDateInstant);
        Date endDate = Date.from(endDateInstant);
        Set<String> highScoreContexts = selector.getHighScoreContexts(startDate, endDate);
        Assert.assertEquals(highScoreContexts.size(),1);
        Assert.assertTrue(highScoreContexts.contains(entityId));


        alertsRepository.delete(alert);
        highScoreContexts = selector.getHighScoreContexts(startDate, endDate);
        Assert.assertTrue(highScoreContexts.isEmpty());
    }

}