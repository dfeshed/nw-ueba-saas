package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import org.junit.Assert;


class DailyMetricsParamsBuilder extends PresidioUrl {

    // REST API - http://10.110.12.33:8882/ueba-daily-metrics?metricNames=output-processor.active_users_count_last_day

    private DailyMetricsParamsBuilder(String url) {
        super(url);
    }


    static class Builder {

        private StringBuilder URL = new StringBuilder();
        private final String METRIC_NAMES = "metricNames";

        Builder(String base_url) {
            Assert.assertNotNull(base_url);
            URL.append(base_url).append("?");
        }

        public Builder setMetricNames(String val) {
            URL.append(METRIC_NAMES).append("=").append(val).append("&");
            return this;
        }


        public DailyMetricsParamsBuilder build() {
            return new DailyMetricsParamsBuilder(URL.toString().substring(0, URL.length()-1));
        }
    }
}
