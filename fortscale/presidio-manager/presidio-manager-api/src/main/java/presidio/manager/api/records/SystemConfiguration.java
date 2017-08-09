package presidio.manager.api.records;


public class SystemConfiguration {

    private String userName;

    private String passWord;

    private String adminGroup;

    private String analystGroup;

    private String smtpHost;

    private String kdcUrl;

    public SystemConfiguration() {
        this.userName = "";
        this.passWord = "";
        this.adminGroup = "";
        this.analystGroup = "";
        this.smtpHost = "";
        this.kdcUrl = "";
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
}
