package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import presidio.config.server.client.ConfigurationServerClientService;

import javax.annotation.Generated;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
@Controller
public class ConfigurationApiController implements ConfigurationApi {
    private static final Logger logger = Logger.getLogger(ConfigurationApiController.class);
    private static final ObjectMapper objectMapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
    private static final String CONFIGURATION_FILE_NAME = "application-presidio";

    private final List<String> profiles;
    private final String keytabFilePathname;
    private final String workflowsModuleName;
    private final String workflowsConfigurationPath;
    private final ConfigurationServerClientService configurationServerClientService;

    public ConfigurationApiController(
            List<String> profiles,
            String keytabFilePathname,
            String workflowsModuleName,
            String workflowsConfigurationPath,
            ConfigurationServerClientService configurationServerClientService) {

        this.profiles = profiles;
        this.keytabFilePathname = keytabFilePathname;
        this.workflowsModuleName = workflowsModuleName;
        this.workflowsConfigurationPath = workflowsConfigurationPath;
        this.configurationServerClientService = configurationServerClientService;
    }

    @Override
    public ResponseEntity<SecureConfiguration> configurationGet() {
        try {
            SecureConfiguration secureConfiguration = readConfiguration(SecureConfiguration.class);
            return new ResponseEntity<>(secureConfiguration, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while trying to read the configuration", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ConfigurationResponse> configurationPut(
            @ApiParam(value = "Presidio configuration", required = true) @RequestBody String body) {

        Configuration configuration;

        try {
            // Mapping the body to a Configuration instance also enforces validations
            configuration = objectMapper.readValue(body, Configuration.class);
        } catch (JsonMappingException e) {
            return handleConfigurationErrorAndReturnResponse("map", e);
        } catch (Exception e) {
            return handleConfigurationErrorAndReturnResponse("parse", null);
        }

        try {
            writeConfiguration(configuration);
            ConfigurationResponse configurationResponse = new ConfigurationResponse(HttpStatus.CREATED.getReasonPhrase());
            return new ResponseEntity<>(configurationResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return handleConfigurationErrorAndReturnResponse("write", null);
        }
    }

    @Override
    public ResponseEntity<ConfigurationResponse> configurationPatch(
            @ApiParam(value = "A JSON patch as defined by RFC 6902 (www.jsonpatch.com)", required = true) @RequestBody JsonPatch jsonPatch) {

        JsonNode jsonNode;

        try {
            // Get the original configuration
            jsonNode = readConfiguration(JsonNode.class);
        } catch (Exception e) {
            return handleConfigurationErrorAndReturnResponse("read", null);
        }

        try {
            // Apply the JSON patch on the original configuration
            jsonNode = jsonPatch.apply(jsonNode);
        } catch (Exception e) {
            return handleConfigurationErrorAndReturnResponse("patch", null);
        }

        return configurationPut(jsonNode.toString());
    }

    @Override
    public ResponseEntity<Void> configurationKeytabFilePost(
            @ApiParam(value = "The keytab file to upload") @RequestPart("keytabFile") MultipartFile keytabFile) {

        try {
            File file = new File(keytabFilePathname);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(keytabFile.getBytes());
            fileOutputStream.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while trying to upload the keytab file", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> T readConfiguration(Class<T> clazz) {
        // Assume that the first profile is the relevant one for configuration purposes
        return (T)configurationServerClientService.readConfiguration(clazz, CONFIGURATION_FILE_NAME, profiles.get(0)).getBody();
    }

    private void writeConfiguration(Configuration configuration) {
        updateStartTimeConfiguration(configuration);
        JsonNode jsonNode = objectMapper.valueToTree(configuration);
        configurationServerClientService.storeConfigurationFile(CONFIGURATION_FILE_NAME, jsonNode);
        updateWorkflowsConfiguration();
    }

    private void updateStartTimeConfiguration(Configuration configuration) {
        // Round down the start time to the nearest hour
        DataPipelineConfiguration dataPipeline = configuration.getDataPipeline();
        Instant startTime = dataPipeline.getStartTime();
        dataPipeline.setStartTime(startTime.truncatedTo(ChronoUnit.HOURS));
    }

    private void updateWorkflowsConfiguration() {
        try {
            for (String profile : profiles) {
                // Create the workflows configuration path if it does not exist
                new File(workflowsConfigurationPath).mkdirs();
                // Create a File instance for the workflows JSON file
                String pathname = String.format("%s/%s-%s.json", workflowsConfigurationPath, workflowsModuleName, profile);
                File file = new File(pathname);
                // Get the original configuration as a JSON string
                Object value = configurationServerClientService.readConfigurationAsJson(workflowsModuleName, profile, Object.class);
                String string = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
                // Write the original configuration to the workflows JSON file
                logger.info("Updating the workflows configuration (pathname = {})", pathname);
                FileWriter fileWriter = new FileWriter(file, false);
                fileWriter.write(string);
                fileWriter.close();
                // Set posix file permissions to the workflows JSON file
                Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
                posixFilePermissions.add(PosixFilePermission.OWNER_READ);
                posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
                posixFilePermissions.add(PosixFilePermission.GROUP_READ);
                posixFilePermissions.add(PosixFilePermission.GROUP_WRITE);
                posixFilePermissions.add(PosixFilePermission.OTHERS_READ);
                posixFilePermissions.add(PosixFilePermission.OTHERS_WRITE);
                Files.setPosixFilePermissions(Paths.get(pathname), posixFilePermissions);
            }
        } catch (Exception e) {
            logger.error("Failed to update the workflows configuration", e);
        }
    }

    private ResponseEntity<ConfigurationResponse> handleConfigurationErrorAndReturnResponse(
            String verb, JsonMappingException e) {

        String message = String.format("An error occurred while trying to %s the configuration", verb);
        ConfigurationResponse configurationResponse = new ConfigurationResponse(message, e);
        logger.error(configurationResponse.toString());
        return new ResponseEntity<>(configurationResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
