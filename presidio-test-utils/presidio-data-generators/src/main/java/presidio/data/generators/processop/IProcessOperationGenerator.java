package presidio.data.generators.processop;

import presidio.data.domain.event.process.ProcessOperation;

public interface IProcessOperationGenerator {
    ProcessOperation getNext();
}
