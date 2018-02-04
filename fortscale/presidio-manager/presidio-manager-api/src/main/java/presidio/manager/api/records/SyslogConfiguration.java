package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.PresidioForwarderConfiguration;
import presidio.manager.api.configuration.SyslogSenderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SyslogConfiguration extends createConfigurationAndStructureValidiation implements PresidioForwarderConfiguration {

    private SyslogSenderConfiguration alertSyslogConfiguration;
    private SyslogSenderConfiguration userSyslogConfiguration;

    private final String ALERT = "alert";
    private final String USER = "user";


    public SyslogConfiguration(JsonNode node) {
        setBadParams(new ArrayList<>());
        createConfiguration(node);
        if (alertSyslogConfiguration != null) {
            badParamsAddKeys(addPrefixToBadParams(ALERT, alertSyslogConfiguration.badParams()));
        }
        if (userSyslogConfiguration != null) {
            badParamsAddKeys(addPrefixToBadParams(USER, userSyslogConfiguration.badParams()));
        }
        checkStructure();
    }

    public SyslogSenderConfiguration getAlertSyslogConfiguration() {
        return alertSyslogConfiguration;
    }

    public SyslogSenderConfiguration getUserSyslogConfiguration() {
        return userSyslogConfiguration;
    }


    public void setAlertSyslogConfiguration(SyslogSenderConfiguration alertSyslogConfiguration) {
        this.alertSyslogConfiguration = alertSyslogConfiguration;
    }

    public void setUserSyslogConfiguration(SyslogSenderConfiguration userSyslogConfiguration) {
        this.userSyslogConfiguration = userSyslogConfiguration;
    }

    @Override
    public void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case ALERT:
                setAlertSyslogConfiguration(new SyslogMessageSenderConfiguration(value));
                break;
            case USER:
                setUserSyslogConfiguration(new SyslogMessageSenderConfiguration(value));
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
}
