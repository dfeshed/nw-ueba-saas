package com.rsa.netwitness.presidio.automation.domain.authentication;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.data.domain.event.Event;

import java.time.Instant;
import java.util.List;

@Document(collection = "enriched_authentication")
public class EnrichedAuthenticationStoredData extends Event {

    @Id
    private String id;

    @Expose
    @Field("userId")
    private String userId;

    @Expose
    @Field("srcMachineId")
    private String srcMachineId;

    @Expose
    @Field("dstMachineId")
    private String dstMachineId;

    @Expose
    @Field("srcMachineNameRegexCluster")
    private String srcMachineNameRegexCluster;

    @Expose
    @Field("dstMachineNameRegexCluster")
    private String dstMachineNameRegexCluster;

    @Expose
    @Field("dstMachineDomain")
    private String dstMachineDomain;

    @Expose
    @Field("site")
    private String site;

    @Expose
    @Field("city")
    private String city;

    @Expose
    @Field("country")
    private String country;

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
    private List operationTypeCategories;

    @Expose
    @Field("result")
    private String result;

    @Expose
    @Field("resultCode")
    private String resultCode;

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

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public Instant getDateTime() {
        return null;
    }
}