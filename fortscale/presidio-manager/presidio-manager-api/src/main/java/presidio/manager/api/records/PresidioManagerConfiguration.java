package presidio.manager.api.records;


import java.time.Instant;

public class PresidioManagerConfiguration {

    private DataPipeLineConfiguration dataPipeLineConfiguration;

    private SystemConfiguration systemConfiguration;

    public PresidioManagerConfiguration(DataPipeLineConfiguration dataPipeLineConfiguration, SystemConfiguration systemConfiguration) {
        this.dataPipeLineConfiguration = dataPipeLineConfiguration;
        this.systemConfiguration = systemConfiguration;
    }

    public void setStartTime(Instant startTime) {
        dataPipeLineConfiguration.setStartTime(startTime);
    }

    public void setSchemasEnum(String schemasEnum) {
        dataPipeLineConfiguration.setSchemasEnum(schemasEnum);
    }


    public void setUserName(String userName) {
        systemConfiguration.setUserName(userName);
    }

    public void setPassWord(String passWord) {
        systemConfiguration.setPassWord(passWord);
    }

    public void setAdminGroup(String adminGroup) {
        systemConfiguration.setAdminGroup(adminGroup);
    }

    public void setAnalystGroup(String analystGroup) {
        systemConfiguration.setAnalystGroup(analystGroup);
    }

    public void setSmtpHost(String smtpHost) {
        systemConfiguration.setSmtpHost(smtpHost);
    }

    public void setKdcUrl(String kdcUrl) {
        systemConfiguration.setKdcUrl(kdcUrl);
    }

    public void setEmpty() {
        setAdminGroup("");
        setAnalystGroup("");
        setKdcUrl("");
        setPassWord("");
        setSmtpHost("");
        setUserName("");
        setSchemasEnum(null);
        setStartTime(null);
    }

}
