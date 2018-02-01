package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class SyslogConfiguration extends JsonToObjectConfiguration implements ForwarderConfiguration {

    private SyslogSenderConfiguration alert;
    private SyslogSenderConfiguration user;

    private final String ALERT = "alert";
    private final String USER = "user";


    public SyslogConfiguration(JsonNode node) {
        setBadParams(new ArrayList<>());
        createConfiguration(node);
        badParamsAddKeys(alert.badParams());
        badParamsAddKeys(user.badParams());
        checkStructure();
    }

    public SyslogSenderConfiguration getAlert() {
        return alert;
    }

    public SyslogSenderConfiguration getUser() {
        return user;
    }


    public void setAlert(SyslogSenderConfiguration alert) {
        this.alert = alert;
    }

    public void setUser(SyslogSenderConfiguration user) {
        this.user = user;
    }


    @Override
    public void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case ALERT:
                setAlert(new SyslogMessageSenderConfiguration(value));
                break;
            case USER:
                setUser(new SyslogMessageSenderConfiguration(value));
                break;
            default:
                badParamsAddKey(key);
        }
    }

    @Override
    void checkStructure() {
        setStructureValid(isStructureValid() &&
                alert != null && alert.isValid() &&
                user != null && user.isValid());
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
