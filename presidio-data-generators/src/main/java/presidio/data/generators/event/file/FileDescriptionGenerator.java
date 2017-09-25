package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

public class FileDescriptionGenerator implements IFileDescriptionGenerator{

    private String buildFileDescription(FileEvent fileEvent){
        String operationType = fileEvent.getFileOperation().getOperationType().getName();
        String description;
        String op = operationType.substring(operationType.indexOf("_") + 1, operationType.length()).toLowerCase();

        if (operationType.contains("FOLDER")) {
            description = "The folder " + fileEvent.getFileOperation().getSourceFile().getFilePath() +
                    " " + op + " on " + fileEvent.getUser().getUsername();
        }

        else {
            description = "The file " + fileEvent.getFileOperation().getSourceFile().getAbsoluteFilePath() +
                    " " + op + " on " + fileEvent.getUser().getUsername();
        }
        return description;
    }

    @Override
    public void updateFileDescription(FileEvent fileEvent) {
        fileEvent.setFileDescription(buildFileDescription(fileEvent));
    }
}
