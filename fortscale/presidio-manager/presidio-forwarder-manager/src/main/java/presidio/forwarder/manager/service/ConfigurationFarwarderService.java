package presidio.forwarder.manager.service;

import fortscale.utils.logging.Logger;
import org.elasticsearch.index.mapper.ObjectMapper;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.OutputConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.List;


public class ConfigurationFarwarderService implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationFarwarderService.class);

    private final String FORWARDER = "forwarder";
    private final String LOCATION_TYPE = "jsonPath";
    private final String UNSUPPORTED_ERROR = "unsupportedFieldError";
    private final String UNSUPPORTED_ERROR_MESSAGE = "Unsupported Error, %s field is not supported. Allowed values: [syslog]";
    private final String MISSIG_DATA_ERROR_MESSAGE = "Missing forwarder configuration";
    private final String MISSING_PROPERTY = "missingProperty";

    private final ConfigurationServerClientService configServerClient;


    public ConfigurationFarwarderService(ConfigurationServerClientService configServerClient) {
        this.configServerClient = configServerClient;

    }

    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return validateForwarderConfiguration(presidioManagerConfiguration.getOutputConfiguration());
    }

    public boolean applyConfiguration() {
        return false;
    }


    public ValidationResults validateForwarderConfiguration(OutputConfiguration outputConfiguration) {
        ValidationResults validationResults = new ValidationResults();
        outputConfiguration.getBadParams();
        if (outputConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(FORWARDER, FORWARDER, MISSING_PROPERTY, LOCATION_TYPE, MISSIG_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            return validationResults;
        }
        if (!outputConfiguration.isStructureValid()) {
            return UnsupportedError(outputConfiguration);
        }
        return null;
    }

    private ValidationResults UnsupportedError(OutputConfiguration outputConfiguration) {
        List<String> badParams = outputConfiguration.getBadParams();
        ValidationResults validationResults = new ValidationResults();
        String location;
        for (String param : badParams) {
            location = new StringBuilder(FORWARDER).append("/").append(param).toString();
            validationResults.addError(new ConfigurationBadParamDetails(FORWARDER, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE, param)));
        }
        return validationResults;
    }
}
