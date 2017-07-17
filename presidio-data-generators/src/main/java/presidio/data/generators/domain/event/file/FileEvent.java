package presidio.data.generators.domain.event.file;


import presidio.data.generators.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class FileEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant dateTime;
    private String normalizedUsername;
    private String dataSource;
    private FileOperation fileOperation;

    public FileEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public FileEvent(Instant dateTime, String normalizedUsername, FileOperation fileOperation, String dataSource) {
        this.dateTime = dateTime;
        this.normalizedUsername = normalizedUsername;
        this.dataSource = dataSource;
        this.fileOperation = fileOperation;
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

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
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
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", fileOperation=" + fileOperation.toString() +
                '}';
    }
}
