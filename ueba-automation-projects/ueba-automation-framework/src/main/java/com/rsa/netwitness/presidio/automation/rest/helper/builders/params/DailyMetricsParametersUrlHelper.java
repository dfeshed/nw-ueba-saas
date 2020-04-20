package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public class DailyMetricsParametersUrlHelper extends PresidioUrl {
    public static final String OUTPUT_PROCESSOR_ACTIVE_USER_ID_COUNT_LAST_DAY = "output-processor.active_userId_count_last_day";
    public static final String OUTPUT_PROCESSOR_EVENTS_PROCESSED_COUNT_DAILY = "output-processor.events_processed_count_daily";
    public static final String OUTPUT_PROCESSOR_SMARTS_COUNT_LAST_DAY = "output-processor.smarts_count_last_day";
    public static final String OUTPUT_PROCESSOR_ALERTS_COUNT_LAST_DAY = "output-processor.alerts_count_last_day";
    public static final String OUTPUT_PROCESSOR_SMART_INDICATORS_COUNT_DAILY = "output-processor.smart_indicators_count_daily";
    public static final String OUTPUT_PROCESSOR_ALERT_INDICATORS_COUNT_DAILY = "output-processor.alert_indicators_count_daily";

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

    public PresidioUrl withOutputProcessorActiveUserIdCountLastDay() {
        return withPageParameters(OUTPUT_PROCESSOR_ACTIVE_USER_ID_COUNT_LAST_DAY);
    }

    public PresidioUrl withOutputProcessorEventsProcessedCountDaily() {
        return withPageParameters(OUTPUT_PROCESSOR_EVENTS_PROCESSED_COUNT_DAILY);
    }

    public PresidioUrl withOutputProcessorSmartsCountLastDay() {
        return withPageParameters(OUTPUT_PROCESSOR_SMARTS_COUNT_LAST_DAY);
    }

    public PresidioUrl withOutputProcessorAlertsCountLastDay() {
        return withPageParameters(OUTPUT_PROCESSOR_ALERTS_COUNT_LAST_DAY);
    }

    public PresidioUrl withOutputProcessorSmartIndicatorsCountDaily() {
        return withPageParameters(OUTPUT_PROCESSOR_SMART_INDICATORS_COUNT_DAILY);
    }

    public PresidioUrl withOutputProcessorAlertIndicatorsCountDaily() {
        return withPageParameters(OUTPUT_PROCESSOR_ALERT_INDICATORS_COUNT_DAILY);
    }
}
