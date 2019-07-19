package com.rsa.netwitness.presidio.automation.domain.authentication;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "output_authentication_enriched_events")
public class OutputAuthenticationEnrichStoredData {

    @Id
    private String id;

    @Expose
    @Field("SrcMachineId")
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
    @Field("userId")
    private String userId;

    @Expose
    @Field("userName")
    private String userName;

    @Expose
    @Field("userDisplayName")
    private String userDisplayName;

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

    @Expose
    @Field("additionalInfo")
    private Map additionalInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
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

    public Map getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "OutputAuthenticationEnrichStoredData{" +
                "id='" + id + '\'' +
                ", srcMachineId='" + srcMachineId + '\'' +
                ", dstMachineId='" + dstMachineId + '\'' +
                ", srcMachineNameRegexCluster='" + srcMachineNameRegexCluster + '\'' +
                ", dstMachineNameRegexCluster='" + dstMachineNameRegexCluster + '\'' +
                ", dstMachineDomain='" + dstMachineDomain + '\'' +
                ", site='" + site + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategories=" + operationTypeCategories +
                ", result='" + result + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}