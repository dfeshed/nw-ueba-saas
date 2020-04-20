package presidio.manager.webapp.configuration;

import fortscale.utils.rest.jsonpatch.JsonPatch;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Generated;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
@Api(value = "Configuration", description = "Configure the UEBA server, licenses and execution methods")
public interface ConfigurationApi {
    public static final String CODE_200_MESSAGE = "OK";
    public static final String CODE_201_MESSAGE = "Created";
    public static final String CODE_500_MESSAGE = "Internal Server Error";

    @ApiOperation(
            value = "Returns the current configuration",
            notes = "For security reasons, the password would not be shown",
            response = SecureConfiguration.class,
            authorizations = {@Authorization(value = "basicAuth")},
            tags = {"Configuration"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = CODE_200_MESSAGE, response = SecureConfiguration.class),
            @ApiResponse(code = 500, message = CODE_500_MESSAGE, response = SecureConfiguration.class)
    })
    @RequestMapping(value = "/configuration", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<SecureConfiguration> configurationGet();

    @ApiOperation(
            value = "Sets up the configuration",
            notes = "During the initial deployment of Presidio, a number of parameters have to be supplied in order to configure the server",
            response = ConfigurationResponse.class,
            authorizations = {@Authorization(value = "basicAuth")},
            tags = {"Configuration"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = CODE_201_MESSAGE, response = ConfigurationResponse.class),
            @ApiResponse(code = 500, message = CODE_500_MESSAGE, response = ConfigurationResponse.class)
    })
    @RequestMapping(value = "/configuration", method = RequestMethod.PUT)
    ResponseEntity<ConfigurationResponse> configurationPut(
            @ApiParam(value = "Presidio configuration", required = true) @RequestBody String body);

    @ApiOperation(
            value = "Updates the configuration",
            notes = "The new parameters will be subject to validation",
            response = ConfigurationResponse.class,
            authorizations = {@Authorization(value = "basicAuth")},
            tags = {"Configuration"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = CODE_201_MESSAGE, response = ConfigurationResponse.class),
            @ApiResponse(code = 500, message = CODE_500_MESSAGE, response = ConfigurationResponse.class)
    })
    @RequestMapping(value = "/configuration", method = RequestMethod.PATCH)
    ResponseEntity<ConfigurationResponse> configurationPatch(
            @ApiParam(value = "A JSON patch as defined by RFC 6902 (www.jsonpatch.com)", required = true) @RequestBody JsonPatch jsonPatch);

    @ApiOperation(
            value = "Uploads a keytab file",
            notes = "A keytab is a file containing pairs of Kerberos principals and encrypted keys " +
                    "(which are derived from the Kerberos password) - This file should match the machine DNS",
            response = Void.class,
            authorizations = {@Authorization(value = "basicAuth")},
            tags = {"Configuration"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = CODE_200_MESSAGE, response = Void.class),
            @ApiResponse(code = 500, message = CODE_500_MESSAGE, response = Void.class)
    })
    @RequestMapping(value = "/configuration/keytabFile", consumes = {"multipart/form-data"}, method = RequestMethod.POST)
    ResponseEntity<Void> configurationKeytabFilePost(
            @ApiParam(value = "The keytab file to upload") @RequestPart("keytabFile") MultipartFile keytabFile);
}
