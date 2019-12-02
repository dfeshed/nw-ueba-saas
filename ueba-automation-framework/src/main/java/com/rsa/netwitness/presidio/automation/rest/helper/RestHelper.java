package com.rsa.netwitness.presidio.automation.rest.helper;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.AlertsUrlBuilder;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.DailyMetricsUrlBuilder;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.EntitiesUrlBuilder;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.url.EntitiesWatchListBuilder;

public class RestHelper {

    public AlertsUrlBuilder alerts() {
        return new AlertsUrlBuilder("/alerts");
    }

    public EntitiesUrlBuilder entities() {
        return new EntitiesUrlBuilder("/entities");
    }

    public DailyMetricsUrlBuilder dailyMetrics() {
        return new DailyMetricsUrlBuilder("/ueba-daily-metrics");
    }

    public EntitiesWatchListBuilder entitiesWatchList() {
        return new EntitiesWatchListBuilder("/entities");
    }
}
