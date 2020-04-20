package presidio.webapp.service;

import fortscale.utils.time.TimeRange;
import io.swagger.models.auth.In;
import presidio.monitoring.records.MetricDocument;
import presidio.webapp.model.Metric;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RestMetricsService {

    List<Metric> getMetricsByNamesAndTime(Collection<String> names, TimeRange timeRange);
}
