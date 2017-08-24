package presidio.connector.manager.impl.services;

import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.sdk.api.service.ConnectorService;

public class ConnectorServiceImpl implements ConnectorService {




    @Override
    /**
     * Apply validation for "/configuation" from "https://app.swaggerhub.com/apis/Fortscale/Presidio-V2/1.0.0"
     */
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {

        ValidationResults validationResults = new ValidationResults();
        if (presidioManagerConfiguration == null) {

            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("ALL", "ALL", "invalidParamter", "jsonPath", "Missing Configuration");
            validationResults.addError(error);
        }
        if (presidioManagerConfiguration.getDataPipeLineConfiguration() == null) {

            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("dataPipeline", "dataPipeline", "invalidParameter", "jsonPath", "DataPipline is empty");
            validationResults.addError(error);
        }
        String[] schemas = presidioManagerConfiguration.getDataPipeLineConfiguration().getSchemas();
        if (schemas == null || schemas.length == 0) {

            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("dataPipeline", "dataPipeline/scehmas", "No Schema Defined", "jsonPath", "Scehma Types Cannot be empty");
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




}

