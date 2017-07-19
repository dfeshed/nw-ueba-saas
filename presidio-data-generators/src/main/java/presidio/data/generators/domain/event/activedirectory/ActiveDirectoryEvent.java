package presidio.data.generators.domain.event.activedirectory;

import java.time.Instant;

public class ActiveDirectoryEvent {

    private Instant eventTime;
    private String operationType;
    private Boolean isSecuritySensitiveOperation;
    private Boolean isUserAdministrator;
    private String objectName;
    private String result;
    private String normalizedUsername;
    private String dataSource;

    public ActiveDirectoryEvent(Instant eventTime, String normalizedUsername, String operationType, Boolean isSecuritySensitiveOperation, Boolean isUserAdministrator, String objectName, String result, String dataSource) {
        this.eventTime = eventTime;
        this.normalizedUsername = normalizedUsername;
        this.operationType = operationType;
        this.isSecuritySensitiveOperation = isSecuritySensitiveOperation;
        this.isUserAdministrator = isUserAdministrator;
        this.objectName = objectName;
        this.result = result;
        this.dataSource = dataSource;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean getSecuritySensitiveOperation() {
        return isSecuritySensitiveOperation;
    }

    public void setSecuritySensitiveOperation(Boolean securitySensitiveOperation) {
        isSecuritySensitiveOperation = securitySensitiveOperation;
    }

    public Boolean getUserAdministrator() {
        return isUserAdministrator;
    }

    public void setUserAdministrator(Boolean userAdministrator) {
        isUserAdministrator = userAdministrator;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
