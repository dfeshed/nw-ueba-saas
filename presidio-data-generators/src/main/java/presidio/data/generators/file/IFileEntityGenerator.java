package presidio.data.generators.file;

import presidio.data.generators.domain.FileEntity;

public interface IFileEntityGenerator {
    FileEntity getNext();
}
