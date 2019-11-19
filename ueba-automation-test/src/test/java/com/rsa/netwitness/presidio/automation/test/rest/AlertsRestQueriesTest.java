package com.rsa.netwitness.presidio.automation.test.rest;

import com.google.gson.Gson;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

public class AlertsRestQueriesTest extends AbstractTestNGSpringContextTests {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AlertsRestQueriesTest.class.getName());

    private RestHelper restHelper = new RestHelper();

    @BeforeClass
    public void preconditionCheck() {
        PresidioUrl url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nAlerts list is empty or unable to getOperationTypeToCategoryMap response from the output.")
                .isNotNull()
                .isNotEmpty();

    }

    @BeforeMethod
    public void nameBefore(Method method) {
        LOGGER.info("Starting test:  +++" +  method.getName());
    }

    @Test
    public void sort_by_start_date_desc_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedParameters("DESC", "START_DATE");
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nEmpty response.")
                .isNotEmpty();

        List<Long> alertsStartDates = alerts.stream().sequential()
                .map(e -> e.getStartDate().toEpochMilli())
                .collect(Collectors.toList());

        assertThat(alertsStartDates)
                .as(url.toString())
                .isSortedAccordingTo(reverseOrder());

    }

    @Test
    public void sort_by_end_date_asc_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedParameters("ASC", "END_DATE");
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        assertThat(alerts)
                .as(url + "\nEmpty response.")
                .isNotEmpty();

        List<Long> alertsEndDates = alerts.stream().sequential()
                .map(e -> e.getStartDate().toEpochMilli())
                .collect(Collectors.toList());

        assertThat(alertsEndDates)
                .describedAs(url.toString())
                .isSorted();
    }

    @Test
    public void max_min_score_range_filter_result_is_correct() {
        List<Integer> distinctScoresSorted = getAlertsDistinctSortedScores();
        int minScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("minScore");
        int maxScore;

        if (distinctScoresSorted.size() == 2) {
            maxScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("minScore");
        } else {
            maxScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("maxScore");
        }

        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedAscAndMinMaxScoreParameters(minScore, maxScore);
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(parseInt(alerts.get(0).getScore()))
                .as(url + "\nRequested minScore doesn't match the actual result")
                .isEqualTo(minScore);

        assertThat(parseInt(alerts.get(alerts.size()-1).getScore()))
                .as(url + "\nRequested maxScore doesn't match the actual result")
                .isEqualTo(maxScore);
    }

    @Test
    public void min_score_filter_result_is_correct() {
        List<Integer> distinctScoresSorted = getAlertsDistinctSortedScores();
        int minScore;

        if (distinctScoresSorted.size() == 2) {
            minScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("maxScore");
        } else {
            minScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("minScore");
        }

        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedAscAndMinScoreParameters(minScore);
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(parseInt(alerts.get(0).getScore()))
                .as(url + "\nRequested minScore doesn't match the actual result")
                .isEqualTo(minScore);
    }

    @Test
    public void max_score_filter_result_is_correct() {
        List<Integer> distinctScoresSorted = getAlertsDistinctSortedScores();
        int maxScore;

        if (distinctScoresSorted.size() == 2) {
            maxScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("minScore");
        } else {
            maxScore = getValuesForMinMaxScoreTest(distinctScoresSorted).get("maxScore");
        }

        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedAscAndMaxScoreParameters(maxScore);
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(parseInt(alerts.get(alerts.size()-1).getScore()))
                .as(url + "\nRequested maxScore doesn't match the actual result")
                .isEqualTo(maxScore);
    }

    @Test
    public void filter_by_entity_name_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        long distinctEntitySize = alerts.stream()
                .map(AlertsStoredRecord::getEntityName)
                .distinct()
                .count();

        assertThat(distinctEntitySize)
                .as(url + "\nInsufficient amount of entities for the test")
                .isGreaterThan(1L);


        String entityNameExpected = alerts.get(alerts.size()/2).getEntityName();

        url = restHelper.alerts().url().alertsWithEntityNamesParameters(entityNameExpected);
        alerts = restHelper.alerts().request().getAlerts(url);

        List<String> entityNamesActual = alerts.stream()
                .map(AlertsStoredRecord::getEntityName)
                .distinct()
                .collect(Collectors.toList());

        assertThat(entityNamesActual)
                .as(url.toString())
                .startsWith(entityNameExpected);
    }

    @Test
    public void filter_by_missing_entity_returns_null() {
        PresidioUrl url = restHelper.alerts().url().alertsWithEntityNamesParameters("abcdefgNotExist");
        RestApiResponse restApiResponse = restHelper.alerts().request().getRestApiResponse(url);
        assertThat(restApiResponse)
                .as(url + "should return null instead of an empty list")
                .isNull();
    }

    @Test
    public void filter_by_severity_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        long distinctSeveritySize = alerts.stream()
                .map(AlertsStoredRecord::getSeverity)
                .distinct()
                .count();

        if (distinctSeveritySize <= 1) {
            skipTest("All alerts are in the same severity. Filter result wouldn't make sense");
        }


        String severityExpected = alerts.get(alerts.size()/2).getSeverity();

        url = restHelper.alerts().url().withSeverityParameter(severityExpected);
        alerts = restHelper.alerts().request().getAlerts(url);

        List<String> severitiesActual = alerts.stream()
                .map(AlertsStoredRecord::getSeverity)
                .distinct()
                .collect(Collectors.toList());

        assertThat(severitiesActual)
                .as(url.toString())
                .containsOnly(severityExpected);
    }


    @Test
    public void static_only_indicators_alert_does_not_exist() {
        SoftAssertions softly = new SoftAssertions();

        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        for(AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            long notStaticIndicatorsCount = indicators.stream()
                    .filter(indicator -> ! indicator.getType().equals("STATIC_INDICATOR"))
                    .count();

            softly.assertThat(notStaticIndicatorsCount)
                   .as("Found an alert with static only indicators. alertId = " + alert.getId())
                    .isGreaterThan(0);
        }
        softly.assertAll();
    }


    @Test
    public void aggregate_by_severity_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        Map<String, Long> countBySeverity = alerts.stream()
                .collect(groupingBy(AlertsStoredRecord::getSeverity, counting()));

        url = restHelper.alerts().url().withAggregatedFieldParameter("SEVERITY");

        try {
            JSONObject json =  restHelper.alerts().request().getRestApiResponseAsJsonObj(url)
                    .getJSONObject("aggregationData")
                    .getJSONObject("SEVERITY");

            Map<String, Double> actualHashMap = new Gson().fromJson(json.toString(), HashMap.class);

            Map<String, Long> actualResult = actualHashMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue().longValue()
                    ));

            assertThat(actualResult)
                    .as(url.toString())
                    .isEqualTo(countBySeverity);

        } catch (Exception e) {
            LOGGER.error(url.toString());
            LOGGER.error("Unable to parse severity");
            Fail.fail(e.getMessage());
        }
    }

    @Test
    public void aggregate_by_feedback_result_is_correct() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        Map<String, Long> countBySeverity = alerts.stream()
                .collect(groupingBy(AlertsStoredRecord::getFeedback, counting()));

        url = restHelper.alerts().url().withAggregatedFieldParameter("FEEDBACK");

        try {
            JSONObject json = restHelper.alerts().request().getRestApiResponseAsJsonObj(url)
                    .getJSONObject("aggregationData")
                    .getJSONObject("FEEDBACK");

            Map<String, Double> actualHashMap = new Gson().fromJson(json.toString(), HashMap.class);

            Map<String, Long> actualResult = actualHashMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue().longValue()
                    ));

            assertThat(actualResult)
                    .as(url.toString())
                    .isEqualTo(countBySeverity);

        } catch (Exception e) {
            LOGGER.error(url.toString());
            LOGGER.error("Unable to parse FEEDBACK");
            Fail.fail(e.getMessage());
        }
    }




    private List<Integer> getAlertsDistinctSortedScores() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndSortedParameters("ASC", "SCORE");
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(alerts)
                .as(url + "\nEmpty response.")
                .isNotEmpty();

        List<Integer> distinctScoresSorted = alerts.stream().sequential()
                .map(e -> parseInt(e.getScore()))
                .distinct()
                .collect(Collectors.toList());

        if (distinctScoresSorted.size() < 3) {
            skipTest("Insufficient amount of distinct score samples");
        }

        assertThat(distinctScoresSorted)
                .describedAs(url.toString())
                .isSorted();

        return distinctScoresSorted;
    }

    private Map<String,Integer> getValuesForMinMaxScoreTest(final List<Integer> distinctScoresSorted) {
        Map<String,Integer> values = Maps.newHashMap();

        if (distinctScoresSorted.size() > 2) {
            values.put("minScore", distinctScoresSorted.get(1));
            values.put("maxScore", distinctScoresSorted.get(distinctScoresSorted.size()-2));
        } else {
            values.put("minScore", distinctScoresSorted.get(0));
            values.put("maxScore", distinctScoresSorted.get(max(0,distinctScoresSorted.size()-1)));
        }

        return values;
    }

}