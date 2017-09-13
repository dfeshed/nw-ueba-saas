package presidio.data.domain;

import java.io.File;

public class FileEntity {
    String filePath;
    String  fileName;
    Long    fileSize;
    Boolean isDriveShared;
    Boolean isDirectory;

    public FileEntity(String fileName, String filePath, Long fileSize, Boolean isDriveShared, Boolean isDirectory) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.isDriveShared = isDriveShared;
        this.isDirectory = isDirectory;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Boolean isDriveShared() {
        return isDriveShared;
    }

    public void setDriveShared(Boolean driveShared) {
        isDriveShared = driveShared;
    }

    public Boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    public String getAbsoluteFilePath() {
        String separator = "/";
        if(isDirectory == null || isDirectory){
            return fileName;
        } else {
            if(filePath.contains("\\")){
                separator = "\\";
            }

            return filePath + separator + fileName;
        }
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", isDriveShared=" + isDriveShared +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
