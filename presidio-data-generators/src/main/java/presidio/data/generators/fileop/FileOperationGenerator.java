package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.*;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.fileentity.FileEntityGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;

import java.util.Collections;

/**
 *
 * **/
public class FileOperationGenerator implements IFileOperationGenerator {
    private IFileEntityGenerator sourceFileEntityGenerator;
    private IFileEntityGenerator destFileEntityGenerator;
    private IOperationTypeGenerator operationTypeGenerator;
    private IStringGenerator operationResultGenerator;
    private IStringGenerator operationResultCodeGenerator;

    public FileOperationGenerator() throws GeneratorException {
        sourceFileEntityGenerator = new FileEntityGenerator();
        destFileEntityGenerator = new FileEntityGenerator();
        operationTypeGenerator = new FixedOperationTypeGenerator(new OperationType("dummyOperationType", Collections.emptyList()));
        operationResultGenerator = new OperationResultPercentageGenerator();
        operationResultCodeGenerator = new RandomStringGenerator(6);
    }

    public FileOperation getNext(){
        return new FileOperation(getSourceFileEntityGenerator().getNext(), getDestFileEntityGenerator().getNext(),
                operationTypeGenerator.getNext(), (String) getOperationResultGenerator().getNext(), (String) getOperationResultCodeGenerator().getNext());
    }

    public IFileEntityGenerator getSourceFileEntityGenerator() {
        return sourceFileEntityGenerator;
    }

    public void setSourceFileEntityGenerator(IFileEntityGenerator sourceFileEntityGenerator) {
        this.sourceFileEntityGenerator = sourceFileEntityGenerator;
    }

    public IFileEntityGenerator getDestFileEntityGenerator() {
        return destFileEntityGenerator;
    }

    public void setDestFileEntityGenerator(IFileEntityGenerator destFileEntityGenerator) {
        this.destFileEntityGenerator = destFileEntityGenerator;
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

    public IStringGenerator getOperationResultGenerator() {
        return operationResultGenerator;
    }

    public void setOperationResultGenerator(IStringGenerator operationResultGenerator) {
        this.operationResultGenerator = operationResultGenerator;
    }

    public IStringGenerator getOperationResultCodeGenerator() {
        return operationResultCodeGenerator;
    }

    public void setOperationResultCodeGenerator(IStringGenerator operationResultCodeGenerator) {
        this.operationResultCodeGenerator = operationResultCodeGenerator;
    }
}
