package presidio.data.generators.printop;

import presidio.data.domain.event.print.PrintFileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.ILongGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;

public class PrintFileOperationGenerator extends FileOperationGenerator implements IPrintFileOperationGenerator {
    private ILongGenerator numOfPagesGenerator;

    public PrintFileOperationGenerator() throws GeneratorException {
        super();
        numOfPagesGenerator = new LongIncrementalGenerator();
    }

    public PrintFileOperation getNext(){
        return new PrintFileOperation(getSourceFileEntityGenerator().getNext(), getDestFileEntityGenerator().getNext(),
                getOperationTypeGenerator().getNext(), getOperationResultGenerator().getNext(), getOperationResultCodeGenerator().getNext(),getNumOfPagesGenerator().getNext());
    }

    public ILongGenerator getNumOfPagesGenerator() {
        return numOfPagesGenerator;
    }

    public void setNumOfPagesGenerator(ILongGenerator numOfPagesGenerator) {
        this.numOfPagesGenerator = numOfPagesGenerator;
    }
}
