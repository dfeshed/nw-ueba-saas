package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.common.rest.RestApiResponse;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.helpers.rest.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.helpers.rest.RestHelper;
import com.rsa.netwitness.presidio.automation.static_content.AlertClassificationIndicatorDictionary;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.rsa.netwitness.presidio.automation.static_content.AlertClassificationIndicatorDictionary.getClassificationListByPrioritizedOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AlertsRestCorrectnessTest extends AbstractTestNGSpringContextTests {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AlertsRestCorrectnessTest.class.getName());

    private RestHelper restHelper = new RestHelper();

    @BeforeClass
    public void preconditionCheck() {
        ParametersUrlBuilder url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nAlerts list is empty or unable to get response from the output.")
                .isNotNull()
                .isNotEmpty();

        url = restHelper.alerts().url().withMaxSizeParameters();
        alerts = restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nEmpty response.")
                .isNotEmpty();

        url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        alerts =  restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nEmpty response.")
                .isNotEmpty();
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        LOGGER.info("Starting test:  +++" +  method.getName());
    }

    @Test
    public void alerts_count_of_default_request_should_be_correct() throws JSONException {
        ParametersUrlBuilder url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);
        assertThat(alerts.size())
                .as(url+ "\nThe alert count is not 10 as should be by default")
                .isEqualTo(10);
    }

    @Test
    public void alerts_count_of_page_size_should_be_correct() {
        ParametersUrlBuilder url = restHelper.alerts().url().withPageParameters(3,1);
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);

        assertThat(alerts.size())
                .as(url+ "\nThe alert count is not 3 as expected by pageSize setting")
                .isEqualTo(3);
    }

    @Test
    public void total_alerts_count_should_be_correct() throws JSONException {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);
        int totalAlertsSize = alerts.size();

        RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);
        JSONObject json = new JSONObject(response.getResultBody());
        int total = json.getInt("total");

        assertThat(totalAlertsSize)
                .as(url+ "\nTotal alerts count mismatch")
                .isEqualTo(total);
    }


    @Test
    public void severity_score_should_be_in_configuration_score_range() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);
        Map<String, Integer> configurationSeverityScores = OutputTestsUtils.getSeveritiesValues();

        for(AlertsStoredRecord alert : alerts) {
            String alertSeverity = alert.getSeverity().toLowerCase();
            long score = Long.parseLong(alert.getScore());

            assertThat(score)
                    .as("Score must be between [0, 100]")
                    .isBetween(0L, 100L);

            if(score >= configurationSeverityScores.get("low") && score < configurationSeverityScores.get("medium")) {
                assertThat(alertSeverity)
                        .as("%s severity alert score is %s. It is not between [%s, %s]",
                                alertSeverity, score, configurationSeverityScores.get("low"), configurationSeverityScores.get("medium"))
                        .isEqualToIgnoringCase("LOW");
            }
            else if(score >= configurationSeverityScores.get("medium") && score < configurationSeverityScores.get("high")) {
                assertThat(alertSeverity)
                        .as("%s severity alert score is %s. It is not between [%s, %s]",
                                alertSeverity, score, configurationSeverityScores.get("medium"), configurationSeverityScores.get("high"))
                        .isEqualToIgnoringCase("MEDIUM");
            }
            else if(score >= configurationSeverityScores.get("high") && score < configurationSeverityScores.get("critical")) {
                assertThat(alertSeverity)
                        .as("%s severity alert score is %s. It is not between [%s, %s]",
                                alertSeverity, score, configurationSeverityScores.get("high"), configurationSeverityScores.get("critical"))
                        .isEqualToIgnoringCase("HIGH");
            }
            else if (score >= configurationSeverityScores.get("critical")){
                assertThat(alertSeverity)
                        .as("%s severity alert score is %s. It is not between [%s, %s]",
                                alertSeverity, score, configurationSeverityScores.get("critical"), 100)
                        .isEqualToIgnoringCase("CRITICAL");
            }
        }
    }

    @Test
    public void entity_score_contribution_should_match_alert_severity_range() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);
        Map<String, Integer> configurationScoreContributions = OutputTestsUtils.getAlertEntityScoreContributions();

        for(AlertsStoredRecord alert : alerts) {
            String alertSeverity = alert.getSeverity().toLowerCase();
            int actualEntityScoreContribution = Integer.valueOf(alert.getEntityScoreContribution());
            assertThat(configurationScoreContributions)
                    .as("EntityScoreContribution score for severity: " + alertSeverity + " is missing from output-processor configuration")
                    .containsKey(alertSeverity);

            int expectedEntityScoreContribution = configurationScoreContributions.get(alertSeverity);
            assertThat(actualEntityScoreContribution)
                    .as(url+"\nAlertId = " + alert.getId() + "\nentityScoreContribution value mismatch")
                    .isEqualTo(expectedEntityScoreContribution);
        }
    }


    // https://github.rsa.lab.emc.com/asoc/presidio-core/blob/master/fortscale/presidio-output/presidio-output-processor/src/main/resources/supporting_information_config.yml
    @Test
    public void no_missing_classifications_and_right_order() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts =  restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<String> actualClassifications = Arrays.asList(alert.getClassification());
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            //  contributions sum for each indicator
            Map<String, Double> scoreContributionSumByIndicator = indicators.stream()
                    .collect(groupingBy(AlertsStoredRecord.Indicator::getName,
                                summingDouble(AlertsStoredRecord.Indicator::getScoreContribution)));

            // find max of contribution sums
            Double maxScoreContribution = scoreContributionSumByIndicator.values().stream()
                    .max(Double::compare).get();


            // Build a list of indicators that will be used to create expected classifications list
            // If found indicators with contribution >= 0.3, will count only that indicators
            List<String> expectedClassificationsContributors = new ArrayList<>();

            if(maxScoreContribution >= 0.3) {
                expectedClassificationsContributors = scoreContributionSumByIndicator.entrySet().stream()
                        .filter(indicator -> indicator.getValue() >= 0.3)
                        .map(indicator -> indicator.getKey())
                        .collect(toList());
            } else {
                expectedClassificationsContributors = scoreContributionSumByIndicator.entrySet().stream()
                        .filter(indicator -> indicator.getValue().equals(maxScoreContribution))
                        .map(indicator -> indicator.getKey())
                        .collect(toList());
            }

            assertThat(expectedClassificationsContributors)
                    .as(url + "\nAlertId = " + alert.getId() + "\nEmpty list of indicators which contribute classifications")
                    .isNotNull()
                    .isNotEmpty();

            assertThat(AlertClassificationIndicatorDictionary.getAll().keySet())
                    .as(url + "\nAlertId = " + alert.getId() + "\nUndefined indicators. Missing from classifications map.")
                    .containsAll(expectedClassificationsContributors);

            List<String> expectedClassifications = expectedClassificationsContributors.stream()
                    .map(AlertClassificationIndicatorDictionary::getIndicatorClassification)
                    .collect(toList());

            List<String> expectedClassificationsOrdered = getClassificationListByPrioritizedOrder()
                    .stream().sequential()
                    .filter(expectedClassifications::contains)
                    .collect(toList());

            assertThat(actualClassifications)
                    .as(url + "\nAlertId = " + alert.getId() + "\nMissing classification or wrong order.")
                    .hasSameSizeAs(expectedClassificationsOrdered)
                    .containsExactlyElementsOf(expectedClassificationsOrdered);
        }
    }


    @Test
    public void response_values_are_not_null() {
        ParametersUrlBuilder url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alertList =  restHelper.alerts().request().getAlerts(url);
        assertThat(alertList)
                .as(url + "\nEmpty response.")
                .isNotEmpty();

        for(AlertsStoredRecord alert : alertList) {
            if(alert.getId() == null || alert.getId().equals("null")) {
                Assert.fail("alert id is 'null'");
            }
            if(alert.getClassification().length == 0 || alert.getClassification() == null) {
                Assert.fail("alert classification is empty or null");
            }
            if(alert.getStartDate() == null || alert.getStartDate().equals("null")) {
                Assert.fail("alert startDate is 'null'");
            }
            if(alert.getEndDate() == null || alert.getEndDate().equals("null")) {
                Assert.fail("alert endDate is 'null'");
            }
            if(alert.getEntityName() == null || alert.getEntityName().equals("null")) {
                Assert.fail("alert username is 'null'");
            }
            if(alert.getIndicatorsNum() == null){
                Assert.fail("alert indicatorNum is 'null'");
            }
            if(alert.getIndicatorsName() == null){
                Assert.fail("alert indicatorsName is null.");
            }
            if(alert.getScore() == null) {
                Assert.fail("alert score is 'null'");
            }
            if(alert.getFeedback() == null || alert.getFeedback().equals("null")){
                Assert.fail("alert feedback is 'null'");
            }
            if(alert.getEntityScoreContribution() == null || alert.getEntityScoreContribution().equals("null")){
                Assert.fail("alert userScoreContribution is 'null'");
            }
            if(alert.getTimeframe() == null || alert.getTimeframe().equals("null")){
                Assert.fail("alert timeframe is 'null'");
            }
            if(alert.getSeverity() == null || alert.getSeverity().equals("null")){
                Assert.fail("alert severity is 'null'");
            }
            if(alert.getEntityDocumentId() == null || alert.getEntityDocumentId().equals("null")){
                Assert.fail("alert userId is 'null'");
            }

        }
    }

    @Test
    public void indicators_list_is_not_empty() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            assertThat(alert.getIndicatorsList())
                    .as(url + "\nindicatorsList equals 'null'")
                    .isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    public void alert_indicator_list_size_equals_to_indicators_name_size_and_indicators_num() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();
            int indicatorNum = alert.getIndicatorsNum();
            String[] indicatorsName = alert.getIndicatorsName();

            assertThat(indicators.size())
                    .as(url + "\nIndicator list size, indicators name size and indicator num are not equals")
                    .isEqualTo(indicatorsName.length)
                    .isEqualTo(indicatorNum);
        }
    }

    @Test
    public void alerts_indicator_list_names_equals_to_indicator_name_list() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<String> indicatorsFromArray = alert.getIndicatorsList().stream()
                    .map(AlertsStoredRecord.Indicator::getName)
                    .collect(toList());

            List<String> fromIndicatorsNameField = Arrays.asList(alert.getIndicatorsName());

            assertThat(fromIndicatorsNameField)
                    .as(url+"\nAlertId="+alert.getId())
                    .hasSameSizeAs(indicatorsFromArray)
                    .containsExactlyInAnyOrderElementsOf(indicatorsFromArray);
        }
    }

    @Test
    public void indicators_list_sorted_by_score_contribution_descending() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<Double> scoreContributions = alert.getIndicatorsList().stream().sequential()
                    .map(AlertsStoredRecord.Indicator::getScoreContribution)
                    .collect(toList());

            assertThat(scoreContributions)
                    .as(url.toString() + "\nAlert: " + alert.getId())
                    .isSortedAccordingTo(reverseOrder());
        }
    }

    @Test
    public void indicators_start_end_time_inside_alert_time_range() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for (AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            long alertStart = Long.parseLong(alert.getStartDate());
            long alertEnd = Long.parseLong(alert.getEndDate());

            for(AlertsStoredRecord.Indicator ind : indicators) {
                long indicatorStartDate = Long.parseLong(ind.getStartDate());
                long indicatorEndDate = Long.parseLong(ind.getEndDate());

                assertThat(indicatorStartDate < alertStart || indicatorEndDate > alertEnd )
                        .as("Indicator time is out of the alert time range." +
                                "\n" + url +
                                "\nAlert " + alert.getId() +
                                "\nIndicator " + ind.getId())
                        .isTrue();
            }
        }
    }

    @Test
    public void sum_of_indicator_contribution_result_is_correct() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();
            double indicatorsScoreSum = indicators.stream()
                    .mapToDouble(ind -> ind.getScoreContribution())
                    .sum();

            assertThat(indicatorsScoreSum)
                    .as(url + "\nAlertId =" + alert.getId()+
                            "\nsum of the contribution score of alert's indicator is not between 0.99 to 1.01")
                    .isBetween(0.98, 1.01);

        }
    }

    @Test
    public void alert_time_range_is_one_hour() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for (AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            long alertStart = Long.parseLong(alert.getStartDate());
            long alertEnd = Long.parseLong(alert.getEndDate());

            assertThat(alertEnd-alertStart)
                    .as(url + "\nAlertId =" + alert.getId())
                    .isEqualTo(HOURS.toMillis(1));
        }
    }
}