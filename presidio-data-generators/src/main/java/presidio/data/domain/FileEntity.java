package presidio.data.domain;

import java.io.File;

public class FileEntity {
    String filePath;
    String  fileName;
    long    fileSize;
    boolean isDriveShared;
    boolean isDirectory;

    public FileEntity(String fileName, String filePath, long fileSize, boolean isDriveShared, boolean isDirectory) {
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDriveShared() {
        return isDriveShared;
    }

    public void setDriveShared(boolean driveShared) {
        isDriveShared = driveShared;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getAbsoluteFilePath() {
        return isDirectory ? "" : (filePath + File.separator + fileName);
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
