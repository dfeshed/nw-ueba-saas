package presidio.webapp.controllers.alerts;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.model.*;

import java.util.List;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T02:53:18.177Z")

@Api(value = "alerts")
public interface AlertsApi {

    @ApiOperation(value = "Use this endpoint to get details about single alert", notes = "Alerts endpoint", response = Alert.class, tags={ "alerts", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single Alert", response = Alert.class) })
    @RequestMapping(value = "/alerts/{alertId}",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                           @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue="false") Boolean expand);

    @ApiOperation(value = "Use this end point to get alerts by various filters", notes = "By passing in the appropriate options, you can search for alerts", response = AlertsWrapper.class, tags={ "alerts", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class) })
    @RequestMapping(value = "/alerts",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<AlertsWrapper> getAlerts(@ApiParam(value = "object that hold all the parameters for getting specific alerts") AlertQuery alertQuery);


    @ApiOperation(value = "Use this endpoint to get details about a single indication", notes = "Example: fields=schema", response = Indicator.class, tags={ "alerts", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Single indicator", response = Indicator.class) })
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<Indicator> getIndicatorByAlert(@ApiParam(value = "The ID of the indicator to return",required=true ) @PathVariable("indicatorId") String indicatorId,
                                                          @ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                                          @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue="false") Boolean expand);

    @ApiOperation(value = "An endpoint to get all the events of the specific indicator", notes = "Return list of events", response = EventsWrapper.class, tags={ "alerts", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of events and more general data", response = EventsWrapper.class) })
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}/events",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<EventsWrapper> getIndicatorEventsByAlert(@ApiParam(value = "The ID of the indicator to return",required=true ) @PathVariable("indicatorId") String indicatorId,
                                                                    @ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId);

    @ApiOperation(value = "An endpoint to get all the indicators of the specific alert", notes = "Return list of indicators", response = IndicatorsWrapper.class, tags={ "alerts", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of indicators and more general data", response = IndicatorsWrapper.class) })
    @RequestMapping(value = "/alerts/{alertId}/indicators",
            produces = "application/json",
            method = RequestMethod.GET)
    ResponseEntity<IndicatorsWrapper> getIndicatorsByAlert(@ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId);

    @ApiOperation(value = "Use this method to update feedback for bulk of alerts", notes = "", tags={"alerts"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class)})
    @RequestMapping(value = "/alerts/updateFeedback",
            consumes = "application/json",
            method = RequestMethod.POST)
    ResponseEntity<Void> updateAlertsFeedback(
            @ApiParam(value = "request contains alert ids to be updated and the new feedback",required=true)
            @RequestBody
                    UpdateFeedbackRequest updateFeedbackRequest);
}
