package com.rsa.netwitness.presidio.automation.domain.activedirectory;


import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "enriched_active_directory")
public class ActiveDirectoryEnrichStoredData {

    @Id
    private String id;

    @Expose
    @Field("userId")
    private String userId;

    @Expose
    @Field("isUserAdmin")
    private Boolean isUserAdmin;

    @Expose
    @Field("objectId")
    private String objectId;

    @Expose
    @Field("eventId")
    private String eventId;

    @Expose
    @Field("dataSource")
    private String dataSource;

    @Expose
    @Field("operationType")
    private String operationType;

    @Expose
    @Field("operationTypeCategories")
    private List<String> operationTypeCategories;

    @Expose
    @Field("result")
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(Boolean userAdmin) {
        isUserAdmin = userAdmin;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
        return null;
    }
}
