package presidio.data.domain.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class AuthenticationEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant eventTime;
    private String dataSource;
    private User user;
    private AuthenticationOperation authenticationOperation;
    private MachineEntity srcMachineEntity;
    private MachineEntity dstMachineEntity;
    private String result;
    private String resultCode;
    private String authenticationDescription;
    private String objectDN;
    private String objectCanonical;
    private String site;

    public AuthenticationEvent(String eventId, Instant eventTime, String dataSource, User user, AuthenticationOperation authenticationOperation, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result, String resultCode) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.user = user;
        this.authenticationOperation = authenticationOperation;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
        this.resultCode = resultCode;
    }

    public AuthenticationEvent(String eventId, Instant eventTime, String dataSource, User user, AuthenticationOperation authenticationOperation, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result, String resultCode, String objectDN, String objectCanonical) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.user = user;
        this.authenticationOperation = authenticationOperation;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
        this.resultCode = resultCode;
        this.objectDN = objectDN;
        this.objectCanonical = objectCanonical;
    }

    public AuthenticationEvent(String eventId, Instant eventTime, String dataSource, User user, AuthenticationOperation authenticationOperation, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result, String resultCode, String objectDN, String objectCanonical, String site) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.user = user;
        this.authenticationOperation = authenticationOperation;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
        this.resultCode = resultCode;
        this.objectDN = objectDN;
        this.objectCanonical = objectCanonical;
        this.site = site;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthenticationOperation getAuthenticationOperation() {
        return authenticationOperation;
    }

    public void setAuthenticationOperation(AuthenticationOperation authenticationOperation) {
        this.authenticationOperation = authenticationOperation;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getAuthenticationDescription() {
        return authenticationDescription;
    }

    public void setAuthenticationDescription(String authenticationDescription) {
        this.authenticationDescription = authenticationDescription;
    }

    public String getObjectDN() {
        return objectDN;
    }

    public void setObjectDN(String objectDN) {
        this.objectDN = objectDN;
    }

    public String getObjectCanonical() {
        return objectCanonical;
    }

    public void setObjectCanonical(String objectCanonical) {
        this.objectCanonical = objectCanonical;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public Instant getDateTime() {
        return this.eventTime;
    }

    @Override
    public String toString() {
        return "AuthenticationEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventTime=" + eventTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user +
                ", authenticationOperation=" + authenticationOperation +
                ", srcMachineEntity=" + srcMachineEntity +
                ", dstMachineEntity=" + dstMachineEntity +
                ", result='" + result + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", authenticationDescription='" + authenticationDescription + '\'' +
                ", objectDN='" + objectDN + '\'' +
                ", objectCanonical='" + objectCanonical + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}
