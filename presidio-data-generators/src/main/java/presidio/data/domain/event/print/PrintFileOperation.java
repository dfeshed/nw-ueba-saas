package presidio.data.domain.event.print;

import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileOperation;

public class PrintFileOperation extends FileOperation{
    private Long numOfPages;

    public PrintFileOperation(FileEntity sourceFile, FileEntity destinationFile, OperationType operationType, String operationResult, String operationResultCode, Long numOfPages) {
        super(sourceFile, destinationFile, operationType, operationResult, operationResultCode);
        this.numOfPages = numOfPages;
    }

    public Long getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(Long numOfPages) {
        this.numOfPages = numOfPages;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
