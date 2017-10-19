package presidio.manager.airlfow.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;


public class ConfigurationAirflowServcie implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationAirflowServcie.class);

    private final String DATA_PIPE_LINE = "dataPipeline";
    private final String UNSUPPORTED_ERROR = "unsupportedFieldError";
    private final String MISSING_PROPERTY = "missingProperty";
    private final String INVALID_PROPERTY = "invalidProperty";
    private final String LOCATION_TYPE = "jsonPath";
    private final String LOCATION_TYPE_SCHEMAS = "dataPipeline/schemas";
    private final String LOCATION_TYPE_START_TIME = "dataPipeline/startTime";
    private final String UNSUPPORTED_ERROR_MESSAGE = "Unsupported Error, %s field is not supported. Allowed values: [schemas,startTime]";
    private final String MISSIG_DATA_ERROR_MESSAGE = "Missing dataPipeline configuration";
    private final String MISSIG_SCHEMAS_ERROR_MESSAGE = "Missing schemas configuration";
    private final String MISSIG_START_TIME_ERROR_MESSAGE = "Missing dataPipeline startTime configuration";
    private final String START_TIME_UNVALID_MESSAGE = "dataPipeline startTime format is invalid. Allowed format is: yyyy-MM-ddTHH:mm:ssZ";
    private final String SCHEMA_UNVALID_MESSAGE = "dataPipeline schema %s field is not supported. Allowed values:%s";
    private final String START_TIME_FUTRE_DATE_MESSAGE = "dataPipeline startTime date is in the future.";
    private final String FILE = "FILE";
    private final String ACTIVE_DIRECTORY = "ACTIVE_DIRECTORY";
    private final String AUTHENTICATION = "AUTHENTICATION";
    private final List<String> schemas = new ArrayList<String>(Arrays.asList(FILE, ACTIVE_DIRECTORY, AUTHENTICATION));
    private final ConfigurationServerClientService configServerClient;
    private final String moduleName;
    private final List<String> activeProfiles;
    private final String configurationFolderPath;
    private final ObjectMapper mapper;

    public ConfigurationAirflowServcie(ConfigurationServerClientService configServerClient, String moduleName, List<String> activeProfiles, String configurationFolderPath) {
        this.configServerClient = configServerClient;
        this.moduleName = moduleName;
        this.activeProfiles = activeProfiles;
        this.configurationFolderPath = configurationFolderPath;
        this.mapper = new ObjectMapper();

    }

    @Override
    public boolean applyConfiguration() {
        try {
            for (String profile: activeProfiles) {

                Object response = configServerClient.readConfigurationAsJson(moduleName, profile, Object.class);
                String newConfJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                File dir = new File(configurationFolderPath);
                dir.mkdirs();

                String filePath = String.format("%s/%s-%s.json", configurationFolderPath, moduleName, profile);
                logger.info("applying configuration path={}",filePath);
                File file = new File(filePath);
                Set<PosixFilePermission> perms = new HashSet<>();

                //add owners permission
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                //add group permissions
                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_WRITE);
                //add others permissions
                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_WRITE);

                FileWriter fileWriter = new FileWriter(file,false);
                fileWriter.write(newConfJson);
                fileWriter.close();
                //Files.setPosixFilePermissions(Paths.get(filePath),perms);
            }
            return true;
        } catch (Exception e) {
            String msg = "failed to apply configuration";
            logger.error(msg,e);
            return false;
        }
    }


    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        DataPipeLineConfiguration dataPipeLineConfiguration = presidioManagerConfiguration.getDataPipeLineConfiguration();
        ValidationResults validationResults = new ValidationResults();
        if (dataPipeLineConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(DATA_PIPE_LINE, DATA_PIPE_LINE, MISSING_PROPERTY, LOCATION_TYPE, MISSIG_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            return validationResults;
        }
        if (!dataPipeLineConfiguration.isStracturValid()) {
            return UnsupportedError(dataPipeLineConfiguration);
        }

        String[] schemas = dataPipeLineConfiguration.getSchemas();
        if (ArrayUtils.isEmpty(schemas)) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(DATA_PIPE_LINE, LOCATION_TYPE_SCHEMAS, MISSING_PROPERTY, LOCATION_TYPE, MISSIG_SCHEMAS_ERROR_MESSAGE);
            validationResults.addError(error);
        } else {
            validationResults.addErrors(schemasValidation(schemas));
        }

        String startTime = dataPipeLineConfiguration.getStartTime();
        if (startTime == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(DATA_PIPE_LINE, LOCATION_TYPE_START_TIME, MISSING_PROPERTY, LOCATION_TYPE, MISSIG_START_TIME_ERROR_MESSAGE);
            validationResults.addError(error);
        } else {
            validationResults.addErrors(startTimeValidation(startTime));
        }

        return validationResults;
    }

    private ValidationResults UnsupportedError(DataPipeLineConfiguration dataPipeLineConfiguration) {
        List<String> badParams = dataPipeLineConfiguration.getBadParams();
        ValidationResults validationResults = new ValidationResults();
        String location;
        for (String param : badParams) {
            location = new StringBuilder(DATA_PIPE_LINE).append("/").append(param).toString();
            validationResults.addError(new ConfigurationBadParamDetails(DATA_PIPE_LINE, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE, param)));
        }
        return validationResults;
    }

    private List<ConfigurationBadParamDetails> schemasValidation(String[] schemas) {
        List<ConfigurationBadParamDetails> errorsList = new ArrayList<>();
        for (String schema : schemas) {
            if (!this.schemas.contains(schema)) {
                errorsList.add(new ConfigurationBadParamDetails(DATA_PIPE_LINE, LOCATION_TYPE_SCHEMAS, INVALID_PROPERTY, LOCATION_TYPE, String.format(SCHEMA_UNVALID_MESSAGE, schema, this.schemas.toString())));
            }
        }
        if (errorsList.size() == 0)
            errorsList = null;
        return errorsList;
    }

    private List<ConfigurationBadParamDetails> startTimeValidation(String startTime) {

        List<ConfigurationBadParamDetails> errorsList = null;
        try {
            if (Instant.parse(startTime).isAfter(Instant.now())) {
                errorsList = new ArrayList<>();
                errorsList.add(new ConfigurationBadParamDetails(DATA_PIPE_LINE, LOCATION_TYPE_START_TIME, INVALID_PROPERTY, LOCATION_TYPE, START_TIME_FUTRE_DATE_MESSAGE));
            }
        } catch (DateTimeParseException ex) {
            errorsList = new ArrayList<>();
            errorsList.add(new ConfigurationBadParamDetails(DATA_PIPE_LINE, LOCATION_TYPE_START_TIME, INVALID_PROPERTY, LOCATION_TYPE, START_TIME_UNVALID_MESSAGE));
            return errorsList;
        }
        return errorsList;
    }
}
