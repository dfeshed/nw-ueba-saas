package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PresidioSystemConfiguration {

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("ldapUrl")
    private String ldapUrl;

    @JsonProperty("realmName")
    private String realmName;

    @JsonProperty("analystGroup")
    private String analystGroup;

    @JsonProperty("smtpHost")
    private String smtpHost;

    @JsonProperty("krbServiceName")
    private String krbServiceName;

    private List<String> unknownFields;

    private final String USER_NAME = "username";
    private final String PASSWORD = "password";
    private final String LDAP_URL = "ldapUrl";
    private final String REALM_NAME = "realmName";
    private final String ANALYST_GROUP = "analystGroup";
    private final String SMTP_HOST = "smtpHost";
    private final String KRB_SERVICE_NAME = "krbServiceName";

    public PresidioSystemConfiguration() {
    }


    public PresidioSystemConfiguration(JsonNode node) {
        this.unknownFields = new ArrayList();
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            setKeyValue(key, node.get(key).asText());
        }

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getAnalystGroup() {
        return analystGroup;
    }

    public void setAnalystGroup(String analystGroup) {
        this.analystGroup = analystGroup;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }


    public String getKrbServiceName() {
        return krbServiceName;
    }

    public void setKrbServiceName(String krbServiceName) {
        this.krbServiceName = krbServiceName;
    }

    private void setKeyValue(String key, String value) {
        switch (key) {
            case USER_NAME:
                setUserName(value);
                break;
            case PASSWORD:
                setPassword(value);
                break;
            case LDAP_URL:
                setLdapUrl(value);
                break;
            case REALM_NAME:
                setRealmName(value);
                break;
            case ANALYST_GROUP:
                setAnalystGroup(value);
                break;
            case SMTP_HOST:
                setSmtpHost(value);
                break;
            case KRB_SERVICE_NAME:
                setKrbServiceName(value);
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
        if (password == null || password.isEmpty()) {
            emptyFields.add(PASSWORD);
        }
        if (ldapUrl == null || ldapUrl.isEmpty()) {
            emptyFields.add(LDAP_URL);
        }
        if (realmName == null || realmName.isEmpty()) {
            emptyFields.add(REALM_NAME);
        }
        if (analystGroup == null || analystGroup.isEmpty()) {
            emptyFields.add(ANALYST_GROUP);
        }
        if (smtpHost == null || smtpHost.isEmpty()) {
            emptyFields.add(SMTP_HOST);
        }

        if (krbServiceName == null || krbServiceName.isEmpty()) {
            emptyFields.add(KRB_SERVICE_NAME);
        }

        return emptyFields;
    }



}
