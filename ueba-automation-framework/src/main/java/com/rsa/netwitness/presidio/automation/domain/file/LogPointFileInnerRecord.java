package com.rsa.netwitness.presidio.automation.domain.file;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import presidio.data.domain.event.Event;

import java.time.Instant;
import java.util.List;

public class LogPointFileInnerRecord extends Event {

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
    private Boolean isSrcDriveShared;
    @Expose
    private String srcFilePath;
    @Expose
    private String dataSource;
    @Expose
    private String userId;
    @Expose
    private String operationType;
    @Expose
    private List<String> operationTypeCategories;
    @Expose
    private String result;

    @Override
    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String absoluteSrcFilePath) {
        this.srcFilePath = absoluteSrcFilePath;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "LogPointFileInnerRecord{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", colDateTime=" + colDateTime +
                ", logDateTime=" + logDateTime +
                ", isSrcDriveShared=" + isSrcDriveShared +
                ", srcFilePath='" + srcFilePath + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategories=" + operationTypeCategories +
                ", result='" + result + '\'' +
                '}';
    }
}
