package presidio.data.generators.file;

import presidio.data.generators.domain.event.file.FileEntity;

public interface IFileEntityGenerator {
    FileEntity getNext();
}
