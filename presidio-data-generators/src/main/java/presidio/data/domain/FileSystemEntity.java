package presidio.data.domain;

public class FileSystemEntity {
    String fileSystemType;
    String  fileSystemLogonID;

    public FileSystemEntity() {
        this.fileSystemType = "1";
        this.fileSystemLogonID = "0x0,0x3e7";
    }

    public FileSystemEntity(String fileSystemType, String fileSystemLogonID) {
        this.fileSystemType = fileSystemType;
        this.fileSystemLogonID = fileSystemLogonID;
    }

    public String getFileSystemType() {
        return fileSystemType;
    }

    public void setFileSystemType(String fileSystemType) {
        this.fileSystemType = fileSystemType;
    }

    public String getFileSystemLogonID() {
        return fileSystemLogonID;
    }

    public void setFileSystemLogonID(String fileSystemLogonID) {
        this.fileSystemLogonID = fileSystemLogonID;
    }

    @Override
    public String toString() {
        return "FileSystemEntity{" +
                "fileSystemType='" + fileSystemType + '\'' +
                ", fileSystemLogonID='" + fileSystemLogonID + '\'' +
                '}';
    }
}
