package presidio.data.generators.dlpfileop;

import presidio.data.domain.event.dlpfile.DLPFileOperation;

// This class will be replaced by IFileOperationsGenerator
public interface IDLPFileOperationGenerator {
    DLPFileOperation getNext();
}
