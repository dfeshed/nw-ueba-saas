package presidio.security.manager.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;
import freemarker.template.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

public class ConfigurationSecurityService implements ConfigurationProcessingService {


    private static final Logger logger = Logger.getLogger(ConfigurationSecurityService.class);

    private static final String LOCATION_TYPE = "jsonPath";
    private static final String ETC_CONFIG_PATH = "/etc/httpd/conf/httpd.conf";
    private static final String DOMAIN_SYSTEM = "System";
    private static final String REASON_UNKNOWN_PROPERTY = "unknownProperty";
    private static final String REASON_MISSING_PROPERTY = "missingProperty";
    private static final String HTTPD_CONF_TEMPLATE = "httpd.conf.template";

    private final ConfigurationServerClientService configurationServerClientService;

    private final Configuration freeMakerConfiguration;

    public ConfigurationSecurityService(ConfigurationServerClientService configurationServerClientService, Configuration freeMakerConfiguration) {
        this.configurationServerClientService = configurationServerClientService;
        this.freeMakerConfiguration = freeMakerConfiguration;
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return validateSystemConfiguration(presidioManagerConfiguration);
    }

    @Override
    public boolean applyConfiguration() {
        try {
            final PresidioManagerConfiguration presidioManagerConfiguration = configurationServerClientService.readConfigurationAsJson("application-presidio", "default", PresidioManagerConfiguration.class);

            Map<String, Object> securityConfiguration = new ObjectMapper().convertValue(presidioManagerConfiguration.getSystemConfiguration(), Map.class);
            String httpdConf = FreeMarkerTemplateUtils.processTemplateIntoString(freeMakerConfiguration.getTemplate(HTTPD_CONF_TEMPLATE), securityConfiguration);

            File file = new File(ETC_CONFIG_PATH);
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(httpdConf);
            fileWriter.close();

        } catch (Exception e) {
            String msg = "failed to apply configuration";
            logger.error(msg,e);
            return false;
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
