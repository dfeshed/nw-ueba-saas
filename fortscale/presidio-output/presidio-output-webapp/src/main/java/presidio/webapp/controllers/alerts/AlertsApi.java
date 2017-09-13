package presidio.webapp.controllers.alerts;


import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import presidio.webapp.model.*;

import java.util.List;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-05T14:49:57.028Z")

@Api(value = "alerts", description = "the alerts API")
public interface AlertsApi {

    @ApiOperation(value = "Use this endpoint to get details about single alert", notes = "Alerts endpoint", response = Alert.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Alert.class)})
    @RequestMapping(value = "/alerts/{alertId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return", required = true) @PathVariable("alertId") String alertId) {
        // do some magic!
        return new ResponseEntity<Alert>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this end point to get alerts by various filters", notes = "By passing in the appropriate options, you can search for alerts", response = AlertsWrapper.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of alerts and more general data", response = AlertsWrapper.class)})
    @RequestMapping(value = "/alerts",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<AlertsWrapper> getAlerts(@ApiParam(value = "object that hold all the parameters for getting specific alerts") AlertQuery alertQuery) {
        // do some magic!
        return new ResponseEntity<AlertsWrapper>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this endpoint to get details about a single indication", notes = "Example: fields=datasource,anomalyType", response = InlineResponse2001.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Supporting infromation for charts", response = InlineResponse2001.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse2001>> getIndicatorByAlert(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") Integer indicatorId,
                                                                         @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse2001>>(HttpStatus.OK);
    }


    @ApiOperation(value = "An endpoint to get all the events of the specific indicator", notes = "Return list of events", response = InlineResponse200.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of events", response = InlineResponse200.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators/{indicatorId}/events",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse200>> getIndicatorEventsByAlert(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") Integer indicatorId,
                                                                              @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse200>>(HttpStatus.OK);
    }


    @ApiOperation(value = "An endpoint to get all the indicators of the specific alert", notes = "Return list of indicators", response = InlineResponse200.class, responseContainer = "List", tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of events", response = InlineResponse200.class)})
    @RequestMapping(value = "/alerts/{alertId}/indicators",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.GET)
    default ResponseEntity<List<InlineResponse200>> getIndicatorsByAlert(@ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") Integer alertId) {
        // do some magic!
        return new ResponseEntity<List<InlineResponse200>>(HttpStatus.OK);
    }


    @ApiOperation(value = "Use this method to update the alert feedback", notes = "", response = Alert.class, tags = {"alerts",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Alert.class)})
    @RequestMapping(value = "/alerts/{alertId}",
            produces = "application/json",
            consumes = "application/json",
            method = RequestMethod.PATCH)
    default ResponseEntity<Alert> updateAlert(@ApiParam(value = "Exact match to user name", required = true) @RequestBody List<Patch> patch) {
        // do some magic!
        return new ResponseEntity<Alert>(HttpStatus.OK);
    }

}
