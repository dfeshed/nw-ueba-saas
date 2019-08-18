package com.rsa.netwitness.presidio.automation.test.rest;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.mapping.indicators.IndicatorsInfo;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.rsa.netwitness.presidio.automation.mapping.indicators.IndicatorsInfo.ALL_MANDATORY_INDICATORS;
import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class AlertsIndicatorsTests extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AlertsIndicatorsTests.class.getName());

    private RestHelper restHelper = new RestHelper();
    private List<AlertsStoredRecord> allAlerts = Lists.newArrayList();
    private List<String> indicatorsDistinctNames = Lists.newArrayList();
    private String testName;

    private Object[][] scoreAggregation;
    private Object[][] staticIndicator;
    private Object[][] featureAggregation;
    private Object[][] distinctFeatureAggregation;
    private Object[][] allExistingIndicatorsObj;

    private Set<String> scoreAggregationIndicatorNames = Sets.newHashSet();
    private Set<String> staticIndicatorIndicatorNames = Sets.newHashSet();
    private Set<String> featureAggregationIndicatorNames = Sets.newHashSet();
    private Set<String> distinctFeatureAggregationIndicatorNames = Sets.newHashSet();
    private List<String> allActualIndicatorNames = Lists.newArrayList();

    private Map<String, String[]> staticIndicatorMap = new HashMap<>();
    private Map<String, String[]> scoreAggregationMap = new HashMap<>();
    private Map<String, String[]> featureAggregationMap = new HashMap<>();
    private Map<String, String[]> distinctFeatureAggregationMap = new HashMap<>();
    private Map<String, String[]> allIndicatorsTypeNameSamples = new HashMap<>();
    private SoftAssertions softly = new SoftAssertions();

    @BeforeClass
    public void preconditionCheckAndPrepare() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        allAlerts = restHelper.alerts().request().getAlerts(url);
        assertThat(allAlerts)
                .as(url + "\nAlerts list is empty or unable to getOperationTypeToCategoryMap response from the output.")
                .isNotNull()
                .isNotEmpty();

        initTestingData();
    }

    @Test
    public void all_mandatory_indicators_from_static_list_are_present() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        assertThat(allActualIndicatorNames)
                .as(url + "\nIndicators are missing in alerts")
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrderElementsOf(ALL_MANDATORY_INDICATORS);
    }

    @Test
    public void alerts_count_result_filtered_by_indicator_name_is_correct() {
        for (String indicatorName : indicatorsDistinctNames) {
            ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndIndicatorNameParameters(indicatorName);
            List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

            // alertIds from getAllAlerts REST call.
            long countOfAllAlertsHaveGivenIndicatorName = allAlerts.stream()
                    .map(e -> Arrays.stream(e.getIndicatorsName()).collect(toList()))
                    .filter(e -> e.contains(indicatorName))
                    .count();

            long countOfAlertsFilteredByIndicatorHaveGivenIndicatorName = alerts.stream()
                    .map(e -> Arrays.stream(e.getIndicatorsName()).collect(toList()))
                    .filter(e -> e.contains(indicatorName))
                    .count();

            softly.assertThat(alerts.size())
                    .as(url + "\nAlerts count mismatch")
                    .isEqualTo(countOfAllAlertsHaveGivenIndicatorName)
                    .isEqualTo(countOfAlertsFilteredByIndicatorHaveGivenIndicatorName);
        }
        softly.assertAll();
    }


    @Test
    public void alert_indicators_event_time_range_is_within_the_alert_time_range() {
        for (AlertsStoredRecord alert : allAlerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            long alertStartDate = alert.getStartDate().toEpochMilli() / 1000;
            long alertEndDate = alert.getStartDate().toEpochMilli() / 1000;

            for (AlertsStoredRecord.Indicator singleIndicator : indicators) {

                ParametersUrlBuilder url = restHelper.alerts().withId(alert.getId())
                        .indicators().withId(singleIndicator.getId())
                        .events().url().withMaxSizeParameters();

                JSONObject indicatorEvents = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);

                try {
                    JSONArray indicatorsEventsList = indicatorEvents.getJSONArray("events");
                    softly.assertThat(indicatorsEventsList.length())
                            .as(url + "\nEmpty response body")
                            .isGreaterThan(0);


                    for (int i = 0; i < indicatorsEventsList.length(); i++) {
                        long time = Long.parseLong(indicatorsEventsList.getJSONObject(i).getJSONObject("eventDate").get("epochSecond").toString());
                        String msg = url +
                                "\nindicator's event time is not within the alert time range.\n" +
                                "AlertId = " + alert.getId() + "\n" +
                                "UserId = " + alert.getEntityDocumentId() + "\n" +
                                "IndicatorId = " + singleIndicator.getId() + "\n" +
                                "EventId = " + indicatorsEventsList.getJSONObject(i).getString("id");
                        softly.assertThat(time >= alertStartDate && time <= alertEndDate).overridingErrorMessage(msg).isTrue();
                    }

                } catch (JSONException e) {
                    Assert.fail(e.getMessage());
                }
            }
            softly.assertAll();
        }
    }

    @Test
    public void indicator_start_date_and_the_first_indicator_event_date_should_be_equal() {
        for (AlertsStoredRecord alert : allAlerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            for (AlertsStoredRecord.Indicator singleIndicator : indicators) {
                ParametersUrlBuilder indicatorUrl = restHelper.alerts().withId(alert.getId())
                        .indicators().withId(singleIndicator.getId())
                        .url()
                        .withMaxSizeParameters();

                ParametersUrlBuilder indicatorEventsUrl = restHelper.alerts().withId(alert.getId())
                        .indicators().withId(singleIndicator.getId())
                        .events()
                        .url()
                        .withMaxSizeParameters();

                if (!singleIndicator.getName().startsWith("high_number")) {
                    JSONObject indicator = restHelper.alerts().request().getRestApiResponseAsJsonObj(indicatorUrl);
                    JSONObject indicatorEvents = restHelper.alerts().request().getRestApiResponseAsJsonObj(indicatorEventsUrl);

                    try {
                        long startTime = indicator.getLong("startDate");
                        JSONArray indicatorsEventsList = indicatorEvents.getJSONArray("events");
                        Assert.assertTrue(indicatorsEventsList.length() > 0, indicatorUrl + "\n" + indicatorEventsUrl + "\n" +
                                "Indicator '" + singleIndicator.getName() + "' have no events." +
                                "UserId = " + alert.getEntityDocumentId() + "\nAlertId = " + alert.getId() + "\nIndicatorId = " + singleIndicator.getId());

                        int firstEventIndex = OutputTestsUtils.findEarlierEventTimeIndex(indicatorsEventsList);
                        JSONObject firstEvent = indicatorsEventsList.getJSONObject(firstEventIndex);

                        long time = Long.parseLong(firstEvent.getJSONObject("eventDate").get("epochSecond").toString());
                        Assert.assertTrue(time == startTime, indicatorUrl + "\n" + indicatorEventsUrl + "\n" +
                                "indicator startDate and the first indicator's event date are not equal.\n" +
                                "UserId = " + alert.getEntityDocumentId() + "\nAlertId = " + alert.getId() + "\n" +
                                "IndicatorId = " + singleIndicator.getId());
                    } catch (JSONException e) {
                        Assert.fail(indicatorUrl + "\n" + indicatorEventsUrl + "\n" + e.getMessage());
                    }
                }
            }
        }
    }


    @Test(dataProvider = "allIndicatorsDataProvider")
    public void anomaly_true_indicator_count_should_be_correct_in_historical_data(String indicator) {
        String alertId = allIndicatorsTypeNameSamples.get(indicator)[0];
        String indicatorId = allIndicatorsTypeNameSamples.get(indicator)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withExpandedParameter();

        try {
            IndicatorResult actualIndicator = getIndicatorWithHisoricalData(alertId, indicatorId);
            List<HistoricalDataBucket> anomalyBuckets = getAnomalyHistoricalDataBuckets(actualIndicator);
            List<Boolean> anomalyFlags = anomalyBuckets.stream().map(e -> e.anomaly).collect(toList());

            if (actualIndicator.historicalDataType.equals("TimeAggregation")) {
                assertThat(anomalyFlags)
                        .describedAs(url.toString())
                        .hasSizeGreaterThanOrEqualTo(1)
                        .containsOnly(true);
            } else {
                assertThat(anomalyFlags)
                        .describedAs(url.toString())
                        .hasSize(1)
                        .containsOnly(true);
            }
        } catch (JSONException e) {
            Assert.fail(url + "\nCannot getOperationTypeToCategoryMap the requested information from json object. \n" + e.getMessage());
        }
    }

    @Test(dataProvider = "indicatorTypeFeatureAggregation")
    public void feature_aggregation_anomaly_value_should_match_historical_data_and_events_num(String indicator) {
        String alertId = featureAggregationMap.get(indicator)[0];
        String indicatorId = featureAggregationMap.get(indicator)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withExpandedParameter();

        IndicatorResult actualIndicator = getIndicatorWithHisoricalData(alertId, indicatorId);
        List<HistoricalDataBucket> anomalyBuckets = getAnomalyHistoricalDataBuckets(actualIndicator);

        List<String> historicalDataAnomalyValues = anomalyBuckets.stream().map(e -> e.value).collect(toList());
        assertThat(historicalDataAnomalyValues).as(url + "\nhistoricalData anomaly value is missing").isNotEmpty();

        boolean notInExclusionList = !(
                indicator.equals("high_number_of_file_move_operations_from_shared_drive")
                        || indicator.equals("high_number_of_successful_file_action_operations")
                        || actualIndicator.schema.equals("TLS")
        );

        Function<String, Long> toLong = st -> Double.valueOf(st).longValue();


        if (actualIndicator.name.startsWith("high_number_of_") && notInExclusionList) {
            if (actualIndicator.eventsNum == 10000) {
                assertThat(toLong.apply(actualIndicator.anomalyValue))
                        .as(url + "anomalyValue should be >= 10000")
                        .isGreaterThanOrEqualTo(actualIndicator.eventsNum);
            } else {
                assertThat(toLong.apply(actualIndicator.anomalyValue))
                        .as(url + "\nIndicatorName = " + actualIndicator.name + "\nExpected: anomalyValue == historicalData.anomalyValue == eventsNum")
                        .isEqualTo(toLong.apply(historicalDataAnomalyValues.get(0)))
                        .isEqualTo(actualIndicator.eventsNum);
            }

        } else {
            assertThat(actualIndicator.anomalyValue)
                    .as(url + "\nIndicatorName = " + actualIndicator.name + "\nExpected: anomalyValue == historicalData.anomalyValue")
                    .isEqualTo(historicalDataAnomalyValues.get(0));
        }
    }


    @Test(dataProvider = "allIndicatorsDataProvider")
    public void successful_and_failure_indicator_names_should_match_all_result_values_in_related_events(String indicatorName) {
        Boolean success = null;
        if (indicatorName.contains("successful")) {
            success = true;
        } else if (indicatorName.contains("failure")) {
            success = false;
        }

        if (success != null) {
            String alertId = allIndicatorsTypeNameSamples.get(indicatorName)[0];
            String indicatorId = allIndicatorsTypeNameSamples.get(indicatorName)[1];
            JSONArray events = getEvents(alertId, indicatorId);
            ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId)
                    .events().url().withMaxSizeParameters();

            for (int i = 0; i < events.length(); i++) {
                String result = events.getJSONObject(i).getString("result");
                if (success) {
                    softly.assertThat(result)
                            .as(url + "\nIndicator with name " + indicatorName + "has events with `result` other than `SUCCESS`.\nResult = " + result)
                            .isEqualTo("SUCCESS");
                } else {
                    softly.assertThat(result)
                            .as(url + "\nIndicator with name " + indicatorName + "has events with `result` other than `FAILURE`.\nResult = " + result)
                            .isEqualTo("FAILURE");
                }
            }
        }
        softly.assertAll();
    }

    @Test(dataProvider = "allIndicatorsDataProvider")
    public void indicator_schema_name_should_match_static_map(String indicatorName) {
        String alertId = allIndicatorsTypeNameSamples.get(indicatorName)[0];
        String indicatorId = allIndicatorsTypeNameSamples.get(indicatorName)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withNoParameters();
        IndicatorResult indicator = getIndicator(alertId, indicatorId);
        String expectedSchema = IndicatorsInfo.getSchemaNameByIndicator(indicatorName).toUpperCase();
        assertThat(indicator.schema)
                .as(url + "\nIndicator schema name mismatch.\nIndicator name = " + indicatorName)
                .isEqualTo(expectedSchema);
    }


    @Test(dataProvider = "indicatorTypeStaticIndicator")
    public void event_count_of_static_indicator_should_match_indicator_events_response(String indicator) {
        String alertId = staticIndicatorMap.get(indicator)[0];
        String indicatorId = staticIndicatorMap.get(indicator)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withNoParameters();
        JSONObject indicatorData = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);

        try {
            String indicatorName = indicatorData.getString("name");
            int selectedIndicatorEventsNum = indicatorData.getInt("eventsNum");

            url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).events().url().withMaxSizeParameters();
            JSONObject indicatorsEvents = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);
            JSONArray eventsList = indicatorsEvents.getJSONArray("events");

            assertThat(selectedIndicatorEventsNum)
                    .as(url + "\nAlerts page indicators count is different from the events page. indicatorName = " + indicatorName)
                    .isEqualTo(eventsList.length())
                    .isGreaterThan(0);

            // TODO: Split this part to another different test.
            for (int i = 0; i < eventsList.length(); i++) {
                if (indicator.equals("admin_changed_his_own_password")) {
                    Assert.assertTrue(eventsList.getJSONObject(i).getBoolean("isUserAdmin"), "User should be Admin for indicator '" + indicator);
                    Assert.assertEquals("user_password_changed", eventsList.getJSONObject(i).getString("operationType").toLowerCase(), "eventType not matched to indicator name. indicator: " + indicatorName);
                } else {
                    Assert.assertEquals(indicatorName, eventsList.getJSONObject(i).getString("operationType").toLowerCase(), "eventType not matched to indicator name. indicator: " + indicatorName);
                }

            }

        } catch (JSONException e) {
            Assert.fail(url + "\n" + e.getMessage());
        }
    }


    @Test
    public void static_indicator_name_should_appear_only_once_in_alert() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        for (AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();
            List<String> staticIndicatorNames = indicators.stream()
                    .filter(indicator -> indicator.getType().equals("STATIC_INDICATOR"))
                    .map(indicator -> indicator.getName())
                    .collect(toList());

            softly.assertThat(staticIndicatorNames)
                    .as(url + "\nStatic indicator name appears twice for the same alert." +
                            "\nUserId = " + alert.getEntityDocumentId() +
                            "\nAlertId = " + alert.getId())
                    .doesNotHaveDuplicates();
        }
        softly.assertAll();
    }


    // todo: ask Yuval
    @Test
    public void highNumberOfDistinctFilesOpenedAttemptsTest() throws JSONException {
        String indicator = "high_number_of_distinct_files_opened_attempts";

        if (!distinctFeatureAggregationMap.containsKey(indicator)) {
            skipTest("Required indicator not found: " + indicator);
        }

        String alertId = distinctFeatureAggregationMap.get(indicator)[0];
        String indicatorId = distinctFeatureAggregationMap.get(indicator)[1];

        IndicatorResult actualIndicator = getIndicator(alertId, indicatorId);
        JSONArray events = getEvents(alertId, indicatorId);

        List<String> filePaths = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {
            String path = events.getJSONObject(i).getString("absoluteSrcFilePath");

            if (!filePaths.contains(path) && !path.equals("null")) {
                filePaths.add(path);
            }
        }
        int anomalyValueAsInteger = Integer.parseInt(actualIndicator.anomalyValue.split("\\.")[0]);
        assertThat(anomalyValueAsInteger)
                .as("anomaly value is not matched to the event's distinct paths")
                .isEqualByComparingTo(filePaths.size());
    }


    @Test
    public void score_aggregation_indicator_is_unique_by_alertId_name_anomalyValue_and_context() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        for (AlertsStoredRecord alert : alerts) {
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            List<AlertsStoredRecord.Indicator> scoreAggIndicators = indicators.parallelStream()
                    .filter(indicator -> indicator.getType().equals("SCORE_AGGREGATION"))
                    .collect(toList());


            Function<AlertsStoredRecord.Indicator, String> relevantContextValue =
                    indicator -> {
                        if (indicator.getContexts().containsKey("srcProcessFileName")) {
                            return indicator.getContexts().get("srcProcessFileName").toString()
                                    + "_" + indicator.getContexts().get("dstProcessFileName").toString();
                        } else if (indicator.getContexts().containsKey("processFileName")) {
                            return indicator.getContexts().get("processFileName").toString()
                                    + "_" + indicator.getContexts().get("registryKeyGroup").toString();
                        } else return "NONE";
                    };

            Predicate<List<AlertsStoredRecord.Indicator>> notSingleElementLists = e -> e.size() != 1;

            // AlertsStoredRecords grouped by Indicator Name, Anomaly Value and Relevant Context Value
            Map<String, Map<String, Map<String, List<AlertsStoredRecord.Indicator>>>> indicatorsByNameAndAnomalyValueAndContext =
                    scoreAggIndicators.parallelStream()
                            .collect(groupingBy(relevantContextValue,
                                    groupingBy(AlertsStoredRecord.Indicator::getAnomalyValue,
                                            groupingBy(AlertsStoredRecord.Indicator::getName))));

            // taking these groups for validation.
            List<List<AlertsStoredRecord.Indicator>> collectIndicatorGroups =
                    indicatorsByNameAndAnomalyValueAndContext.values().stream()
                            .flatMap(e -> e.values().stream())
                            .flatMap(e -> e.values().stream())
                            .collect(toList());

            // there  must be unique indicator record per Indicator Name, Anomaly Value and Relevant Context Value,
            // so getting the record groups that have many records.
            List<List<AlertsStoredRecord.Indicator>> groupsWithMultipleIndicators =
                    collectIndicatorGroups.stream()
                            .filter(notSingleElementLists)
                            .collect(toList());

            softly.assertThat(groupsWithMultipleIndicators)
                    .as(url + "\nAlertId = " + alert.getId() + "\nAlert indicators with same name, anomaly_value and context:\n" + groupsWithMultipleIndicators)
                    .isEmpty();
        }
        softly.assertAll();
    }


    class HistoricalDataBucket {
        String key;
        String value;
        boolean anomaly;
    }

    class IndicatorResult {
        final JsonElement json;
        final String type, schema, anomalyValue, name, historicalDataType;
        final long eventsNum;

        IndicatorResult(JsonElement json) {
            this.json = json;
            type = json.getAsJsonObject().get("type").getAsString();
            schema = json.getAsJsonObject().get("schema").getAsString();
            anomalyValue = json.getAsJsonObject().get("anomalyValue").getAsString();
            name = json.getAsJsonObject().get("name").getAsString();
            if (json.getAsJsonObject().has("historicalData")) {
                historicalDataType = json.getAsJsonObject().get("historicalData").getAsJsonObject().get("type").getAsString();
            } else {
                historicalDataType = null;
            }
            eventsNum = json.getAsJsonObject().get("eventsNum").getAsLong();
        }
    }

    private JSONArray getEvents(String alertId, String indicatorId) {
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId)
                .events().url().withMaxSizeParameters();

        JSONObject indicatorsEvents = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);
        JSONArray eventsList = indicatorsEvents.getJSONArray("events");
        return eventsList;
    }

    private IndicatorResult getIndicator(String alertId, String indicatorId) {
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withNoParameters();
        RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);
        assertThat(response).as(url + "\nnull response").isNotNull();
        return new IndicatorResult(new Gson().fromJson(response.getResultBody(), JsonElement.class));
    }

    private IndicatorResult getIndicatorWithHisoricalData(String alertId, String indicatorId) {
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withExpandedParameter();
        RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);
        assertThat(response).as(url + "\nnull response").isNotNull();
        return new IndicatorResult(new Gson().fromJson(response.getResultBody(), JsonElement.class));
    }

    private List<HistoricalDataBucket> getAnomalyHistoricalDataBuckets(IndicatorResult indicator) {
        JsonObject historicalData = indicator.json.getAsJsonObject().get("historicalData").getAsJsonObject();
        JsonArray buckets = historicalData.get("buckets").getAsJsonArray();

        boolean isValueArray = buckets.getAsJsonArray().get(0).getAsJsonObject().get("value").isJsonArray();
        List<HistoricalDataBucket> historicalDataBuckets = Lists.newArrayList();

        if (isValueArray) {
            buckets.forEach(
                    bucket -> bucket.getAsJsonObject().get("value").getAsJsonArray()
                            .forEach(
                                    value -> getAllAnomalyBuckets.apply(value.getAsJsonObject()).ifPresent(historicalDataBuckets::add)));
        } else {
            buckets.forEach(
                    bucket -> getAllAnomalyBuckets.apply(bucket.getAsJsonObject()).ifPresent(historicalDataBuckets::add));
        }
        return historicalDataBuckets;
    }

    private Function<JsonObject, Optional<HistoricalDataBucket>> getAllAnomalyBuckets = obj -> {
        if (obj.has("anomaly")) {
            HistoricalDataBucket bucket = new HistoricalDataBucket();
            bucket.key = obj.get("key").getAsString();
            bucket.value = obj.get("value").getAsString();
            bucket.anomaly = obj.get("anomaly").getAsBoolean();
            return Optional.of(bucket);
        } else {
            return Optional.empty();
        }
    };

    private Function<JsonObject, Optional<String>> getAnomalyValue = obj -> {
        if (obj.has("anomaly")) {
            return Optional.of(obj.get("value").getAsString());
        } else {
            return Optional.empty();
        }
    };


    @DataProvider(name = "indicatorTypeScoreAggregation")
    public Object[][] getScoreAggregationType() {
        scoreAggregation = new Object[scoreAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(scoreAggregationIndicatorNames);
        for (int i = 0; i < asList.size(); i++) {
            String[] arr = {asList.get(i)};
            scoreAggregation[i] = arr;
        }

        return scoreAggregation;
    }

    @DataProvider(name = "indicatorTypeStaticIndicator")
    public Object[][] getStaticIndicatorType() {
        staticIndicator = new Object[staticIndicatorIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(staticIndicatorIndicatorNames);
        for (int i = 0; i < asList.size(); i++) {
            String[] arr = {asList.get(i)};
            staticIndicator[i] = arr;
        }

        return staticIndicator;
    }

    @DataProvider(name = "indicatorTypeFeatureAggregation")
    public Object[][] getFeatureAggregationType() {
        featureAggregation = new Object[featureAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(featureAggregationIndicatorNames);
        for (int i = 0; i < asList.size(); i++) {
            String[] arr = {asList.get(i)};
            featureAggregation[i] = arr;
        }

        return featureAggregation;
    }

    @DataProvider(name = "indicatorTypeDistictFeatureAggregation")
    public Object[][] getDistictFeatureAggregationType() {
        distinctFeatureAggregation = new Object[distinctFeatureAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(distinctFeatureAggregationIndicatorNames);
        for (int i = 0; i < asList.size(); i++) {
            String[] arr = {asList.get(i)};
            distinctFeatureAggregation[i] = arr;
        }

        return distinctFeatureAggregation;
    }

    @DataProvider(name = "allIndicatorsDataProvider")
    public Object[][] getAllActualIndicators() {
        allExistingIndicatorsObj = new Object[allActualIndicatorNames.size()][];
        for (int i = 0; i < allActualIndicatorNames.size(); i++) {
            String[] arr = {allActualIndicatorNames.get(i)};
            allExistingIndicatorsObj[i] = arr;
        }

        return allExistingIndicatorsObj;
    }

    @BeforeMethod
    public void beforeTest(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }


    private void initTestingData() {
        indicatorsDistinctNames = allAlerts.stream()
                .flatMap(e -> Arrays.stream(e.getIndicatorsName()))
                .distinct()
                .collect(toList());

        for (AlertsStoredRecord alert : allAlerts) {
            List<AlertsStoredRecord.Indicator> indicatorsList = alert.getIndicatorsList();
            if (indicatorsList != null && indicatorsList.size() > 0) {
                for (AlertsStoredRecord.Indicator indicator : indicatorsList) {
                    String indicatorType = indicator.getType();
                    if (indicatorType.equals("SCORE_AGGREGATION")) {
                        scoreAggregationMap.putIfAbsent(indicator.getName(), new String[]{alert.getId(), indicator.getId()});
                    } else if (indicatorType.equals("STATIC_INDICATOR")) {
                        staticIndicatorMap.putIfAbsent(indicator.getName(), new String[]{alert.getId(), indicator.getId()});
                    } else if (indicatorType.equals("FEATURE_AGGREGATION") && !indicator.getName().contains("distinct")) {
                        featureAggregationMap.putIfAbsent(indicator.getName(), new String[]{alert.getId(), indicator.getId()});
                    } else if (indicatorType.equals("FEATURE_AGGREGATION") && indicator.getName().contains("distinct")) {
                        distinctFeatureAggregationMap.putIfAbsent(indicator.getName(), new String[]{alert.getId(), indicator.getId()});
                    } else {
                        throw new RuntimeException("Missing type: " + indicatorType);
                    }
                }
            }
        }

        scoreAggregationIndicatorNames = scoreAggregationMap.keySet();
        staticIndicatorIndicatorNames = staticIndicatorMap.keySet();
        featureAggregationIndicatorNames = featureAggregationMap.keySet();
        distinctFeatureAggregationIndicatorNames = distinctFeatureAggregationMap.keySet();

        allActualIndicatorNames.addAll(scoreAggregationIndicatorNames);
        allActualIndicatorNames.addAll(staticIndicatorIndicatorNames);
        allActualIndicatorNames.addAll(featureAggregationIndicatorNames);
        allActualIndicatorNames.addAll(distinctFeatureAggregationIndicatorNames);

        allIndicatorsTypeNameSamples.putAll(scoreAggregationMap);
        allIndicatorsTypeNameSamples.putAll(staticIndicatorMap);
        allIndicatorsTypeNameSamples.putAll(featureAggregationMap);
        allIndicatorsTypeNameSamples.putAll(distinctFeatureAggregationMap);
    }

}