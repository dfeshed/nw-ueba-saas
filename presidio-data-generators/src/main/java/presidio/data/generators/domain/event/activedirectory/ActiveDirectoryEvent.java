package presidio.data.generators.domain.event.activedirectory;

import presidio.data.generators.domain.User;

import java.time.Instant;

public class ActiveDirectoryEvent {

    private Instant eventTime;
    private String eventId;
    private ActiveDirectoryOperation operation;
    private User user;
    private String dataSource;

    public ActiveDirectoryEvent(Instant eventTime, String eventId, ActiveDirectoryOperation operation, User user, String dataSource) {
        this.eventTime = eventTime;
        this.eventId = eventId;
        this.operation = operation;
        this.user = user;
        this.dataSource = dataSource;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ActiveDirectoryOperation getOperation() {
        return operation;
    }

    public void setOperation(ActiveDirectoryOperation operation) {
        this.operation = operation;
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

    @Override
    public String toString() {
        return "ActiveDirectoryEvent{" +
                "eventTime=" + eventTime +
                ", eventId=" + eventId +
                ", operation=" + operation +
                ", user=" + user +
                ", dataSource='" + dataSource + '\'' +
                '}';
    }
}
