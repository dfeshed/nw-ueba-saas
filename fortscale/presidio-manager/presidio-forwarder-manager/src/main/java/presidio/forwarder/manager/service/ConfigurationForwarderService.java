package presidio.forwarder.manager.service;

import fortscale.utils.logging.Logger;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.OutputConfigurationCreator;
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
    private final String UNSUPPORTED_ERROR_MESSAGE_BAD_VALUE = "Unsupported Error, %s field is missing or has bad value.";
    private final String UNSUPPORTED_ERROR_MESSAGE_MISSING_FIELD = "Unsupported Error, %s field is missing.";
    private final String MISSING_DATA_ERROR_MESSAGE = "Missing forwarder configuration";
    private final String MISSING_PROPERTY = "missingProperty";
    private final String ENABLE_FORWARDING = "enableForwarding";


    public ConfigurationForwarderService() {

    }

    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        logger.debug("Validating forwarder configuration");
        return validateForwarderConfiguration(presidioManagerConfiguration.getOutputConfigurationCreator());
    }

    public boolean applyConfiguration() {
        logger.debug("No applying is needed");
        return true;
    }


    public ValidationResults validateForwarderConfiguration(OutputConfigurationCreator outputConfigurationCreator) {
        ValidationResults validationResults = new ValidationResults();
        if (outputConfigurationCreator == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(OUTPUT_FORWARDING, OUTPUT_FORWARDING, MISSING_PROPERTY, LOCATION_TYPE, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            logger.error("Missing forwarder configuration");
            return validationResults;
        }
        if (!outputConfigurationCreator.isStructureValid()) {
            return createUnsupportedError(outputConfigurationCreator);
        }
        return validationResults;
    }

    private ValidationResults createUnsupportedError(OutputConfigurationCreator outputConfigurationCreator) {
        logger.error("Forwarder configuration structure is invalid");
        List<String> badParams = outputConfigurationCreator.getBadParams();
        List<String> missingParams = outputConfigurationCreator.getMissingParams();
        ValidationResults validationResults = new ValidationResults();
        String location;
        for (String param : badParams) {
            location = new StringBuilder(OUTPUT_FORWARDING).append("/").append(param).toString();
            ConfigurationBadParamDetails badParam;
            if (param.equals(ENABLE_FORWARDING)) {
                badParam = new ConfigurationBadParamDetails(OUTPUT_FORWARDING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE_BAD_VALUE, param));
            } else {
                badParam = new ConfigurationBadParamDetails(OUTPUT_FORWARDING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE, param));
            }
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
