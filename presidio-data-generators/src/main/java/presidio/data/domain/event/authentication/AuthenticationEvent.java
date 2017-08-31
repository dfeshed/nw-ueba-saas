package presidio.data.domain.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class AuthenticationEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant eventTime;
    private String dataSource;
    private User user;
    private String operationType;
    private List<String> operationTypeCategories;
    private MachineEntity srcMachineEntity;
    private MachineEntity dstMachineEntity;
    private String result;
    private String resultCode;
    private String authenticationDescription;
    private String objectDN;
    private String objectCanonical;

    public AuthenticationEvent(String eventId, Instant eventTime, String dataSource, User user, String operationType, List<String> operationTypeCategories, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result, String resultCode) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.user = user;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
        this.resultCode = resultCode;
    }

    public AuthenticationEvent(String eventId, Instant eventTime, String dataSource, User user, String operationType, List<String> operationTypeCategories, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity, String result, String resultCode, String objectDN, String objectCanonical) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.dataSource = dataSource;
        this.user = user;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.result = result;
        this.resultCode = resultCode;
        this.objectDN = objectDN;
        this.objectCanonical = objectCanonical;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
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

    @Override
    public String toString() {
        return "AuthenticationEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventTime=" + eventTime +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategories=" + operationTypeCategories +
                ", srcMachineEntity=" + srcMachineEntity +
                ", dstMachineEntity=" + dstMachineEntity +
                ", result='" + result + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", authenticationDescription='" + authenticationDescription + '\'' +
                ", objectDN='" + objectDN + '\'' +
                ", objectCanonical='" + objectCanonical + '\'' +
                '}';
    }

    @Override
    public Instant getDateTime() {
        return this.eventTime;
    }
}
