package presidio.data.domain.event.file;

import presidio.data.domain.FileSystemEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class FileEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private String dataSource;
    private User user;
    private FileOperation fileOperation;
    private FileSystemEntity fileSystemEntity;
    private MachineEntity machineEntity;
    private String fileDescription;

    public FileEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public FileEvent(String eventId, Instant dateTime, User user, FileOperation fileOperation, String dataSource) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.user = user;
        this.dataSource = dataSource;
        this.fileOperation = fileOperation;
    }

    public FileEvent(String eventId, Instant dateTime, User user, FileOperation fileOperation, String dataSource, FileSystemEntity fileSystemEntity) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.user = user;
        this.dataSource = dataSource;
        this.fileOperation = fileOperation;
        this.fileSystemEntity = fileSystemEntity;
    }

    public FileEvent(User user, Instant dateTime, String eventId, FileOperation fileOperation, String dataSource, FileSystemEntity fileSystemEntity, MachineEntity machineEntity) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.user = user;
        this.dataSource = dataSource;
        this.fileOperation = fileOperation;
        this.fileSystemEntity = fileSystemEntity;
        this.machineEntity = machineEntity;

    }

    public FileEvent(String eventId, Instant dateTime, User user, FileOperation fileOperation, String dataSource, FileSystemEntity fileSystemEntity, MachineEntity machineEntity, String fileDescription) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.user = user;
        this.dataSource = dataSource;
        this.fileOperation = fileOperation;
        this.fileSystemEntity = fileSystemEntity;
        this.machineEntity = machineEntity;
        this.fileDescription = fileDescription;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public FileOperation getFileOperation() {
        return fileOperation;
    }

    public void setFileOperation(FileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    public FileSystemEntity getFileSystemEntity() {
        return fileSystemEntity;
    }

    public void setFileSystemEntity(FileSystemEntity fileSystemEntity) {
        this.fileSystemEntity = fileSystemEntity;
    }

    public MachineEntity getMachineEntity() {
        return machineEntity;
    }

    public void setMachineEntity(MachineEntity machineEntity) {
        this.machineEntity = machineEntity;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @Override
    public String toString() {
        return "FileEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user +
                ", fileOperation=" + fileOperation +
                ", fileSystemEntity=" + fileSystemEntity +
                ", machineEntity=" + machineEntity +
                ", fileDescription='" + fileDescription + '\'' +
                '}';
    }
}
