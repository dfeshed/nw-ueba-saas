package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PresidioSystemConfiguration {


    private String userName;

    private String passWord;

    private String adminGroup;

    private String analystGroup;

    private String smtpHost;

    private String kdcUrl;

    private List<String> unknownFields;

    private final String USER_NAME = "username";
    private final String PASSWORD = "password";
    private final String ADMIN_GROUP = "adminGroup";
    private final String ANALYST_GROUP = "analystGroup";
    private final String KDC_URL = "kdcUrl";
    private final String SMTP_HOST = "smtpHost";

    public PresidioSystemConfiguration(JsonNode node) {
        this.unknownFields = new ArrayList();
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            setKeyValue(key, node.get(key).asText());
        }

    }

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


    private void setKeyValue(String key, String value) {
        switch (key) {
            case USER_NAME:
                setUserName(value);
                break;
            case PASSWORD:
                setPassWord(value);
                break;
            case ADMIN_GROUP:
                setAdminGroup(value);
                break;
            case ANALYST_GROUP:
                setAnalystGroup(value);
                break;
            case KDC_URL:
                setKdcUrl(value);
                break;
            case SMTP_HOST:
                setSmtpHost(value);
                break;
            default:
                unknownFields.add(key);
        }
    }

    public List<String> getUnknownFields() {
        return unknownFields;
    }

    public List<String> getEmptyFields() {
        List<String> emptyFields = new ArrayList<>();
        if (userName == null || userName.isEmpty()) {
            emptyFields.add(USER_NAME);
        }
        if (passWord == null || passWord.isEmpty()) {
            emptyFields.add(PASSWORD);
        }
        if (adminGroup == null || adminGroup.isEmpty()) {
            emptyFields.add(ADMIN_GROUP);
        }
        if (analystGroup == null || analystGroup.isEmpty()) {
            emptyFields.add(ANALYST_GROUP);
        }
        if (smtpHost == null || smtpHost.isEmpty()) {
            emptyFields.add(SMTP_HOST);
        }
        if (kdcUrl == null || kdcUrl.isEmpty()) {
            emptyFields.add(KDC_URL);
        }

        return emptyFields;
    }

}
