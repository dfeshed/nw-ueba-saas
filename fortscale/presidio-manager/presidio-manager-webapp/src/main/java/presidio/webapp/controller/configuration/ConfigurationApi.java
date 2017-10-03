package presidio.webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import presidio.webapp.model.configuration.ConfigurationResponse;
import presidio.webapp.model.configuration.SecuredConfiguration;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Api(value = "configuration", description = "the configuration API")
public interface ConfigurationApi {

    @ApiOperation(value = "Returns the current configuration", notes = "Note: For security reason password would not be shown", response = SecuredConfiguration.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Configuration", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = SecuredConfiguration.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = SecuredConfiguration.class) })
    @RequestMapping(value = "/configuration",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<SecuredConfiguration> configurationGet();


    @ApiOperation(value = "Uploads a keytab file", notes = "A keytab is a file containing pairs of Kerberos principals and encrypted keys (which are derived from the Kerberos password) - this file should match the machine DNS.", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Configuration", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity", response = Void.class) })
    @RequestMapping(value = "/configuration/keytabFile",
        consumes = { "multipart/form-data" },
        method = RequestMethod.POST)
    ResponseEntity<Void> configurationKeytabFilePost(@ApiParam(value = "file detail") @RequestPart("keytabFile") MultipartFile keytabFile);


    @ApiOperation(value = "Use this method to update the configuration", notes = "", response = SecuredConfiguration.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Configuration", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response", response = SecuredConfiguration.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = SecuredConfiguration.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity, The configuration is invalid", response = SecuredConfiguration.class) })
    @RequestMapping(value = "/configuration",
        method = RequestMethod.PATCH)
    ResponseEntity<ConfigurationResponse> configurationPatch(@ApiParam(value = "A Json patch request as defined by RFC 6902 (http://jsonpatch.com/)", required = true) @RequestBody JsonPatch jsonPatch);


    @ApiOperation(value = "Configuration setup", notes = "During the initial deployment and setup of the Presidio server, a number of parameters have to be supplied to configure the server. ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Configuration", })
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = Void.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = Void.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity, The configuration is invalid", response = Void.class) })
    @RequestMapping(value = "/configuration",
        method = RequestMethod.PUT)
    ResponseEntity<ConfigurationResponse> configurationPut(@ApiParam(value = "Presidio Configuration", required = true) @RequestBody JsonNode body);

}
