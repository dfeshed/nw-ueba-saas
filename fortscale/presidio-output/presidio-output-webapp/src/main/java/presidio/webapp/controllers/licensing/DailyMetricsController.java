package presidio.webapp.controllers.licensing;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.webapp.model.Metric;
import presidio.webapp.model.MetricQuery;
import presidio.webapp.model.MetricsWrapper;
import presidio.webapp.service.RestMetricsService;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Controller
@Api(value = "ueba-daily-metrics")
public class DailyMetricsController implements DailyMetricsApi {

    private final Logger logger = Logger.getLogger(DailyMetricsController.class);

    private final RestMetricsService restMetricsService;

    public DailyMetricsController(RestMetricsService restMetricsService) {
        this.restMetricsService = restMetricsService;
    }

    @Override
    public ResponseEntity<MetricsWrapper> getMetrics(MetricQuery metricQuery) {
        //TODO- pagination
        logger.debug(String.format("fetching daily metrics information for metrics: %s", metricQuery.getMetricNames().toString()));

        //Temp implementation!
        Metric metric = new Metric("numOfActiveUsersInLastDay", 1, "java.lang.Number", Instant.now(), Instant.now());
        MetricsWrapper metricWrapper = new MetricsWrapper();
        metricWrapper.setMetrics(Arrays.asList(metric));
        metricWrapper.setTotal(1);
        return new ResponseEntity(metricWrapper, HttpStatus.OK);
    }

}
