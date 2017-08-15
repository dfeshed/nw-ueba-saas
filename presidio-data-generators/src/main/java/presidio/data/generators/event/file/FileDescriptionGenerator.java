package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;

public class FileDescriptionGenerator implements IFileDescriptionGenerator{

    private String buildFileDescription(FileEvent fileEvent){
        String operationType = fileEvent.getFileOperation().getOperationType().getName();//.split("\\s|[_]")[1].toLowerCase();
        String fileDescription = "The file " + fileEvent.getFileOperation().getSourceFile().getFilePath() +
                " " + operationType + " on " + fileEvent.getUser().getUsername();
        return fileDescription;
    }

    @Override
    public void updateFileDescription(FileEvent fileEvent) {
        fileEvent.setFileDescription(buildFileDescription(fileEvent));
    }
}
