package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.ConfigurationValidatable;

import java.util.List;

public class SyslogForwardingConfigurationCreation extends ConfigurationCreation implements ConfigurationValidatable {

    private ConfigurationValidatable alertSyslogConfiguration;
    private ConfigurationValidatable userSyslogConfiguration;

    private final String ALERT = "alert";
    private final String USER = "user";


    public SyslogForwardingConfigurationCreation() {
    }

    public SyslogForwardingConfigurationCreation(JsonNode node) {
        createConfiguration(node);
        checkConfigurationValidatableParams(ALERT, alertSyslogConfiguration);
        checkConfigurationValidatableParams(USER, userSyslogConfiguration);
        checkStructure();
    }

    private void checkConfigurationValidatableParams(String object, ConfigurationValidatable configurationValidatable) {
        if (configurationValidatable != null) {
            badParamsAddKeys(addPrefixToBadParams(object, configurationValidatable.badParams()));
            missingParamsAddKeys(addPrefixToBadParams(object, configurationValidatable.missingParams()));
        } else {
            missingParamsAddKeys(object);
        }
    }

    public ConfigurationValidatable getAlertSyslogConfiguration() {
        return alertSyslogConfiguration;
    }

    public ConfigurationValidatable getUserSyslogConfiguration() {
        return userSyslogConfiguration;
    }


    public void setAlertSyslogConfiguration(ConfigurationValidatable alertSyslogConfiguration) {
        this.alertSyslogConfiguration = alertSyslogConfiguration;
    }

    public void setUserSyslogConfiguration(ConfigurationValidatable userSyslogConfiguration) {
        this.userSyslogConfiguration = userSyslogConfiguration;
    }

    @Override
    public void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case ALERT:
                setAlertSyslogConfiguration(new SyslogConfigurationCreation(value));
                break;
            case USER:
                setUserSyslogConfiguration(new SyslogConfigurationCreation(value));
                break;
            default:
                badParamsAddKey(key);
        }
    }

    @Override
    void checkStructure() {
        setStructureValid(isStructureValid() &&
                alertSyslogConfiguration != null && alertSyslogConfiguration.isValid() &&
                userSyslogConfiguration != null && userSyslogConfiguration.isValid());
    }

    @Override
    public boolean isValid() {
        return isStructureValid();
    }

    @Override
    public List<String> badParams() {
        return getBadParams();
    }

    @Override
    public List<String> missingParams() {
        return getMissingParams();
    }
}
