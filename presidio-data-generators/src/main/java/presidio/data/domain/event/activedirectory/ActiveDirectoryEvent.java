package presidio.data.domain.event.activedirectory;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import presidio.data.domain.User;

import java.time.Instant;
import java.util.List;

public class ActiveDirectoryEvent extends Event implements Serializable {

    private Instant eventTime;
    private String eventId;
    private ActiveDirectoryOperation operation;
    private User user;
    private String dataSource;

    private MachineEntity srcMachineEntity;
    private MachineEntity dstMachineEntity;
    private String result;

    public ActiveDirectoryEvent(Instant eventTime, String eventId, User user, String dataSource, ActiveDirectoryOperation operation) {
        this.eventTime = eventTime;
        this.eventId = eventId;
        this.user = user;
        this.dataSource = dataSource;
        this.operation = operation;
    }

    public ActiveDirectoryEvent(Instant eventTime, String eventId, User user, String dataSource, ActiveDirectoryOperation operation, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result) {
        this.eventTime = eventTime;
        this.eventId = eventId;
        this.operation = operation;
        this.user = user;
        this.dataSource = dataSource;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime){
        this.eventTime=eventTime;
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

    public MachineEntity getSrcMachineEntity() {
        return srcMachineEntity;
    }

    public void setSrcMachineEntity(MachineEntity srcMachineEntity) {
        this.srcMachineEntity = srcMachineEntity;
    }

    public MachineEntity getDstMachineEntity() {
        return dstMachineEntity;
    }

    public void setDstMachineEntity(MachineEntity dstMachineEntity) {
        this.dstMachineEntity = dstMachineEntity;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public Instant getDateTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryEvent{" +
                "eventTime=" + eventTime +
                ", eventId='" + eventId + '\'' +
                ", operation=" + operation +
                ", user=" + user +
                ", dataSource='" + dataSource + '\'' +
                ", srcMachineEntity=" + srcMachineEntity +
                ", dstMachineEntity=" + dstMachineEntity +
                ", result='" + result + '\'' +
                '}';
    }
}
