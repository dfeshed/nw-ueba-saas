package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

/**
 * Created by YaronDL on 8/15/2017.
 */
public interface IFileDescriptionGenerator {
    void updateFileDescription(FileEvent fileEvent);
}
