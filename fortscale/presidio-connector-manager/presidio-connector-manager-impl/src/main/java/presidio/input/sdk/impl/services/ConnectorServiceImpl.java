package presidio.input.sdk.impl.services;

import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.sdk.api.service.ConnectorService;

public class ConnectorServiceImpl implements ConnectorService {

//    private static final Logger logger = Logger.getLogger(ConnectorServiceImpl.class);
@Override
/**
 * Apply validation for "/configuation" from "https://app.swaggerhub.com/apis/Fortscale/Presidio-V2/1.0.0"
 */
public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {

    ValidationResults validationResults = new ValidationResults();
    if (presidioManagerConfiguration == null) {
        //TODO: Set Error
        ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("1", "1", "1", "2", "3");
        validationResults.addError(error);
    }
    if (presidioManagerConfiguration.getDataPipeLineConfiguration() == null) {
        //TODO: Set Error
        ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("1", "1", "1", "2", "3");
        validationResults.addError(error);
    }
    String[] schemas = presidioManagerConfiguration.getDataPipeLineConfiguration().getSchemas();
    if (schemas == null || schemas.length == 0) {
        //TODO: Set Error
        ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("1", "1", "1", "2", "3");
        validationResults.addError(error);
    }

    return validationResults;
}

    @Override
    /**
     * Apply configuration for "/configuation" from "https://app.swaggerhub.com/apis/Fortscale/Presidio-V2/1.0.0"
     */
    public boolean applyConfiguration() {
        return true;
    }

    @Override
    public boolean applyCollector() {
        //TODO: Implement
        return true;
    }


}

