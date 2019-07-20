package com.rsa.netwitness.presidio.automation.test.rest;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rsa.netwitness.presidio.automation.common.rest.RestApiResponse;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.helpers.rest.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.helpers.rest.RestHelper;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils;
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
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.static_content.AlertsMandatoryIndicators.ALERTS_TEST_MANDATORY_INDICATOR_NAMES;
import static org.assertj.core.api.Assertions.assertThat;

public class AlertsIndicatorsTests extends AbstractTestNGSpringContextTests {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
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
    private Map<String, String[]> indicatorsTypeAndNameSamples = new HashMap<>();

    @BeforeClass
    public void preconditionCheckAndPrepare() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        allAlerts = restHelper.alerts().request().getAlerts(url);
        assertThat(allAlerts)
                .as(url + "\nAlerts list is empty or unable to get response from the output.")
                .isNotNull()
                .isNotEmpty();

        initTestingData();
    }

    @Test
    public void all_mandatory_indicators_from_static_list_are_present() {
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        assertThat(allActualIndicatorNames)
                .as(url + "Indicators are missing in alerts")
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrderElementsOf(ALERTS_TEST_MANDATORY_INDICATOR_NAMES);
    }

    @Test
    public void alerts_count_result_filtered_by_indicator_name_is_correct() {
        for (String indicatorName: indicatorsDistinctNames) {
            ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndIndicatorNameParameters(indicatorName);
            List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

            // alertIds from getAllAlerts REST call.
            long countOfAllAlertsHaveGivenIndicatorName = allAlerts.stream()
                    .map(e -> Arrays.stream(e.getIndicatorsName()).collect(Collectors.toList()))
                    .filter(e -> e.contains(indicatorName))
                    .count();

            long countOfAlertsFilteredByIndicatorHaveGivenIndicatorName = alerts.stream()
                    .map(e -> Arrays.stream(e.getIndicatorsName()).collect(Collectors.toList()))
                    .filter(e -> e.contains(indicatorName))
                    .count();

            assertThat(alerts.size())
                    .as(url+"\nAlerts count mismatch")
                    .isEqualTo(countOfAllAlertsHaveGivenIndicatorName)
                    .isEqualTo(countOfAlertsFilteredByIndicatorHaveGivenIndicatorName);
        }
    }



    @Test
    public void alert_indicators_event_time_range_is_within_the_alert_time_range() {
        for(AlertsStoredRecord alert : allAlerts){
            List<AlertsStoredRecord.Indicator> indicators = alert.getIndicatorsList();

            long alertStartDate = Long.parseLong(alert.getStartDate()) / 1000;
            long alertEndDate = Long.parseLong(alert.getEndDate()) / 1000;

            for(AlertsStoredRecord.Indicator singleIndicator : indicators) {

                ParametersUrlBuilder url = restHelper.alerts().withId(alert.getId())
                        .indicators().withId(singleIndicator.getId())
                        .events().url().withMaxSizeParameters();

                RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);
                assertThat(response)
                        .as(url+"\nnull response")
                        .isNotNull();
                JSONObject indicatorEvents =  new JSONObject(response.getResultBody());

                try {
                    JSONArray indicatorsEventsList = indicatorEvents.getJSONArray("events");
                    assertThat(indicatorsEventsList.length())
                            .as(url+"\nEmpty response body")
                            .isGreaterThan(0);


                    for(int i=0 ; i<indicatorsEventsList.length() ; i++){
                        long time = Long.parseLong(indicatorsEventsList.getJSONObject(i).getJSONObject("eventDate").get("epochSecond").toString());
                        String msg = url +
                                "\nindicator's event time is not within the alert time range.\n" +
                                "AlertId = " + alert.getId() + "\n" +
                                "UserId = " + alert.getEntityDocumentId() + "\n" +
                                "IndicatorId = " + singleIndicator.getId() + "\n" +
                                "EventId = " + indicatorsEventsList.getJSONObject(i).getString("id");
                        Assert.assertTrue(time >= alertStartDate && time <= alertEndDate, msg);
                    }

                } catch (JSONException e) {
                    Assert.fail(e.getMessage());
                }
            }
        }
    }

    @Test
    public void indicator_start_date_and_the_first_indicator_event_date_should_be_equal() {
        for(AlertsStoredRecord alert : allAlerts) {
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

                if(!singleIndicator.getName().startsWith("high_number")){
                    RestApiResponse indicatorResponse = restHelper.alerts().request().getRestApiResponse(indicatorUrl);
                    assertThat(indicatorResponse)
                            .as(indicatorUrl+"\nnull response")
                            .isNotNull();

                    RestApiResponse indicatorEventsResponse = restHelper.alerts().request().getRestApiResponse(indicatorEventsUrl);
                    assertThat(indicatorResponse)
                            .as(indicatorEventsUrl+"\nnull response")
                            .isNotNull();

                    JSONObject indicator =  new JSONObject(indicatorResponse.getResultBody());
                    JSONObject indicatorEvents =  new JSONObject(indicatorEventsResponse.getResultBody());

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
    public void anomaly_true_indicator_count_should_be_correct_in_historical_data(String indicator){
        String alertId = indicatorsTypeAndNameSamples.get(indicator)[0];
        String indicatorId = indicatorsTypeAndNameSamples.get(indicator)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withExpandedParameter();

        try{
            List<Boolean> results = getAnomalyValuesResult(url);
            String anomalyType = getAnomalyType(url);

            if (anomalyType.equals("TimeAggregation")) {
                assertThat(results)
                        .describedAs(url.toString())
                        .hasSizeGreaterThanOrEqualTo(1)
                        .containsOnly(true);
            } else {
                assertThat(results)
                        .describedAs(url.toString())
                        .hasSize(1)
                        .containsOnly(true);
            }
        } catch (JSONException e) {
            Assert.fail(url+ "\nCannot get the requested information from json object. \n" + e.getMessage());
        }
    }

    @Test (dataProvider = "indicatorTypeStaticIndicator")
    public void validateEventCountOfStaticIndicator(String indicator) {
        String alertId = staticIndicatorMap.get(indicator)[0];
        String indicatorId = staticIndicatorMap.get(indicator)[1];
        ParametersUrlBuilder url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).url().withNoParameters();
        RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);

        assertThat(response).as(url+"\nnull response").isNotNull();
        JSONObject indicatorData =  new JSONObject(response.getResultBody());

        try {
            String indicatorName = indicatorData.getString("name");
            int eventNum = indicatorData.getInt("eventsNum");

            url = restHelper.alerts().withId(alertId).indicators().withId(indicatorId).events().url().withMaxSizeParameters();
            response = restHelper.alerts().request().getRestApiResponse(url);
            JSONObject indicatorsEvents =  new JSONObject(response.getResultBody());

            JSONArray eventsList = indicatorsEvents.getJSONArray("events");
            assertThat(response).as(url+"\nnull response").isNotNull();

            assertThat(eventNum)
                    .as(url+"\nAlerts page indicators count is different from the events page. indicatorName = " +indicatorName)
                    .isEqualTo(eventsList.length())
                    .isGreaterThan(0);

            // TODO: Split this part to another different test.
            for(int i=0 ; i<eventsList.length() ; i++){
                if(indicator.equals("admin_changed_his_own_password")){
                    Assert.assertTrue(eventsList.getJSONObject(i).getBoolean("isUserAdmin"), "User should be Admin for indicator '" + indicator);
                    Assert.assertEquals("user_password_changed", eventsList.getJSONObject(i).getString("operationType").toLowerCase(), "eventType not matched to indicator name. indicator: " + indicatorName);
                } else {
                    Assert.assertEquals(indicatorName, eventsList.getJSONObject(i).getString("operationType").toLowerCase(), "eventType not matched to indicator name. indicator: " + indicatorName);
                }

            }

        } catch (JSONException e) {
            Assert.fail(url+"\n"+e.getMessage());
        }
    }


























    private String getAnomalyType(ParametersUrlBuilder url) {
         return getAnomalyHistoricalData(url).get("type").getAsString();
    }

    private List<Boolean> getAnomalyValuesResult(ParametersUrlBuilder url) {
        JsonArray buckets = getAnomalyHistoricalData(url).get("buckets").getAsJsonArray();
        List<Boolean> result = Lists.newArrayList();

        /** array value is pie chart **/
        boolean valueIsArray = buckets.getAsJsonArray().get(0).getAsJsonObject().get("value").isJsonArray();
        if (valueIsArray) {
            buckets.forEach(
                    bucket -> bucket.getAsJsonObject().get("value").getAsJsonArray().
                            forEach(
                                    value -> result.add(getAnomalyValue.apply(value.getAsJsonObject()).orElse(false))));
        } else {
            buckets.forEach(e -> result.add(getAnomalyValue.apply(e.getAsJsonObject()).orElse(false)));
        }
        return result.stream().filter(e -> e.equals(true)).collect(Collectors.toList());
    }

    private Function<JsonObject, Optional<Boolean>> getAnomalyValue = obj -> {
        if (obj.has("anomaly")) {
            return Optional.of(obj.get("anomaly").getAsBoolean());
        } else {
            return Optional.empty();
        }
    };

    private JsonObject getAnomalyHistoricalData(ParametersUrlBuilder url) {
        RestApiResponse response = restHelper.alerts().request().getRestApiResponse(url);
        assertThat(response).as(url+"\nnull response").isNotNull();

        JsonElement json = new Gson().fromJson(response.getResultBody(), JsonElement.class);
        return json.getAsJsonObject().get("historicalData").getAsJsonObject();
    }















    @DataProvider(name = "indicatorTypeScoreAggregation")
    public Object[][] getScoreAggregationType() {
        scoreAggregation = new Object[scoreAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(scoreAggregationIndicatorNames);
        for(int i = 0; i < asList.size() ; i++) {
            String[] arr = {asList.get(i)};
            scoreAggregation[i] = arr;
        }

        return scoreAggregation;
    }

    @DataProvider(name = "indicatorTypeStaticIndicator")
    public Object[][] getStaticIndicatorType() {
        staticIndicator = new Object[staticIndicatorIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(staticIndicatorIndicatorNames);
        for(int i = 0 ; i < asList.size() ; i++) {
            String[] arr = {asList.get(i)};
            staticIndicator[i] = arr;
        }

        return staticIndicator;
    }

    @DataProvider(name = "indicatorTypeFeatureAggregation")
    public Object[][] getFeatureAggregationType() {
        featureAggregation = new Object[featureAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(featureAggregationIndicatorNames);
        for(int i = 0; i < asList.size() ; i++) {
            String[] arr = {asList.get(i)};
            featureAggregation[i] = arr;
        }

        return featureAggregation;
    }

    @DataProvider(name = "indicatorTypeDistictFeatureAggregation")
    public Object[][] getDistictFeatureAggregationType() {
        distinctFeatureAggregation = new Object[distinctFeatureAggregationIndicatorNames.size()][];
        List<String> asList = Lists.newArrayList(distinctFeatureAggregationIndicatorNames);
        for(int i = 0; i < asList.size() ; i++) {
            String[] arr = {asList.get(i)};
            distinctFeatureAggregation[i] = arr;
        }

        return distinctFeatureAggregation;
    }

    @DataProvider(name = "allIndicatorsDataProvider")
    public Object[][] getAllActualIndicators() {
        allExistingIndicatorsObj = new Object[allActualIndicatorNames.size()][];
        for(int i = 0; i < allActualIndicatorNames.size() ; i++){
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
                .collect(Collectors.toList());

        for(AlertsStoredRecord alert : allAlerts){
            List<AlertsStoredRecord.Indicator> indicatorsList = alert.getIndicatorsList();
            if(indicatorsList != null && indicatorsList.size() > 0){
                for(AlertsStoredRecord.Indicator indicator : indicatorsList){
                    String indicatorType = indicator.getType();
                    if(indicatorType.equals("SCORE_AGGREGATION")){
                        scoreAggregationMap.putIfAbsent(indicator.getName(), new String[] {alert.getId(), indicator.getId()});
                    }
                    else if(indicatorType.equals("STATIC_INDICATOR")){
                        staticIndicatorMap.putIfAbsent(indicator.getName(), new String[] {alert.getId(), indicator.getId()});
                    }
                    else if(indicatorType.equals("FEATURE_AGGREGATION") && !indicator.getName().contains("distinct")){
                        featureAggregationMap.putIfAbsent(indicator.getName(), new String[] {alert.getId(), indicator.getId()});
                    }
                    else if(indicatorType.equals("FEATURE_AGGREGATION") && !distinctFeatureAggregationIndicatorNames.contains(indicator.getName()) && indicator.getName().contains("distinct")){
                        distinctFeatureAggregationMap.putIfAbsent(indicator.getName(), new String[] {alert.getId(), indicator.getId()});
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

        indicatorsTypeAndNameSamples.putAll(scoreAggregationMap);
        indicatorsTypeAndNameSamples.putAll(staticIndicatorMap);
        indicatorsTypeAndNameSamples.putAll(featureAggregationMap);
        indicatorsTypeAndNameSamples.putAll(distinctFeatureAggregationMap);
    }

}