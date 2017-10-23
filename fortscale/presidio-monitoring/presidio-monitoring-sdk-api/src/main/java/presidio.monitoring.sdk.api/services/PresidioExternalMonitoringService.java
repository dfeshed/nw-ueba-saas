package presidio.monitoring.sdk.api.services;


import java.util.Date;
import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportNumberOfFilteredEventMetric(long value, Set<String> tags, Date logicTime);

    void reportNumberOfProcessedEventsMetric(long value, Set<String> tags, Date logicTime);

    void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Date logicTime);


}
