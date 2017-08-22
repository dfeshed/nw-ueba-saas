package presidio.connector.manager.impl.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.sdk.api.service.ConnectorService;

public class ConnectorServiceImpl implements ConnectorService {


    private static final String URL_PLACEHOLDER = "http//%s:%s/api/v1.0/refresh-configuration";
    private final RestTemplate restTemplate;
    private String connectorHostname;
    private String connectorPort;

    public ConnectorServiceImpl(RestTemplate restTemplate, String connectorHostname, String connectorPort) {
        this.restTemplate = restTemplate;
        this.connectorHostname = connectorHostname;
        this.connectorPort = connectorPort;
    }

    @Override
    /**
     * Apply validation for "/configuation" from "https://app.swaggerhub.com/apis/Fortscale/Presidio-V2/1.0.0"
     */
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {

        ValidationResults validationResults = new ValidationResults();
        if (presidioManagerConfiguration == null) {

            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("ALL", "ALL", "invalidParamter", "jsonPath", "Nothing Configured");
            validationResults.addError(error);
        }
        if (presidioManagerConfiguration.getDataPipeLineConfiguration() == null) {

            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("dataPipeline", "dataPipeline", "invalidParamter", "jsonPath", "DataPipline is empty");
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

    /**
     * apply collector responsible that the collector will download the new configuration for the collector.
     * If the collector is awake - the apply collector send refresh-configuration call
     * If not - it should start the collector
     *
     * @return
     */
    @Override
    public boolean applyCollector() {


        String url = String.format(URL_PLACEHOLDER, this.connectorHostname, this.connectorPort);
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return true;
        }
        //If server is down
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            //TODO: start the collector
            return true;
        } else {
            return false;
        }


    }


}

