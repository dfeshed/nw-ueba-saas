package presidio.manager.air.flow.service;

import presidio.manager.api.records.*;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.time.Instant;


public class ConfigurationProcessingServiceImpl implements ConfigurationProcessingService {

    @Override
    public boolean applyConfiguration() {
        return true;
    }

    @Override
    public ValidationResponse validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        DataPipeLineConfiguration dataPipeLineConfiguration = presidioManagerConfiguration.getDataPipeLineConfiguration();
        if(dataPipeLineConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("dataPipeline", "missing data pipleine configuration", "missingProperty", "jsonPath", null);
            return new ValidationResponse(error);
        }

        SchemasEnum[] schemasEnum = dataPipeLineConfiguration.getSchemasEnum();
        if(schemasEnum == null || schemasEnum.length == 0 ) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("schemas", "missing schemas configuration", "missingProperty", "jsonPath", null);
            return new ValidationResponse(error);
        }

        Instant startTime = dataPipeLineConfiguration.getStartTime();
        if(startTime == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("startTime", "missing datapipline startTime configuration", "missingProperty", "jsonPath", null);
            return new ValidationResponse(error);
        }

        return new ValidationResponse();
    }

    public ConfigurationProcessingServiceImpl() {
    }
}
