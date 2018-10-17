package presidio.data.domain.event.registry;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.RegistryEntry;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class RegistryEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private String dataSource;
    private User user;
    private RegistryOperation registryOperation;
    private MachineEntity machineEntity;
    private RegistryEntry registryEntry;

    public RegistryEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public RegistryEvent(String eventId, Instant dateTime, String dataSource, User user, RegistryOperation registryOperation, MachineEntity machineEntity) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.dataSource = dataSource;
        this.user = user;
        this.registryOperation = registryOperation;
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

    public RegistryOperation getRegistryOperation() {
        return registryOperation;
    }

    public void setRegistryOperation(RegistryOperation registryOperation) {
        this.registryOperation = registryOperation;
    }

    public RegistryEntry getRegistryEntry() {
        return registryEntry;
    }

    public void setRegistryEntry(RegistryEntry registryEntry) {
        this.registryEntry = registryEntry;
    }

    @Override
    public String toString() {
        return "RegistryEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user.toString() +
                ", registryOperation=" + registryOperation.toString() +
                ", machineEntity=" + machineEntity.toString() +
                ", registryEntry=" + registryEntry.toString() +
                '}';
    }
}
