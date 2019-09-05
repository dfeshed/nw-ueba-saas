package com.rsa.netwitness.presidio.automation.rest.helper.rest;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.domain.output.DailyMetricRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DailyMetricsRestCallHelper implements IRestCallHelper {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(DailyMetricsRestCallHelper.class.getName());

    public List<DailyMetricRecord> getMetrics(PresidioUrl url) {
        JSONObject jsonObject = getRestApiResponseAsJsonObj(url);
        Type listOfMetrics = new TypeToken<ArrayList<DailyMetricRecord>>() {
        }.getType();
        assertThat(jsonObject.has("metrics"))
                .withFailMessage(url + "\n'metrics' field is missing.\nActual: " + jsonObject).isTrue();
        return new Gson().fromJson(jsonObject.getJSONArray("metrics").toString(), listOfMetrics);
    }

    public Optional<DailyMetricRecord> getActiveUserIdCountLastDay(PresidioUrl url) {
        List<DailyMetricRecord> metrics = getMetrics(url);
        return metrics.stream().filter(e -> e.metricName.equals("output-processor.active_userId_count_last_day")).findAny();
    }
}
