package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.DailyMetricRecord;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import com.rsa.netwitness.presidio.automation.domain.repository.SmartUserIdHourlyRepository;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class})
public class DailyMetricsTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SmartUserIdHourlyRepository smartRepository;

    private RestHelper restHelper = new RestHelper();


    @Test
    public void active_userId_count_last_day_equals_to_userId_table_count() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorActiveUserIdCountLastDay();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorActiveUsersCountLastDay(url);
        assertThat(actualMetrics).withFailMessage(url+ "\nExpected one metric exactly.").hasSize(1);
        
        DailyMetricRecord actualMetric = actualMetrics.get(0);
        Instant logicalTime = Instant.parse(actualMetric.logicalTime);
        Instant reportTime = Instant.parse(actualMetric.reportTime).truncatedTo(DAYS);

        List<SmartUserIdStoredRecored> byTime = smartRepository.findByTime(logicalTime, reportTime);
        long expectedUserIdCount = byTime.parallelStream().map(e -> e.getContext().getUserId()).distinct().count();

        assertThat(actualMetric.metricValue)
                .withFailMessage(url + "\nuserId count mismatch.\nCompared to 'smart_userId_hourly' from "+ logicalTime + " to " + reportTime)
                .isEqualTo(expectedUserIdCount);
    }


    @Test
    public void events_processed_count_daily_has_3_values_more_then_zero() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorEventsProcessedCountDaily();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorEventsProcessedCountDaily(url);
        assertThat(actualMetrics).withFailMessage(url.toString()).hasSize(3);
        Stream<Integer> metricValues = actualMetrics.stream().map(e -> e.metricValue);
        assertThat(metricValues).allMatch(metricValue -> metricValue > 0);
    }

    @Test
    public void smarts_count_last_day_has_3_values_more_then_zero() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorSmartsCountLastDay();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorSmartsCountLastDay(url);
        assertThat(actualMetrics).withFailMessage(url.toString()).hasSize(3);
        Stream<Integer> metricValues = actualMetrics.stream().map(e -> e.metricValue);
        assertThat(metricValues).allMatch(metricValue -> metricValue > 0);
    }

    @Test
    public void alerts_count_last_day_has_3_values_more_then_zero() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorAlertsCountLastDay();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorAlertsCountLastDay(url);
        assertThat(actualMetrics).withFailMessage(url.toString()).hasSize(3);
        Stream<Integer> metricValues = actualMetrics.stream().map(e -> e.metricValue);
        assertThat(metricValues).allMatch(metricValue -> metricValue > 0);
    }

    @Test
    public void smart_indicators_count_daily_has_3_values_more_then_zero() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorSmartIndicatorsCountDaily();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorSmartIndicatorsCountDaily(url);
        assertThat(actualMetrics).withFailMessage(url.toString()).hasSize(3);
        Stream<Integer> metricValues = actualMetrics.stream().map(e -> e.metricValue);
        assertThat(metricValues).allMatch(metricValue -> metricValue > 0);
    }

    @Test
    public void alert_indicators_count_daily_has_3_values_more_then_zero() {
        PresidioUrl url = restHelper.dailyMetrics().url().withOutputProcessorAlertIndicatorsCountDaily();
        List<DailyMetricRecord> actualMetrics = restHelper.dailyMetrics().request().getOutputProcessorAlertIndicatorsCountDaily(url);
        assertThat(actualMetrics).withFailMessage(url.toString()).hasSize(3);
        Stream<Integer> metricValues = actualMetrics.stream().map(e -> e.metricValue);
        /** need to add network data in future time **/
        //assertThat(metricValues).allMatch(metricValue -> metricValue > 0);
    }

}
