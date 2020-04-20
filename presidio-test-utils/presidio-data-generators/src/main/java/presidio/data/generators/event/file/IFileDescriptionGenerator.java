package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

public interface IFileDescriptionGenerator {
    void updateFileDescription(FileEvent fileEvent);
}
