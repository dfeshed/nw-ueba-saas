package presidio.data.domain.event.file;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.OperationType;

/**
 * Events generator domain, contains all fields for DLPFileOperation generator
 */
public class FileOperation {
    private FileEntity sourceFile;
    private FileEntity destinationFile;
    private OperationType operationType;
    private String operationResult;
    private String operationResultCode;

    public FileOperation(FileEntity sourceFile, FileEntity destinationFile, OperationType operationType, String operationResult, String operationResultCode) {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        this.operationType = operationType;
        this.operationResult = operationResult;
        this.operationResultCode = operationResultCode;
    }

    public FileEntity getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(FileEntity sourceFile) {
        this.sourceFile = sourceFile;
    }

    public FileEntity getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(FileEntity destinationFile) {
        this.destinationFile = destinationFile;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
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
        return  "File operation: " + sourceFile.toString() +
                "," + destinationFile.toString() +
                "," + operationType.toString() +
                "," + operationResult +
                "," + operationResultCode;
    }
}
