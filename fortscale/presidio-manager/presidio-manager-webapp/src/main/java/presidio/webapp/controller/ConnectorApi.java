package presidio.webapp.controller;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import presidio.webapp.model.WebhookConfiguration;
import presidio.webapp.model.WebhookResponse;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Api(value = "connector", description = "the connector API")
public interface ConnectorApi {

    @ApiOperation(value = "Use this API to configure the web hook and get authorization id", notes = "Configure connector source", response = WebhookResponse.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Connector", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = WebhookResponse.class),
        @ApiResponse(code = 401, message = "Authentication information is missing or invalid", response = WebhookResponse.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity", response = WebhookResponse.class) })
    @RequestMapping(value = "/connector/webhook/register",
        method = RequestMethod.PUT)
    ResponseEntity<WebhookResponse> connectorWebhookRegisterPut(@ApiParam(value = "Connector configuration object", required = true) @RequestBody WebhookConfiguration body);

}
