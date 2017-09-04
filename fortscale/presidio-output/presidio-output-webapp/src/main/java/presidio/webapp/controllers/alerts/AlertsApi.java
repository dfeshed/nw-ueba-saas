package presidio.webapp.controllers.alerts;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.InlineResponse200;
import presidio.webapp.model.InlineResponse2001;
import presidio.webapp.model.Patch;

import java.math.BigDecimal;
import java.util.List;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-03T07:54:56.866Z")

@Api(value = "alerts", description = "the alerts API")
public interface AlertsApi {

    @ApiOperation(value = "Use this endpoint to get details about single alert", notes = "Alerts endpoint", response = Alert.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Alert.class)})
    @RequestMapping(value = "/alerts/{alertId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<Alert> alertsAlertIdGet(@ApiParam(value = "The UUID of the alert to return", required = true) @PathVariable("alertId") String alertId) {
        // do some magic!
        return new ResponseEntity<Alert>(HttpStatus.OK);
    }


    @ApiOperation(value = "An endpoint to get all the indicators of the specific alert", notes = "Return list of indicators", response = InlineResponse200.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of events", response = InlineResponse200.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse200>> alertsAlertIdIndicatorsGet(@ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse200>>(HttpStatus.OK);
    }


    @ApiOperation(value = "An endpoint to get all the events of the specific indicator", notes = "Return list of events", response = InlineResponse200.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of events", response = InlineResponse200.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}/events",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse200>> alertsAlertIdIndicatorsIndicatorIdEventsGet(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") Integer indicatorId,
                                                                                                @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse200>>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get details about a single indication", notes = "Example: fields=datasource,anomalyType", response = InlineResponse2001.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Supporting infromation for charts", response = InlineResponse2001.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse2001>> alertsAlertIdIndicatorsIndicatorIdGet(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") Integer indicatorId,
                                                                                           @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse2001>>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this method to update the alert feedback", notes = "", response = Alert.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Alert.class)})
    @RequestMapping(value = "/alerts/{alertId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<Alert> alertsAlertIdPatch(@ApiParam(value = "Exact match to user name", required = true) @RequestBody List<Patch> patch) {
        // do some magic!
        return new ResponseEntity<Alert>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this end point to get alerts by various filters", notes = "By passing in the appropriate options, you can search for alerts", response = AlertsWrapper.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class),
            @ApiResponse(code = 400, message = "bad input parameter", response = AlertsWrapper.class)})
    @RequestMapping(value = "/alerts",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<AlertsWrapper> searchAlerts(@ApiParam(value = "The maximum number of records to return", required = true, defaultValue = "10") @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize,
                                                       @ApiParam(value = "The number of page, start from 0", required = true) @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                                       @ApiParam(value = "Alerts with start time greate or equals to (date as long)") @RequestParam(value = "startTimeFrom", required = false) BigDecimal startTimeFrom,
                                                       @ApiParam(value = "Alerts with start time smaller or equals to (date as long)") @RequestParam(value = "startTimeTo", required = false) BigDecimal startTimeTo,
                                                       @ApiParam(value = "Alert Feedback", allowableValues = "NONE, APPROVED, REJECTED") @RequestParam(value = "feedback", required = false) String feedback,
                                                       @ApiParam(value = "Filtering alerts which have a score less than the minimum specified in minScore") @RequestParam(value = "minScore", required = false) Integer minScore,
                                                       @ApiParam(value = "Filtering alerts which have a score higher than the maximum specified in minScore") @RequestParam(value = "maxScore", required = false) Integer maxScore,
                                                       @ApiParam(value = "Comma Seperated List of tags. User should have at least one of the tags. Using '*' says \"all tags\"") @RequestParam(value = "tags", required = false) List<String> tags,
                                                       @ApiParam(value = "Comma Seperated List of alert ids (UUID)") @RequestParam(value = "ids", required = false) List<String> ids,
                                                       @ApiParam(value = "Classification of the alert (exact match)") @RequestParam(value = "classification", required = false) List<String> classification,
                                                       @ApiParam(value = "Alert includes indicators with this name (exact match)") @RequestParam(value = "indicatorsType", required = false) List<String> indicatorsType,
                                                       @ApiParam(value = "The fields to sort by. Sort directions can optionally be appended to the sort key, separated by the ‘:’ character. Sort fieds should be seperated with comma. I.E. sort=field1:ASC,field2:DESC.") @RequestParam(value = "sort", required = false) List<String> sort,
                                                       @ApiParam(value = "Alert Severity", allowableValues = "CRITICAL, HIGH, MEDIUM, LOW") @RequestParam(value = "severity", required = false) String severity) {
        // do some magic!
        return new ResponseEntity<AlertsWrapper>(HttpStatus.OK);
    }

}
