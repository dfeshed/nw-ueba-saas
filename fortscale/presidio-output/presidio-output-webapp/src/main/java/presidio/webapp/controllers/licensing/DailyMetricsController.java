package presidio.webapp.controllers.licensing;

import fortscale.utils.logging.Logger;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.webapp.model.Metric;
import presidio.webapp.model.MetricQuery;
import presidio.webapp.service.RestMetricsService;

import java.time.Instant;

@Controller
@Api(value = "ueba-daily-metrics")
public class DailyMetricsController implements DailyMetricsApi {

    private final Logger logger = Logger.getLogger(DailyMetricsController.class);

    private final RestMetricsService restMetricsService;

    public DailyMetricsController(RestMetricsService restMetricsService) {
        this.restMetricsService = restMetricsService;
    }

    @Override
    public ResponseEntity<Metric> getMetrics(MetricQuery metricQuery) {
        //TODO- pagination
        logger.debug(String.format("fetching daily metrics information for metrics: %s", metricQuery.getMetricNames().toString()));
        return new ResponseEntity(new Metric("numOfActiveUsersInLastDay", 1, "java.lang.Number", Instant.now(), Instant.now()), HttpStatus.OK);
    }

}
