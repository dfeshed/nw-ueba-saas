package presidio.webapp.service;

import edu.emory.mathcs.backport.java.util.Collections;
import fortscale.utils.time.TimeRange;
import org.springframework.stereotype.Component;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.records.MetricDocument;
import presidio.webapp.convertors.MetricConverter;
import presidio.webapp.model.Metric;

import javax.validation.ValidationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestMetricServiceImpl implements RestMetricsService {

    private PresidioMetricPersistencyService presidioMetricPersistencyService;
    private MetricConverter metricConvertor;

    public RestMetricServiceImpl(PresidioMetricPersistencyService presidioMetricPersistencyService, MetricConverter metricConvertor) {
        this.presidioMetricPersistencyService = presidioMetricPersistencyService;
        this.metricConvertor = metricConvertor;
    }

    @Override
    public List<Metric> getMetricsByNamesAndTime(Collection<String> names, TimeRange timeRange) {
        List<MetricDocument> metricDocuments= presidioMetricPersistencyService.getMetricsByNamesAndTime(names,timeRange);
        if (metricDocuments == null){
            return Collections.emptyList();
        }

        //Convert
        List<Metric> metrics = metricDocuments
                .stream()
                .map((metricDocument) -> metricConvertor.convertFromPersistentToRest(metricDocument))
                .collect(Collectors.toList());


        //Validate
        for (Metric metric:metrics){
            if (metric.getMetricValue() == null){
                throw new ValidationException("Metric must have default value");
            }

        }
        return metrics;
    }
}
