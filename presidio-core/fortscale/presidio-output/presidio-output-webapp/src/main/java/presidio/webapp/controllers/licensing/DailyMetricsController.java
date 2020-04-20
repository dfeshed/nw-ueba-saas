package presidio.webapp.controllers.licensing;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.webapp.model.Metric;
import presidio.webapp.model.MetricQuery;
import presidio.webapp.model.MetricsWrapper;
import presidio.webapp.service.RestMetricsService;


import javax.validation.ValidationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1,ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startOfDay, endOfDay);


        logger.debug(String.format("fetching daily metrics information for metrics: %s", metricQuery.getMetricNames().toString()));


        List<Metric> metrics=null;
        try {
            metrics = restMetricsService.getMetricsByNamesAndTime(metricQuery.getMetricNames(),timeRange);
        } catch (ValidationException e){
            logger.error(e.getMessage());
        }

        MetricsWrapper metricWrapper = new MetricsWrapper();
        metricWrapper.setMetrics(metrics);
        metricWrapper.setTotal(metrics.size());
        return new ResponseEntity(metricWrapper, HttpStatus.OK);
    }

}
