package presidio.data.generators.file;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.domain.event.file.FileOperation;

public class FileOperationGenerator implements IFileOperationGenerator {

    private FileEntityGenerator sourceFileEntityGenerator;
    private FileEntityGenerator destFileEntityGenerator;
    private FileOperationTypeCyclicGenerator operationTypeGenerator;
    private OperationResultPercentageGenerator operationResultGenerator;

    public FileOperationGenerator() throws GeneratorException {
        sourceFileEntityGenerator = new FileEntityGenerator();
        destFileEntityGenerator = new FileEntityGenerator();
        operationTypeGenerator = new FileOperationTypeCyclicGenerator();
        operationResultGenerator = new OperationResultPercentageGenerator();
    }

    public FileOperation getNext(){
        return new FileOperation(getSourceFileEntityGenerator().getNext(), getDestFileEntityGenerator().getNext(),
                (String)getOperationTypeGenerator().getNext(), (String) getOperationResultGenerator().getNext());
    }

    public FileEntityGenerator getSourceFileEntityGenerator() {
        return sourceFileEntityGenerator;
    }

    public void setSourceFileEntityGenerator(FileEntityGenerator sourceFileEntityGenerator) {
        this.sourceFileEntityGenerator = sourceFileEntityGenerator;
    }

    public FileEntityGenerator getDestFileEntityGenerator() {
        return destFileEntityGenerator;
    }

    public void setDestFileEntityGenerator(FileEntityGenerator destFileEntityGenerator) {
        this.destFileEntityGenerator = destFileEntityGenerator;
    }

    public FileOperationTypeCyclicGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(FileOperationTypeCyclicGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

    public OperationResultPercentageGenerator getOperationResultGenerator() {
        return operationResultGenerator;
    }

    public void setOperationResultGenerator(OperationResultPercentageGenerator operationResultGenerator) {
        this.operationResultGenerator = operationResultGenerator;
    }
}
