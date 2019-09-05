package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import org.junit.Assert;


class DailyMetricsParamsBuilder extends PresidioUrl {

    private DailyMetricsParamsBuilder(String url) {
        super(url);
    }

    static class Builder {
        private final String METRIC_NAMES = "metricNames";
        private StringBuilder URL = new StringBuilder();

        Builder(String base_url) {
            Assert.assertNotNull(base_url);
            URL.append(base_url).append("?");
        }

        public Builder setMetricNames(String val) {
            URL.append(METRIC_NAMES).append("=").append(val).append("&");
            return this;
        }

        public DailyMetricsParamsBuilder build() {
            return new DailyMetricsParamsBuilder(URL.toString().substring(0, URL.length() - 1));
        }
    }
}
