package presidio.webapp.controller.control;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import presidio.webapp.model.PresidioVersion;
import presidio.webapp.model.UpgradeState;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Api(value = "Control", description = "Control the product (start and stop the application, upgrade etc.) ")
public interface ControlApi {

    @ApiOperation(value = "Deploys upgrade file", notes = "", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Control", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class) })
    @RequestMapping(value = "/control/upgrade/start",
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<Void> controlUpgradeStartPost();


    @ApiOperation(value = "Report the upgrade state", notes = "", response = UpgradeState.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Control", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = UpgradeState.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = UpgradeState.class) })
    @RequestMapping(value = "/control/upgrade/status",
        consumes = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<UpgradeState> controlUpgradeStatusGet();


    @ApiOperation(value = "Get presidio version", notes = "", response = PresidioVersion.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Control", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = PresidioVersion.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = PresidioVersion.class) })
    @RequestMapping(value = "/control/version",
        method = RequestMethod.GET)
    ResponseEntity<PresidioVersion> controlVersionGet();

}
