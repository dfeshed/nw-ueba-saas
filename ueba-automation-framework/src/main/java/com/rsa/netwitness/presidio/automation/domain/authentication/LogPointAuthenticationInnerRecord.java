package com.rsa.netwitness.presidio.automation.domain.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import presidio.data.domain.event.Event;

import java.time.Instant;

public class LogPointAuthenticationInnerRecord extends Event {

    @Id
    private String eventId;
    private Instant dateTime;
    @Expose
    @SerializedName("col_ts")
    private long colDateTime;
    @Expose
    @SerializedName("log_ts")
    private long logDateTime;
    @Expose
    private String userId;
    @Expose
    private String srcMachineId;
    @Expose
    private String srcMachineName;
    @Expose
    private String dstMachineDomain;
    @Expose
    private String dataSource;
    @Expose
    private String operationType;
    @Expose
    private String result;
    @Expose
    private String resultCode;

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public long getColDateTime() {
        return colDateTime;
    }

    public void setColDateTime(long colDateTime) {
        this.colDateTime = colDateTime;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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
        return "LogPointAuthenticationInnerRecord{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", logDateTime=" + logDateTime +
                ", userId='" + userId + '\'' +
                ", srcMachineId='" + srcMachineId + '\'' +
                ", srcMachineName='" + srcMachineName + '\'' +
                ", dstMachineDomain='" + dstMachineDomain + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", operationType='" + operationType + '\'' +
                ", result='" + result + '\'' +
                ", resultCode='" + resultCode + '\'' +
                '}';
    }
}
