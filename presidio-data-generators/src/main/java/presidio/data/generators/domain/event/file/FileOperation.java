package presidio.data.generators.domain.event.file;


import presidio.data.generators.domain.FileEntity;

/**
 * Events generator domain, contains all fields for DLPFileOperation generator
 */
public class FileOperation {
    private FileEntity sourceFile;
    private FileEntity destinationFile;
    private String operationType;
    private String operationResult;

    public FileOperation(FileEntity sourceFile, FileEntity destinationFile, String operationType, String operationResult) {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        this.operationType = operationType;
        this.operationResult = operationResult;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    @Override
    public String toString() {
        return  "File operation: " + sourceFile.toString() +
                "," + destinationFile.toString() +
                "," + operationType +
                "," + operationResult ;
    }
}
