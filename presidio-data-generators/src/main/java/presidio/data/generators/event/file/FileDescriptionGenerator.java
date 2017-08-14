package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

public class FileDescriptionGenerator {

    public static String buildFileDescription(FileEvent fileEvent){
        String operationType;
        try {
            operationType = fileEvent.getFileOperation().getOperationType().getName().split("\\s|[_]")[1].toLowerCase();
        } catch (Exception e){
            e.printStackTrace();
            operationType = fileEvent.getFileOperation().getOperationType().getName().toLowerCase();
        }
        String fileDescription = "The file " + fileEvent.getFileOperation().getSourceFile().getFilePath() +
                " " + operationType + " on " + fileEvent.getUser().getUsername();
        return fileDescription;
    }
}
