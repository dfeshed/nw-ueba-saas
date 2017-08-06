package presidio.data.domain.event.activedirectory;


import java.util.List;

/**
 * ActiveDirectory operation entity.
 * Filled by ActiveDirectory events generator.
 * Consumed by ActiveDirectory converter to prepare data for component's SDK.
 */
public class ActiveDirectoryOperation {
    private String operationType;   // not using AD_OPERATION_TYPE enum, to be able create data for invalid values test
    private List<String> operationTypeCategories;
    private String objectName;
    private String operationResult;
    private String operationResultCode;

    public ActiveDirectoryOperation(String operationType, List<String> operationTypeCategories,
                                    String objectName, String operationResult, String operationResultCode) {
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.objectName = objectName;
        this.operationResult = operationResult;
        this.operationResultCode = operationResultCode;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
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

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
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
                ", operationTypeCategories=" + operationTypeCategories +
                ", objectName='" + objectName + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", operationResultCode='" + operationResultCode + '\'' +
                '}';
    }
}
