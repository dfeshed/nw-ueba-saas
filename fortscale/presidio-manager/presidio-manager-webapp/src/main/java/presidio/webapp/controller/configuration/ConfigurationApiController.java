package presidio.webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.ValidationResults;
import presidio.webapp.model.PatchRequest;
import presidio.webapp.model.configuration.ConfigurationResponse;
import presidio.webapp.model.configuration.ConfigurationResponseError;
import presidio.webapp.model.configuration.SecuredConfiguration;
import presidio.webapp.service.ConfigurationManagerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class ConfigurationApiController implements ConfigurationApi {

    @Value("${keytab.file.path}")
    private String keytabFileLocation;

    private static String PRESIDO_CONFIGURATION_FILE_NAME = "application-presidio";

    private ConfigurationManagerService configurationManagerService;

    private ConfigurationServerClientService configServerClient;

    public ConfigurationApiController(ConfigurationManagerService configurationManagerService,
                                      ConfigurationServerClientService configServerClient) {
        this.configurationManagerService = configurationManagerService;
        this.configServerClient = configServerClient;
    }

    public ResponseEntity<List<SecuredConfiguration>> configurationGet() {
        //TODO implement
        return new ResponseEntity<List<SecuredConfiguration>>(HttpStatus.OK);
    }

    public ResponseEntity<Void> configurationKeytabFilePost(@ApiParam(value = "file detail") @RequestPart("file") MultipartFile keytabFile) {
        File convFile = new File(keytabFileLocation);
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(keytabFile.getBytes());
            fos.close();
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }



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

        ValidationResults validationResults = configurationManagerService.validateConfiguration(configurationManagerService.presidioManagerConfigurationFactory(body));
        if (!validationResults.isValid()) {
            configurationResponse.setMessage("error message");
            configurationResponse.setCode(HttpStatus.BAD_REQUEST.toString());

            List<ConfigurationResponseError> errorList = new ArrayList<ConfigurationResponseError>();
            ConfigurationResponseError error;
            for (ConfigurationBadParamDetails cbpd : validationResults.getErrorsList()) {
                error = new ConfigurationResponseError();
                error.domain(ConfigurationResponseError.DomainEnum.fromValue(cbpd.getDomain()));
                error.reason(ConfigurationResponseError.ReasonEnum.fromValue(cbpd.getReason()));
                error.message(cbpd.getErrorMessage());
                error.location(cbpd.getLocation());
                error.locationType(ConfigurationResponseError.LocationTypeEnum.fromValue(cbpd.getLocationType()));
                errorList.add(error);
            }
            configurationResponse.error(errorList);
            return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.BAD_REQUEST);
        }
        
        configurationResponse.code("201");
        configurationResponse.message("Created");

            //storing configuration file into config server
            configServerClient.storeConfigurationFile(PRESIDO_CONFIGURATION_FILE_NAME, body);

            //applying configuration to all consumers
            configurationManagerService.applyConfiguration();

        return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.OK);
    }

}
