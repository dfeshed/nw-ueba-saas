package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class PresidioSystemConfiguration {


    private String userName = null;

    private String passWord = null;

    private String adminGroup = null;

    private String analystGroup = null;

    private String smtpHost = null;

    private String kdcUrl = null;


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setAdminGroup(String adminGroup) {
        this.adminGroup = adminGroup;
    }

    public void setAnalystGroup(String analystGroup) {
        this.analystGroup = analystGroup;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setKdcUrl(String kdcUrl) {
        this.kdcUrl = kdcUrl;
    }

    public void setParameters(JsonNode node) {
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next().toString();
            setKeyValue(key, node.get(key).toString());
        }
    }

    private void setKeyValue(String key, String value) {
        switch (key) {
            case "userName":
                setUserName(value);
                break;
            case "passWord":
                setPassWord(value);
                break;
            case "adminGroup":
                setAdminGroup(value);
                break;
            case "analystGroup":
                setAnalystGroup(value);
                break;
            case "kdcUrl":
                setKdcUrl(value);
                break;
            case "smtpHost":
                setSmtpHost(value);
                break;
        }
    }
}
