package presidio.webapp.controller.datapipeline;

import fortscale.common.SDK.PipelineState;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import presidio.webapp.model.datapipeline.CleanupCmd;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Api(value = "data-pipeline", description = "the data-pipeline API")
public interface DataPipelineApi {

    @ApiOperation(value = "Clean up the system", notes = "", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Data Pipeline", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class),
        @ApiResponse(code = 500, message = "Operation failed", response = Void.class) })
    @RequestMapping(value = "/data-pipeline/cleanup",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<Void> dataPipelineCleanupPost(@ApiParam(value = "", required = true) @RequestBody CleanupCmd body);


    @ApiOperation(value = "Start data digestion", notes = "Once data is digested, configurations cannot be changed until the system is stopped", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Data Pipeline", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class),
        @ApiResponse(code = 500, message = "Operation failed", response = Void.class) })
    @RequestMapping(value = "/data-pipeline/start",
        method = RequestMethod.POST)
    ResponseEntity<Void> dataPipelineStartPost();


    @ApiOperation(value = "Report the data pipeline state", notes = "", response = PipelineState.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Data Pipeline", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = PipelineState.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = PipelineState.class) })
    @RequestMapping(value = "/data-pipeline/status",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<PipelineState> dataPipelineStatusGet();


    @ApiOperation(value = "Stops data digestion", notes = "system will gracefully stop data digestion. Heartbeat will stop occuring and events will not be recieved", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Data Pipeline", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class),
        @ApiResponse(code = 500, message = "Operation failed", response = Void.class) })
    @RequestMapping(value = "/data-pipeline/stop",
        method = RequestMethod.POST)
    ResponseEntity<Void> dataPipelineStopPost();

}
