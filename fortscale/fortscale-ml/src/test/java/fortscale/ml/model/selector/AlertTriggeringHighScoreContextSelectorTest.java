package fortscale.ml.model.selector;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.AlertsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
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

    @Configuration
    @Profile("test")
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    @Import({AlertTriggeringHighScoreContextSelectorTestConfig.class})
    @EnableMongoRepositories(basePackageClasses = AlertsRepository.class,
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes= AlertsRepository.class))
    public static class springConfig
    {
        @Autowired
        AlertsRepository alertsRepository;
        @Bean
        public AlertTriggeringHighScoreContextTestSelector alertTriggeringHighScoreContextTestSelector ()
        {
            return new AlertTriggeringHighScoreContextTestSelector();
        }
    }

    @Autowired
    private AlertsRepository alertsRepository;
    @Autowired
    private AlertTriggeringHighScoreContextTestSelector selector;

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