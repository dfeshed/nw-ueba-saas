package presidio.data.domain.event.print;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileOperation;

public class PrintFileOperation extends FileOperation{
    private long numOfPages;

    public PrintFileOperation(FileEntity sourceFile, FileEntity destinationFile, OperationType operationType, String operationResult, String operationResultCode, long numOfPages) {
        super(sourceFile, destinationFile, operationType, operationResult, operationResultCode);
        this.numOfPages = numOfPages;
    }

    public long getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(long numOfPages) {
        this.numOfPages = numOfPages;
    }

    @Override
    public String toString() {
        return "PrintFileOperation{" +
                super.toString() +
                "numOfPages=" + numOfPages +
                '}';
    }
}
