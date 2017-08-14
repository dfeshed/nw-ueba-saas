package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

public class FileDescriptionGenerator {

    public static String buildFileDescription(FileEvent fileEvent){
        String fileDescription = "The file " + fileEvent.getFileOperation().getSourceFile().getFilePath() +
                " " + fileEvent.getFileOperation().getOperationType().getName().split("_")[1].toLowerCase() +
                " on " + fileEvent.getUser().getUsername();
        return fileDescription;
    }
}
