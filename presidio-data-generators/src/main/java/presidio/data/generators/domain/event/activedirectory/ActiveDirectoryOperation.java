package presidio.data.generators.domain.event.activedirectory;


/**
 * ActiveDirectory operation entity.
 * Filled by ActiveDirectory events generator.
 * Consumed by ActiveDirectory converter to prepare data for component's SDK.
 */
public class ActiveDirectoryOperation {
    private String operationType;   // not using AD_OPERATION_TYPE enum, to be able create data for invalid values test
    private Boolean isSecuritySensitiveOperation;
    private String objectName;
    private String operationResult;

    public ActiveDirectoryOperation(String operationType, Boolean isSecuritySensitiveOperation, String objectName, String operationResult) {
        this.operationType = operationType;
        this.isSecuritySensitiveOperation = isSecuritySensitiveOperation;
        this.objectName = objectName;
        this.operationResult = operationResult;
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

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryOperation{" +
                "operationType='" + operationType + '\'' +
                ", isSecuritySensitiveOperation=" + isSecuritySensitiveOperation +
                ", objectName='" + objectName + '\'' +
                ", operationResult='" + operationResult + '\'' +
                '}';
    }
}
