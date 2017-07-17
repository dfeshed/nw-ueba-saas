package presidio.data.generators.file;

import presidio.data.generators.domain.event.file.FileOperation;

public interface IFileOperationGenerator {
    FileOperation getNext();
}
