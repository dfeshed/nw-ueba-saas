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

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class ConfigurationSecurityService implements ConfigurationProcessingService {


    public static final String LDAP_URL = "ldapUrl";
    public static final String KDC = "kdc";
    public static final String UPPER_CASE_REALM = "upperCaseRealmsName";
    public static final String REALM = "realmName";
    private static final Logger logger = Logger.getLogger(ConfigurationSecurityService.class);

    private static final String LOCATION_TYPE = "jsonPath";
    private static final String DOMAIN_SYSTEM = "System";
    private static final String REASON_UNKNOWN_PROPERTY = "unknownProperty";
    private static final String REASON_MISSING_PROPERTY = "missingProperty";
    private static final String HTTPD_CONF_TEMPLATE_FILENAME = "/templates/httpd.conf.template";
    private static final String KRB5_CONF_TEMPLATE_FILENAME = "/templates/krb5.conf.template";

    private final ConfigurationServerClientService configurationServerClientService;

    private final Configuration freeMakerConfiguration;
    
    private final ObjectMapper mapper;
    private final String securityConfPath;
    private final String krb5ConfPath;
    private final boolean shouldReloadHttpd;

    public ConfigurationSecurityService(ConfigurationServerClientService configurationServerClientService,
                                        Configuration freeMakerConfiguration, String securityConfPath, String krb5Path, boolean shouldReloadHttpd) {
        this.configurationServerClientService = configurationServerClientService;
        this.freeMakerConfiguration = freeMakerConfiguration;
        this.mapper = new ObjectMapper();
        this.securityConfPath = securityConfPath;
        this.krb5ConfPath = krb5Path;
        this.shouldReloadHttpd = shouldReloadHttpd;
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return validateSystemConfiguration(presidioManagerConfiguration);
    }

    @Override
    public boolean applyConfiguration() {
        FileWriter fileWriter = null;
        FileWriter krb5ConfFileWriter = null;

        try {
            final PresidioManagerConfiguration presidioManagerConfiguration = configurationServerClientService.readConfigurationAsJson("application-presidio", "default", PresidioManagerConfiguration.class);

            freeMakerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/");

            // Handle httpd conf
            Map<String, Object> securityConfiguration = mapper.convertValue(presidioManagerConfiguration.getSystemConfiguration(), Map.class);
            String httpdTemplatePath = getClass().getResource(HTTPD_CONF_TEMPLATE_FILENAME).getPath();
            String httpdConf = FreeMarkerTemplateUtils.processTemplateIntoString(freeMakerConfiguration.getTemplate(httpdTemplatePath), securityConfiguration);

            File file = new File(securityConfPath);
            fileWriter = new FileWriter(file,false);
            fileWriter.write(httpdConf);

            URI uri = new URI((String) securityConfiguration.get(LDAP_URL));
            String domain = uri.getHost();
            securityConfiguration.put(KDC, domain);

            String upperCaseRealm = ((String) securityConfiguration.get(REALM)).toUpperCase();
            securityConfiguration.put(UPPER_CASE_REALM, upperCaseRealm);

            // Handle krb5 conf
            String krb5TemplatePath = getClass().getResource(KRB5_CONF_TEMPLATE_FILENAME).getPath();
            String krb5Conf = FreeMarkerTemplateUtils.processTemplateIntoString(freeMakerConfiguration.getTemplate(krb5TemplatePath), securityConfiguration);

            File krb5ConfFile = new File(krb5ConfPath);
            krb5ConfFileWriter = new FileWriter(krb5ConfFile,false);
            krb5ConfFileWriter.write(krb5Conf);


        } catch (Exception e) {
            String msg = "failed to apply configuration";
            logger.error(msg,e);
            return false;
        }

        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    krb5ConfFileWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close filewriter.", e);
                }
            }
        }

        if(shouldReloadHttpd) {
            return reloadHttpConfig();
        }
        else
        {
            logger.warn("not reloading httpd config. you must be running a unit test... right?");
            return true;
        }
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

    private boolean reloadHttpConfig() {
        String s;
        Process p=null;
        logger.info("Reload apache httpd");
//        String command = "/bin/sh -c sudo service " + webService  + " start";
        String command = "sudo /usr/bin/systemctl reload httpd";

        try {
            // run the command
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // get the result
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            // get the exit code
            if (p.exitValue()==0){
                logger.info("HTTPD reloaded successfully");
                return true;
            } else {
                logger.info("HTTPD reloaded failed");
                return false;
            }

        } catch (Exception e) {
            logger.info("HTTPD restart failed");
            return false;
        } finally {
            if (p!=null) {
                p.destroy();
            }
        }
    }


}
