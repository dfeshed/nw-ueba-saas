package presidio.webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ValidationResponse;
import presidio.webapp.model.PatchRequest;
import presidio.webapp.model.configuration.ConfigurationResponse;
import presidio.webapp.model.configuration.ConfigurationResponseError;
import presidio.webapp.model.configuration.SecuredConfiguration;
import presidio.webapp.service.ConfigurationProcessingManager;

import java.util.ArrayList;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class ConfigurationApiController implements ConfigurationApi {

    private static String PRESIDO_CONFIGURATION_FILE_NAME = "application-presidio.json";

    private ConfigurationProcessingManager configurationProcessingManager;

    private ConfigurationServerClientService configServerClient;

    public ConfigurationApiController(ConfigurationProcessingManager configurationProcessingManager,
                                      ConfigurationServerClientService configServerClient) {
        this.configurationProcessingManager = configurationProcessingManager;
        this.configServerClient = configServerClient;
    }

    public ResponseEntity<List<SecuredConfiguration>> configurationGet() {
        //TODO implement
        return new ResponseEntity<List<SecuredConfiguration>>(HttpStatus.OK);
    }

    public ResponseEntity<Void> configurationKeytabFilePost(@ApiParam(value = "file detail") @RequestPart("file") MultipartFile keytabFile) {
        throw new UnsupportedOperationException();
//        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<SecuredConfiguration> configurationPatch(
            @ApiParam(value = "A Json patch request as defined by RFC 6902 (http://jsonpatch.com/)",
                    required = true)
            @RequestBody PatchRequest jsonPatch) {

        //TODO- implement
        return new ResponseEntity<SecuredConfiguration>(HttpStatus.OK);
    }

    public ResponseEntity<ConfigurationResponse> configurationPut(@ApiParam(value = "Presidio Configuration", required = true) @RequestBody JsonNode body) {
        ConfigurationResponse configurationResponse = new ConfigurationResponse();

        ValidationResponse validationResponse = configurationProcessingManager.validateConfiguration(configurationProcessingManager.presidioManagerConfigurationFactory(body));
        if (!validationResponse.isValid()) {

            configurationResponse.setMessage("error message");
            configurationResponse.setCode(HttpStatus.BAD_REQUEST.toString());

            List<ConfigurationResponseError> errorList = new ArrayList<ConfigurationResponseError>();
            ConfigurationResponseError error = new ConfigurationResponseError();
            error.domain(ConfigurationResponseError.DomainEnum.DATA_PIPLINE);
            error.reason(ConfigurationResponseError.ReasonEnum.INVALID_PROPERTY);
            error.message("error message");
            error.location("location");
            error.locationType(ConfigurationResponseError.LocationTypeEnum.JSON_PATH);
            errorList.add(error);
            configurationResponse.error(errorList);
            return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.BAD_REQUEST);
        }

        //storing configuration file into config server
        configServerClient.storeConfigurationFile(PRESIDO_CONFIGURATION_FILE_NAME, body);

        //applying configuration to all consumers
        configurationProcessingManager.applyConfiguration();

        return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.OK);
    }

}
