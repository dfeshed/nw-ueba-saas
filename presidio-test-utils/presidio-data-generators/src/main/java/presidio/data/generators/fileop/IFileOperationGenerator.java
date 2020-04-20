package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FileOperation;

public interface IFileOperationGenerator {
    FileOperation getNext();
}
