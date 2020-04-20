package presidio.data.domain.event.activedirectory;

import presidio.data.domain.event.OperationType;

/**
 * ActiveDirectory operation entity.
 * Filled by ActiveDirectory events generator.
 * Consumed by ActiveDirectory converter to prepare data for component's SDK.
 */
public class ActiveDirectoryOperation {
    private OperationType operationType;   // not using AD_OPERATION_TYPE enum, to be able create data for invalid values test
    private String objectName;
    private String operationResult;
    private String operationResultCode;

    public ActiveDirectoryOperation(OperationType operationType,
                                    String objectName, String operationResult, String operationResultCode) {
        this.operationType = operationType;
        this.objectName = objectName;
        this.operationResult = operationResult;
        this.operationResultCode = operationResultCode;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
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

    public String getOperationResultCode() {
        return operationResultCode;
    }

    public void setOperationResultCode(String operationResultCode) {
        this.operationResultCode = operationResultCode;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryOperation{" +
                "operationType='" + operationType + '\'' +
                ", objectName='" + objectName + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", operationResultCode='" + operationResultCode + '\'' +
                '}';
    }
}
