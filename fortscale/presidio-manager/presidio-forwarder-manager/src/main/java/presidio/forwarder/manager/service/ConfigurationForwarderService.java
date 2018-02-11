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

    private final String OUTPUT_FORWARDING = "outputForwarding";
    private final String LOCATION_TYPE = "jsonPath";
    private final String UNSUPPORTED_ERROR = "unsupportedFieldError";
    private final String UNSUPPORTED_ERROR_MESSAGE = "Unsupported Error, %s field is not supported.";
    private final String UNSUPPORTED_ERROR_MESSAGE_MISSING_FIELD = "Unsupported Error, %s field is missing.";
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
            logger.debug("No need for forwarding");
            return validationResults;
        }
        if (!outputConfiguration.isStructureValid()) {
            return createUnsupportedError(outputConfiguration);
        }
        return validationResults;
    }

    private ValidationResults createUnsupportedError(OutputConfiguration outputConfiguration) {
        logger.error("Forwarder configuration structure is invalid");
        List<String> badParams = outputConfiguration.getBadParams();
        List<String> missingParams = outputConfiguration.getMissingParams();
        ValidationResults validationResults = new ValidationResults();
        String location;
        for (String param : badParams) {
            location = new StringBuilder(OUTPUT_FORWARDING).append("/").append(param).toString();
            ConfigurationBadParamDetails badParam = new ConfigurationBadParamDetails(OUTPUT_FORWARDING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE, param));
            validationResults.addError(badParam);
            logger.debug("Forwarder configuration structure is invalid. bad param = {}", badParam.toString());
        }
        for (String param : missingParams) {
            location = new StringBuilder(OUTPUT_FORWARDING).append("/").append(param).toString();
            ConfigurationBadParamDetails missingParam = new ConfigurationBadParamDetails(OUTPUT_FORWARDING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE_MISSING_FIELD, param));
            validationResults.addError(missingParam);
            logger.debug("Forwarder configuration structure is invalid. missing param = {}", missingParam.toString());
        }
        return validationResults;
    }
}
