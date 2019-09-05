package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.DailyMetricRecord;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import com.rsa.netwitness.presidio.automation.domain.repository.SmartUserIdHourlyRepository;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.test_managers.OutputTestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {OutputTestManager.class, MongoConfig.class})
public class DailyMetricsTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SmartUserIdHourlyRepository smartRepository;

    private RestHelper restHelper = new RestHelper();


    @Test
    public void active_userId_count_last_day() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorActiveUsersCountLastDay();
        Optional<DailyMetricRecord> metric = restHelper.dailyMetrics().request().getActiveUserIdCountLastDay(url);
        DailyMetricRecord actualMetric = metric.orElseGet(() -> fail(url + "\nRequired metric not found:'output-processor.active_userId_count_last_day'"));
        Instant logicalTime = Instant.parse(actualMetric.logicalTime);
        Instant reportTime = Instant.parse(actualMetric.reportTime).truncatedTo(DAYS);

        List<SmartUserIdStoredRecored> byTime = smartRepository.findByTime(logicalTime, reportTime);
        long expectedUserIdCount = byTime.parallelStream().map(e -> e.getContext().getUserId()).distinct().count();

        assertThat(actualMetric.metricValue)
                .withFailMessage(url + "\nuserId count mismatch.\nCompared to 'smart_userId_hourly' from "+ logicalTime + " to " + reportTime)
                .isEqualTo(expectedUserIdCount);
    }

}
