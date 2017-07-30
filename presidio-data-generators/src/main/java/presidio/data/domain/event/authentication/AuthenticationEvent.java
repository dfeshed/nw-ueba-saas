package presidio.data.domain.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class AuthenticationEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant eventTime;
    private String dataSource;
    private String authenticationType;
    private String eventId;
    private MachineEntity dstMachineEntity;
    private MachineEntity srcMachineEntity;
    private User user;
    private String result;
    private String resultCode;

    public AuthenticationEvent(Instant eventTime, String dataSource, String authenticationType, String eventId, MachineEntity dstMachineEntity, MachineEntity srcMachineEntity, User user, String result, String resultCode) {
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.authenticationType = authenticationType;
        this.eventId = eventId;
        this.dstMachineEntity = dstMachineEntity;
        this.srcMachineEntity = srcMachineEntity;
        this.user = user;
        this.result = result;
        this.resultCode = resultCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public MachineEntity getDstMachineEntity() {
        return dstMachineEntity;
    }

    public void setDstMachineEntity(MachineEntity dstMachineEntity) {
        this.dstMachineEntity = dstMachineEntity;
    }

    public MachineEntity getSrcMachineEntity() {
        return srcMachineEntity;
    }

    public void setSrcMachineEntity(MachineEntity srcMachineEntity) {
        this.srcMachineEntity = srcMachineEntity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "AuthenticationEvent{" +
                "eventTime=" + eventTime +
                ", dataSource='" + dataSource + '\'' +
                ", authenticationType='" + authenticationType.toString() + '\'' +
                ", eventId='" + eventId + '\'' +
                ", dstMachineEntity=" + dstMachineEntity.toString() +
                ", srcMachineEntity=" + srcMachineEntity.toString() +
                ", user=" + user.toString() +
                ", result='" + result + '\'' +
                ", resultCode='" + resultCode + '\'' +
                '}';
    }

    @Override
    public Instant getDateTime() {
        return this.eventTime;
    }
}
