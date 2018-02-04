package presidio.webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatchException;
import fortscale.utils.PresidioEncryptionUtils;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.webapp.model.configuration.Configuration;
import presidio.webapp.model.configuration.ConfigurationResponse;
import presidio.webapp.model.configuration.ConfigurationResponseError;
import presidio.webapp.model.configuration.SecuredConfiguration;
import presidio.webapp.service.ConfigurationManagerService;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class ConfigurationApiController implements ConfigurationApi {
    private static final Logger logger = Logger.getLogger(ConfigurationApiController.class);

    private String keytabFileLocation;
    private List<String> activeProfiles;

    private static String PRESIDO_CONFIGURATION_FILE_NAME = "application-presidio";

    public static final String PASSWORD = "password";

    private ConfigurationManagerService configurationManagerService;

    private ConfigurationServerClientService configServerClient;

    private final PresidioEncryptionUtils presidioEncryptionUtils;

    public ConfigurationApiController(ConfigurationManagerService configurationManagerService,
                                      ConfigurationServerClientService configServerClient, List<String> activeProfiles, String keytabFileLocation, PresidioEncryptionUtils presidioEncryptionUtils) {
        this.configurationManagerService = configurationManagerService;
        this.configServerClient = configServerClient;
        this.activeProfiles = activeProfiles;
        this.keytabFileLocation = keytabFileLocation;
        this.presidioEncryptionUtils = presidioEncryptionUtils;
    }

    private String getProfile() {
        return activeProfiles.get(0);
    }

    public ResponseEntity<SecuredConfiguration> configurationGet() {
        SecuredConfiguration configuration;
        try {
            configuration = readCurrentSecuredConfiguration();
            return new ResponseEntity<SecuredConfiguration>(configuration, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("an error occured while getting configuration", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private SecuredConfiguration readCurrentSecuredConfiguration() {
        // assuming the first profile is the relvant for configuration retrieval
        String profile = getProfile();
        return (SecuredConfiguration) configServerClient.readConfiguration(SecuredConfiguration.class, PRESIDO_CONFIGURATION_FILE_NAME, profile).getBody();
    }

    private JsonNode readCurrentFullConfigurationAsJsonNode() {
        // assuming the first profile is the relvant for configuration retrieval
        String profile = getProfile();
        return (JsonNode) configServerClient.readConfiguration(JsonNode.class, PRESIDO_CONFIGURATION_FILE_NAME, profile).getBody();
    }

    public ResponseEntity<Void> configurationKeytabFilePost(@ApiParam(value = "file detail") @RequestPart("keytabFile") MultipartFile keytabFile) {
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

    public ResponseEntity<ConfigurationResponse> configurationPatch(
            @ApiParam(value = "A Json patch request as defined by RFC 6902 (http://jsonpatch.com/)",
                    required = true)
            @RequestBody JsonPatch jsonPatch) {
        JsonNode jsonNode = readCurrentFullConfigurationAsJsonNode();
        JsonNode patchedJson;
        try {
            patchedJson = jsonPatch.apply(jsonNode);

            return updatedConfiguration(patchedJson);
        } catch (JsonPatchException e) {
            logger.error("got an error while performaing jsonPatch={}", jsonPatch, e);
            return new ResponseEntity<ConfigurationResponse>(HttpStatus.UNPROCESSABLE_ENTITY);

        }
    }

    public ResponseEntity<ConfigurationResponse> configurationPut(@ApiParam(value = "Presidio Configuration", required = true) @RequestBody Configuration configuration) {
        JsonNode body = ObjectMapperProvider.getInstance().getDefaultObjectMapper().valueToTree(configuration);
        return updatedConfiguration(body);
    }

    private ResponseEntity<ConfigurationResponse> updatedConfiguration(@ApiParam(value = "Presidio Configuration", required = true) @RequestBody JsonNode body) {
        ConfigurationResponse configurationResponse = new ConfigurationResponse();
        ValidationResults validationResults = configurationManagerService.validateConfiguration(configurationManagerService.presidioManagerConfigurationFactory(body));
        if (!validationResults.isValid()) {
            configurationResponse.setMessage("error message");

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

        // Round the start time value
        JsonNode dataPipelineJson = body.get(PresidioManagerConfiguration.DATA_PIPE_LINE);
        if (!dataPipelineJson.isNull()) {
            String fieldValue = dataPipelineJson.get(PresidioManagerConfiguration.START_TIME).asText();
            if (StringUtils.isNotEmpty(fieldValue)) {
                Instant startTimeValue = Instant.parse(fieldValue);
                ((ObjectNode) dataPipelineJson).put(PresidioManagerConfiguration.START_TIME, startTimeValue.truncatedTo(ChronoUnit.HOURS).toString());
            }
        }

        // Round the start time value
        JsonNode systemJson = body.get(PresidioManagerConfiguration.SYSTEM);
        if (!systemJson.isNull()) {
            String plainPassword = systemJson.get(PASSWORD).asText();
            if (StringUtils.isNotEmpty(plainPassword)) {
                try {
                    final String encryptPassword = presidioEncryptionUtils.encrypt(plainPassword);
                    ((ObjectNode) systemJson).put(PASSWORD, encryptPassword);
                } catch (Exception e) {
                    logger.error("Failed to encrypt password.");
                    return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        configurationResponse.message("Created");

        //storing configuration file into config server
        configServerClient.storeConfigurationFile(PRESIDO_CONFIGURATION_FILE_NAME, body);

        //applying configuration to all consumers
        boolean applyConfigurationResult = configurationManagerService.applyConfiguration();
        if (!applyConfigurationResult) {
            logger.error("failed to apply configuration");
            return new ResponseEntity<ConfigurationResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<ConfigurationResponse>(configurationResponse, HttpStatus.CREATED);
    }

}
