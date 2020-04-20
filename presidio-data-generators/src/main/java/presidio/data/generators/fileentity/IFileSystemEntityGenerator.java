package presidio.data.generators.fileentity;

import presidio.data.domain.FileSystemEntity;

public interface IFileSystemEntityGenerator {
    FileSystemEntity getNext();
}
