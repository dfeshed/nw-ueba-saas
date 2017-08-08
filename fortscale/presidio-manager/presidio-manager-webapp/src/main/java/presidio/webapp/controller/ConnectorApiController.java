package presidio.webapp.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import presidio.webapp.model.WebhookConfiguration;
import presidio.webapp.model.WebhookResponse;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class ConnectorApiController implements ConnectorApi {

    public ResponseEntity<WebhookResponse> connectorWebhookRegisterPut(@ApiParam(value = "Connector configuration object" ,required=true ) @RequestBody WebhookConfiguration body) {
        throw new UnsupportedOperationException();
//        return new ResponseEntity<WebhookResponse>(HttpStatus.OK);
    }

}
