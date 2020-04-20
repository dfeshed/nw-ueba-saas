package com.rsa.netwitness.presidio.automation.rest.helper.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.domain.output.DailyMetricRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.rest.helper.builders.params.DailyMetricsParametersUrlHelper.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


public class DailyMetricsRestCallHelper implements IRestCallHelper {

    public List<DailyMetricRecord> getMetrics(PresidioUrl url) {
        JSONObject jsonObject = getRestApiResponseAsJsonObj(url);
        Type listOfMetrics = new TypeToken<ArrayList<DailyMetricRecord>>() {
        }.getType();
        assertThat(jsonObject.has("metrics"))
                .withFailMessage(url + "\n'metrics' field is missing.\nActual: " + jsonObject).isTrue();
        return new Gson().fromJson(jsonObject.getJSONArray("metrics").toString(), listOfMetrics);
    }

    public List<DailyMetricRecord> getOutputProcessorActiveUsersCountLastDay(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_ACTIVE_USER_ID_COUNT_LAST_DAY)).collect(toList());
    }

    public List<DailyMetricRecord> getOutputProcessorEventsProcessedCountDaily(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_EVENTS_PROCESSED_COUNT_DAILY)).collect(toList());
    }

    public List<DailyMetricRecord> getOutputProcessorSmartsCountLastDay(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_SMARTS_COUNT_LAST_DAY)).collect(toList());
    }

    public List<DailyMetricRecord> getOutputProcessorAlertsCountLastDay(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_ALERTS_COUNT_LAST_DAY)).collect(toList());
    }

    public List<DailyMetricRecord> getOutputProcessorSmartIndicatorsCountDaily(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_SMART_INDICATORS_COUNT_DAILY)).collect(toList());
    }

    public List<DailyMetricRecord> getOutputProcessorAlertIndicatorsCountDaily(PresidioUrl url) {
        return getMetrics(url).stream().filter(e -> e.metricName.equals(OUTPUT_PROCESSOR_ALERT_INDICATORS_COUNT_DAILY)).collect(toList());
    }

}
