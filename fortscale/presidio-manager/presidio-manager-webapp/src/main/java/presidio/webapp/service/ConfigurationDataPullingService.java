package presidio.webapp.service;


import fortscale.utils.logging.Logger;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.DataPullingConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.*;


public class ConfigurationDataPullingService implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationDataPullingService.class);

    private static final String DATA_PULLING = "dataPulling";
    private final String LOCATION_TYPE = "jsonPath";
    private final String UNSUPPORTED_ERROR = "unsupportedFieldError";
    private final String UNSUPPORTED_ERROR_MESSAGE = "Unsupported Error, %s field is not supported.";
    private final String UNSUPPORTED_ERROR_MESSAGE_BAD_VALUE = "Unsupported Error, %s field is missing or has bad value.";
    private final String UNSUPPORTED_ERROR_MESSAGE_MISSING_FIELD = "Unsupported Error, %s field is missing.";
    private final String MISSING_DATA_ERROR_MESSAGE = "Missing data pulling configuration";
    private final String MISSING_PROPERTY = "missingProperty";
    private final String SOURCE_FIELD = "source";

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        logger.debug("Validating data pulling configuration");
        DataPullingConfiguration dataPullingConfiguration = presidioManagerConfiguration.getDataPullingConfiguration();
        ValidationResults validationResults = new ValidationResults();
        if (dataPullingConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(DATA_PULLING, DATA_PULLING, MISSING_PROPERTY, LOCATION_TYPE, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            logger.error("Missing data pulling configuration");
            return validationResults;
        }
//        if (dataPullingConfiguration.getSource() == null || dataPullingConfiguration.getSource().isEmpty()) {
//            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails(DATA_PULLING, DATA_PULLING, MISSING_PROPERTY, LOCATION_TYPE, MISSING_DATA_ERROR_MESSAGE);
//            validationResults.addError(error);
//            logger.error("Missing data pulling configuration");
//        }

        if (!dataPullingConfiguration.isStructureValid()) {
            return createUnsupportedError(dataPullingConfiguration);
        }
        return validationResults;
    }

    private ValidationResults createUnsupportedError(DataPullingConfiguration dataPullingConfiguration) {
        logger.error("Data pulling configuration structure is invalid");
        List<String> emptyParams = dataPullingConfiguration.getEmptyFields(); //TODO- should check missing params
        List<String> unknownParams = dataPullingConfiguration.getUnknownFields();
        ValidationResults validationResults = new ValidationResults();
        String location;
        for (String param : emptyParams) {
            location = new StringBuilder(DATA_PULLING).append("/").append(param).toString();
            ConfigurationBadParamDetails badParam;
            if (param.equals(SOURCE_FIELD)) {
                badParam = new ConfigurationBadParamDetails(DATA_PULLING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE_BAD_VALUE, param));
            } else {
                badParam = new ConfigurationBadParamDetails(DATA_PULLING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE, param));
            }
            validationResults.addError(badParam);
            logger.debug("Data pulling configuration structure is invalid. bad param = {}", badParam.toString());
        }
        for (String param : unknownParams) {
            location = new StringBuilder(DATA_PULLING).append("/").append(param).toString();
            ConfigurationBadParamDetails missingParam = new ConfigurationBadParamDetails(DATA_PULLING, location, UNSUPPORTED_ERROR, LOCATION_TYPE, String.format(UNSUPPORTED_ERROR_MESSAGE_MISSING_FIELD, param));
            validationResults.addError(missingParam);
            logger.debug("Data pulling configuration structure is invalid. missing param = {}", missingParam.toString());
        }
        return validationResults;
    }

    @Override
    public boolean applyConfiguration() {
        logger.debug("No applying is needed");
        return true;
    }
}
