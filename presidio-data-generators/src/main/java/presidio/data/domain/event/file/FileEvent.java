package presidio.data.domain.event.file;


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

    @Override
    public String toString() {
        return "FileEvent{" +
                "dateTime=" + dateTime +
                ", user='" + user.toString() + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", fileOperation=" + fileOperation.toString() +
                '}';
    }
}
