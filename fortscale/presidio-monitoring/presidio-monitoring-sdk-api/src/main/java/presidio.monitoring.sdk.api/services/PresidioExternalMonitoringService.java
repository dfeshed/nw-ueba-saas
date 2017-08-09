package presidio.monitoring.sdk.api.services;


import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportNumberOfFilteredEventMetric(long value, Set<String> tags);

    void reportNumberOfProcessedEventsMetric(long value, Set<String> tags);

    void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType);


}
