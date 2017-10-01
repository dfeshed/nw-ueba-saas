package presidio.security.manager.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import freemarker.template.Configuration;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigurationSecurityService implements ConfigurationProcessingService {


    private static final Logger logger = Logger.getLogger(ConfigurationSecurityService.class);

    private static final String LOCATION_TYPE = "jsonPath";
    private static final String DOMAIN_SYSTEM = "System";
    private static final String REASON_UNKNOWN_PROPERTY = "unknownProperty";
    private static final String REASON_MISSING_PROPERTY = "missingProperty";
    private static final String HTTPD_CONF_TEMPLATE = "httpd.conf.template";

    private final ConfigurationServerClientService configurationServerClientService;

    private final Configuration freeMakerConfiguration;
    
    private final ObjectMapper mapper;
    private final String securityConfPath;

    public ConfigurationSecurityService(ConfigurationServerClientService configurationServerClientService, Configuration freeMakerConfiguration, String securityConfPath) {
        this.configurationServerClientService = configurationServerClientService;
        this.freeMakerConfiguration = freeMakerConfiguration;
        this.mapper = new ObjectMapper();
        this.securityConfPath = securityConfPath;
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return validateSystemConfiguration(presidioManagerConfiguration);
    }

    @Override
    public boolean applyConfiguration() {
        FileWriter fileWriter = null;

        try {
            final PresidioManagerConfiguration presidioManagerConfiguration = configurationServerClientService.readConfigurationAsJson("application-presidio", "default", PresidioManagerConfiguration.class);

            Map<String, Object> securityConfiguration = mapper.convertValue(presidioManagerConfiguration.getSystemConfiguration(), Map.class);
            String httpdConf = FreeMarkerTemplateUtils.processTemplateIntoString(freeMakerConfiguration.getTemplate(HTTPD_CONF_TEMPLATE), securityConfiguration);

            File file = new File(securityConfPath);
            fileWriter = new FileWriter(file,false);
            fileWriter.write(httpdConf);

        } catch (Exception e) {
            String msg = "failed to apply configuration";
            logger.error(msg,e);
            return false;
        }

        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close filewriter.", e);
                }
            }
        }

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
