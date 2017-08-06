package presidio.monitoring.sdk.api.services;


import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportNumberOfFilteredEventMetric(long value, Set tags);

    void reportNumberOfProcessedEventsMetric(long value, Set tags);


}
