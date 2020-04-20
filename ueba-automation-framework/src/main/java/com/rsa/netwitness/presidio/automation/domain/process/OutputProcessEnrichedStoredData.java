package com.rsa.netwitness.presidio.automation.domain.process;


import com.google.gson.annotations.Expose;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "output_process_enriched_events")
public class OutputProcessEnrichedStoredData {

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
    @Field("userId")
    private String userId;

    @Expose
    @Field("userName")
    private String userName;

    @Expose
    @Field("userdisplayName")
    private String userdisplayName;


    @Expose
    @Field("dataSource")
    private String dataSource;

    @Expose
    @Field("operationType")
    private String operationType;

    @Expose
    @Field("result")
    private String result;

    @Expose
    @Field("operationTypeCategories")
    private List operationTypeCategories;

    @Expose
    @Field("additionalInfo")
    private Map additionalInfo;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserdisplayName() {
        return userdisplayName;
    }

    public void setUserdisplayName(String userdisplayName) {
        this.userdisplayName = userdisplayName;
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

    public List getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return null;
    }
}
