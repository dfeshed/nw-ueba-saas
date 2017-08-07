package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.fileentity.FileEntityGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * **/
public class FileOperationGenerator implements IFileOperationGenerator {
    private HashMap<String,String[]> opType2OpCategoryMap = new OpTypeCategories().getOpType2OpCategoryMap();

    private IFileEntityGenerator sourceFileEntityGenerator;
    private IFileEntityGenerator destFileEntityGenerator;
    private IStringGenerator operationTypeGenerator;
    private IStringGenerator operationResultGenerator;
    private IStringGenerator operationResultCodeGenerator;

    public FileOperationGenerator() throws GeneratorException {
        sourceFileEntityGenerator = new FileEntityGenerator();
        destFileEntityGenerator = new FileEntityGenerator();
        operationTypeGenerator = new FileOperationTypeCyclicGenerator();
        operationResultGenerator = new OperationResultPercentageGenerator();
        operationResultCodeGenerator = new RandomStringGenerator(6);
    }

    public FileOperation getNext(){
        String operationType = (String) getOperationTypeGenerator().getNext();
        return new FileOperation(getSourceFileEntityGenerator().getNext(), getDestFileEntityGenerator().getNext(),
                operationType, getOperationTypeCategories(operationType), (String) getOperationResultGenerator().getNext(), (String) getOperationResultCodeGenerator().getNext());
    }

    private List<String> getOperationTypeCategories(String operationType) {
        return Arrays.asList(opType2OpCategoryMap.get(operationType));
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
}
