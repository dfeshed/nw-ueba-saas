package presidio.forwarder.manager.service;

import fortscale.utils.logging.Logger;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.OutputConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.List;


public class ConfigurationForwarderService implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationForwarderService.class);

    private final String FORWARDER = "forwarder";
    private final String LOCATION_TYPE = "jsonPath";
    private final String UNSUPPORTED_ERROR = "unsupportedFieldError";
    private final String UNSUPPORTED_ERROR_MESSAGE = "Unsupported Error, %s field is not supported. Allowed values: [syslog]";
    private final String MISSING_DATA_ERROR_MESSAGE = "Missing forwarder configuration";
    private final String MISSING_PROPERTY = "missingProperty";


    public ConfigurationForwarderService() {

    }

    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        logger.debug("Validating forwarder configuration");
        return validateForwarderConfiguration(presidioManagerConfiguration.getOutputConfiguration());
    }

    public boolean applyConfiguration() {
        logger.debug("No applying is needed");
        return true;
    }


    public ValidationResults validateForwarderConfiguration(OutputConfiguration outputConfiguration) {
        ValidationResults validationResults = new ValidationResults();
        if (outputConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(FORWARDER, FORWARDER, MISSING_PROPERTY, LOCATION_TYPE, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            logger.debug("Missing forwarder configuration");
            return validationResults;
        }
        if (!outputConfiguration.isStructureValid()) {
            return UnsupportedError(outputConfiguration);
        }
        return validationResults;
    }

    private ValidationResults UnsupportedError(OutputConfiguration outputConfiguration) {
        logger.debug("Forwarder configuration is invalid");
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
