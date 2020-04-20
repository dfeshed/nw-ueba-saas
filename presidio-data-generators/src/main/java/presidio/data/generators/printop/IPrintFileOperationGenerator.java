package presidio.data.generators.printop;

import presidio.data.domain.event.print.PrintFileOperation;

public interface IPrintFileOperationGenerator {
    PrintFileOperation getNext();
}
