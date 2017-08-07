package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.fileentity.FileEntityGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;

/**
 *
 * **/
public class CustomFileOperationGenerator implements IFileOperationGenerator {
    private IFileEntityGenerator sourceFileEntityGenerator;
    private IFileEntityGenerator destFileEntityGenerator;
    private IStringGenerator operationTypeGenerator;
    private IStringListGenerator operationTypeCategoriesGenerator;
    private IStringGenerator operationResultGenerator;
    private IStringGenerator operationResultCodeGenerator;

    public CustomFileOperationGenerator() throws GeneratorException {

        sourceFileEntityGenerator = new FileEntityGenerator();
        destFileEntityGenerator = new FileEntityGenerator();
        operationTypeGenerator = new FileOperationTypeCyclicGenerator();
        operationTypeCategoriesGenerator = new FileOpTypeCategoriesGenerator();
        operationResultGenerator = new OperationResultPercentageGenerator();
        operationResultCodeGenerator = new RandomStringGenerator(6);
    }

    public FileOperation getNext(){
        String operationType = (String) getOperationTypeGenerator().getNext();
        return new FileOperation(getSourceFileEntityGenerator().getNext(), getDestFileEntityGenerator().getNext(),
                operationType, getOperationTypeCategoriesGenerator().getNext(), (String) getOperationResultGenerator().getNext(), (String) getOperationResultCodeGenerator().getNext());
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

    public IStringGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IStringGenerator operationTypeGenerator) {
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

    public IStringListGenerator getOperationTypeCategoriesGenerator() {
        return operationTypeCategoriesGenerator;
    }

    public void setOperationTypeCategoriesGenerator(IStringListGenerator operationTypeCategoriesGenerator) {
        this.operationTypeCategoriesGenerator = operationTypeCategoriesGenerator;
    }
}
