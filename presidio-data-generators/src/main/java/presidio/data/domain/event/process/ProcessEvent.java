package presidio.data.domain.event.process;

import presidio.data.domain.IUser;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class ProcessEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private String dataSource;
    private IUser user;
    private ProcessOperation processOperation;
    private MachineEntity machineEntity;
    private String processEventDescription;

    public ProcessEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public ProcessEvent(String eventId, Instant dateTime, String dataSource, IUser user, ProcessOperation processOperation, MachineEntity machineEntity) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.dataSource = dataSource;
        this.user = user;
        this.processOperation = processOperation;
        this.machineEntity = machineEntity;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    public ProcessOperation getProcessOperation() {
        return processOperation;
    }

    public void setProcessOperation(ProcessOperation processOperation) {
        this.processOperation = processOperation;
    }

    public MachineEntity getMachineEntity() {
        return machineEntity;
    }

    public void setMachineEntity(MachineEntity machineEntity) {
        this.machineEntity = machineEntity;
    }

    public String getProcessEventDescription() {
        return processEventDescription;
    }

    public void setProcessEventDescription(String processEventDescription) {
        this.processEventDescription = processEventDescription;
    }

    @Override
    public String toString() {
        return "ProcessEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user.toString() +
                ", processOperation=" + processOperation.toString() +
                ", machineEntity=" + machineEntity.toString() +
                ", description=" + processEventDescription +
                '}';
    }
}
