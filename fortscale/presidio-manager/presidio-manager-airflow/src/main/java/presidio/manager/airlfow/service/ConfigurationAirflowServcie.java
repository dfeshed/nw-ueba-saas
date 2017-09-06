package presidio.manager.airlfow.service;


import org.apache.commons.lang3.ArrayUtils;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigurationAirflowServcie implements ConfigurationProcessingService {


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
    private final String START_TIME_UNVALID_MESSAGE = "dataPipeline startTime format is invalid. Allowed format is: yyyy-MM-dd HH:mm:ss";
    private final String SCHEMA_UNVALID_MESSAGE = "dataPipeline schema %s field is not supported. Allowed values:%s";
    private final String START_TIME_FUTRE_DATE_MESSAGE = "dataPipeline startTime date is in the future.";
    private final String FILE = "FILE";
    private final String ACTIVE_DIRECTORY = "ACTIVE_DIRECTORY";
    private final String AUTHENTICATION = "AUTHENTICATION";
    private final List<String> schemas = new ArrayList<String>(Arrays.asList(FILE, ACTIVE_DIRECTORY, AUTHENTICATION));
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean applyConfiguration() {
        return true;
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
            if (LocalDateTime.parse(startTime, dateTimeFormatter).isAfter(LocalDateTime.now())) {
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
