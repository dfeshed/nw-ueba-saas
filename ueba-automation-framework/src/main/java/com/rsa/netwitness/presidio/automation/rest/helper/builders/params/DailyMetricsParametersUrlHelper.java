package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public class DailyMetricsParametersUrlHelper extends PresidioUrl {

    public DailyMetricsParametersUrlHelper(String url) {
        super(url);
    }


    public PresidioUrl withNoParameters() {
        return new DailyMetricsParamsBuilder.Builder(URL).build();
    }

    public PresidioUrl withPageParameters(String name) {
        return new DailyMetricsParamsBuilder.Builder(URL)
                .setMetricNames(name)
                .build();
    }


}
