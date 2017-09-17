package presidio.security.manager.service;


import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.List;

public class ConfigurationSecurityService implements ConfigurationProcessingService {

    private static final String DOMAIN_SYSTEM = "System";
    private static final String REASON_UNKNOWN_PROPERTY = "unknownProperty";
    private static final String REASON_MISSING_PROPERTY = "missingProperty";
    private final String LOCATION_TYPE = "jsonPath";


    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return validateSystemConfiguration(presidioManagerConfiguration);
    }

    @Override
    public boolean applyConfiguration() {
        return true;
    }


    private ValidationResults validateSystemConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        ValidationResults validationResults = new ValidationResults();
        final PresidioSystemConfiguration systemConfiguration = presidioManagerConfiguration.getSystemConfiguration();
        final List<String> unknownFields = systemConfiguration.getUnknownFields();
        for (String unknownField : unknownFields) {
            final String errorMessage = String.format("Unknown %s configuration", unknownField);
            ConfigurationBadParamDetails configurationBadParamDetails = createConfigurationBadParamDetails(unknownField, REASON_UNKNOWN_PROPERTY, errorMessage);
            validationResults.addError(configurationBadParamDetails);
        }
        final List<String> emptyFields = systemConfiguration.getEmptyFields();
        for (String emptyField : emptyFields) {
            final String errorMessage = String.format("Missing %s configuration", emptyField);
            ConfigurationBadParamDetails configurationBadParamDetails = createConfigurationBadParamDetails(emptyField, REASON_MISSING_PROPERTY, errorMessage);
            validationResults.addError(configurationBadParamDetails);
        }
        return validationResults;
    }

    private ConfigurationBadParamDetails createConfigurationBadParamDetails(String field, String reason, String errorMessage) {
        final String location = DOMAIN_SYSTEM + "/" + field;
        return new ConfigurationBadParamDetails(DOMAIN_SYSTEM, location, reason, LOCATION_TYPE, errorMessage);
    }


}
