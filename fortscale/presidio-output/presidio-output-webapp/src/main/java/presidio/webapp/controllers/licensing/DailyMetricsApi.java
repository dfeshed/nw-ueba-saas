package presidio.webapp.controllers.licensing;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import presidio.webapp.model.Metric;
import presidio.webapp.model.MetricQuery;
import presidio.webapp.model.MetricsWrapper;

public interface DailyMetricsApi {

    @ApiOperation(value = "Use this end point to get presidio metrics by various filters", notes = "UEBA Metrics endpoint", response = Metric.class, tags={ "metrics", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Metrcis list", response = Metric.class) })
    @RequestMapping(value = "/ueba-daily-metrics",
            produces = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<MetricsWrapper> getMetrics(
            @ApiParam(value = "metrics query", required = true) MetricQuery metricQuery) {
        // do some magic!
        return new ResponseEntity<MetricsWrapper>(HttpStatus.OK);
    }
}
