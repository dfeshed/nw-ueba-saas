package presidio.data.domain.event.ioc;

import presidio.data.domain.IocEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class IocEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private String dataSource;
    private User user;
    private IocEntity iocEntity;
    private MachineEntity machineEntity;
    private String iocEventDescription;

    public IocEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public IocEvent(String eventId, Instant dateTime, String dataSource, User user, IocEntity iocEntity, MachineEntity machineEntity) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.dataSource = dataSource;
        this.user = user;
        this.iocEntity = iocEntity;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public MachineEntity getMachineEntity() {
        return machineEntity;
    }

    public void setMachineEntity(MachineEntity machineEntity) {
        this.machineEntity = machineEntity;
    }

    public IocEntity getIocEntity() {
        return iocEntity;
    }

    public void setIocEntity(IocEntity iocEntity) {
        this.iocEntity = iocEntity;
    }

    public String getIocEventDescription() {
        return iocEventDescription;
    }

    public void setIocEventDescription(String iocEventDescription) {
        this.iocEventDescription = iocEventDescription;
    }

    @Override
    public String toString() {
        return "IocEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user +
                ", iocEntity=" + iocEntity +
                ", machineEntity=" + machineEntity +
                ", iocEventDescription='" + iocEventDescription + '\'' +
                '}';
    }
}
