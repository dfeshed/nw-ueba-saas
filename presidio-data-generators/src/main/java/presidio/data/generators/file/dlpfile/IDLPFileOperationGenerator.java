package presidio.data.generators.file.dlpfile;

import presidio.data.generators.domain.event.file.dlpfile.DLPFileOperation;

// This class will be replaced by IFileOperationsGenerator
public interface IDLPFileOperationGenerator {
    DLPFileOperation getNext();
}
